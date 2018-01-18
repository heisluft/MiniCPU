package de.heisluft.minicpu;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import javax.swing.JComponent;

class CpuPicture extends JComponent {
    int cpuWidth = 380;
    int cpuHeight = 160;
    int memWidth = 160;
    int memHeight = 180;
    int boxWidth = 80;
    int boxHeight = 20;
    int minWidth = cpuWidth + memWidth + 40;
    int minHeight = memHeight + 120;
    int[] xCoords = new int[]{0, 40, 80, 120, 80, 60, 40};
    int[] yCoords = new int[]{0, 60, 60, 0, 0, 30, 0};
    private String dataValue = "";
    private String memoryValue = "";
    private String alu1 = "0";
    private String alu2 = "0";
    private String alu3 = "0";
    private String ac = "0";
    private String sr = "Z:  N:  V:";
    private String ir1 = "0";
    private String ir2 = "0";
    private String pc = "0";
    private String[] progadr = new String[]{"", "", "", ""};
    private String[] progmem = new String[]{"", "", "", ""};
    private String[] dataadr = new String[]{"", ""};
    private String[] datamem = new String[]{"", ""};

    CpuPicture() {}

    void setData(String dataValue, String memoryValue, String alu1, String alu2, String alu3, String ac, String sr,
            String ir1, String ir2, String pc, String[] arrstring, String[] arrstring2, String[] arrstring3,
            String[] arrstring4) {
        this.dataValue = dataValue;
        this.memoryValue = memoryValue;
        this.alu1 = alu1;
        this.alu2 = alu2;
        this.alu3 = alu3;
        this.ac = ac;
        this.sr = sr;
        this.ir1 = ir1;
        this.ir2 = ir2;
        this.pc = pc;
        progadr = arrstring;
        progmem = arrstring2;
        dataadr = arrstring3;
        datamem = arrstring4;
        invalidate();
        repaint();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(minWidth, minHeight);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(minWidth + 10, minHeight + 20);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        int n;
        int n2 = getWidth();
        int n3 = getHeight();
        if (isOpaque()) {
            graphics.setColor(getBackground());
            graphics.fillRect(0, 0, n2, n3);
            graphics.setColor(getForeground());
        }
        graphics.setColor(Color.red);
        graphics.drawLine(10, boxHeight + 10, n2 - 10, boxHeight + 10);
        graphics.drawLine(10, boxHeight + 11, n2 - 10, boxHeight + 11);
        graphics.drawString("Datenbus", 15, boxHeight);
        graphics.drawLine(190, boxHeight + 10, 190, n3 / 2 - cpuHeight / 2 - 1);
        graphics.drawLine(191, boxHeight + 10, 191, n3 / 2 - cpuHeight / 2 - 1);
        graphics.drawLine(n2 - 90, boxHeight + 10, n2 - 90, n3 / 2 - memHeight / 2 - 1);
        graphics.drawLine(n2 - 89, boxHeight + 10, n2 - 89, n3 / 2 - memHeight / 2 - 1);
        if (!"".equals(dataValue)) {
            graphics.drawString(dataValue, cpuWidth - 10, boxHeight);
        }
        graphics.setColor(Color.black);
        graphics.drawRect(10, n3 / 2 - cpuHeight / 2, cpuWidth, cpuHeight);
        graphics.drawRect(20, n3 / 2 - cpuHeight / 2 + boxHeight + 10, boxWidth, boxHeight);
        graphics.drawString(ac, 25, n3 / 2 - cpuHeight / 2 + boxHeight * 2 + 5);
        graphics.drawRect(cpuWidth - 2 * boxWidth, n3 / 2 - cpuHeight / 2 + boxHeight + 10, boxWidth, boxHeight);
        if (ir1.length() > 0 && ir1.charAt(0) >= '0' && ir1.charAt(0) <= '9') {
            graphics.drawString(ir1, cpuWidth - 2 * boxWidth + 5, n3 / 2 - cpuHeight / 2 + boxHeight * 2 + 5);
        } else {
            graphics.setColor(new Color(255, 127, 0));
            graphics.drawString(ir1, cpuWidth - 2 * boxWidth + 5, n3 / 2 - cpuHeight / 2 + boxHeight * 2 + 5);
            graphics.setColor(Color.black);
        }
        graphics.drawRect(cpuWidth - boxWidth, n3 / 2 - cpuHeight / 2 + boxHeight + 10, boxWidth, boxHeight);
        graphics.drawString(ir2, cpuWidth - boxWidth + 5, n3 / 2 - cpuHeight / 2 + boxHeight * 2 + 5);
        graphics.drawRect(cpuWidth - 2 * boxWidth, n3 / 2 - cpuHeight / 2 + 3 * boxHeight + 30, boxWidth, boxHeight);
        graphics.drawString(pc, cpuWidth - 2 * boxWidth + 5, n3 / 2 - cpuHeight / 2 + 4 * boxHeight + 25);
        Polygon polygon = new Polygon(xCoords, yCoords, xCoords.length);
        polygon.translate(40, n3 / 2 - cpuHeight / 2 + 2 * boxHeight + 20);
        graphics.drawPolygon(polygon);
        graphics.drawRect(20, n3 / 2 + cpuHeight / 2 - (boxHeight + 10), boxWidth, boxHeight);
        graphics.drawString(sr, 25, n3 / 2 + cpuHeight / 2 - 15);
        graphics.setColor(new Color(255, 0, 255));
        graphics.drawString(alu1, 40 + xCoords[1] / 4, n3 / 2 - cpuHeight / 2 + 3 * boxHeight + 15);
        graphics.drawString(alu2, 40 + xCoords[4], n3 / 2 - cpuHeight / 2 + 3 * boxHeight + 15);
        graphics.drawString(alu3, 40 + xCoords[1], n3 / 2 - cpuHeight / 2 + 2 * boxHeight + yCoords[1] + 15);
        graphics.setColor(Color.black);
        graphics.drawRect(n2 - (memWidth + 10), n3 / 2 - memHeight / 2, memWidth, memHeight);
        for (n = 0; n < progmem.length; ++n) {
            graphics.drawRect(n2 - (boxWidth + 20), n3 / 2 - memHeight / 2 + 5 + n * boxHeight, boxWidth, boxHeight);
            graphics.drawString(progmem[n], n2 - (boxWidth + 15), n3 / 2 - memHeight / 2 + boxHeight + n * boxHeight);
        }
        for (n = 0; n < datamem.length; ++n) {
            graphics.drawRect(n2 - (boxWidth + 20), n3 / 2 + memHeight / 2 - boxHeight * datamem.length - 5
                    + n * boxHeight, boxWidth, boxHeight);
            graphics.drawString(datamem[n], n2 - (boxWidth + 15), n3 / 2 + memHeight / 2
                    - boxHeight * datamem.length + boxHeight - 10 + n * boxHeight);
        }
        graphics.drawString(". . .", n2 - (boxWidth + 10), n3 / 2 + boxHeight);
        graphics.setColor(Color.blue);
        graphics.drawLine(10, n3 - 30, n2 - 10, n3 - 30);
        graphics.drawLine(10, n3 - 29, n2 - 10, n3 - 29);
        graphics.drawString("Adressbus", 15, n3 - 40);
        if (!"".equals(memoryValue)) {
            graphics.drawString(memoryValue, cpuWidth - 10, n3 - 40);
        }
        graphics.drawLine(190, n3 - 30, 190, n3 / 2 + cpuHeight / 2 + 1);
        graphics.drawLine(191, n3 - 30, 191, n3 / 2 + cpuHeight / 2 + 1);
        graphics.drawLine(n2 - 90, n3 - 30, n2 - 90, n3 / 2 + memHeight / 2 + 1);
        graphics.drawLine(n2 - 89, n3 - 30, n2 - 89, n3 / 2 + memHeight / 2 + 1);
        graphics.drawString("Akkumulator", 25, n3 / 2 - cpuHeight / 2 + boxHeight);
        graphics.drawString("Status", 25, n3 / 2 + cpuHeight / 2 - (boxHeight + 20));
        graphics.drawString("Befehlsregister", cpuWidth - 2 * boxWidth + 5, n3 / 2 - cpuHeight / 2 + boxHeight);
        graphics.drawString("Programmz\u00e4hler", cpuWidth - 2 * boxWidth + 5, n3 / 2 - cpuHeight / 2 + 3 * boxHeight + 20);
        for (n = 0; n < progmem.length; ++n) {
            graphics.drawString(progadr[n], n2 - (memWidth + 5), n3 / 2 - memHeight / 2 + boxHeight + n * boxHeight);
        }
        for (n = 0; n < datamem.length; ++n) {
            graphics.drawString(dataadr[n], n2 - (memWidth + 5), n3 / 2 + memHeight / 2 - boxHeight * datamem.length + boxHeight - 10 + n * boxHeight);
        }
        graphics.setColor(getForeground());
    }
}

