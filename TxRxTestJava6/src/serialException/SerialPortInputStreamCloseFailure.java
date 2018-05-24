package serialException;

public class SerialPortInputStreamCloseFailure extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerialPortInputStreamCloseFailure()
	{
	}

	@Override
	public String toString()
	{
		return "端口输入流关闭失败！";
	}
}
