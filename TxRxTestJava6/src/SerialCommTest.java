import java.util.ArrayList;



public class SerialCommTest
{

	public static void main(String[] args)
	{
		ArrayList<String> commList;
		// TODO Auto-generated method stub
		SerialTool serialObject = SerialTool.getSerialTool();
		commList = serialObject.findPort();
		
		int commSize = commList.size();
	}

}
