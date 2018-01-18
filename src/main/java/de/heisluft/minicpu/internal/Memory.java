package de.heisluft.minicpu.internal;

import java.util.ArrayList;

class Memory implements MemoryWatchRegistrar {
    private short[] memory = new short[65536];
    private int lastChangedAddress;
    private ArrayList<MemoryWatcher> watchers = new ArrayList<>();
    private static Memory instance = new Memory();

    static Memory getInstance() {
        return instance;
    }

    private Memory() {
        wipe();
        lastChangedAddress = -1;
    }

    @Override
    public void register(MemoryWatcher watcher) {
        watchers.add(watcher);
    }

    private void report() {
        for (MemoryWatcher watcher : watchers) {
            watcher.reportMemoryChange(lastChangedAddress);
        }
    }

    void wipe() {
        for (int i = 0; i < memory.length; ++i) {
            memory[i] = 0;
        }
        memory[memory.length - 1] = -1;
        memory[memory.length - 2] = -1;
        report();
    }

    void setWord(int memAddress, int value) {
        if (value > 32767)
            value -= 65536;
        if (memAddress < 0)
            memAddress += 65536;
        memory[memAddress] = (short)value;
        lastChangedAddress = memAddress;
        report();
    }

    int getUnsignedWord(int memAddress) {
        int signed = getSignedWord(memAddress);
        if (signed < 0)
            signed += 65536;
        return signed;
    }

    int getSignedWord(int memAddress) {
        if (memAddress < 0)
            memAddress += 65536;
        return memory[memAddress];
    }
}

