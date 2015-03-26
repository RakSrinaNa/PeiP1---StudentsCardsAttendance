package fr.tours.polytech.DI.RFID.objects;

import fr.tours.polytech.DI.RFID.utils.Utils;
import java.io.Serializable;

/**
 * Class representing a student.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Student implements Serializable, Comparable<Student>
{
	private static final long serialVersionUID = 546546596L;
	private final String surname;
	private final String firstname;
	private final String uid;

	/**
	 * Constructor.
	 *
	 * @param uid The UID of the student card.
	 * @param surname The surname of the student.
	 * @param firstname The firstname of the student.
	 */
	public Student(String uid, String surname, String firstname)
	{
		this.uid = uid;
		this.surname = Utils.capitalize(surname.toLowerCase().trim());
		this.firstname = Utils.capitalize(firstname.toLowerCase().trim());
	}

	/**
	 * Used to get the name of the student.
	 *
	 * @return The student's name.
	 */
	public String getName()
	{
		return getLastname().toUpperCase() + " " + getFirstName();
	}

	/**
	 * Used to get the student's card UID without any tirets.
	 *
	 * @return The UID.
	 */
	public String getRawUid()
	{
		return this.uid.replace("-", "");
	}

	/**
	 * Used to get the student's card UID.
	 *
	 * @return The UID.
	 */
	public String getUid()
	{
		return this.uid;
	}

	@Override
	public int hashCode()
	{
		return this.uid.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Student)
			return isSameName(((Student) o).getName());
		return o == this;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * Used to know if a student have a valid name.
	 *
	 * @return True if have a valid name, false if not.
	 */
	public boolean hasValidName()
	{
		return getFirstName() != null && getLastname() != null && !getFirstName().equals("") && !getLastname().equals("");
	}

	/**
	 * Used to know if the names are the same.
	 *
	 * @param name The name to test.
	 * @return True if they are the same, false if not.
	 */
	public boolean isSameName(String name)
	{
		return this.getName().equalsIgnoreCase(name);
	}

	/**
	 * Used to get the fistname of the student.
	 *
	 * @return The firstname.
	 */
	public String getFirstName()
	{
		return this.firstname;
	}

	/**
	 * Used to get the lastname of the student.
	 *
	 * @return The lastname.
	 */
	public String getLastname()
	{
		return this.surname;
	}

	@Override
	public int compareTo(Student o)
	{
		return o == null ? 0 : this.getName().compareTo(o.getName());
	}
}
