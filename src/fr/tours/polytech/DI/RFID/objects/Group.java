package fr.tours.polytech.DI.RFID.objects;

import java.io.*;
import java.util.ArrayList;

public class Group
{
	private String name;
	private ArrayList<Student> students;
	private ArrayList<Period> periods;

	public Group(String name)
	{
		this.name = name;
		this.students = new ArrayList<Student>();
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
		return false; //TODO
	}

	private Group readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		return new Group((String)ois.readObject(), (ArrayList<Student>)ois.readObject(), (ArrayList<Period>)ois.readObject());
	}

	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		oos.writeObject(getName());
		oos.writeObject(getStudents());
		oos.writeObject(getPeriods());
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

	public static void writeGroups(ArrayList<Group> groups)
	{
		//TODO
	}

	public static ArrayList<Group> loadGroups()
	{
		return new ArrayList<Group>();//TODO
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
