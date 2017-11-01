package looke.sensor.data.parse;

public class DataPair
{
	String name;
	String value;
	
	public DataPair(String input_name, String input_value)
	{
		this.name = input_name;
		this.value = input_value;
	}
	
	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}
}
