package de.heisluft.minicpu;

import java.util.ArrayList;

class WindowManager {
    private ArrayList<Display> openDisplays = new ArrayList<>();
    private Display activeCpuDisplay;
    private Display simpleCpuDisplay;
    private Display detailedCpuDisplay;
    private Display memoryDisplay;

    WindowManager(Display simpleCpu, Display detailedCpu, Display memory) {
        activeCpuDisplay = simpleCpu;
        simpleCpuDisplay = simpleCpu;
        detailedCpuDisplay = detailedCpu;
        detailedCpu.hide();
        memoryDisplay = memory;
    }

    void register(Display display) {
        for (int i = 0; i < openDisplays.size(); i++) {
            display.addDisplayAt(i, openDisplays.get(i));
        }
        openDisplays.add(display);
        int pos = openDisplays.indexOf(display);
        for (Display open : openDisplays) {
            open.addDisplayAt(pos, display);
        }
        activeCpuDisplay.addDisplayAt(pos, display);
        memoryDisplay.addDisplayAt(pos, display);
    }

    void close(Display display) {
        int pos = openDisplays.indexOf(display);
        openDisplays.remove(display);
        for (Display open : openDisplays) {
            open.removeDisplay(pos);
        }
        activeCpuDisplay.removeDisplay(pos);
        memoryDisplay.removeDisplay(pos);
    }

    void updateEditorTitle(Display display) {
        int pos = openDisplays.indexOf(display);
        for (Display open : openDisplays) {
            open.changeDisplayAt(pos, display);
        }
        activeCpuDisplay.changeDisplayAt(pos, display);
        memoryDisplay.changeDisplayAt(pos, display);
    }

    void showActiveCpuDisplay() {
        activeCpuDisplay.show();
    }

    void showMemoryDisplay() {
        memoryDisplay.show();
    }

    void setActiveCpuDisplay(boolean detailed) {
        if (detailed) {
            activeCpuDisplay = detailedCpuDisplay;
            detailedCpuDisplay.show();
            simpleCpuDisplay.hide();
        } else {
            activeCpuDisplay = simpleCpuDisplay;
            simpleCpuDisplay.show();
            detailedCpuDisplay.hide();
        }
    }

    void onExit() {
        for (Display open : openDisplays)
            open.reportShutdown();
    }
}

