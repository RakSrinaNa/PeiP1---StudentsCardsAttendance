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
public class StudentsEditGroupRenderer implements TableCellRenderer
{
	private final TableCellRenderer wrappedRenderer;
	private final Color selected;

	/**
	 * Constructor.
	 *
	 * @param wrappedRenderer The default renderer wrapped to the table.
	 */
	public StudentsEditGroupRenderer(TableCellRenderer wrappedRenderer)
	{
		this.wrappedRenderer = wrappedRenderer;
		UIDefaults defaults = javax.swing.UIManager.getDefaults();
		selected = defaults.getColor("List.selectionBackground");
	}

	/**
	 * Used to get the colour of the cell.
	 *
	 * @param value The value of the cell.
	 * @param isSelected Either or not the cell is selected.
	 * @return The color to set for this student.
	 */
	public Color getTableBackgroundColour(Student value, boolean isSelected)
	{
		return isSelected ? selected : Utils.getStudentByUID(value.getUid(), true) != null ? null : Color.ORANGE;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component component = this.wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(value instanceof Student)
			component.setBackground(getTableBackgroundColour((Student) value, isSelected));
		return component;
	}
}