package fr.tours.polytech.DI.RFID.frames.components;

import javax.swing.table.DefaultTableModel;

public class JTableUneditableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 1595933236184593763L;

	public JTableUneditableModel(Object[][] tableData, Object[] colNames)
	{
		super(tableData, colNames);
	}

	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}
