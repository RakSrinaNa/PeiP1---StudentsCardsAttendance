package polytech.DI.RFID.frames.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A panel containing a buffered image.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class ImagePanel extends JPanel
{
	private static final long serialVersionUID = -6952599309580686281L;
	private BufferedImage image;

	/**
	 * Constructor.
	 * Will call {@link ImagePanel#ImagePanel(BufferedImage, Dimension)} with null, null.
	 */
	public ImagePanel()
	{
		this(null, null);
	}

	/**
	 * Constructor.
	 * Will call {@link ImagePanel#ImagePanel(BufferedImage, Dimension)} with image, null.
	 *
	 * @param image The image to display.
	 */
	public ImagePanel(BufferedImage image)
	{
		this(image, null);
	}

	/**
	 * Constructor.
	 *
	 * @param image The image to display.
	 * @param dimension The dimension of the panel.
	 */
	public ImagePanel(BufferedImage image, Dimension dimension)
	{
		if(dimension != null)
			setPreferredSize(dimension);
		setImage(image);
	}

	/**
	 * Used to resize a BufferedImage and keep the ratio.
	 *
	 * @param image The image to resize.
	 * @param size The maximum sizes of the final image.
	 * @return The new BufferedImage
	 */
	private static BufferedImage resizeBufferedImage(BufferedImage image, Dimension size)
	{
		return resizeBufferedImage(image, (float) size.getWidth(), (float) size.getHeight());
	}

	/**
	 * Used to resize a BufferedImage and keep the ratio.
	 *
	 * @param image The image to resize.
	 * @param width The maximum final width.
	 * @param height The maximum final height.
	 * @return The new BufferedImage
	 */
	private static BufferedImage resizeBufferedImage(BufferedImage image, float width, float height)
	{
		if(image == null)
			return null;
		int baseWidth = image.getWidth(), baseHeight = image.getHeight();
		float ratio = baseWidth > baseHeight ? width / baseWidth : height / baseHeight;
		Image tmp = image.getScaledInstance((int) (ratio * baseWidth), (int) (ratio * baseHeight), BufferedImage.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage((int) (ratio * baseWidth), (int) (ratio * baseHeight), BufferedImage.TYPE_INT_ARGB);
		buffered.getGraphics().drawImage(tmp, 0, 0, null);
		return buffered;
	}

	/**
	 * Used to set the displayed image.
	 *
	 * @param image the image to display.
	 */
	public void setImage(BufferedImage image)
	{
		this.image = resizeBufferedImage(image, getPreferredSize());
		this.repaint();
		invalidate();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		if(this.image != null)
		{
			int baseY = (getHeight() - this.image.getHeight()) / 2, baseX = (getWidth() - this.image.getWidth()) / 2;
			g.drawImage(this.image, baseX, baseY, null);
		}
	}
}