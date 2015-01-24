package fr.tours.polytech.DI.RFID.frames.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import fr.tours.polytech.DI.RFID.frames.MainFrame;

public class StudentsRenderer implements TableCellRenderer
{
	private final TableCellRenderer wrappedRenderer;
	private MainFrame parent;

	public StudentsRenderer(TableCellRenderer wrappedRenderer, MainFrame frame)
	{
		this.wrappedRenderer = wrappedRenderer;
		this.parent = frame;
	}

	public Color getTableBackgroundColour(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		return this.parent.hasChecked(value.toString()) ? Color.GREEN : Color.ORANGE;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component ret = this.wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		ret.setBackground(getTableBackgroundColour(table, value, isSelected, hasFocus, row, column));
		return ret;
	}
}