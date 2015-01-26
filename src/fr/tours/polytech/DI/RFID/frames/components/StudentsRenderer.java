package fr.tours.polytech.DI.RFID.frames.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import fr.tours.polytech.DI.RFID.frames.MainFrame;

/**
 * Renderer for the students table.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class StudentsRenderer implements TableCellRenderer
{
	private final TableCellRenderer wrappedRenderer;
	private MainFrame parent;

	/**
	 * Constructor.
	 * 
	 * @param wrappedRenderer The default renderer wrapped to the table.
	 * @param frame The MainFrame where this table is.
	 */
	public StudentsRenderer(TableCellRenderer wrappedRenderer, MainFrame frame)
	{
		this.wrappedRenderer = wrappedRenderer;
		this.parent = frame;
	}

	/**
	 * Used to get the colour of the cell.
	 *
	 * @param value The value of the cell (in that case the name of the student).
	 * @return
	 */
	public Color getTableBackgroundColour(Object value)
	{
		return this.parent.hasChecked(value.toString()) ? Color.GREEN : Color.ORANGE;
	}

	/**
	 * Set the component drawn to have the background set to the correct colour.
	 *
	 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component component = this.wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		component.setBackground(getTableBackgroundColour(value));
		return component;
	}
}