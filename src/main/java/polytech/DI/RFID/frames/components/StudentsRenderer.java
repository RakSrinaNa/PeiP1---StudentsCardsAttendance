package polytech.DI.RFID.frames.components;

import polytech.DI.RFID.objects.Student;
import polytech.DI.RFID.utils.Utils;
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
	public Color getTableBackgroundColour(Student value)
	{
		return Utils.hasChecked(value) ? Color.GREEN : Color.ORANGE;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component component = this.wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(value instanceof Student)
			component.setBackground(getTableBackgroundColour((Student) value));
		return component;
	}
}