package fr.tours.polytech.DI.RFID.utils;

import fr.tours.polytech.DI.RFID.frames.MainFrame;
import fr.tours.polytech.DI.RFID.objects.Configuration;
import fr.tours.polytech.DI.RFID.objects.Group;
import fr.tours.polytech.DI.RFID.objects.Period;
import fr.tours.polytech.DI.RFID.objects.Student;
import fr.tours.polytech.DI.TerminalReader.threads.TerminalReader;
import javax.imageio.ImageIO;
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
	private static TerminalReader terminalReader;
	private static MainFrame mainFrame;
	public static Configuration configuration;

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
	 * @throws IOException If files couldn't be read.
	 * @throws SecurityException If the database connection can't be made.
	 * @see FileHandler#FileHandler(String, boolean)
	 */
	public static void init() throws SecurityException, IOException
	{
		logger = Logger.getLogger("TerminalReader");
		resourceBundle = ResourceBundle.getBundle("lang/messages", Locale.getDefault());
		baseFile = new File("." + File.separator + "RFID");
		icons = new ArrayList<>();
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon16.png")));
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon32.png")));
		icons.add(ImageIO.read(Utils.class.getClassLoader().getResource("icons/icon64.png")));
		configuration = Configuration.deserialize(new File(baseFile, "configuration"));
		terminalReader = new TerminalReader("Contactless");
		sql = new SQLManager(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddUser(), configuration.getBddPassword());
		students = Utils.sql.getAllStudents();
		groups = Group.loadGroups();
		mainFrame = new MainFrame();
		terminalReader.addListener(mainFrame);
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
			if(student != null && student.is(name))
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
					fileWriter = new FileWriter(file, true);
					bufferedWriter = new BufferedWriter(fileWriter);
					printWriter = new PrintWriter(bufferedWriter);
					printWriter.print(dateFormat.format(date) + ";" + period.getTimeInterval() + ";" + student.getName() + "\n");
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
				if(stu.equals(student))
					return true;
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

	public static ArrayList<Student> getRefreshedStudents()
	{
		ArrayList<Student> list = new ArrayList<>(students);
		list.addAll(sql.getAllStudents());
		Utils.removeDuplicates(list);
		return list;
	}

	public static void reloadSQLFromConfig()
	{
		sql.reloadInfos(configuration.getBddIP(), configuration.getBddPort(), configuration.getBddName(), configuration.getBddUser(), configuration.getBddPassword());
	}

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
}
