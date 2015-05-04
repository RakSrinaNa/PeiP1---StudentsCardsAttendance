package polytech.DI.RFID.objects;

import polytech.DI.RFID.utils.Utils;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * An object representing a group.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Group implements Serializable
{
	private static final long serialVersionUID = 546546546L;
	private final String name;
	private final ArrayList<Student> students;
	private final ArrayList<Period> periods;
	private transient Period currentPeriod;
	private ArrayList<Student> checkedStudents;

	/**
	 * Constructor.
	 *
	 * @param name The group name.
	 */
	public Group(String name)
	{
		this.name = name;
		this.students = new ArrayList<>();
		this.checkedStudents = new ArrayList<>();
		this.periods = new ArrayList<>();
	}

	/**
	 * Used to deserialize a group file.
	 *
	 * @param file The serialized file.
	 * @return The group correcponding to the serialized object.
	 *
	 * @throws IOException If the file cannot be read.
	 * @throws ClassNotFoundException If the file couldn't be deserialized.
	 */
	public static Group deserialize(File file) throws IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Group group;
		try
		{
			group = (Group) ois.readObject();
		}
		catch(Exception e)
		{
			ois.close();
			throw e;
		}
		ois.close();
		group.removeNull();
		return group;
	}

	/**
	 * Used to save all groups into serialized file.
	 *
	 * @param groups The groups to save.
	 */
	public static void saveGroups(ArrayList<Group> groups)
	{
		for(File file : new File(Utils.baseFile, "Groups").listFiles())
			file.delete();
		for(Group group : groups)
			try
			{
				group.saveGroup();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Used to load all groups from serialized files.
	 *
	 * @return The groups deserialized.
	 */
	public static ArrayList<Group> loadGroups()
	{
		ArrayList<Group> groups = new ArrayList<>();
		File folder = new File(Utils.baseFile, "Groups");
		folder.mkdirs();
		for(File file : folder.listFiles())
			try
			{
				groups.add(Group.deserialize(file));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		return groups;
	}

	/**
	 * Used to remove all null objects from lists.
	 */
	private void removeNull()
	{
		students.remove(null);
		periods.remove(null);
	}

	/**
	 * Used to get the students of the group.
	 *
	 * @return The students.
	 */
	public ArrayList<Student> getStudents()
	{
		return students;
	}

	/**
	 * Used to get the periods of the group.
	 *
	 * @return The periods.
	 */
	public ArrayList<Period> getPeriods()
	{
		return periods;
	}

	/**
	 * Used to get the groupe's name.
	 *
	 * @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Used to check a student.
	 *
	 * @param student The student to check.
	 * @return True if checked, false if already checked, not in a checking period or not in this group.
	 */
	public boolean checkStudent(Student student)
	{
		return isCurrentlyPeriod() && !Utils.containsStudent(checkedStudents, student) && this.checkedStudents.add(student);
	}

	/**
	 * Used to serialize this object to a file.
	 *
	 * @param file The file where to serialize.
	 * @throws IOException If the file couldn't be done.
	 */
	public void serialize(File file) throws IOException
	{
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this);
		oos.close();
	}

	/**
	 * Used to serialize the group.
	 *
	 * @throws IOException If the file couldn't be done.
	 */
	private void saveGroup() throws IOException
	{
		this.serialize(new File(Utils.baseFile, "Groups" + File.separator + this.getName() + ".grp"));
	}

	/**
	 * Used to get a period by his name.
	 *
	 * @param name The period name.
	 * @return The corresponding period.
	 */
	public Period getPeriodByName(String name)
	{
		for(Period period : periods)
			if(period.isSame(name))
				return period;
		return null;
	}

	/**
	 * Used to remove a period.
	 *
	 * @param period The period to remove.
	 */
	public void remove(Period period)
	{
		this.periods.remove(period);
	}

	/**
	 * Used to remove a student by his name.
	 *
	 * @param name The name of the student.
	 */
	public void removeStudent(String name)
	{
		for(Student st : students)
			if(st.isSameName(name))
			{
				this.students.remove(st);
				break;
			}
	}

	/**
	 * used to remove a student.
	 *
	 * @param student The student to remove.
	 */
	public void remove(Student student)
	{
		ArrayList<Student> toRemove = new ArrayList<>();
		for(Student stu : students)
			if(stu.equals(student))
				toRemove.add(stu);
		this.students.removeAll(toRemove);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + this.students.hashCode() + this.periods.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		return o != null && o instanceof Group && ((Group) o).getName().equals(this.getName());
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Used to get the students that can be added to this group.
	 *
	 * @return A list of students.
	 */
	public ArrayList<Student> getAddableStudents()
	{
		return Utils.removeStudentsInList(new ArrayList<>(Utils.getRefreshedStudents()), this.students);
	}

	/**
	 * Used to add a student to the group.
	 *
	 * @param student The student to add.
	 * @return True if added, false if not.
	 */
	public boolean addStudent(Student student)
	{
		if(student == null)
			return false;
		if(!Utils.containsStudent(students, student))
		{
			this.students.add(Utils.getStudentByName(student.getName(), true));
			return true;
		}
		return false;
	}

	/**
	 * Used to add a period to the group.
	 *
	 * @param period The period to add.
	 * @return True if added, false if not.
	 *
	 * @throws Exception If period is null
	 */
	public boolean addPeriod(Period period) throws Exception
	{
		if(period == null)
			throw new Exception("A null period has benn added to the group!");
		for(Period per : periods)
			if(per.isOverlapped(period))
				return false;
		this.periods.add(period);
		return true;
	}

	/**
	 * Used to update the current group. Will update current period and write absents file if needed.
	 */
	public void update()
	{
		if(!periods.contains(currentPeriod))
			currentPeriod = null;
		if(currentPeriod == null)
			currentPeriod = getNewPeriod();
		else if(!currentPeriod.isInPeriod(new Date()))
		{
			Utils.writeAbsents(currentPeriod, this.students, this.checkedStudents);
			this.checkedStudents.clear();
			currentPeriod = null;
		}
	}

	/**
	 * Used to get a new period.
	 *
	 * @return A period if we are in one, null if no period for the current time.
	 */
	private Period getNewPeriod()
	{
		Date date = new Date();
		for(Period period : periods)
			if(period.isInPeriod(date))
				return period;
		return null;
	}

	/**
	 * Used to know if a student have checked.
	 *
	 * @param student The student.
	 * @return True if he have checked, false if not.
	 */
	public boolean hasChecked(Student student)
	{
		return this.checkedStudents.contains(student);
	}

	/**
	 * Used to read a serialized object.
	 *
	 * @param in The input stream.
	 * @throws IOException If the stream can't be read.
	 * @throws ClassNotFoundException If the file couldn't be deserialized.
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		this.checkedStudents = new ArrayList<>();
	}

	/**
	 * Used to get the students that should check for this period.
	 *
	 * @return A list of students.
	 */
	public ArrayList<Student> getAllToCheck()
	{
		if(isCurrentlyPeriod())
			return this.students;
		return new ArrayList<>();
	}

	/**
	 * Used to know if this group is currently in a period.
	 *
	 * @return True if in a period, false if not.
	 */
	public boolean isCurrentlyPeriod()
	{
		return currentPeriod != null;
	}

	/**
	 * Used to uncheck a student.
	 *
	 * @param student The student.
	 */
	public void uncheckStudent(Student student)
	{
		ArrayList<Student> toRemove = new ArrayList<>();
		for(Student stu : checkedStudents)
			if(stu.equals(student))
				toRemove.add(stu);
		checkedStudents.removeAll(toRemove);
	}

	/**
	 * Used to get the current period as a string.
	 *
	 * @return The period string.
	 */
	public String getCurrentPeriodString()
	{
		if(currentPeriod == null)
			return Utils.resourceBundle.getString("not_in_period");
		return currentPeriod.getRawTimeInterval();
	}

	/**
	 * Verify if the the group have a correct name.
	 *
	 * @return True if it have a correct name, false if not.
	 */
	public boolean hasName()
	{
		return this.name != null && !this.name.equals("");
	}
}
