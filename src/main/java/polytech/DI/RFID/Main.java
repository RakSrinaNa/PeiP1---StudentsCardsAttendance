package polytech.DI.RFID;

import polytech.DI.RFID.utils.Utils;
import java.io.IOException;

/**
 * Program used to replace the attendance sheet with the student cards.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Main
{
	/**
	 * The main function, launched on startup.
	 *
	 * @param args Arguments for the program - Not used.
	 * @throws IOException If files couldn't be read.
	 * @throws SecurityException If the database connection can't be made.
	 * @see Utils#init(String[])
	 */
	public static void main(String[] args) throws SecurityException, IOException
	{
		Utils.init(args);
	}
}
