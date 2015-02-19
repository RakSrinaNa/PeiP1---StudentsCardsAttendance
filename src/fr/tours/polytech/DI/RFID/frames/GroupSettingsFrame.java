package fr.tours.polytech.DI.RFID.frames;

import fr.tours.polytech.DI.RFID.frames.components.JTableUneditableModel;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.utils.Utils;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.logging.Level;

public class GroupSettingsFrame extends JDialog
{
	private MainFrame parent;
	private JTable tableGroups;
	private JTableUneditableModel modelGroups;
	private ArrayList<Group> groups;

	public GroupSettingsFrame(MainFrame parent, ArrayList<Group> groups)
	{
		super(parent);
		this.parent = parent;
		this.groups = groups;
		this.groups.add(new Group("G1"));
		this.groups.add(new Group("G2"));
		this.groups.add(new Group("G3"));
		this.groups.add(new Group("G4"));
		this.groups.add(new Group("G5"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		/**************************************************************************/
		JButton addButton = new JButton("Add group");
		addButton.addActionListener(event -> addGroup());
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		this.modelGroups = new JTableUneditableModel(getTableList(this.groups), new String[]
				{"Name"});
		this.tableGroups = new JTable(this.modelGroups)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		this.tableGroups.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent event) {
			}

			@Override
			public void mouseEntered(MouseEvent event) {
			}

			@Override
			public void mouseExited(MouseEvent event) {
			}

			@Override
			public void mousePressed(MouseEvent event) {
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				int row = GroupSettingsFrame.this.tableGroups.rowAtPoint(event.getPoint());
				if (row >= 0 && row < GroupSettingsFrame.this.tableGroups.getRowCount()) GroupSettingsFrame.this.tableGroups.setRowSelectionInterval(row, row);
				else GroupSettingsFrame.this.tableGroups.clearSelection();
				int rowindex = GroupSettingsFrame.this.tableGroups.getSelectedRow();
				if (event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					Group group = getGroupByName(GroupSettingsFrame.this.tableGroups.getValueAt(rowindex, 0).toString());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem editGroup = new JMenuItem("Edit group");
					editGroup.addActionListener(event1 -> {
						try {
							editGroup(row,group);
						} catch (Exception exception) {
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem deleteGroup = new JMenuItem("Delete group");
					deleteGroup.addActionListener(event1 -> {
						try {
							removeGroup(row,group);
						} catch (Exception exception) {
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					popup.add(editGroup);
					popup.add(deleteGroup);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		this.tableGroups.setDefaultRenderer(String.class, centerRenderer);
		this.tableGroups.getTableHeader().setReorderingAllowed(false);
		this.tableGroups.getTableHeader().setResizingAllowed(true);
		this.tableGroups.setRowHeight(20);
		this.tableGroups.setShowGrid(true);
		this.tableGroups.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.tableGroups.setGridColor(Color.BLACK);
		JScrollPane scrollPane = new JScrollPane(this.tableGroups);
		scrollPane.setAutoscrolls(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		/**************************************************************************/
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		this.getContentPane().add(scrollPane, gcb);
		gcb.gridy = line++;
		gcb.weighty = 1;
		this.getContentPane().add(addButton, gcb);
		this.setLocationRelativeTo(this.parent);
		pack();
		this.setVisible(true);
	}

	private void editGroup(int row, Group group)
	{
		new GroupEditFrame(this, group);
	}

	private Group getGroupByName(String name)
	{
		for(Group group : this.groups)
			if(group.getName().equals(name))
				return group;
		return null;
	}

	private Group[][] getTableList(ArrayList<Group> groups)
	{
		Group[][] array = new Group[this.groups.size()][1];
		int i = 0;
		for(Group group : groups)
			array[i++][0] = group;
		return array;
	}

	private void removeGroup(int index, Group group)
	{
		groups.remove(group);
		modelGroups.removeRow(index);
		modelGroups.fireTableDataChanged();
	}

	private void addGroup()
	{
		Group group = new Group(JOptionPane.showInputDialog(this, "Entrez le nom du groupe:", ""));
		this.groups.add(group);
		this.modelGroups.addRow(new Group[]{group});
	}
}
