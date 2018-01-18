package de.heisluft.minicpu.internal;

class CpuDetailed extends Cpu {
    private MicroSteps curStep = MicroSteps.COMPLETE;
    private int op1;
    private int op2;
    private int pcAlt = 0;

    CpuDetailed(Memory memory) {
        super(memory);
    }

    @Override
    public void reset() {
        curStep = MicroSteps.COMPLETE;
        pcAlt = 0;
        super.reset();
    }

    @Override
    public void singleStep() {
        do microStep(); while (curStep != MicroSteps.COMPLETE);
    }

    @Override
    public void microStep() {
        switch (curStep) {
            case COMPLETE: {
                curStep = MicroSteps.FETCH_OPCODE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case FETCH_OPCODE: {
                pcAlt = pc.getValue();
                int n = memory.getUnsignedWord(pcAlt);
                pc.increment();
                addressMode = n / 256;
                currentOpCode = n % 256;
                curStep = MicroSteps.FETCH_ADDRESS;
                report("" + n, "" + (pcAlt < 0 ? 65536 + pcAlt : pcAlt), "", "", "", false, pcAlt, -1);
                break;
            }
            case FETCH_ADDRESS: {
                pcAlt = pc.getValue();
                address = memory.getSignedWord(pcAlt);
                pc.increment();
                if (addressMode == 3) {
                    curStep = MicroSteps.FETCH_INDIRECT;
                    addressMode = 1;
                } else curStep = MicroSteps.DECODE;
                report("" + address, "" + (pcAlt < 0 ? 65536 + pcAlt : pcAlt), "", "", "", false, -1, -1);
                break;
            }
            case FETCH_INDIRECT: {
                curStep = MicroSteps.DECODE;
                int n = address;
                address = memory.getSignedWord(address);
                report("" + address, "" + n, "", "", "", false, -1, -1);
                break;
            }
            case DECODE: {
                testOpCode();
                curStep = MicroSteps.EXECUTE_1;
                report("", "", "", "", "", true, -1, -1);
                break;
            }
            case EXECUTE_1: {
                execute1();
                break;
            }
            case EXECUTE_2: {
                execute2();
            }
        }
    }

    private void execute1() {
        switch (currentOpCode) {
            case NOOP: {
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, -1, -1);
                break;
            }
            case HALT: {
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, -1, -1);
                break;
            }
            case RESET: {
                reset();
                memory.wipe();
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, -1, -1);
                break;
            }
            case NOT: {
                op1 = a.getValue();
                curStep = MicroSteps.EXECUTE_2;
                report("", "", "" + op1, "", "", true, -1, -1);
                break;
            }
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case CMP:
            case AND:
            case OR:
            case XOR:
            case SHL:
            case SHR:
            case SHRA: {
                op1 = a.getValue();
                op2 = getOpCode(address, addressMode);
                curStep = MicroSteps.EXECUTE_2;
                if (addressMode == 1) {
                    report("" + op2, "" + (address < 0 ? 65536 + address : address), "" + op1, "" + op2, "", true, -1, address);
                    break;
                }
                report("", "", "" + op1, "" + op2, "", true, -1, -1);
                break;
            }
            case LOAD: {
                a.setValue(getOpCode(address, addressMode));
                ovflag = false;
                int n = a.getValue();
                ltflag = n < 0;
                eqflag = n == 0;
                curStep = MicroSteps.COMPLETE;
                if (addressMode == 1) {
                    report("" + n, "" + (address < 0 ? 65536 + address : address), "", "", "", true, -1, address);
                    break;
                }
                report("", "", "", "", "", true, -1, -1);
                break;
            }
            case STORE: {
                memory.setWord(address, a.getValue());
                curStep = MicroSteps.COMPLETE;
                report("" + a.getValue(), "" + (address < 0 ? 65536 + address : address), "", "", "", true, -1, address);
                break;
            }
            case JGT: {
                if (!ltflag && !eqflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JGE: {
                if (!ltflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JLT: {
                if (ltflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JLE: {
                if (ltflag || eqflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JEG: {
                if (eqflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JNE: {
                if (!eqflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JOV: {
                if (ovflag) pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            case JMP: {
                pc.setValue(address);
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", true, pc.getValue(), -1);
                break;
            }
            default: {
                curStep = MicroSteps.COMPLETE;
                report("", "", "", "", "", false, -1, -1);
                reportError("Illegaler Befehlscode");
                currentOpCode = -1;
            }
        }
    }

    private void execute2() {
        int res;
        switch (currentOpCode) {
            case NOT:
            {
                res = ~ op1;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "", "" + res, true, -1, -1);
                break;
            }
            case ADD: {
                res = op1 + op2;
                ovflag = res > 32767 || res < -32768;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case SUB: {
                res = op1 - op2;
                ovflag = res > 32767 || res < -32768;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case MUL: {
                res = op1 * op2;
                ovflag = res > 32767 || res < -32768;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case DIV: {
                if (op2 == 0) {
                    reportError("Division durch 0");
                    res = op1;
                    ovflag = true;
                } else {
                    res = op1 / op2;
                    ovflag = res > 32767 || res < -32768;
                }
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case MOD: {
                if (op2 == 0) {
                    reportError("Division durch 0");
                    res = op1;
                    ovflag = true;
                } else {
                    res = op1 % op2;
                    ovflag = res > 32767 || res < -32768;
                }
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case CMP: {
                ovflag = false;
                ltflag = op1 < op2;
                eqflag = op1 == op2;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "", true, -1, -1);
                break;
            }
            case AND: {
                res = op1 & op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case OR: {
                res = op1 | op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case XOR: {
                res = op1 ^ op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case SHL: {
                res = op1 << op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case SHR: {
                res = (op1 & 65535) >> op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
                break;
            }
            case SHRA: {
                res = op1 >> op2;
                ovflag = false;
                a.setValue(res);
                res = a.getValue();
                ltflag = res < 0;
                eqflag = res == 0;
                curStep = MicroSteps.COMPLETE;
                report("", "", "" + op1, "" + op2, "" + res, true, -1, -1);
            }
        }
    }

    @Override
    public void copyValuesTo(Cpu other) {
        if (this != other) while (curStep != MicroSteps.COMPLETE) microStep();
        super.copyValuesTo(other);
    }

}

