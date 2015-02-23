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
package fr.tours.polytech.DI.TerminalReader.enums;

/**
 * Enumeration of the APDU errors.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public enum APDUResponse
{
	UNKNOWN(0x0, "Unknown response"), NO_ERROR(0x9000, "No error occurred"), NOT_AVAILABLE(0x6282, "Data object warning, requested information not available"), NO_INFORMATION(0x6300, "No information"), EXECUTION_STOPPED(0x6301, "Execution stopped due to failure in other data object"), DATA_NOT_SUPPORTED(0x6A81, "Data object not supported"), UNEXPECTED_LENGTH(0x6700, "Data object have unexpected length"), UNEXPECTED_VALUE(0x6A80, "Data object have unexcepted value"), IFD_EXECUTION_ERROR(0x6400, "Data object execution error, noresponse from IFD"), ICC_EXECUTION_ERROR(0x6401, "Data object execution error, noresponse from ICC"), DATA_FAIL(0x6F00, "Data object failed, no precise diagnosis");
	private final String errorString;
	private final int errorCode;

	/**
	 * Constructor.
	 *
	 * @param code The error code.
	 * @param string A description of the error.
	 */
	APDUResponse(int code, String string)
	{
		this.errorCode = code;
		this.errorString = string;
	}

	/**
	 * Used to get the description for an error code.
	 *
	 * @param code The error code.
	 * @return The description of the error.
	 */
	public static String getErrorString(int code)
	{
		return APDUResponse.getErrorByCode(code).getErrorString();
	}

	/**
	 * Used to get the enumeration item corresponding to the error code.
	 *
	 * @param code The error code.
	 * @return The APDUResponse for this error.
	 */
	private static APDUResponse getErrorByCode(int code)
	{
		for(APDUResponse response : APDUResponse.values())
			if(response.getErrorCode() == code)
				return response;
		return APDUResponse.UNKNOWN;
	}

	/**
	 * Used to get the description of the error.
	 *
	 * @return The description.
	 */
	public String getErrorString()
	{
		return this.errorString;
	}

	/**
	 * Used to get the error code of the error.
	 *
	 * @return The error code.
	 */
	private int getErrorCode()
	{
		return this.errorCode;
	}
}
