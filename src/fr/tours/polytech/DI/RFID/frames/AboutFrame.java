package fr.tours.polytech.DI.RFID.frames;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import fr.tours.polytech.DI.RFID.Main;
import fr.tours.polytech.DI.RFID.frames.components.ImagePanel;
import fr.tours.polytech.DI.RFID.utils.Utils;

public class AboutFrame extends JWindow
{
	private static final long serialVersionUID = -475220568920430189L;

	public AboutFrame(JFrame parent)
	{
		super(parent);
		setAlwaysOnTop(true);
		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{}

			@Override
			public void mouseEntered(MouseEvent arg0)
			{}

			@Override
			public void mouseExited(MouseEvent arg0)
			{}

			@Override
			public void mousePressed(MouseEvent arg0)
			{
				dispose();
			}

			@Override
			public void mouseReleased(MouseEvent arg0)
			{}
		});
		ImagePanel logoPanel = new ImagePanel();
		logoPanel.setPreferredSize(new Dimension(250, 77));
		try
		{
			logoPanel.setImage(ImageIO.read(Main.class.getClassLoader().getResource("resources/images/logo_polytech.jpg")));
		}
		catch(IOException e)
		{
			Utils.logger.log(Level.WARNING, "Couldn't load logo image", e);
		}
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		JLabel infoText = new JLabel("<html><p align=\"center\">" + parent.getTitle() + " v" + MainFrame.VERSION + "<br/>COLEAU Victor<br />COUCHOUD Thomas</p></html>");
		infoText.setHorizontalAlignment(JLabel.CENTER);
		infoText.setVerticalAlignment(JLabel.CENTER);
		infoPanel.add(infoText);
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.GRAY);
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weightx = 1;
		gcb.weighty = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(logoPanel, gcb);
		gcb.weighty = 10;
		gcb.gridy = line++;
		getContentPane().add(infoPanel, gcb);
		getContentPane().setBackground(Color.WHITE);
		pack();
		setVisible(true);
		setLocationRelativeTo(getParent());
	}
}
