package de.heisluft.minicpu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

abstract class Display {
    JFrame window;
    JMenuBar menuBar;
    JMenu editMenu;
    JMenu toolMenu;
    private JMenu windowMenu;
    JMenuItem closeItem;
    JMenuItem saveItem;
    JMenuItem saveAsItem;
    JMenuItem printItem;
    JCheckBoxMenuItem groesseItem;
    ControllerInterface controller;
    private static boolean init = false;


    Display(ControllerInterface controller) {
        this.controller = controller;
        if (!init) {
            init = true;
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception exception) {
               throw new AssertionError("DAFUQ?");
            }
        }
        createMenus();
        initGUI();
    }

    protected abstract void initGUI();

    protected void createMenus() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Ablage");
        menuBar.add(fileMenu);
        JMenuItem jMenuItem = new JMenuItem("Neu", 78);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(78, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.newEditor());
        fileMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("\u00d6ffnen \u2026", 79);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(79, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.openFile());
        fileMenu.add(jMenuItem);
        fileMenu.addSeparator();
        closeItem = new JMenuItem("Schlie\u00dfen", 87);
        closeItem.setAccelerator(KeyStroke.getKeyStroke(87, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(closeItem);
        saveItem = new JMenuItem("Sichern", 83);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(83, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(saveItem);
        saveAsItem = new JMenuItem("Sichern unter \u2026");
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(83, InputEvent.CTRL_DOWN_MASK + InputEvent.SHIFT_DOWN_MASK));
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        printItem = new JMenuItem("Drucken \u2026");
        printItem.setAccelerator(KeyStroke.getKeyStroke(80, InputEvent.CTRL_DOWN_MASK));
        fileMenu.add(printItem);
        fileMenu.addSeparator();
        jMenuItem = new JMenuItem("Beenden", 81);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(81, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.exitJava());
        fileMenu.add(jMenuItem);
        editMenu = new JMenu("Bearbeiten");
        menuBar.add(editMenu);
        toolMenu = new JMenu("Werkzeuge");
        menuBar.add(toolMenu);
        groesseItem = new JCheckBoxMenuItem("Gro\u00dfe Darstellung");
        groesseItem.setEnabled(true);
        groesseItem.setSelected(false);
        groesseItem.addActionListener(actionEvent -> setFontSize(groesseItem.isSelected()));
        toolMenu.add(groesseItem);
        windowMenu = new JMenu("Fenster");
        menuBar.add(windowMenu);
        jMenuItem = new JMenuItem("\u00dcber Minimaschine");
        jMenuItem.addActionListener(actionEvent -> About.show());
        windowMenu.add(jMenuItem);
        windowMenu.addSeparator();
        jMenuItem = new JMenuItem("CPU-Fenster");
        jMenuItem.addActionListener(actionEvent -> controller.showActiveCpu());
        windowMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Speicher-Fenster");
        jMenuItem.addActionListener(actionEvent -> controller.showMemory());
        windowMenu.add(jMenuItem);
        windowMenu.addSeparator();
    }

    protected abstract void setFontSize(boolean bigFont);

    private String getTitle() {
        return window.getTitle();
    }

    void addDisplayAt(int position, Display display) {
        JMenuItem jMenuItem = new JMenuItem(display.getTitle());
        jMenuItem.addActionListener(new WindowAction(display));
        windowMenu.insert(jMenuItem, position + 3);
    }

    void removeDisplay(int position) {
        windowMenu.remove(position + 3);
    }

    void changeDisplayAt(int position, Display display) {
        windowMenu.getItem(position + 3).setText(display.getTitle());
    }

    void show() {
        if (!window.isVisible())
            window.setVisible(true);
        window.toFront();
    }

    void hide() {
        window.setVisible(false);
    }

    void reportShutdown() {}

    class WindowAction implements ActionListener {
        private Display display;

        WindowAction(Display display) {
            this.display = display;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            display.show();
        }
    }

}

