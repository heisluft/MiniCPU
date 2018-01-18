package de.heisluft.minicpu.internal;

class CpuSimple extends Cpu {
    CpuSimple(Memory memory) {
        super(memory);
    }

    @Override
    public void singleStep() {
        currentOpCode = memory.getUnsignedWord(pc.getValue());
        pc.increment();
        addressMode = currentOpCode / 256;
        currentOpCode %= 256;
        address = memory.getSignedWord(pc.getValue());
        testOpCode();
        pc.increment();
        switch (currentOpCode) {
            case NOOP: break;
            case HALT: break;
            case RESET: {
                reset();
                memory.wipe();
                break;
            }
            case NOT: {
                int op1 = a.getValue();
                int op2 = ~op1;
                ovflag = false;
                a.setValue(op2);
                op2 = a.getValue();
                ltflag = op2 < 0;
                eqflag = op2 == 0;
                break;
            }
            case ADD: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 + op2;
                ovflag = result > 32767 || result < -32768;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case SUB: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 - op2;
                ovflag = result > 32767 || result < -32768;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case MUL: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 * op2;
                ovflag = result > 32767 || result < -32768;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case DIV: {
                int result;
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                if (op2 == 0) {
                    reportError("Division durch 0");
                    result = op1;
                    ovflag = true;
                } else {
                    result = op1 / op2;
                    ovflag = result > 32767 || result < -32768;
                }
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case MOD: {
                int result;
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                if (op2 == 0) {
                    reportError("Division durch 0");
                    result = 0;
                    ovflag = true;
                } else {
                    result = op1 % op2;
                    ovflag = result > 32767 || result < -32768;
                }
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case CMP: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                ovflag = false;
                ltflag = op1 < op2;
                eqflag = op1 == op2;
                break;
            }
            case AND: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 & op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case OR: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 | op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case XOR: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 ^ op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case SHL: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 << op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case SHR: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = (op1 & 65535) >> op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case SHRA: {
                int op1 = a.getValue();
                int op2 = getOpCode(address, addressMode);
                int result = op1 >> op2;
                ovflag = false;
                a.setValue(result);
                result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case LOAD: {
                a.setValue(getOpCode(address, addressMode));
                ovflag = false;
                int result = a.getValue();
                ltflag = result < 0;
                eqflag = result == 0;
                break;
            }
            case STORE: {
                memory.setWord(address, a.getValue());
                break;
            }
            case JGT: {
                if (ltflag || eqflag) break;
                pc.setValue(address);
                break;
            }
            case JGE: {
                if (ltflag) break;
                pc.setValue(address);
                break;
            }
            case JLT: {
                if (!ltflag) break;
                pc.setValue(address);
                break;
            }
            case JLE: {
                if (!ltflag && !eqflag) break;
                pc.setValue(address);
                break;
            }
            case JEG: {
                if (!eqflag) break;
                pc.setValue(address);
                break;
            }
            case JNE: {
                if (eqflag) break;
                pc.setValue(address);
                break;
            }
            case JOV: {
                if (!ovflag) break;
                pc.setValue(address);
                break;
            }
            case JMP: {
                pc.setValue(address);
                break;
            }
            default: {
                reportError("Illegaler Befehlscode");
                currentOpCode = -1;
            }
        }
        report("", "", "", "", "", true, pc.getValue() - 2, addressMode == 1 ? address : -1);
    }

    @Override
    public void microStep() {}
}

