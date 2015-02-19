/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package fr.tours.polytech.DI.RFID.objects;

import fr.tours.polytech.DI.RFID.utils.SQLManager;
import fr.tours.polytech.DI.RFID.utils.Utils;

import java.io.*;

/**
 * Class representing a student (or anyone else, maybe we should call it User?).
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Student
{
	private String name;
	private String uid;
	private boolean isStaff;

	/**
	 * Constructor.
	 *
	 * @param uid The UID of the student card.
	 * @param name The student's name.
	 * @param isStaff Either if it's a staff member or not.
	 */
	public Student(String uid, String name, boolean isStaff)
	{
		this.uid = uid;
		this.name = name;
		this.isStaff = isStaff;
	}

	/**
	 * Used to get a Student by his name from the database.
	 *
	 * @param name The name to fetch.
	 * @return A Student object.
	 *
	 * @see SQLManager#getStudentByName(String)
	 */
	public static Student fetchSQL(String name)
	{
		return Utils.sql.getStudentByName(name);
	}

	/**
	 * Used to get the name of the student.
	 *
	 * @return The student's name.
	 */
	public String getName()
	{
		return this.name;
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

	/**
	 * Used to compare two objects are the same.
	 */
	@Override
	public int hashCode()
	{
		return this.uid.hashCode();
	}

	/**
	 * Used to know if a student have a valid name.
	 *
	 * @return True if have a valid name, false if not.
	 */
	public boolean hasValidName()
	{
		return this.name != null && !this.name.equals("");
	}

	/**
	 * Used to know if the student is a staff member.
	 *
	 * @return true if from the staff, false if not.
	 */
	public boolean isStaff()
	{
		return this.isStaff;
	}

	/**
	 * Used to know if the student is a staff member.
	 *
	 * @return 1 if from the staff, 0 if not.
	 */
	public int isStaffSQL()
	{
		return isStaff() ? 1 : 0;
	}

	/**
	 * Used to get a String representing the object. Formatted as <b><i>name</i></b> if not from the staff, <b><i>name</i> (Staff)</b> if from the staff.
	 */
	@Override
	public String toString()
	{
		return this.name + (this.isStaff ? " (Staff)" : "");
	}

	private Student readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		return new Student((String)ois.readObject(), (String)ois.readObject(), (boolean)ois.readObject());
	}

	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		oos.writeObject(getUid());
		oos.writeObject(getName());
		oos.writeBoolean(isStaff());
	}
}
