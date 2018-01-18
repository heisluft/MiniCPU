package de.heisluft.minicpu;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class MemoryTableRenderer extends DefaultTableCellRenderer {
	private final MemoryDisplay display;
	private final boolean big;

	MemoryTableRenderer(MemoryDisplay display, boolean big) {
		this.big = big;
		this.display = display;
	}

	private Font f = null;
	private Font fbold = null;

	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object object, boolean bl, boolean bl2, int n, int n2) {
		Component component = super.getTableCellRendererComponent(jTable, object, bl, bl2, n, n2);
		if (f == null) {
			f = component.getFont();
			if(big) f = new Font(f.getName(), f.getStyle(), 24);
			fbold = f.deriveFont(Font.BOLD);
		}
		if (n2 == 0) {
			component.setBackground(Color.gray);
			component.setFont(fbold);
		} else {
			component.setBackground(Color.lightGray);
			component.setFont(f);
			if (n * 10 + (n2 - 1) == display.getLastChangedCell()) {
				component.setForeground(Color.red);
			} else {
				component.setForeground(Color.black);
			}
		}
		return component;
	}
}
