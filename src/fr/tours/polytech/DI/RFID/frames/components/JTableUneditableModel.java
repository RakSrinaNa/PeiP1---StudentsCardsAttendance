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

import javax.swing.table.DefaultTableModel;

/**
 * Used to create an uneditable JTable.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class JTableUneditableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 1595933236184593763L;

	/**
	 * Constructor.
	 *
	 * @param tableData The data of the table.
	 * @param columnsNames The columns name.
	 *
	 * @see DefaultTableModel#DefaultTableModel(Object[][], Object[])
	 */
	public JTableUneditableModel(Object[][] tableData, Object[] columnsNames)
	{
		super(tableData, columnsNames);
	}

	/**
	 * Used to know if the cell id editable.
	 *
	 * @return false as we want the table to be uneditable.
	 */
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}
