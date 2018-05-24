package serialException;

public class NoSuchPort extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchPort()
	{
	}

	@Override
	public String toString()
	{
		return "指定端口不存在！";
	}
}
