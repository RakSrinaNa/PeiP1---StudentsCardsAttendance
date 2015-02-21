/**
 * ****************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:
 * IBM Corporation - initial API and implementation
 * *****************************************************************************
 */
package fr.tours.polytech.DI.RFID.objects;

import org.apache.commons.lang3.text.WordUtils;
import java.io.Serializable;

/**
 * Class representing a student (or anyone else, maybe we should call it User?).
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Student implements Serializable
{
	private static final long serialVersionUID = 546546596L;
	private final String name;
	private final String uid;
	private final boolean isStaff;

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
	 * Used to get the name of the student.
	 *
	 * @return The student's name.
	 */
	public String getName()
	{
		return WordUtils.capitalize(this.name.toLowerCase());
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

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Student)
			return isSameName(((Student) o).getName());
		return o == this;
	}

	/**
	 * Used to get a String representing the object. Formatted as <b><i>name</i></b> if not from the staff, <b><i>name</i> (Staff)</b> if from the staff.
	 */
	@Override
	public String toString()
	{
		return this.name + (this.isStaff ? " (Staff)" : "");
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

	private boolean isSameName(String name)
	{
		return this.getName().equalsIgnoreCase(name);
	}

	public boolean is(String name)
	{
		return isSameName(name.replace("(Staff)", "").trim());
	}
}
