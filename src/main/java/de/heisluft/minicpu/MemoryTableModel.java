package de.heisluft.minicpu;

import de.heisluft.minicpu.internal.MemoryAccess;

import javax.swing.table.AbstractTableModel;

public class MemoryTableModel extends AbstractTableModel {
	private final MemoryDisplay display;

	MemoryTableModel(MemoryDisplay display) {
		this.display = display;
	}
	@Override
	public String getColumnName(int column) {
		return column == 0 ? "" : String.valueOf(column - 1);
	}

	@Override
	public int getColumnCount() {
		return 11;
	}

	@Override
	public int getRowCount() {
		return 6555;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) {
			return rowIndex * 10;
		}
		int memAddress = rowIndex * 10 + (columnIndex - 1);
		if(memAddress < 65536) {
			if(display.isHexa()) {
				String string = "0000" + Integer.toHexString(MemoryAccess.getUnsignedWord(memAddress)).toUpperCase();
				return string.substring(string.length() - 4);
			}
			return MemoryAccess.getSignedWord(memAddress);
		}
		return "";
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return display.isEditable() && columnIndex != 0;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if(value instanceof String) {
			try {
				int word;
				String string = (String) value;
				if(display.isHexa()) {
					if(string.startsWith("0x"))
						string = string.substring(2);
					word = Integer.parseInt(string, 16);
				} else
					word = Integer.parseInt(string);
				if(-32768 <= word && word <= 65535) {
					display.setLastChangedCell(-1);
					MemoryAccess.setWord(rowIndex * 10 + (columnIndex - 1), word);
					display.setLastChangedCell(-1);
				}
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
	}
}
