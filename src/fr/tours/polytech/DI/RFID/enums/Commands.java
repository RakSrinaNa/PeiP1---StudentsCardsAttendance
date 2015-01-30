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
package fr.tours.polytech.DI.RFID.enums;

import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Enumeration for the commands that can be sent to the contactless card.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public enum Commands
{
	UID("UID", new byte[]
	{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
	private byte[] command;
	private String name;

	/**
	 * Constructor.
	 *
	 * @param name The name of the command.
	 * @param command The command.
	 */
	Commands(String name, byte[] command)
	{
		this.command = command;
		this.name = name;
	}

	/**
	 * Used to get the command.
	 *
	 * @return The command.
	 */
	public byte[] getCommand()
	{
		return this.command;
	}

	/**
	 * Used to get the name.
	 *
	 * @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Used to get a String representing this item, will be formatted as
	 * <b>[name] ([command])</b>
	 */
	@Override
	public String toString()
	{
		return this.name + " (" + Utils.bytesToHex(this.command) + ")";
	}
}
