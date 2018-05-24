package serialException;

public class ReadDataFromSerialPortFailure extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReadDataFromSerialPortFailure()
	{
	}

	@Override
	public String toString()
	{
		return "从端口读取数据失败！";
	}
}
