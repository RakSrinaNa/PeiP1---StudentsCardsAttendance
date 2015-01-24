package fr.tours.polytech.DI.RFID.enums;

public enum APDUResponse
{
	UNKNOWN((byte) 0x0, "Unknown response"), NO_ERROR((byte) 0x9000, "No error occurred"), NOT_AVAILABLE((byte) 0x6282, "Data object warning, requested information not available"), NO_INFORMATION((byte) 0x6300, "No information"), EXECUTION_STOPPED((byte) 0x6301, "Execution stopped due to failure in other data object"), DATA_NOT_SUPPORTED((byte) 0x6A81, "Data object not supported"), UNEXPECTED_LENGTH((byte) 0x6700, "Data object have unexpected length"), UNEXPECTED_VALUE((byte) 0x6A80, "Data object have unexcepted value"), IFD_EXECUTION_ERROR((byte) 0x6400, "Data object execution error, noresponse from IFD"), ICC_EXECUTION_ERROR((byte) 0x6401, "Data object execution error, noresponse from ICC"), DATA_FAIL((byte) 0x6F00, "Data object failed, no precise diagnosis");
	private String errorString;
	private byte errorCode;

	APDUResponse(byte code, String string)
	{
		this.errorCode = code;
		this.errorString = string;
	}

	public static String getErrorString(byte code)
	{
		return APDUResponse.getErrorByCode(code).getErrorString();
	}

	private static APDUResponse getErrorByCode(byte code)
	{
		for(APDUResponse response : APDUResponse.values())
			if(response.getErrorCode() == code)
				return response;
		return APDUResponse.UNKNOWN;
	}

	public String getErrorString()
	{
		return this.errorString;
	}

	private byte getErrorCode()
	{
		return this.errorCode;
	}
}
