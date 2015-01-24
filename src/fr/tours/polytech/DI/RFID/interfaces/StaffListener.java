package fr.tours.polytech.DI.RFID.interfaces;

import fr.tours.polytech.DI.RFID.objects.RFIDCard;
import fr.tours.polytech.DI.RFID.objects.Student;

public interface StaffListener
{
	void cardAdded(RFIDCard rfidCard, Student student);
}
