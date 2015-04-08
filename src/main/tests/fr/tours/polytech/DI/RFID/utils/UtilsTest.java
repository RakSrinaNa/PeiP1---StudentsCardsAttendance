package main.tests.fr.tours.polytech.DI.RFID.utils;

import fr.tours.polytech.DI.RFID.utils.Utils;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

public class UtilsTest
{
	@org.junit.Test
	public void testRemoveDuplicates() throws Exception
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("Test");
		list.add("TesT");
		list.add("test");
		list.add("Test");
		assertTrue("Remove duplicate", Utils.removeDuplicates(list).size() == 3);
	}

	@org.junit.Test
	public void testDurationToString() throws Exception
	{
		assertTrue("Duration to string", Utils.durationToString(2 * (60 * 60 * 1000) + 43 * (60 * 1000)).equals("2H43"));
	}

	@org.junit.Test
	public void testStringToDuration() throws Exception
	{
		assertTrue("String to duration", Utils.stringToDuration("2H43") == 2 * (60 * 60 * 1000) + 43 * (60 * 1000));
	}

	@org.junit.Test
	public void testCapitalize() throws Exception
	{
		assertTrue("Capitalize", Utils.capitalize("je suis").equals("Je suis"));
	}
}