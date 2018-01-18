package de.heisluft.minicpu.internal;

public final class MemoryAccess {

    private MemoryAccess() {throw new UnsupportedOperationException();}

    public static int getUnsignedWord(int memAddress) {
        return Memory.getInstance().getUnsignedWord(memAddress);
    }

    public static int getSignedWord(int memAddress) {
        return Memory.getInstance().getSignedWord(memAddress);
    }

    public static void setWord(int memAddress, int value) {
        Memory.getInstance().setWord(memAddress, value);
    }

    public static void wipe() {
        Memory.getInstance().wipe();
    }
}

