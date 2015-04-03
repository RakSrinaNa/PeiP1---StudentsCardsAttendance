package fr.tours.polytech.DI.RFID.frames.components;

import fr.tours.polytech.DI.RFID.utils.Students;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Renderer for the students table.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class StudentsRenderer implements TableCellRenderer
{
	private final TableCellRenderer wrappedRenderer;

	/**
	 * Constructor.
	 *
	 * @param wrappedRenderer The default renderer wrapped to the table.
	 */
	public StudentsRenderer(TableCellRenderer wrappedRenderer)
	{
		this.wrappedRenderer = wrappedRenderer;
	}

	/**
	 * Used to get the colour of the cell.
	 *
	 * @param value The value of the cell.
	 * @return The color to set for this student.
	 */
	public Color getTableBackgroundColour(String value)
	{
		return Students.hasStudentChecked(value) ? Color.GREEN : Color.ORANGE;
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
		if(value instanceof String)
			component.setBackground(getTableBackgroundColour((String) value));
		return component;
	}
}