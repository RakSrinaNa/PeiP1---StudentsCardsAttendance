package polytech.DI.RFID.utils;

import fr.tours.polytech.DI.TerminalReader.threads.TerminalReader;
import polytech.DI.RFID.frames.MainFrame;
import polytech.DI.RFID.objects.Configuration;
import polytech.DI.RFID.objects.Group;
import polytech.DI.RFID.objects.Period;
import polytech.DI.RFID.objects.Student;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
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
	public static ArrayList<Student> students;
	public static ArrayList<Group> groups;
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
		Group.saveGroups(Utils.groups);
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
			pw.println("-- ---------------------------");
			pw.println("-- STRUCTURE");
			pw.println("-- ---------------------------");
			pw.println("DROP TABLE IF EXISTS " + sql.getTableName() + ";");
			pw.println("CREATE TABLE " + sql.getTableName() + "(" + SQLManager.UID_LABEL + " varchar(18), " + SQLManager.LASTNAME_LABEL + " varchar(255), " + SQLManager.FIRSTNAME_LABEL + " varchar(255)," + "PRIMARY KEY (" + SQLManager.UID_LABEL + ")) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			pw.println();
			pw.println("-- ---------------------------");
			pw.println("-- DATA OF STUDENTS");
			pw.println("-- ---------------------------");
			for(Student student : Utils.removeDuplicates(Utils.sql.getAllStudents()))
				pw.println("INSERT INTO " + sql.getTableName() + " (" + SQLManager.UID_LABEL + "," + SQLManager.FIRSTNAME_LABEL + "," + SQLManager.LASTNAME_LABEL + ") VALUES(\"" + student.getRawUid() + "\",\"" + student.getFirstName() + "\",\"" + student.getLastname() + "\");");
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
	 * Used to know if a student have checked.
	 *
	 * @param student The student to verify.
	 * @return True if he have checked in at least one group, false if not.
	 */
	public static boolean hasChecked(Student student)
	{
		boolean checked = false;
		for(Group group : groups)
			checked |= group.hasChecked(student);
		return checked;
	}

	/**
	 * used to check a student.
	 *
	 * @param student The student to check.
	 * @return True if the student is been checked in at least one group, false if not.
	 */
	public static boolean checkStudent(Student student)
	{
		boolean checked = false;
		for(Group group : groups)
			if(group.checkStudent(student))
				checked |= true;
		return checked;
	}

	/**
	 * Used to uncheck a student.
	 *
	 * @param student The student to uncheck.
	 */
	public static void uncheckStudent(Student student)
	{
		for(Group group : groups)
			group.uncheckStudent(student);
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
	public static void init(String args[]) throws SecurityException, IOException
	{
		File libPcscLite = new File("/lib/x86_64-linux-gnu/libpcsclite.so.1");
		if(libPcscLite.exists())
			System.setProperty("sun.security.smartcardio.library", libPcscLite.getAbsolutePath());
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
		sql = new SQLManager(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddTableName(), configuration.getBddUser(), configuration.getBddPassword());
		students = Utils.sql.getAllStudents();
		groups = Group.loadGroups();
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
	 * Used to get a student by his name.
	 *
	 * @param name The name of the student.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	public static Student getStudentByName(String name, boolean checkDB)
	{
		for(Student student : students)
			if(student != null && student.isSameName(name))
				return student;
		return checkDB ? Utils.sql.getStudentByName(capitalize(name.substring(0, name.lastIndexOf(" ")).trim().toLowerCase()), name.substring(name.lastIndexOf(" ")).trim()) : null;
	}

	/**
	 * Used to get a student by his UID.
	 *
	 * @param uid The student's card UID.
	 * @param checkDB Should check him in the database if we don't know him?
	 * @return The student or null if unknown.
	 */
	public static Student getStudentByUID(String uid, boolean checkDB)
	{
		for(Student student : students)
			if(student != null && student.getUid().equals(uid.replaceAll("-", "")))
				return student;
		return checkDB ? Utils.sql.getStudentByUID(uid.replaceAll("-", "")) : null;
	}

	/**
	 * Used to log a check in the CSV file.
	 *
	 * @param student The student that checked.
	 */
	public static void logCheck(Student student)
	{
		if(!configuration.isLogAll())
			return;
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		PrintWriter printWriter = null;
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("[zzz] dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			File file = new File(baseFile, "Log" + File.separator + "checked_" + calendar.get(Calendar.YEAR) + ".csv");
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				try
				{
					file.createNewFile();
				}
				catch(IOException exception)
				{
					exception.printStackTrace();
				}
			}
			fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(bufferedWriter);
			printWriter.print(dateFormat.format(date) + ";" + student.getName() + ";" + student.getUid().replaceAll("-", "") + "\n");
		}
		catch(Exception exception)
		{
			Utils.logger.log(Level.SEVERE, "Cannot write checked file", exception);
		}
		if(printWriter != null)
			try
			{
				printWriter.close();
			}
			catch(Exception exception)
			{
			}
		if(bufferedWriter != null)
			try
			{
				bufferedWriter.close();
			}
			catch(Exception exception)
			{
			}
		if(fileWriter != null)
			try
			{
				fileWriter.close();
			}
			catch(Exception exception)
			{
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

	/**
	 * Used to log all absents students in a CSV file with their name.
	 *
	 * @param period The period when the students haven't checked.
	 * @param students The list of all the students that need to check.
	 * @param checkedStudents The students that have checked.
	 */
	public static void writeAbsents(Period period, ArrayList<Student> students, ArrayList<Student> checkedStudents)
	{
		for(Student student : students)
			if(!checkedStudents.contains(student))
			{
				logger.log(Level.INFO, student + " is missing");
				FileWriter fileWriter = null;
				BufferedWriter bufferedWriter = null;
				PrintWriter printWriter = null;
				try
				{
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date date = new Date();
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					File file = new File(baseFile, "Absents" + File.separator + "absent_" + student.getName() + "_" + calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + ".csv");
					if(!file.exists())
					{
						file.getParentFile().mkdirs();
						try
						{
							file.createNewFile();
						}
						catch(IOException exception)
						{
							exception.printStackTrace();
						}
					}
					String last = "";
					List<String> lines = readTextFile(file);
					if(period != null && lines.size() > 0 && lines.get(lines.size() - 1).startsWith("Total"))
					{
						last = lines.get(lines.size() - 1);
						lines.remove(lines.size() - 1);
					}
					fileWriter = new FileWriter(file, false);
					bufferedWriter = new BufferedWriter(fileWriter);
					printWriter = new PrintWriter(bufferedWriter);
					for(String line : lines)
						printWriter.println(line);
					printWriter.print(dateFormat.format(date));
					printWriter.print(";");
					printWriter.print(student.getName());
					if(period != null)
					{
						printWriter.print(";");
						printWriter.print(period.getRawTimeInterval());
						printWriter.print(";");
						printWriter.print(period.getDurationString());
					}
					printWriter.println();
					if(period != null)
					{
						if(last.equals(""))
						{
							printWriter.println("Total;" + period.getDurationString());
						}
						else
						{
							String[] vals = last.split(";");
							String duration = vals[vals.length - 1];
							int timeLast = stringToDuration(duration);
							int timeNow = (int) period.getDuration();
							printWriter.println("Total;" + durationToString(timeLast + timeNow));
						}
					}
				}
				catch(Exception exception)
				{
					Utils.logger.log(Level.SEVERE, "Cannot write checked file", exception);
				}
				if(printWriter != null)
					try
					{
						printWriter.close();
					}
					catch(Exception exception)
					{
					}
				if(bufferedWriter != null)
					try
					{
						bufferedWriter.close();
					}
					catch(Exception exception)
					{
					}
				if(fileWriter != null)
					try
					{
						fileWriter.close();
					}
					catch(Exception exception)
					{
					}
			}
	}

	/**
	 * Used to convert a time in seconds to a string like xxHyy, where xx are hours and yy minutes.
	 *
	 * @param time The duration to convert.
	 * @return The corresponding string.
	 */
	public static String durationToString(long time)
	{
		int mins = (int) ((time / 1000) / 60);
		return (mins / 60) + "H" + (mins % 60);
	}

	/**
	 * Used to convert a String formatted as xxHyy, where xx are hours and yy minutes, to a time in seconds.
	 *
	 * @param time The String to convert.
	 * @return The time in seconds.
	 */
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
	 * Used to know if a collection contains a student.
	 *
	 * @param collection The collection to verify.
	 * @param student The student to search for.
	 * @return True if in the collection, false if not.
	 */
	public static boolean containsStudent(Collection collection, Student student)
	{
		if(collection == null || collection.size() < 1)
			return false;
		try
		{
			if(collection.iterator().next() instanceof Vector)
				for(Object obj : collection)
				{
					Vector<Student> vec = (Vector<Student>) obj;
					for(Student stu : vec)
						if(stu.equals(student))
							return true;
				}
			else
				for(Student stu : (Collection<Student>) collection)
					if(stu != null && stu.equals(student))
						return true;
		}
		catch(ConcurrentModificationException e)
		{
		}
		return false;
	}

	/**
	 * Used to remove a student from a list.
	 *
	 * @param list The list where to remove.
	 * @param toRemove The collection of students to remove.
	 * @return The new list with the students removed.
	 */
	public static ArrayList<Student> removeStudentsInList(ArrayList<Student> list, Collection<Student> toRemove)
	{
		ArrayList<Student> toRem = new ArrayList<>();
		for(Student student : toRemove)
		{
			for(Student stu : list)
				if(student.equals(stu))
					toRem.add(stu);
			list.removeAll(toRem);
			toRem.clear();
		}
		return list;
	}

	/**
	 * Used to get a refreshed list of students from database.
	 *
	 * @return The refreshed list.
	 */
	public static ArrayList<Student> getRefreshedStudents()
	{
		ArrayList<Student> list = new ArrayList<>(students);
		list.addAll(sql.getAllStudents());
		Utils.removeDuplicates(list);
		return list;
	}

	/**
	 * Used to update the SQL connection from the Configuration object.
	 */
	public static void reloadSQLFromConfig()
	{
		sql.reloadInfos(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddTableName(), configuration.getBddUser(), configuration.getBddPassword());
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

	/**
	 * Used to import a CSV file from the DTIC department.
	 *
	 * @param parent The parent frame.
	 */
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
				sql.sendUpdateRequest("DROP TABLE IF EXISTS " + sql.getTableName() + ";");
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
				sql.addStudentToDatabase(new Student(infos[UIDIndex], infos[lastnameIndex].replaceAll(" ", "-"), infos[firstnameIndex].replaceAll(" ", "-")));
				req++;
			}
			JOptionPane.showMessageDialog(parent, String.format(resourceBundle.getString("csv_import_done"), req), resourceBundle.getString("csv_import_title"), JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(parent, resourceBundle.getString("csv_import_error"), resourceBundle.getString("csv_import_title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Used to get the index of an object in an array.
	 *
	 * @param table The array containing the elements.
	 * @param test The object to get the index of.
	 * @return The index of the element, -1 if not found.
	 */
	private static int getIndexOf(Object[] table, Object test)
	{
		for(int i = 0; i < table.length; i++)
			if(table[i].equals(test))
				return i;
		return -1;
	}
}
