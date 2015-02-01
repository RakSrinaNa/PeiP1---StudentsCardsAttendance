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
package fr.tours.polytech.DI.RFID.interfaces;

import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.threads.TerminalReader;

/**
 * Interface used by {@link TerminalReader}
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public interface TerminalListener
{
	/**
	 * Called when a card added in the reader.
	 *
	 * @param rfidCard The card placed.
	 */
	void cardAdded(RFIDCard rfidCard);

	/**
	 * Called when a reader is added.
	 */
	void cardReaderAdded();

	/**
	 * Called when a reader is removed.
	 *
	 * @param isPresent If there is a reader currently present or not.
	 */
	void cardReaderRemoved();

	/**
	 * Called when a card is removed from the reader.
	 */
	void cardRemoved();
}
