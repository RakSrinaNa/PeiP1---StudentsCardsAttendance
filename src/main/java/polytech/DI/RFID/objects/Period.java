package polytech.DI.RFID.objects;

import polytech.DI.RFID.utils.Utils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class representing a period for checking.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class Period implements Serializable
{
	public static final int MONDAY = 1, TUESDAY = 2, WEDNESDAY = 4, THURSDAY = 8, FRIDAY = 16, SATURDAY = 32, SUNDAY = 64;
	private static final int SERIALIZATION_VERSION = 1;
	private static final long serialVersionUID = -5261429885680582412L;
	private int day;
	private int startingHour;
	private int startingMinute;
	private int endingHour;
	private int endingMinute;
	private Calendar calendar;
	private DecimalFormat decimalFormat;

	/**
	 * Constructor.
	 *
	 * @param period A string representing the period. This should be formatted as <i>xx</i><b>h</b><i>xx</i><b>-</b><i>yy</i><b>h</b><i>yy</i> where <i>xx</i> and <i>yy</i> are the time to set.
	 * @param day The day where this period should be applied.
	 * @throws IllegalArgumentException If the period isn't formatted as it should be.
	 */
	public Period(int day, String period) throws IllegalArgumentException
	{
		if(!Pattern.matches("(\\d{1,2})(h|H)(\\d{1,2})(-)(\\d{1,2})(h|H)(\\d{1,2})", period))
			throw new IllegalArgumentException("Time should be formatted as xx:xx-yy:yy (was " + period + ")");
		this.day = day;
		period = period.toUpperCase().replaceAll(" ", "");
		String starting = period.substring(0, period.indexOf('-'));
		String ending = period.substring(period.indexOf('-') + 1);
		this.startingHour = Integer.parseInt(starting.substring(0, starting.indexOf("H")));
		this.startingMinute = Integer.parseInt(starting.substring(starting.indexOf("H") + 1));
		this.endingHour = Integer.parseInt(ending.substring(0, ending.indexOf("H")));
		this.endingMinute = Integer.parseInt(ending.substring(ending.indexOf("H") + 1));
		if(!isValidPeriod())
			throw new IllegalArgumentException("The dates aren't in a valid order");
		this.calendar = Calendar.getInstance(Locale.getDefault());
		this.decimalFormat = new DecimalFormat("00");
	}

	/**
	 * Constructor.
	 *
	 * @param start The starting date.
	 * @param end The ending date.
	 */
	public Period(Date start, Date end) throws IllegalArgumentException
	{
		this.calendar = Calendar.getInstance(Locale.getDefault());
		this.calendar.setTime(start);
		this.day = getDayFromCalendar(calendar.get(Calendar.DAY_OF_WEEK));
		this.startingHour = calendar.get(Calendar.HOUR_OF_DAY);
		this.startingMinute = calendar.get(Calendar.MINUTE);
		this.calendar.setTime(end);
		this.endingHour = calendar.get(Calendar.HOUR_OF_DAY);
		this.endingMinute = calendar.get(Calendar.MINUTE);
		if(!isValidPeriod())
			throw new IllegalArgumentException("The dates aren't in a valid order");
		this.decimalFormat = new DecimalFormat("00");
	}

	/**
	 * Used for deserialization.
	 *
	 * @param ois The stream of the file.
	 * @throws IOException If there is an error reading the file.
	 */
	private void readObject(final ObjectInputStream ois) throws IOException
	{
		int ver = ois.readInt();
		if(ver == 1)
		{
			this.startingHour = ois.readInt();
			this.startingMinute = ois.readInt();
			this.endingHour = ois.readInt();
			this.endingMinute = ois.readInt();
			this.day = ois.readInt();
		}
		this.calendar = Calendar.getInstance(Locale.getDefault());
		this.decimalFormat = new DecimalFormat("00");
	}

	/**
	 * Used to serialize.
	 *
	 * @param oos The stream of the file.
	 * @throws IOException If there is an error writing the file.
	 */
	private void writeObject(final ObjectOutputStream oos) throws IOException
	{
		oos.writeInt(SERIALIZATION_VERSION);
		oos.writeInt(startingHour);
		oos.writeInt(startingMinute);
		oos.writeInt(endingHour);
		oos.writeInt(endingMinute);
		oos.writeInt(day);
	}

	/**
	 * Used to get a String representing this interval.
	 *
	 * @return A string formatted as <b>xxHxx - yyHyy</b>
	 */
	public String getRawTimeInterval()
	{
		return this.startingHour + "H" + this.decimalFormat.format(this.startingMinute) + " - " + this.endingHour + "H" + this.decimalFormat.format(this.endingMinute);
	}

	/**
	 * Used to get the short name of the day sets.
	 *
	 * @return A string of teh days.
	 */
	private String getDaysText()
	{
		StringBuilder sb = new StringBuilder();
		if(isDaySet(MONDAY))
			sb.append(Utils.resourceBundle.getString("day_monday")).append(" ");
		if(isDaySet(TUESDAY))
			sb.append(Utils.resourceBundle.getString("day_tuesday")).append(" ");
		if(isDaySet(WEDNESDAY))
			sb.append(Utils.resourceBundle.getString("day_wednesday")).append(" ");
		if(isDaySet(THURSDAY))
			sb.append(Utils.resourceBundle.getString("day_thursday")).append(" ");
		if(isDaySet(FRIDAY))
			sb.append(Utils.resourceBundle.getString("day_friday")).append(" ");
		if(isDaySet(SATURDAY))
			sb.append(Utils.resourceBundle.getString("day_saturday")).append(" ");
		if(isDaySet(SUNDAY))
			sb.append(Utils.resourceBundle.getString("day_sunday")).append(" ");
		return sb.substring(0, sb.length()).trim();
	}

	/**
	 * Used to know if the date is in this period.
	 *
	 * @param date The date to verify.
	 * @return true if the date is in the period, false if not.
	 */
	public boolean isInPeriod(Date date)
	{
		this.calendar.setTime(date);
		int day = this.calendar.get(Calendar.DAY_OF_WEEK);
		int hours = this.calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = this.calendar.get(Calendar.MINUTE);
		if(!isCurrentDayCalendar(day))
			return false;
		if(this.startingHour == this.endingHour)
		{
			if(hours == this.startingHour)
				if(minutes >= this.startingMinute && minutes < this.endingMinute)
					return true;
		}
		else if(hours >= this.startingHour && hours <= this.endingHour)
			if(hours == this.startingHour)
			{
				if(minutes >= this.startingMinute)
					return true;
			}
			else if(hours == this.endingHour)
			{
				if(minutes <= this.endingMinute)
					return true;
			}
			else
				return true;
		return false;
	}

	/**
	 * Used to know if a day from {@link Calendar} is set.
	 *
	 * @param day The day to test.
	 * @return True if set, false if not.
	 */
	private boolean isCurrentDayCalendar(int day)
	{
		switch(day)
		{
			case Calendar.MONDAY:
				return isDaySet(MONDAY);
			case Calendar.TUESDAY:
				return isDaySet(TUESDAY);
			case Calendar.WEDNESDAY:
				return isDaySet(WEDNESDAY);
			case Calendar.THURSDAY:
				return isDaySet(THURSDAY);
			case Calendar.FRIDAY:
				return isDaySet(FRIDAY);
			case Calendar.SATURDAY:
				return isDaySet(SATURDAY);
			case Calendar.SUNDAY:
				return isDaySet(SUNDAY);
		}
		return false;
	}

	/**
	 * Used to know if a day from {@link Calendar} is set.
	 *
	 * @param day The day to test.
	 * @return True if set, false if not.
	 */
	private int getDayFromCalendar(int day)
	{
		switch(day)
		{
			case Calendar.MONDAY:
				return MONDAY;
			case Calendar.TUESDAY:
				return TUESDAY;
			case Calendar.WEDNESDAY:
				return WEDNESDAY;
			case Calendar.THURSDAY:
				return THURSDAY;
			case Calendar.FRIDAY:
				return FRIDAY;
			case Calendar.SATURDAY:
				return SATURDAY;
			case Calendar.SUNDAY:
				return SUNDAY;
		}
		return 0;
	}

	/**
	 * Used to know if two Period objects are overlapping.
	 *
	 * @param period The other Period to check with.
	 * @return true if overlapping, false if not.
	 */
	public boolean isOverlapped(Period period)
	{
		return period != null && period != this && isDaysOverlapped(period) && (period.isInPeriod(getStartingDate()) || period.isInPeriod(getEndingDate()) || isInPeriod(period.getStartingDate()) || isInPeriod(period.getEndingDate()));
	}

	/**
	 * Used to know if there are same days set in the period.
	 *
	 * @param period The period to test with.
	 * @return True if there is at leat one of the days that are the same, false if none.
	 */
	private boolean isDaysOverlapped(Period period)
	{
		return (period.getDay() & this.getDay()) != 0;
	}

	/**
	 * Use to get the ending date of this period in the current day.
	 *
	 * @return The ending date.
	 */
	private Date getEndingDate()
	{
		Calendar calen = Calendar.getInstance(Locale.getDefault());
		calen.setTime(new Date());
		calen.set(Calendar.HOUR_OF_DAY, this.endingHour);
		calen.set(Calendar.MINUTE, this.endingMinute);
		return calen.getTime();
	}

	/**
	 * Use to get the starting date of this period in the current day.
	 *
	 * @return The starting date.
	 */
	private Date getStartingDate()
	{
		Calendar calen = Calendar.getInstance(Locale.getDefault());
		calen.setTime(new Date());
		calen.set(Calendar.HOUR_OF_DAY, this.startingHour);
		calen.set(Calendar.MINUTE, this.startingMinute);
		return calen.getTime();
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Period)
			return isSame(o.toString());
		return o == this;
	}

	@Override
	public String toString()
	{
		return getRawTimeInterval().replaceAll(" ", "") + " (" + getDaysText() + ")";
	}

	/**
	 * Used to know if the periods are the same.
	 *
	 * @param name The period as string.
	 * @return True if the same, false if not.
	 */
	public boolean isSame(String name)
	{
		return this.toString().replaceAll(" ", "").equalsIgnoreCase(name.replaceAll(" ", ""));
	}

	/**
	 * Used to get the day.
	 *
	 * @return The day.
	 */
	public int getDay()
	{
		return day;
	}

	/**
	 * Used to know if a day is set.
	 *
	 * @param day The day to test.
	 * @return True if set, false if not.
	 */
	public boolean isDaySet(int day)
	{
		return (day & this.day) == day;
	}

	/**
	 * Used to know if the period is valid.
	 *
	 * @return True if valid, false if not.
	 */
	public boolean isValidPeriod()
	{
		if(endingHour < startingHour)
			return false;
		if(startingHour == endingHour)
		{
			if(endingMinute <= startingMinute)
				return false;
		}
		return true;
	}

	/**
	 * Used to get the starting hour.
	 *
	 * @return The starting hour.
	 */
	public int getStartingHour()
	{
		return startingHour;
	}

	/**
	 * Used to get the ending hour.
	 *
	 * @return The ending hour.
	 */
	public int getEndingHour()
	{
		return endingHour;
	}

	/**
	 * Used to get the starting minute.
	 *
	 * @return The starting minute.
	 */
	public int getStartingMinute()
	{
		return startingMinute;
	}

	/**
	 * Used to get the ending minute.
	 *
	 * @return The ending minute.
	 */
	public int getEndingMinute()
	{
		return endingMinute;
	}

	/**
	 * The duration of the period as a String.
	 *
	 * @return The duration.
	 */
	public String getDurationString()
	{
		return Utils.durationToString(getDuration());
	}

	/**
	 * Used to get the duration of the period.
	 *
	 * @return The duration in seconds.
	 */
	public long getDuration()
	{
		return getEndingDate().getTime() - getStartingDate().getTime();
	}
}
