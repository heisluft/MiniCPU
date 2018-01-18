package de.heisluft.minicpu;

import de.heisluft.minicpu.internal.CpuWatcher;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.InputEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

class CpuDisplayDetailed extends Display implements CpuWatcher {
    private CpuPicture picture;
    private CpuPicBig picBig;
    private JPanel content;

    CpuDisplayDetailed(ControllerInterface controller) {
        super(controller);
    }

    @Override
    protected void initGUI() {
        window = new JFrame("CPU-Kontrolle");
        window.setJMenuBar(menuBar);
        content = (JPanel)window.getContentPane();
        content.setLayout(new BorderLayout());
        picBig = new CpuPicBig();
        picBig.setOpaque(false);
        picture = new CpuPicture();
        picture.setOpaque(false);
        content.add(picture, "Center");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout());
        content.add(jPanel, "South");
        JButton jButton = new JButton("Ausf\u00fchren");
        jPanel.add(jButton);
        jButton.addActionListener(actionEvent -> controller.execute());
        jButton = new JButton("Einzelschritt");
        jPanel.add(jButton);
        jButton.addActionListener(actionEvent -> controller.singleStep());
        jButton = new JButton("Mikroschritt");
        jPanel.add(jButton);
        jButton.addActionListener(actionEvent -> controller.microStep());
        content.doLayout();
        window.setSize(600, 400);
        window.setVisible(true);
    }

    @Override
    public void reportCmd(String string, String string2, String string3, String string4, String string5, String string6,
            boolean zero, boolean negative, boolean overflow, String string7, String string8, String string9,
            String[] arrstring, String[] arrstring2, String[] arrstring3, String[] arrstring4) {
        picture.setData(string, string2, string3, string4, string5, string6, "Z:" + (zero ? "*" : " ") + " N:"
                + (negative ? "*" : " ") + " V:" + (overflow ? "*" : " "), string7, string8, string9, arrstring,
                arrstring2, arrstring3, arrstring4);
        picBig.setData(string, string2, string3, string4, string5, string6, "Z:" + (zero ? "*" : " ") + " N:"
                + (negative ? "*" : " ") + " V:" + (overflow ? "*" : " "), string7, string8, string9, arrstring,
                arrstring2, arrstring3, arrstring4);
    }

    @Override
    public void reportError(String message) {
        JOptionPane.showMessageDialog(window, message, "CPU-Fehler", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    protected void createMenus() {
        super.createMenus();
        closeItem.setEnabled(false);
        saveItem.setEnabled(false);
        saveAsItem.setEnabled(false);
        printItem.setEnabled(false);
        JMenuItem jMenuItem = new JMenuItem("Widerrufen", 90);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(90, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Wiederholen");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(90, InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        editMenu.addSeparator();
        jMenuItem = new JMenuItem("Ausschneiden", 88);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(88, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Kopieren", 67);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(67, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Einf\u00fcgen", 86);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(86, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Alles ausw\u00e4hlen", 65);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.CTRL_DOWN_MASK));
        jMenuItem.setEnabled(false);
        editMenu.add(jMenuItem);
        toolMenu.addSeparator();
        jMenuItem = new JMenuItem("Einfache Darstellung");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(69, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.activateEasyView());
        toolMenu.add(jMenuItem);
        jMenuItem = new JMenuItem("Detaildarstellung");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(68, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.activateDetailedView());
        toolMenu.add(jMenuItem);
        toolMenu.addSeparator();
        jMenuItem = new JMenuItem("Abbruchschranke setzen");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(65, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> TimeBarrier.show(controller));
        toolMenu.add(jMenuItem);
        toolMenu.addSeparator();
        jMenuItem = new JMenuItem("CPU r\u00fccksetzen");
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(82, InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK));
        jMenuItem.addActionListener(actionEvent -> controller.reset());
        toolMenu.add(jMenuItem);
    }

    @Override
    protected void setFontSize(boolean bigFont) {
        if (bigFont) {
            content.remove(picture);
            content.add(picBig, "Center");
            picBig.invalidate();
            picBig.repaint();
            window.setSize(900, 600);
        } else {
            content.remove(picBig);
            content.add(picture, "Center");
            picture.invalidate();
            picture.repaint();
            window.setSize(600, 400);
        }
        content.doLayout();
        content.revalidate();
    }

}

