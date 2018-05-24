package serialException;

public class PortInUse extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortInUse()
	{
	}

	@Override
	public String toString()
	{
		return "指定端口正在使用中！";
	}
}
