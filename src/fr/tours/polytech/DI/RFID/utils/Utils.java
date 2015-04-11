package fr.tours.polytech.DI.RFID.utils;

import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Configuration;
import fr.tours.polytech.DI.TerminalReader.threads.TerminalReader;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class, contain useful methods for the application.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Utils
{
	public static Logger logger;
	public static SQLManager sql;
	public static ResourceBundle resourceBundle;
	public static ArrayList<BufferedImage> icons;
	public static File baseFile;
	public static Configuration configuration;
	public static TerminalReader terminalReader;
	private static MainFrame mainFrame;

	/**
	 * Call when we need to exit the program.
	 *
	 * @param exitStaus The parameter given to {@link System#exit(int)}
	 * @see System#exit(int)
	 */
	public static void exit(int exitStaus)
	{
		mainFrame.exit();
		configuration.serialize(new File(baseFile, "configuration"));
		terminalReader.stop();
		System.exit(exitStaus);
	}

	/**
	 * Used to export the database as a SQL file.
	 *
	 * @param parent The parent frame, if there is one.
	 */
	public static void exportSQL(JFrame parent)
	{
		try
		{
			File file = new File(baseFile, "SQLExport.sql");
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
			for(String line : Students.exportStudentsTable())
				pw.println(line);
			pw.println();
			pw.println();
			for(String line : sql.exportCheckTable())
				pw.println(line);
			pw.println();
			pw.println();
			for(String line : sql.exportLogTable())
				pw.println(line);
			pw.println();
			pw.println();
			for(String line : Periods.exportPeriodsTable())
				pw.println(line);
			pw.flush();
			pw.close();
			JOptionPane.showMessageDialog(parent, String.format(resourceBundle.getString("sql_export_done"), file.getAbsolutePath()), resourceBundle.getString("sql_export_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(parent, resourceBundle.getString("sql_export_error"), resourceBundle.getString("sql_export_title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Used to import the database as a SQL file.
	 *
	 * @param parent The parent frame, if there is one.
	 */
	public static void importSQL(JFrame parent)
	{
		try
		{
			File file = getNewFilePatch(baseFile, JFileChooser.FILES_ONLY, new FileNameExtensionFilter(Utils.resourceBundle.getString("open_sql_description_file"), "sql"));
			if(file == null)
				return;
			List<String> lines = readTextFile(file);
			boolean com = false;
			int req = 0;
			for(String line : lines)
			{
				if(line == null || line.equals(""))
					continue;
				if(line.startsWith("/*"))
					com = true;
				if(!com && !line.startsWith("--"))
					req += sql.sendUpdateRequest(line);
				if(line.endsWith("*/"))
					com = false;
			}
			JOptionPane.showMessageDialog(parent, String.format(resourceBundle.getString("sql_import_done"), req), resourceBundle.getString("sql_import_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(parent, resourceBundle.getString("sql_import_error"), resourceBundle.getString("sql_import_title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Used to read a text file.
	 *
	 * @param file The file to read.
	 * @return A list corresponding to the different lines in the file.
	 */
	public static List<String> readTextFile(final File file)
	{
		List<String> fileLines = null;
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
		{
			String line = bufferedReader.readLine();
			fileLines = new ArrayList<>();
			while(line != null)
			{
				fileLines.add(line);
				line = bufferedReader.readLine();
			}
		}
		catch(IOException exception)
		{
			logger.log(Level.WARNING, "Failed to read text file " + file.getAbsolutePath());
		}
		return fileLines;
	}

	/**
	 * Used to get a new File object from the used.
	 *
	 * @param lastFile The file where the popup should open.
	 * @param mode The selection mode.
	 * @param filter The filter for the selection.
	 * @return The selected fiel by the user.
	 *
	 * @see JFileChooser
	 */
	public static File getNewFilePatch(File lastFile, int mode, FileNameExtensionFilter filter)
	{
		File file = null;
		try
		{
			File repertoireCourant = new File(System.getProperty("user.home")).getCanonicalFile();
			if(lastFile != null)
				repertoireCourant = lastFile.getCanonicalFile();
			Utils.logger.log(Level.FINE, "Previous folder: " + repertoireCourant.getAbsolutePath());
			final JFileChooser dialogue = new JFileChooser(repertoireCourant);
			dialogue.setFileFilter(filter);
			dialogue.setFileSelectionMode(mode);
			if(dialogue.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
				return null;
			file = dialogue.getSelectedFile();
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
		if(file != null)
			Utils.logger.log(Level.FINE, "Folder selected: " + file.getAbsolutePath());
		else
			Utils.logger.log(Level.FINE, "Folder selected: null");
		return file;
	}

	/**
	 * Call when the program is starting. Initalize some variables like
	 * groups, students, logger, reader and SQL connection.
	 *
	 * @param args The program arguments.
	 * @throws IOException If files couldn't be read.
	 * @throws SecurityException If the database connection can't be made.
	 * @see FileHandler#FileHandler(String, boolean)
	 */
	@SuppressWarnings("ConstantConditions")
	public static void init(String args[]) throws SecurityException, IOException
	{
		logger = Logger.getLogger("TerminalReader");
		resourceBundle = ResourceBundle.getBundle("lang/messages", Locale.getDefault());
		baseFile = new File("." + File.separator + "RFID");
		icons = new ArrayList<>();
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon16.png")));
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon32.png")));
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon64.png")));
		configuration = Configuration.deserialize(new File(baseFile, "configuration"));
		processArgs(args);
		terminalReader = new TerminalReader(configuration.getReaderName());
		sql = new SQLManager(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddUser(), configuration.getBddPassword());
		mainFrame = new MainFrame();
		terminalReader.addListener(mainFrame);
	}

	/**
	 * Used to process the program arguments.
	 *
	 * @param args The arguments.
	 */
	private static void processArgs(String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equals("-b"))
				configuration.setBddName(args[i + 1]);
		}
	}

	/**
	 * Used to remove duplicates in an ArrayList.
	 *
	 * @param <T> The list type.
	 * @param list The list where to remove duplicates.
	 * @return The list without duplicates.
	 */
	public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
	{
		Set<T> setItems = new LinkedHashSet<>(list);
		list.clear();
		list.addAll(setItems);
		return list;
	}

	public static String durationToString(long time)
	{
		int mins = (int) ((time / 1000) / 60);
		return (mins / 60) + "H" + (mins % 60);
	}

	public static int stringToDuration(String time)
	{
		try
		{
			int duration = 0;
			duration += Integer.parseInt(time.split("H|h")[0]) * 60;
			duration += Integer.parseInt(time.split("H|h")[1]);
			return duration * 1000 * 60;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Used to update the SQL connection from the Configuration object.
	 */
	public static void reloadSQLFromConfig()
	{
		sql.reloadInfos(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddUser(), configuration.getBddPassword());
	}

	/**
	 * Used to capitalize the first letter.
	 *
	 * @param s The string to capitalize.
	 * @return The capitalized string.
	 */
	public static String capitalize(String s)
	{
		boolean first = true;
		StringBuilder sb = new StringBuilder();
		for(char c : s.toCharArray())
			if(first)
			{
				sb.append(Character.toUpperCase(c));
				first = false;
			}
			else
				sb.append(c);
		return sb.toString();
	}

	public static void importCSV(JFrame parent)
	{
		try
		{
			File file = getNewFilePatch(baseFile, JFileChooser.FILES_ONLY, new FileNameExtensionFilter(Utils.resourceBundle.getString("open_csv_description_file"), "csv"));
			if(file == null)
				return;
			int reply = JOptionPane.showConfirmDialog(null, "<html><p>" + resourceBundle.getString("import_csv_drop").replaceAll("\n", "<br />") + "</p></html>", resourceBundle.getString("import_csv_drop_title"), JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.YES_OPTION)
			{
				Students.resetStudentsTable();
				sql.createBaseTable();
			}
			List<String> lines = readTextFile(file);
			String[] columns = lines.get(0).split(";");
			lines.remove(0);
			int UIDIndex = getIndexOf(columns, "CSN");
			int firstnameIndex = getIndexOf(columns, "PRENOM");
			int lastnameIndex = getIndexOf(columns, "NOM");
			if(UIDIndex == -1 || firstnameIndex == -1 || lastnameIndex == -1)
				throw new IllegalArgumentException("Cannot find one of the requiered columns");
			int req = 0;
			for(String line : lines)
			{
				String[] infos = line.split(";");
				Students.addStudent(infos[lastnameIndex].replaceAll(" ", "-") + " " + infos[firstnameIndex].replaceAll(" ", "-"), infos[UIDIndex]);
				req++;
			}
			JOptionPane.showMessageDialog(parent, String.format(resourceBundle.getString("csv_import_done"), req), resourceBundle.getString("csv_import_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(parent, resourceBundle.getString("csv_import_error"), resourceBundle.getString("csv_import_title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private static int getIndexOf(Object[] table, Object test)
	{
		for(int i = 0; i < table.length; i++)
			if(table[i].equals(test))
				return i;
		return -1;
	}

	public static <T> boolean vectorContains(Vector<T> vec, T element)
	{

		for(Object el : vec)
			if(el instanceof Vector)
			{
				if(vectorContains((Vector<T>) el, element))
					return true;
			}
			else if(el.equals(element))
				return true;
		return false;
	}

	public static void exportResults(MainFrame parent)
	{
		File file = new File(baseFile, "ResultExport " + new SimpleDateFormat("yyy-MM-dd HH_mm_ss").format(new Date()) + ".csv");
		PrintWriter pw = null;
		boolean reset = false;
		try
		{
			int total = 0;//Integer.parseInt(JOptionPane.showInputDialog(parent, resourceBundle.getString("total_conf"), resourceBundle.getString("total_conf_title"), JOptionPane.QUESTION_MESSAGE));
			int min = Integer.parseInt(JOptionPane.showInputDialog(parent, resourceBundle.getString("min_conf"), resourceBundle.getString("min_conf_title"), JOptionPane.QUESTION_MESSAGE));
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
			ResultSet result = sql.sendQueryRequest("SELECT Date, Start, End FROM Periods;");
			pw.print("NOM");
			pw.print(";");
			pw.print("PRENOM");
			pw.print(";");
			pw.print("# PRESENCES");
			pw.print(";");
			pw.print("# ABSENCES");
			pw.print(";");
			pw.print("SEUIL ATTEINT");
			while(result.next())
			{
				total++;
				pw.print(";");
				pw.print(result.getString("Date"));
				pw.print(" ");
				pw.print(result.getString("Start"));
				pw.print("-");
				pw.print(result.getString("End"));
			}
			pw.println();
			pw.println();
			if(min > total)
			{
				JOptionPane.showMessageDialog(parent, resourceBundle.getString("too_much_min_conf"), resourceBundle.getString("error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			for(String UID : Students.getAllStudentsCSN())
			{
				try
				{
					ArrayList<Integer> checked = new ArrayList<>();
					result = sql.sendQueryRequest("SELECT Period_ID From Checked Where CSN = \"" + UID + "\";");
					while(result.next())
						checked.add(result.getInt("Period_ID"));
					pw.print(Students.getLastname(UID));
					pw.print(";");
					pw.print(Students.getFirstname(UID));
					pw.print(";");
					pw.print("" + checked.size());
					pw.print(";");
					pw.print("" + (total - checked.size()));
					pw.print(";");
					pw.print(checked.size() < min ? "Non" : "Oui");
					for(int i = 1; i < total + 1; i++)
					{
						pw.print(";");
						pw.print(checked.contains(i) ? "Pr\351sent" : "Absent");
					}
					pw.println();
					pw.flush();
				}
				catch(Exception e)
				{
				}
			}
			if(JOptionPane.showConfirmDialog(parent, resourceBundle.getString("export_result_reset"), resourceBundle.getString("export_result_reset_title"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			{
				sql.resetCheckedTable();
				Periods.resetPeriodsTable();
			}
			JOptionPane.showMessageDialog(parent, String.format(resourceBundle.getString("sql_export_done"), file.getAbsolutePath()), resourceBundle.getString("sql_export_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(parent, resourceBundle.getString("only_numbers"), resourceBundle.getString("error"), JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception e)
		{
			reset = true;
			e.printStackTrace();
		}
		finally
		{
			if(pw != null)
				pw.close();
		}
		if(reset)
			if(file.exists())
				file.delete();
	}
}
