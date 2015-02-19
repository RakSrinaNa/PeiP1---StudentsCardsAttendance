package fr.tours.polytech.DI.RFID.objects;

import fr.tours.polytech.DI.RFID.utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class Group implements Serializable
{
	private static final long serialVersionUID = 546546546L;
	private String name;
	private ArrayList<Student> students;
	private ArrayList<Period> periods;
	private transient Period currentPeriod;
	private ArrayList<Student> checkedStudents;
	private boolean currentlyPeriod;

	public Group(String name)
	{
		this.name = name;
		this.students = new ArrayList<Student>();
		this.checkedStudents = new ArrayList<Student>();
		this.periods = new ArrayList<Period>();
			}

	public Group(String name, ArrayList<Student> students, ArrayList<Period> periods)
	{
		this.name = name;
		this.students = students;
		this.periods = periods;
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
		if(!isCurrentlyPeriod())
			return false;
		if(Utils.containsStudent(checkedStudents, student))
			return false;
		return this.checkedStudents.add(student);
	}

	public void serialize(File file) throws IOException
	{
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(this);
		oos.close();
	}

	public static Group deserialize(File file) throws IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		Group group = null;
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
		for(Group group : groups)
			try
			{
				group.saveGroup();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	private void saveGroup() throws IOException
	{
		File folder = new File(new File("."), "RFID\\Groups\\");
		this.serialize(new File(folder, this.getName() + ".grp"));
	}

	public static ArrayList<Group> loadGroups()
	{
		ArrayList<Group> groups = new ArrayList<Group>();
		File folder = new File(new File("."), "RFID\\Groups\\");
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

	@Override
	public String toString()
	{
		return getName();
	}

	public Student getStudentByName(String name)
	{
		for(Student student : students)
			if(student.equals(name))
				return student;
		return null;
	}

	public Period getPeriodByName(String name)
	{
		for(Period period : periods)
			if(period.equals(name))
				return period;
		return null;
	}

	public void remove(Period period)
	{
		this.periods.remove(period);
	}

	public void remove(Student student)
	{
		this.students.remove(student);
	}

	@Override
	public int hashCode()
	{
		return name.hashCode() + this.students.hashCode() + this.periods.hashCode();
	}

	public void addStudent(Student student)
	{
		this.students.add(student);
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
		if(currentPeriod == null) currentPeriod = getNewPeriod();
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
		if(this.checkedStudents == null)
			return false;
		return this.checkedStudents.contains(student);
	}

	public ArrayList<Student> getAllToCheck()
	{
		if(isCurrentlyPeriod())
			return this.students;
		return new ArrayList<Student>();
	}

	public boolean isCurrentlyPeriod()
	{
		return currentPeriod != null;
	}

	public void uncheckStudent(Student student)
	{
		ArrayList<Student> toRemove = new ArrayList<Student>();
		for(Student stu : checkedStudents)
			if(stu.equals(student))
				toRemove.add(stu);
		checkedStudents.removeAll(toRemove);
	}
}
