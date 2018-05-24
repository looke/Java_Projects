package serialException;

public class SerialPortOutputStreamCloseFailure extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerialPortOutputStreamCloseFailure()
	{
	}

	@Override
	public String toString()
	{
		return "端口输出流关闭失败！";
	}
}
