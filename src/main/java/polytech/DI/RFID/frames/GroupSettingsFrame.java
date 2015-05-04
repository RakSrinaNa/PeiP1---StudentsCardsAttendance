package polytech.DI.RFID.frames;

import polytech.DI.RFID.frames.components.JTableUneditableModel;
import polytech.DI.RFID.objects.Group;
import polytech.DI.RFID.utils.Utils;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Class of the groups list frame.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class GroupSettingsFrame extends JDialog
{
	private static final long serialVersionUID = 937868122855164931L;
	private final JTable tableGroups;
	private final JTableUneditableModel modelGroups;
	private final ArrayList<Group> groups;

	/**
	 * Constructor.
	 *
	 * @param parent The parent frame.
	 * @param groups The current groups.
	 */
	public GroupSettingsFrame(MainFrame parent, ArrayList<Group> groups)
	{
		super(parent);
		this.groups = groups;
		this.setIconImages(Utils.icons);
		this.setTitle(Utils.resourceBundle.getString("group_setting"));
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.getContentPane().setLayout(new GridBagLayout());
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				Utils.groups = groups;
				Group.saveGroups(groups);
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
			}

			@Override
			public void windowActivated(WindowEvent e)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
			}
		});
		/**************************************************************************/
		JButton addButton = new JButton(Utils.resourceBundle.getString("add_group"));
		addButton.addActionListener(event -> addGroup());
		addButton.setBackground(MainFrame.backColor);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		this.modelGroups = new JTableUneditableModel(getTableList(this.groups), new String[]{Utils.resourceBundle.getString("groups")});
		this.tableGroups = new JTable(this.modelGroups)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		this.tableGroups.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
				int row = GroupSettingsFrame.this.tableGroups.rowAtPoint(event.getPoint());
				if(row >= 0 && row < GroupSettingsFrame.this.tableGroups.getRowCount())
					GroupSettingsFrame.this.tableGroups.setRowSelectionInterval(row, row);
				else
					GroupSettingsFrame.this.tableGroups.clearSelection();
				int rowindex = GroupSettingsFrame.this.tableGroups.getSelectedRow();
				if(event.getClickCount() == 2 && event.getComponent() instanceof JTable)
					new GroupEditFrame(GroupSettingsFrame.this, getGroupByName(GroupSettingsFrame.this.tableGroups.getValueAt(rowindex, 0).toString()));
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				int row = GroupSettingsFrame.this.tableGroups.rowAtPoint(event.getPoint());
				if(row >= 0 && row < GroupSettingsFrame.this.tableGroups.getRowCount())
					GroupSettingsFrame.this.tableGroups.setRowSelectionInterval(row, row);
				else
					GroupSettingsFrame.this.tableGroups.clearSelection();
				int rowindex = GroupSettingsFrame.this.tableGroups.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					Group group = getGroupByName(GroupSettingsFrame.this.tableGroups.getValueAt(rowindex, 0).toString());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem editGroup = new JMenuItem(Utils.resourceBundle.getString("modify_group"));
					editGroup.addActionListener(event1 -> {
						try
						{
							new GroupEditFrame(GroupSettingsFrame.this, group);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					JMenuItem deleteGroup = new JMenuItem(Utils.resourceBundle.getString("delete_group"));
					deleteGroup.addActionListener(event1 -> {
						try
						{
							removeGroup(row, group);
						}
						catch(Exception exception)
						{
							Utils.logger.log(Level.WARNING, "", exception);
						}
					});
					popup.add(editGroup);
					popup.add(deleteGroup);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
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
		this.getContentPane().setBackground(MainFrame.backColor);
		pack();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	/**
	 * Used to get a group by his name.
	 *
	 * @param name The name of the group.
	 * @return The group with that name.
	 */
	private Group getGroupByName(String name)
	{
		for(Group group : this.groups)
			if(group.getName().equals(name))
				return group;
		return null;
	}

	/**
	 * Used to create the group table.
	 *
	 * @param groups The groups of the table.
	 * @return An array representing the list.
	 */
	private Group[][] getTableList(ArrayList<Group> groups)
	{
		Group[][] array = new Group[this.groups.size()][1];
		int i = 0;
		for(Group group : groups)
			array[i++][0] = group;
		return array;
	}

	/**
	 * Used to add a group.
	 */
	private void addGroup()
	{
		Group group = new Group(JOptionPane.showInputDialog(this, Utils.resourceBundle.getString("group_name") + ":", ""));
		if(!group.hasName())
			return;
		for(Group grp : groups)
			if(grp.equals(group))
			{
				JOptionPane.showMessageDialog(this, Utils.resourceBundle.getString("group_already_exists"), Utils.resourceBundle.getString("error").toUpperCase(), JOptionPane.ERROR_MESSAGE);
				return;
			}
		this.groups.add(group);
		this.modelGroups.addRow(new Group[]{group});
	}

	/**
	 * Used to remove a group.
	 *
	 * @param index The index of the group in the table.
	 * @param group The group to remove.
	 */
	private void removeGroup(int index, Group group)
	{
		groups.remove(group);
		modelGroups.removeRow(index);
		modelGroups.fireTableDataChanged();
	}
}
