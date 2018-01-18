package de.heisluft.minicpu;
import java.awt.Font;
import java.awt.Graphics;

class CpuPicBig extends CpuPicture {
    CpuPicBig() {
        cpuWidth = 570;
        cpuHeight = 240;
        memWidth = 240;
        memHeight = 270;
        minWidth = cpuWidth + memWidth + 40;
        minHeight = memHeight + 120;
        boxWidth = 120;
        boxHeight = 30;
        xCoords = new int[]{0, 60, 120, 180, 120, 90, 60};
        yCoords = new int[]{0, 90, 90, 0, 0, 45, 0};
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.setFont(new Font(graphics.getFont().getName(), graphics.getFont().getStyle(), 24));
        super.paintComponent(graphics);
    }
}

