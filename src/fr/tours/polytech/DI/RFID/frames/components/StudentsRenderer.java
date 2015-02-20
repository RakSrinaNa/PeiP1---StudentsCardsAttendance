/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.tours.polytech.DI.RFID.frames.components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.RFID.utils.Utils;

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
	public Color getTableBackgroundColour(Student value)
	{
		return this.parent.hasChecked(value) ? Color.GREEN : Color.ORANGE;
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
		if(value instanceof Student)
			component.setBackground(getTableBackgroundColour((Student)value));
		return component;
	}
}