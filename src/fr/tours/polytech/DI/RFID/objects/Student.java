package fr.tours.polytech.DI.RFID.objects;

public class Student
{
	private String name;
	private String uid;
	private boolean isTeatcher;

	public Student(String uid, String name, boolean isTeatcher)
	{
		this.uid = uid;
		this.name = name;
		this.isTeatcher = isTeatcher;
	}

	public String getName()
	{
		return this.name;
	}

	public String getUid()
	{
		return this.uid;
	}

	@Override
	public int hashCode()
	{
		return this.uid.hashCode();
	}

	public boolean isTeatcher()
	{
		return this.isTeatcher;
	}

	@Override
	public String toString()
	{
		return this.name + (this.isTeatcher ? " (Staff)" : "");
	}
}
