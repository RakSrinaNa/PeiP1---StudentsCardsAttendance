package fr.tours.polytech.DI.RFID.enums;

import fr.tours.polytech.DI.RFID.utils.Utils;

public enum Commands
{
	UID("UID", new byte[]
	{(byte) 0xFF, (byte) 0xCA, (byte) 0x00, (byte) 0x00, (byte) 0x00});
	private byte[] command;
	private String name;

	Commands(String name, byte[] command)
	{
		this.command = command;
		this.name = name;
	}

	public byte[] getCommand()
	{
		return this.command;
	}

	public String getName()
	{
		return this.name;
	}

	@Override
	public String toString()
	{
		return this.name + " (" + Utils.bytesToHex(this.command) + ")";
	}
}
