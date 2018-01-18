package de.heisluft.minicpu.internal;

public interface MemoryWatcher {
    void reportMemoryChange(int changedCell);
}

