package de.heisluft.minicpu;

import de.heisluft.minicpu.internal.MemoryWatcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.InputEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;

class MemoryDisplay extends Display implements MemoryWatcher {
    private JScrollPane scrollPane;
    private JScrollPane scrollPaneBig;
    private int lastChangedCell = -1;
    private boolean editable = false;
    private JCheckBoxMenuItem editItem;
    private JCheckBoxMenuItem hexaItem;
    private boolean isHexa = false;
    private JPanel content;

    MemoryDisplay(ControllerInterface controller) {
        super(controller);
    }

    public boolean isEditable() {
        return editable;
    }

    boolean isHexa() {
        return isHexa;
    }

    public int getLastChangedCell() {
        return lastChangedCell;
    }

    void setLastChangedCell(int lastChangedCell) {
        this.lastChangedCell = lastChangedCell;
    }

    @Override
    protected void initGUI() {
        window = new JFrame("Speicheranzeige");
        window.setJMenuBar(menuBar);
        window.setVisible(true);
        content = (JPanel)window.getContentPane();
        content.setLayout(new BorderLayout());
        TableModel model = new MemoryTableModel(this);
        JTable table = new JTable(model);
        for (int i = 0; i < 11; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(50);
        table.setDefaultRenderer(Object.class, new MemoryTableRenderer(this, false));
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        scrollPane = new JScrollPane(table);
        JTable tableBig = new JTable(model);
        tableBig.setRowHeight(30);
        for (int i = 0; i < 11; i++) {
            tableBig.getColumnModel().getColumn(i).setPreferredWidth(100);
        }
        tableBig.setDefaultRenderer(Object.class, new MemoryTableRenderer(this, true));
        Font font = tableBig.getTableHeader().getFont();
        tableBig.getTableHeader().setFont(new Font(font.getName(), Font.BOLD, 24));
        scrollPaneBig = new JScrollPane(tableBig);
        content.add(scrollPane, "Center");
        window.setSize(new Dimension(800, 500));
        window.setLocation(600, 50);
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
        jMenuItem = new JMenuItem("Speicher l\u00f6schen", 65);
        jMenuItem.setEnabled(true);
        jMenuItem.addActionListener(actionEvent -> controller.wipe());
        toolMenu.add(jMenuItem);
        toolMenu.addSeparator();
        hexaItem = new JCheckBoxMenuItem("Darstellung hexadezimal");
        hexaItem.setEnabled(true);
        hexaItem.setSelected(false);
        hexaItem.addActionListener(actionEvent -> {
			isHexa = hexaItem.isSelected();
			scrollPane.invalidate();
			scrollPane.repaint();
			scrollPaneBig.invalidate();
			scrollPaneBig.repaint();
		});
        toolMenu.add(hexaItem);
        toolMenu.addSeparator();
        editItem = new JCheckBoxMenuItem("Speicher editieren");
        editItem.setEnabled(true);
        editItem.setSelected(false);
        editItem.addActionListener(actionEvent -> editable = editItem.isSelected());
        toolMenu.add(editItem);
    }

    @Override
    protected void setFontSize(boolean bigFont) {
        if (bigFont) {
            content.remove(scrollPane);
            content.add(scrollPaneBig, "Center");
            scrollPaneBig.invalidate();
            scrollPaneBig.repaint();
        } else {
            content.remove(scrollPaneBig);
            content.add(scrollPane, "Center");
            scrollPane.invalidate();
            scrollPane.repaint();
        }
        content.revalidate();
    }

    @Override
    public void reportMemoryChange(int changedCell) {
        lastChangedCell = changedCell;
        scrollPane.invalidate();
        scrollPane.repaint();
        scrollPaneBig.invalidate();
        scrollPaneBig.repaint();
    }

}

