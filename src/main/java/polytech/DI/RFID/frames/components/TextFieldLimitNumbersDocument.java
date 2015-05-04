package polytech.DI.RFID.frames.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Used to set an JTextArea accept only numbers.
 *
 * @author COLEAU Victor, COUCHOUD Thomas
 */
public class TextFieldLimitNumbersDocument extends PlainDocument
{
	private static final long serialVersionUID = 1L;
	private final int limit;

	/**
	 * Constructor.
	 *
	 * @param limit The maximum of digits the user can write.
	 */
	public TextFieldLimitNumbersDocument(int limit)
	{
		super();
		this.limit = limit;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
	{
		if(getLength() + str.length() <= this.limit && isNumber(str))
			try
			{
				Integer.parseInt(str);
				super.insertString(offset, str, attr);
			}
			catch(Exception ignored)
			{
			}
	}

	/**
	 * Used to know if the string is a number.
	 *
	 * @param str The string.
	 * @return True if it is a number, false if not.
	 */
	private boolean isNumber(String str)
	{
		boolean verif = true;
		for(char c : str.toCharArray())
			verif &= c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
		return verif;
	}
}