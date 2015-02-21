package fr.tours.polytech.DI.RFID.objects;

import fr.tours.polytech.DI.RFID.utils.Utils;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class Group implements Serializable
{
	private static final long serialVersionUID = 546546546L;
	private final String name;
	private final ArrayList<Student> students;
	private final ArrayList<Period> periods;
	private transient Period currentPeriod;
	private ArrayList<Student> checkedStudents;

	public Group(String name)
	{
		this.name = name;
		this.students = new ArrayList<>();
		this.checkedStudents = new ArrayList<>();
		this.periods = new ArrayList<>();
	}

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
		return group;
	}

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

	public ArrayList<Student> getStudents()
	{
		return students;
	}

	public ArrayList<Period> getPeriods()
	{
		return periods;
	}

	public String getName()
	{
		return name;
	}

	public boolean checkStudent(Student student)
	{
		return isCurrentlyPeriod() && !Utils.containsStudent(checkedStudents, student) && this.checkedStudents.add(student);
	}

	public void serialize(File file) throws IOException
	{
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this);
		oos.close();
	}

	private void saveGroup() throws IOException
	{
		this.serialize(new File(Utils.baseFile, "Groups" + File.separator + this.getName() + ".grp"));
	}

	public Period getPeriodByName(String name)
	{
		for(Period period : periods)
			if(period.is(name))
				return period;
		return null;
	}

	public void remove(Period period)
	{
		this.periods.remove(period);
	}

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

	public ArrayList<Student> getAddableStudents()
	{
		return Utils.removeStudentsInList(new ArrayList<>(Utils.students), this.students);
	}

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

	public boolean addPeriod(Period period)
	{
		for(Period per : periods)
			if(per.isOverlapped(period))
				return false;
		this.periods.add(period);
		return true;
	}

	public void update()
	{
		if(currentPeriod == null)
			currentPeriod = getNewPeriod();
		else if(!currentPeriod.isInPeriod(new Date()))
		{
			Utils.writeAbsents(currentPeriod, this.students, this.checkedStudents);
			this.checkedStudents.clear();
			currentPeriod = null;
		}
	}

	private Period getNewPeriod()
	{
		Date date = new Date();
		for(Period period : periods)
			if(period.isInPeriod(date))
				return period;
		return null;
	}

	public boolean hasChecked(Student student)
	{
		return this.checkedStudents.contains(student);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		this.checkedStudents = new ArrayList<>();
	}

	public ArrayList<Student> getAllToCheck()
	{
		if(isCurrentlyPeriod())
			return this.students;
		return new ArrayList<>();
	}

	public boolean isCurrentlyPeriod()
	{
		return currentPeriod != null;
	}

	public void uncheckStudent(Student student)
	{
		ArrayList<Student> toRemove = new ArrayList<>();
		for(Student stu : checkedStudents)
			if(stu.equals(student))
				toRemove.add(stu);
		checkedStudents.removeAll(toRemove);
	}

	public String getCurrentPeriodString()
	{
		if(currentPeriod == null)
			return "N'est pas dans un cr\351neau de validation";
		return currentPeriod.getTimeInterval();
	}
}
