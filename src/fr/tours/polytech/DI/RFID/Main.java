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
package fr.tours.polytech.DI.RFID;

import java.io.IOException;
import fr.tours.polytech.DI.RFID.utils.Utils;

/**
 * Program used to replace the attendance sheet with the student cards.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 * @version 1.0
 * @since 1.8
 */
public class Main
{
	/**
	 * The main function, launched on startup.
	 *
	 * @param args Arguments for the program - Not used.
	 *
	 * @throws SecurityException If the Student.csv file can't be read.
	 * @throws IOException If the Student.csv file can't be read.
	 *
	 * @see Utils#init()
	 */
	public static void main(String[] args) throws SecurityException, IOException
	{
		Utils.init();
	}
}
