package de.heisluft.minicpu;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

class AssemblerDisplay extends Display implements ScrollPaneConstants, WindowConstants {
    private JTextArea textArea;

    AssemblerDisplay(ControllerInterface controller, String text) {
        super(controller);
        textArea.setText(text);
    }

    @Override
    protected void initGUI() {
        window = new JFrame("Assemblertext");
        createMenus();
        window.setJMenuBar(menuBar);
        window.setVisible(true);
        JPanel jPanel = (JPanel)window.getContentPane();
        jPanel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jPanel.add(scroll, "Center");
        window.setSize(400, 200);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                controller.closeDisplay(AssemblerDisplay.this);
            }
        });
        window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    protected void createMenus() {
        super.createMenus();
        closeItem.addActionListener(actionEvent -> {
			controller.closeDisplay(AssemblerDisplay.this);
			window.dispose();
		});
        saveItem.setEnabled(false);
        saveAsItem.setEnabled(false);
        printItem.addActionListener(actionEvent -> { });
        JMenuItem jMenuItem = new JMenuItem("Ausschneiden", 88);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(88, InputEvent.META_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Kopieren", 67);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, InputEvent.META_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> textArea.copy());
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Einf\u00fcgen", 86);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, InputEvent.META_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Alles ausw\u00e4hlen", 65);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.META_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> textArea.selectAll());
        groesseItem.addActionListener(actionEvent -> {
			setFontSize(groesseItem.isSelected());
			textArea.invalidate();
			textArea.repaint();
		});
    }

    @Override
    protected void setFontSize(boolean bigFont) {
        setFontSize(bigFont ? 24 : 13);
        textArea.invalidate();
        textArea.repaint();
    }

    private void setFontSize(int n) {
        Font font = textArea.getFont();
        textArea.setFont(new Font(font.getName(), font.getStyle(), n));
    }

}

