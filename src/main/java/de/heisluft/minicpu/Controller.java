package de.heisluft.minicpu;

import de.heisluft.minicpu.internal.Cpu;
import de.heisluft.minicpu.internal.ErrorProvider;
import de.heisluft.minicpu.internal.minilang.PARSER;
import de.heisluft.minicpu.internal.MemoryAccess;

class Controller implements ControllerInterface {
    private Cpu cpuActive;
    private Cpu cpuEasy;
    private Cpu cpuDetailed;
    private WindowManager manager;

    Controller(Cpu cpuEasy, Cpu cpuDetailed) {
        this.cpuEasy = cpuEasy;
        this.cpuDetailed = cpuDetailed;
        cpuActive = cpuDetailed;
    }

    void setManager(WindowManager manager) {
        this.manager = manager;
        manager.setActiveCpuDisplay(true);
    }

    @Override
    public void assemble(String string, Editor editor) {
        ErrorProvider handler = new ErrorProvider();
        cpuActive.assemble(string, handler);
        if (handler.errored()) {
            editor.displayError(handler.getMessage(), handler.getPosition());
        } else {
            cpuActive.reset();
        }
    }

    @Override
    public void translate(String string, Editor editor) {
        ErrorProvider handler = new ErrorProvider();
        cpuActive.translate(string, handler);
        if (handler.errored()) {
            editor.displayError(handler.getMessage(), handler.getPosition());
        } else {
            cpuActive.reset();
        }
    }

    @Override
    public void showAssemblerText(String string, Editor editor) {
        ErrorProvider handler = new ErrorProvider();
        String string2 = new PARSER(string, handler).Parse();
        if (handler.errored()) {
            editor.displayError(handler.getMessage(), handler.getPosition());
        } else {
            AssemblerDisplay view = new AssemblerDisplay(this, string2);
            manager.register(view);
        }
    }

    @Override
    public void wipe() {
        MemoryAccess.wipe();
    }

    @Override
    public void execute() {
        cpuActive.execute();
    }

    @Override
    public void singleStep() {
        cpuActive.singleStep();
    }

    @Override
    public void microStep() {
        cpuActive.microStep();
    }

    @Override
    public void reset() {
        cpuActive.reset();
    }

    @Override
    public void newEditor() {
        Editor editor = new Editor(this);
        manager.register(editor);
        editor.show();
    }

    @Override
    public void openFile() {
        Editor editor = new Editor(this);
        manager.register(editor);
        editor.read();
    }

    @Override
    public void openFile(String string) {
        Editor editor = new Editor(this);
        manager.register(editor);
        editor.read(string);
    }

    @Override
    public void closeDisplay(Display display) {
        manager.close(display);
    }

    @Override
    public void updateTitle(Display display) {
        manager.updateEditorTitle(display);
    }

    @Override
    public void showActiveCpu() {
        manager.showActiveCpuDisplay();
    }

    @Override
    public void showMemory() {
        manager.showMemoryDisplay();
    }

    @Override
    public void activateEasyView() {
        cpuActive.copyValuesTo(cpuEasy);
        cpuActive = cpuEasy;
        manager.setActiveCpuDisplay(false);
    }

    @Override
    public void activateDetailedView() {
        cpuActive.copyValuesTo(cpuDetailed);
        cpuActive = cpuDetailed;
        manager.setActiveCpuDisplay(true);
    }

    @Override
    public void setTimeBarrier(int n) {
        cpuActive.setTimeBarrier(n);
    }

    @Override
    public void exitJava() {
        manager.onExit();
        System.exit(0);
    }
}

