package polytech.DI.RFID.frames;

import polytech.DI.RFID.Main;
import polytech.DI.RFID.frames.components.ImagePanel;
import polytech.DI.RFID.utils.Utils;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The About Frame of the program.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class AboutFrame extends JDialog
{
	private static final long serialVersionUID = -475220568920430189L;

	/**
	 * Constructor.
	 *
	 * @param parent The parent of the frame.
	 */
	public AboutFrame(JFrame parent)
	{
		super(parent);
		this.setIconImages(Utils.icons);
		this.setUndecorated(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
				dispose();
			}

			@Override
			public void mouseReleased(MouseEvent avent)
			{
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
		ImagePanel logoPolytechPanel = new ImagePanel();
		logoPolytechPanel.setPreferredSize(new Dimension(250, 77));
		try
		{
			logoPolytechPanel.setImage(ImageIO.read(Main.class.getClassLoader().getResource("images/logo_polytech.jpg")));
		}
		catch(IOException exception)
		{
			Utils.logger.log(Level.WARNING, "Couldn't load logo image", exception);
		}
		ImagePanel logoAppPanel = new ImagePanel();
		logoAppPanel.setPreferredSize(new Dimension(250, 77));
		try
		{
			logoAppPanel.setImage(ImageIO.read(Main.class.getClassLoader().getResource("images/logo_app.jpg")));
		}
		catch(IOException exception)
		{
			Utils.logger.log(Level.WARNING, "Couldn't load logo image", exception);
		}
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(Color.WHITE);
		JLabel infoText = new JLabel("<html><p width=\"" + 240 + "\" align=\"center\">" + parent.getTitle() + " v" + MainFrame.VERSION + "<br/>COLEAU Victor<br />COUCHOUD Thomas<br /><br />Projet Peip1 2014-2015</p></html>");
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
		getContentPane().add(logoPolytechPanel, gcb);
		gcb.gridy = line++;
		getContentPane().add(logoAppPanel, gcb);
		gcb.insets = new Insets(15, 0, 0, 0);
		gcb.weighty = 10;
		gcb.gridy = line++;
		getContentPane().add(infoPanel, gcb);
		getContentPane().setBackground(Color.WHITE);
		pack();
		setLocationRelativeTo(parent);
		setVisible(true);
	}
}
