package fr.tours.polytech.DI.RFID.frames.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * A panel containing a buffered image.
 *
 * @author MrCraftCod
 */
public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = -6952599309580686281L;
	private BufferedImage image;
	private boolean printLoading;

	public ImagePanel()
	{
		this(null, null, false);
	}

	public ImagePanel(boolean printLoading)
	{
		this(null, null, printLoading);
	}

	public ImagePanel(BufferedImage image)
	{
		this(image, null, false);
	}

	public ImagePanel(BufferedImage image, Dimension dim)
	{
		this(image, dim, false);
	}

	public ImagePanel(BufferedImage image, Dimension dim, boolean printLoading)
	{
		if(dim != null)
			setPreferredSize(dim);
		this.printLoading = printLoading;
		setImage(image);
	}

	public static BufferedImage resizeBufferedImage(BufferedImage image, Dimension size)
	{
		return resizeBufferedImage(image, (float) size.getWidth(), (float) size.getHeight());
	}

	public static BufferedImage resizeBufferedImage(BufferedImage image, float width, float height)
	{
		if(image == null)
			return image;
		int baseWidth = image.getWidth(), baseHeight = image.getHeight();
		float ratio = baseWidth > baseHeight ? width / baseWidth : height / baseHeight;
		Image tmp = image.getScaledInstance((int) (ratio * baseWidth), (int) (ratio * baseHeight), BufferedImage.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage((int) (ratio * baseWidth), (int) (ratio * baseHeight), BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(tmp, 0, 0, null);
		return buffered;
	}

	public boolean isPrintLoading()
	{
		return this.printLoading;
	}

	public void setImage(BufferedImage image)
	{
		this.image = resizeBufferedImage(image, getPreferredSize());
		this.repaint();
		invalidate();
	}

	public void setPrintLoading(boolean printLoading)
	{
		this.printLoading = printLoading;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(this.image == null)
		{
			if(!isPrintLoading())
				return;
			String string = "loading";
			g.drawString(string, getWidth() / 2 - g.getFontMetrics().stringWidth(string) / 2, getHeight() / 2);
			return;
		}
		int baseY = (getHeight() - this.image.getHeight()) / 2, baseX = (getWidth() - this.image.getWidth()) / 2;
		g.drawImage(this.image, baseX, baseY, null);
	}
}