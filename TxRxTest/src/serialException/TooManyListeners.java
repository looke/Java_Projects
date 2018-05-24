package serialException;

public class TooManyListeners extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TooManyListeners()
	{
	}

	@Override
	public String toString()
	{
		return "设置串口监听失败！已有过多监听器存在！";
	}
}
