package de.heisluft.minicpu.internal;

import java.util.ArrayList;

public abstract class Cpu implements CpuWatchRegistrar, OpCode {
    Register pc;
    Register a;
    boolean ltflag;
    boolean eqflag;
    boolean ovflag;
    Memory memory;
    private OpCodes mnemos;
    private ArrayList<CpuWatcher> watchers;
    int currentOpCode;
    int addressMode;
    int address;
    private String[] progadr = new String[4];
    private String[] progmem = new String[4];
    private String[] dataadr = new String[2];
    private String[] datamem = new String[2];
    private int progAdrAlt;
    private int dataAdrAlt;
    private long timeBarrier;

    Cpu(Memory memory) {
        this.memory = memory;
        pc = new Register();
        a = new Register();
        mnemos = OpCodes.INSTANCE;
        watchers = new ArrayList<>();
        reset();
        timeBarrier = 5;
    }

    @Override
    public void register(CpuWatcher watcher) {
        watchers.add(watcher);
    }


     void report(String string, String string2, String string3, String string4, String string5, boolean bl, int n, int n2) {
        for (CpuWatcher watcher : watchers) {
            int i;
            if (n != -1) {
                progAdrAlt = n;
            }
            if (n2 != -1) {
                dataAdrAlt = n2;
            }
            for (i = 0; i < progmem.length; ++i) {
                progadr[i] = "" + (progAdrAlt + i < 0 ? 65536 + (progAdrAlt + i) : progAdrAlt + i);
                progmem[i] = "" + memory.getSignedWord(progAdrAlt + i);
            }
            for (i = 0; i < datamem.length; ++i) {
                dataadr[i] = "" + (dataAdrAlt + i < 0 ? 65536 + (dataAdrAlt + i) : dataAdrAlt + i);
                datamem[i] = "" + memory.getSignedWord(dataAdrAlt + i);
            }
            String string6 = mnemos.getMnemonic(currentOpCode);
            watcher.reportCmd(string, string2, string3, string4, string5, "" + a.getValue(), eqflag, ltflag, ovflag, bl ? (addressMode == 2 ? string6 + "I" : string6) : "" + (currentOpCode + addressMode * 256), "" + address, "" + pc.getValue(), progadr, progmem, dataadr, datamem);
        }
    }

    void reportError(String string) {
        for (CpuWatcher watcher : watchers) {
            watcher.reportError(string);
        }
    }

     void testOpCode() {
        switch (currentOpCode) {
            case NOOP:
            case RESET:
            case 46: 
            case HALT: {
                if (addressMode == 0 && address == 0) break;
                currentOpCode = -1;
                break;
            }
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
            case CMP:
            case LOAD:
            case STORE: {
                if (addressMode == 1 || addressMode == 3) break;
                currentOpCode = -1;
                break;
            }
            case JGT:
            case JGE:
            case JLT:
            case JLE:
            case JEG:
            case JNE:
            case JMP:
            case JOV: {
                if (addressMode == 1) break;
                currentOpCode = -1;
                break;
            }
            case AND:
            case OR:
            case XOR:
            case SHL:
            case SHR:
            case SHRA: {
                if (addressMode != 0) break;
                currentOpCode = -1;
                break;
            }
            default: {
                currentOpCode = -1;
            }
        }
    }

    public void execute() {
        long l = System.currentTimeMillis() + timeBarrier * 1000;
        do {
            singleStep();
        } while (currentOpCode != HALT && currentOpCode != -1 && l > System.currentTimeMillis());
        if (currentOpCode != HALT && currentOpCode != -1) {
            reportError("Programmabbruch wegen Zeit\u00fcberschreitung");
        }
    }

    public abstract void singleStep();

    public abstract void microStep();

    int getOpCode(int memAddress, int n2) {
        switch (n2) {
            case 1:
                return memory.getSignedWord(memAddress);
            case 2:
                return memAddress;
            case 3:
                return memory.getSignedWord(memory.getUnsignedWord(memAddress));
        }
        return NOOP;
    }

    public void reset() {
        pc.setValue(0);
        a.setValue(0);
        ltflag = false;
        eqflag = false;
        ovflag = false;
        currentOpCode = -1;
        addressMode = 0;
        address = 0;
        for (int i = 0; i < progmem.length; ++i) {
            progadr[i] = "";
            progmem[i] = "";
        }
        for (int i = 0; i < datamem.length; ++i) {
            dataadr[i] = "";
            datamem[i] = "";
        }
        progAdrAlt = 0;
        dataAdrAlt = 0;
        report("", "", "", "", "", true, -1, -1);
    }

    public void copyValuesTo(Cpu other) {
        if (this != other) {
            other.pc.setValue(pc.getValue());
            other.a.setValue(a.getValue());
            other.ltflag = ltflag;
            other.eqflag = eqflag;
            other.ovflag = ovflag;
            other.currentOpCode = currentOpCode;
            other.addressMode = addressMode;
            other.address = address;
            other.progAdrAlt = progAdrAlt;
            other.dataAdrAlt = dataAdrAlt;
        }
    }

    public static Cpu create(boolean detailed) {
        if (detailed)
            return new CpuDetailed(Memory.getInstance());
        else
            return new CpuSimple(Memory.getInstance());
    }

    public void assemble(String string, ErrorProvider provider) {
        new Parser(new Scanner(string), memory, provider).parse();
    }

    public void translate(String string, ErrorProvider provider) {
        String string2 = new de.heisluft.minicpu.internal.minilang.PARSER(string, provider).Parse();
        if (!provider.errored()) {
            memory.wipe();
            new Parser(new Scanner(string2), memory, provider).parse();
        }
    }

    public void setTimeBarrier(int time) {
        timeBarrier = time;
    }

    public void registerMemoryWatcher(MemoryWatcher watcher) {
        memory.register(watcher);
    }
}

