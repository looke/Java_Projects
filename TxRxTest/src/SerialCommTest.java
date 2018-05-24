import java.util.ArrayList;

import javax.swing.JOptionPane;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serialException.NoSuchPort;
import serialException.NotASerialPort;
import serialException.PortInUse;
import serialException.ReadDataFromSerialPortFailure;
import serialException.SerialPortInputStreamCloseFailure;
import serialException.SerialPortParameterFailure;
import serialException.TooManyListeners;



public class SerialCommTest
{

	private static SerialPort serialPort = null;    //���洮�ڶ���
	
	public static void main(String[] args)
	{
		ArrayList<String> commList;
		// TODO Auto-generated method stub
		SerialTool serialObject = SerialTool.getSerialTool();
		
		commList = serialObject.findPort();
		
		int commSize = commList.size();
		String commName = "COM3";
		int bps = 9600;
		
		try
		{
			serialPort = SerialTool.openPort(commName, bps);
			//�ڸô��ڶ�������Ӽ�����
			SerialTool.addListener(serialPort, new SerialPortListener(serialPort));
			//�����ɹ�������ʾ
			JOptionPane.showMessageDialog(null, "�����ɹ����Ժ���ʾ������ݣ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1)
		{
			
			//��������ʱʹ��һ��Dialog��ʾ����Ĵ�����Ϣ
            JOptionPane.showMessageDialog(null, e1, "����", JOptionPane.INFORMATION_MESSAGE);
		}
        
	}

	
	
	/**
	 * ���ڲ�����ʽ����һ�����ڼ�����
	 * 
	 * @author zhong
	 *
	 */
	private class SerialListener implements SerialPortEventListener
	{

		/**
		 * �����ص��Ĵ����¼�
		 */
		public void serialEvent(SerialPortEvent serialPortEvent)
		{

			switch (serialPortEvent.getEventType())
			{

			case SerialPortEvent.BI: // 10 ͨѶ�ж�
				break;

			case SerialPortEvent.OE: // 7 ��λ�����������

			case SerialPortEvent.FE: // 9 ֡����

			case SerialPortEvent.PE: // 8 ��żУ�����

			case SerialPortEvent.CD: // 6 �ز����

			case SerialPortEvent.CTS: // 3 �������������

			case SerialPortEvent.DSR: // 4 ����������׼������

			case SerialPortEvent.RI: // 5 ����ָʾ

			case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 ��������������
				break;

			case SerialPortEvent.DATA_AVAILABLE: // 1 ���ڴ��ڿ�������

				// System.out.println("found data");
				byte[] data = null;
				
				
				try
				{
					if (serialPort == null)
					{
						//JOptionPane.showMessageDialog(null, "���ڶ���Ϊ�գ�����ʧ�ܣ�", "����", JOptionPane.INFORMATION_MESSAGE);
					} 
					else
					{
						data = SerialTool.readFromPort(serialPort); // ��ȡ���ݣ������ֽ�����
						// System.out.println(new String(data));

						// �Զ���������̣�����ʵ��ʹ�ù����п��԰����Լ��������ڽ��յ����ݺ�����ݽ��н���
						if (data == null || data.length < 1)
						{ // ��������Ƿ��ȡ��ȷ
							//JOptionPane.showMessageDialog(null, "��ȡ���ݹ�����δ��ȡ����Ч���ݣ������豸�����", "����",
							//		JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						} 
						else
						{
							String dataOriginal = new String(data); // ���ֽ���������ת��λΪ������ԭʼ���ݵ��ַ���
							String dataValid = ""; // ��Ч���ݣ���������ԭʼ�����ַ���ȥ���ͷ*���Ժ���ַ�����
							String[] elements = null; // �������水�ո���ԭʼ�ַ�����õ����ַ�������
							// ��������
							if (dataOriginal.charAt(0) == '*')
							{ // �����ݵĵ�һ���ַ���*��ʱ��ʾ���ݽ�����ɣ���ʼ����
								dataValid = dataOriginal.substring(1);
								elements = dataValid.split(" ");
								if (elements == null || elements.length < 1)
								{ // ��������Ƿ������ȷ
									//JOptionPane.showMessageDialog(null, "���ݽ������̳��������豸�����", "����",
									//		JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								} else
								{
									try
									{
										// ���½���Labelֵ
										
										   //for (int i=0; i<elements.length; i++) { System.out.println(elements[i]); }
										 
										// System.out.println("win_dir: " + elements[5]);
										//tem.setText(elements[0] + " ��");
										//hum.setText(elements[1] + " %");
										//pa.setText(elements[2] + " hPa");
										//rain.setText(elements[3] + " mm");
										//win_sp.setText(elements[4] + " m/s");
										//win_dir.setText(elements[5] + " ��");
									} 
									catch (ArrayIndexOutOfBoundsException e)
									{
										JOptionPane.showMessageDialog(null, "���ݽ������̳������½�������ʧ�ܣ������豸�����", "����",
												JOptionPane.INFORMATION_MESSAGE);
										System.exit(0);
									}
								}
							}
						}

					}

				} 
				catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e)
				{
					JOptionPane.showMessageDialog(null, e, "����", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0); // ������ȡ����ʱ��ʾ������Ϣ���˳�ϵͳ
				}
				
				break;

			}

		}
	}
	
	
	
}


