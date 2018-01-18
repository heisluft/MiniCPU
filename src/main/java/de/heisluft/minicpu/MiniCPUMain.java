package de.heisluft.minicpu;

import de.heisluft.minicpu.internal.Cpu;

class MiniCPUMain {

    public static void main(String[] args) {
        Cpu simple = Cpu.create(false);
        Cpu detailed = Cpu.create(true);
        Controller controller = new Controller(simple, detailed);
        CpuDisplaySimple displaySimple = new CpuDisplaySimple(controller);
        CpuDisplayDetailed displayDetailed = new CpuDisplayDetailed(controller);
        MemoryDisplay memoryDisplay = new MemoryDisplay(controller);
        WindowManager manager = new WindowManager(displaySimple, displayDetailed, memoryDisplay);
        controller.setManager(manager);
        simple.register(displaySimple);
        detailed.register(displayDetailed);
        simple.registerMemoryWatcher(memoryDisplay);
        for(String arg : args)
            controller.openFile(arg);
    }
}

