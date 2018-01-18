package de.heisluft.minicpu;


interface ControllerInterface {
    void assemble(String string, Editor editor);

    void translate(String string, Editor editor);

    void showAssemblerText(String string, Editor editor);

    void wipe();

    void execute();

    void singleStep();

    void microStep();

    void reset();

    void newEditor();

    void openFile();

    void openFile(String string);

    void closeDisplay(Display display);

    void updateTitle(Display display);

    void showActiveCpu();

    void showMemory();

    void activateEasyView();

    void activateDetailedView();

    void setTimeBarrier(int timeBarrier);

    void exitJava();
}

