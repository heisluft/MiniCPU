package de.heisluft.minicpu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.PrintJob;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.undo.UndoManager;

class Editor extends Display implements ScrollPaneConstants, WindowConstants {
    private static final int CONVERSION = 25410;
    private static File lastFolder = null;
    private JEditorPane editor;
    private JTextArea lineNumberHolder;
    private JLabel status;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JFileChooser fileChooser;
    private File file;
    private UndoManager undo;
    private String lastSave = "";

    Editor(ControllerInterface controller) {
        super(controller);
    }

    private void setLineNumbers(boolean bl) {
        String[] arrstring = editor.getText().split("\n");
        StringBuilder string = new StringBuilder();
        for (int i = 1; i <= arrstring.length; ++i)
            string.append(i).append(" \n");
        if (bl)
            string.append(arrstring.length + 1).append(" \n");
        lineNumberHolder.setText(string.toString());
    }

    @Override
    protected void initGUI() {
        undo = new UndoManager(){
            @Override
            public void undoableEditHappened(UndoableEditEvent undoableEditEvent) {
                super.undoableEditHappened(undoableEditEvent);
                undoItem.setEnabled(canUndo());
                redoItem.setEnabled(canRedo());
            }
        };
        window = new JFrame("Editor");
        window.setJMenuBar(menuBar);
        JPanel jPanel = (JPanel)window.getContentPane();
        jPanel.setLayout(new BorderLayout());
        editor = new JEditorPane("text/plain", null){

            @Override
            public void cut() {
                super.cut();
                setLineNumbers(false);
            }

            @Override
            public void paste() {
                super.paste();
                setLineNumbers(false);
            }
        };
        editor.getDocument().addUndoableEditListener(undo);
        lineNumberHolder = new JTextArea("1 \n");
        lineNumberHolder.setFont(editor.getFont());
        lineNumberHolder.setBackground(new Color(255, 255, 200));
        lineNumberHolder.setBorder(LineBorder.createGrayLineBorder());
        lineNumberHolder.setEditable(false);
        editor.addKeyListener(new KeyAdapter(){

            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == '\b' || keyEvent.getKeyChar() == '' || keyEvent.getKeyChar() == '\n') {
                    setLineNumbers(keyEvent.getKeyChar() == '\n' && editor.getCaretPosition() >= editor.getText().length() - 1);
                }
            }
        });
        JScrollPane scroll = new JScrollPane(editor, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setRowHeaderView(lineNumberHolder);
        jPanel.add( scroll, "Center");
        status = new JLabel();
        status.setBorder(LineBorder.createGrayLineBorder());
        status.setBackground(Color.yellow);
        jPanel.add(status, "South");
        window.setSize(400, 200);
        window.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                close(false);
            }
        });
        window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".mis") || file.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Minimaschine Minisprache";
            }
        });
        fileChooser.addChoosableFileFilter(new FileFilter(){

            @Override
            public boolean accept(File file) {
                return file.getName().toLowerCase().endsWith(".mia") || file.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Minimaschine Assembler";
            }
        });
    }

    private void saveFile(boolean saveAs) {
        if (file == null || saveAs) {
            if (file != null) 
                fileChooser.setSelectedFile(file);
             else 
                fileChooser.setCurrentDirectory(lastFolder);
            if (fileChooser.showSaveDialog(window) == 0) {
                file = fileChooser.getSelectedFile();
                if (fileChooser.getFileFilter().getDescription().equals("Minimaschine Assembler")) {
                    if (!file.getName().toLowerCase().endsWith(".mia"))
                        file = new File(file.getPath() + ".mia");
                    lastFolder = file;
                } else if (fileChooser.getFileFilter().getDescription().equals("Minimaschine Minisprache")) {
                    if (!file.getName().toLowerCase().endsWith(".mis"))
                        file = new File(file.getPath() + ".mis");
                    lastFolder = file;
                }
            } else return;
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            editor.write(fileWriter);
            fileWriter.close();
            lastSave = editor.getText();
            window.setTitle(file.getPath());
            controller.updateTitle(this);
        }
        catch (Exception exception) {
            file = null;
        }
    }

    private void close(boolean allowCancel) {
        if (!lastSave.equals(editor.getText())) {
            int answer = JOptionPane.showConfirmDialog(window, new String[] {
                    "Dieses Fenster enth\u00e4lt ungesicherte \u00c4nderungen.", "Sollen sie gesichert werden?"
            }, "\u00c4nderungen sichern", allowCancel ? JOptionPane.YES_NO_CANCEL_OPTION : JOptionPane.YES_NO_OPTION);
            if (answer == 0)
                saveFile(false);
            else if (answer != 1)
                return;
        }
        controller.closeDisplay(this);
        window.dispose();
    }

    @Override
    void reportShutdown() {
        if (!lastSave.equals(editor.getText()) && (JOptionPane.showConfirmDialog(window, new String[] {
                "Dieses Fenster enth\u00e4lt ungesicherte \u00c4nderungen.", "Sollen sie gesichert werden?"
        }, "\u00c4nderungen sichern", JOptionPane.YES_NO_OPTION)) == 0) {
            saveFile(false);
        }
    }

    @Override
    protected void createMenus() {
        super.createMenus();
        closeItem.addActionListener(actionEvent -> close(true));
        saveItem.addActionListener(actionEvent -> saveFile(false));
        saveAsItem.addActionListener(actionEvent -> saveFile(true));
        printItem.addActionListener(actionEvent -> print());
        undoItem = new JMenuItem("Widerrufen", 90);
        undoItem.setAccelerator(KeyStroke.getKeyStroke(90, InputEvent.CTRL_DOWN_MASK));
        undoItem.setEnabled(false);
        undoItem.addActionListener(actionEvent -> {
			undo.undo();
			undoItem.setEnabled(undo.canUndo());
			redoItem.setEnabled(undo.canRedo());
			setLineNumbers(false);
		});
        editMenu.add(undoItem);
        redoItem = new JMenuItem("Wiederholen");
        redoItem.setAccelerator(KeyStroke.getKeyStroke(90, InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));
        redoItem.setEnabled(false);
        redoItem.addActionListener(actionEvent -> {
			undo.redo();
			undoItem.setEnabled(undo.canUndo());
			redoItem.setEnabled(undo.canRedo());
			setLineNumbers(false);
		});
        editMenu.add(redoItem);
        editMenu.addSeparator();
        JMenuItem jMenuItem = new JMenuItem("Ausschneiden", 88);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(88, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> editor.cut());
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Kopieren", 67);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> editor.copy());
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Einf\u00fcgen", 86);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> editor.paste());
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Alles ausw\u00e4hlen", 65);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> editor.selectAll());
        editMenu.add(jMenuItem);
        String[] arrstring = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JMenu jMenu = new JMenu("Fonts");
        ActionListener actionListener = actionEvent -> {
			String string = ((JMenuItem)actionEvent.getSource()).getText();
			Font font = editor.getFont();
			Font font2 = new Font(string, font.getStyle(), font.getSize());
			editor.setFont(font2);
			lineNumberHolder.setFont(font2);
		};
        for (String string : arrstring) {
            JMenuItem jMenuItem2 = new JMenuItem(string);
            jMenuItem2.addActionListener(actionListener);
            jMenu.add(jMenuItem2);
        }
        toolMenu.addSeparator();
        toolMenu.add(jMenu);
        toolMenu.addSeparator();
        jMenuItem = new JMenuItem("assemble");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> {
			status.setText("");
			controller.assemble(editor.getText(), Editor.this);
		});
        toolMenu.add(jMenuItem);
        toolMenu.addSeparator();
        jMenuItem = new JMenuItem("\u00dcbersetzen");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(85, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> {
			status.setText("");
			controller.translate(editor.getText(), Editor.this);
		});
        toolMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Assemblertext zeigen");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(90, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> {
			status.setText("");
			controller.showAssemblerText(editor.getText(), Editor.this);
		});
        toolMenu.add(jMenuItem);
    }

    @Override
    protected void setFontSize(boolean bigFont) {
        setFontSize(bigFont ? 24 : 13);
    }

    private void setFontSize(int n) {
        Font font = editor.getFont();
        Font font2 = new Font(font.getName(), font.getStyle(), n);
        editor.setFont(font2);
        lineNumberHolder.setFont(font2);
    }

    void read() {
        fileChooser.setCurrentDirectory(lastFolder);
        int n = fileChooser.showOpenDialog(window);
        if (n == 0) {
            file = fileChooser.getSelectedFile();
            try {
                FileReader fileReader = new FileReader(file);
                editor.read(fileReader, null);
                fileReader.close();
                String[] arrstring = editor.getText().split("\n");
                StringBuilder string = new StringBuilder();
                for (int i = 1; i <= arrstring.length; ++i) {
                    string.append(i).append(" \n");
                }
                lineNumberHolder.setText(string.toString());
                lastSave = editor.getText();
                window.setTitle(file.getPath());
                lastFolder = file;
            }
            catch (Exception exception) {
                file = null;
            }
        } else {
            file = null;
        }
        if (file != null) {
            window.setVisible(true);
            undoItem.setEnabled(false);
            redoItem.setEnabled(false);
            controller.updateTitle(this);
        } else {
            controller.closeDisplay(this);
            window.dispose();
        }
    }

    void read(String file) {
        this.file = new File(file);
        try {
            FileReader fileReader = new FileReader(this.file);
            editor.read(fileReader, null);
            fileReader.close();
            lastSave = editor.getText();
            window.setTitle(this.file.getPath());
        }
        catch (Exception exception) {
            this.file = null;
        }
        window.setVisible(true);
        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        controller.updateTitle(this);
    }

    void displayError(String message, int at) {
        status.setText(message);
        editor.select(at - 2, at - 1);
    }

    private void print() {
        int n;
        String[] arrstring = editor.getText().split("\n");
        for (n = 0; n < arrstring.length; ++n) {
            int n2;
            while ((n2 = arrstring[n].indexOf(9)) >= 0) {
                arrstring[n] = arrstring[n].substring(0, n2) + "        ".substring(0, 8 - n2 % 8) + arrstring[n].substring(n2 + 1);
            }
        }
        PrintJob printJob = window.getToolkit().getPrintJob(window, window.getTitle(), null);
        Dimension dimension = printJob.getPageDimension();
        int n3 = printJob.getPageResolution();
        int n4 = 15000 * n3 / CONVERSION;
        int n5 = 10000 * n3 / CONVERSION;
        dimension.width -= n4 * 2;
        dimension.height -= n4 * 2;
        Font font = new Font("Monospaced", Font.PLAIN, 10);
        Font font2 = new Font("Monospaced", Font.PLAIN, 14);
        Graphics graphics = printJob.getGraphics();
        int n6 = graphics.getFontMetrics(font).getHeight();
        int n7 = (dimension.height - n5 * 2) / n6;
        int n8 = (arrstring.length + n7 - 1) / n7;
        printFrame(graphics, dimension, n4, n5, 1, n8, font, font2);
        for (n = 0; n < arrstring.length; ++n) {
            graphics.drawString(arrstring[n], n4 + n5 * 5 / 10, n4 + 2 * n5 + n % n7 * n6);
            if ((n + 1) % n7 != 0) continue;
            graphics.dispose();
            graphics = printJob.getGraphics();
            printFrame(graphics, dimension, n4, n5, (n + 1) / n7, n8, font, font2);
        }
        graphics.dispose();
        printJob.end();
    }

    private void printFrame(Graphics graphics, Dimension dimension, int n, int n2, int n3, int n4, Font font, Font font2) {
        graphics.drawRoundRect(n, n, dimension.width, dimension.height, n2 * 2, n2 * 2);
        graphics.drawLine(n, n + n2, n + dimension.width, n + n2);
        graphics.drawLine(n, n + dimension.height - n2, n + dimension.width, n + dimension.height - n2);
        String string = window.getTitle();
        graphics.setFont(font2);
        graphics.drawString(string, n + dimension.width / 2 - graphics.getFontMetrics().stringWidth(string) / 2, n + n2 * 7 / 10);
        string = "\u2013 " + n3 + " von " + n4 + " \u2013";
        graphics.setFont(font);
        graphics.drawString(string, n + dimension.width / 2 - graphics.getFontMetrics().stringWidth(string) / 2, n + dimension.height - n2 * 4 / 10);
    }

}

