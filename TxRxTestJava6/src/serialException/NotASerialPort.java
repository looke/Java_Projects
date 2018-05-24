package serialException;

public class NotASerialPort extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotASerialPort()
	{
	}

	@Override
	public String toString()
	{
		return "指定端口不是串行端口！";
	}
}
