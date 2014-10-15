package com.mega.credit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Message;

public class BlueToothReadCardThread extends Thread
{
	private InputStream mmInStream = null;
	private OutputStream mmOutStream = null;
	int Readflage = -99;
	byte[] cmd_SAM = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE };
	byte[] cmd_find = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22 };
	byte[] cmd_selt = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21 };
	byte[] cmd_read = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32 };
	byte[] cmd_sleep = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02 };
	byte[] cmd_weak = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03 };
	byte[] recData = new byte[1500];
	private MyHandler handler;
	private static Person person;
	private Thread thread = Thread.currentThread();
	private Timer timer;

	public BlueToothReadCardThread(InputStream mmInStream, OutputStream mmOutStream,MyHandler handler)
	{
		this.mmInStream = mmInStream;
		this.mmOutStream = mmOutStream;
		this.handler = handler;
	}

	private TimerTask readtask = new TimerTask()
	{

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			Message message6 = new Message();
			message6.what = 11;
			Bundle bundle = new Bundle();
			bundle.putString("error", "��ȡ����֤��ʱ�쳣��");
			message6.setData(bundle);
			handler.sendMessage(message6);
			thread.interrupt();
		}
	};

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		super.run();
		try
		{
			timer = new Timer();
			Thread.sleep(100);
			timer.schedule(readtask, 10000);
			Message message = new Message();
			message.what = 12;
			handler.sendMessage(message);
			ReadCard();
			if (Readflage > 0)
			{
				Message message1 = new Message();
				message1.what = 13;
				Bundle bundle = new Bundle();
				bundle.putSerializable("person", person);
				message1.setData(bundle);
				handler.sendMessage(message1);
				thread.interrupt();
			} else
			{
				if (Readflage == -2)
				{
					Message message2 = new Message();
					message2.what = 11;
					Bundle bundle = new Bundle();
					bundle.putString("error", "�����룺" + Readflage + "���豸�����쳣��");
					message2.setData(bundle);
					handler.sendMessage(message2);
					thread.interrupt();
				}
				if (Readflage == -3 || Readflage == -4)
				{
					Message message3 = new Message();
					message3.what = 11;
					Bundle bundle = new Bundle();
					bundle.putString("error", "�����룺" + Readflage + "���޿���Ƭ�Ѷ�����");
					message3.setData(bundle);
					handler.sendMessage(message3);
					thread.interrupt();
				}
				if (Readflage == -5)
				{
					Message message4 = new Message();
					message4.what = 11;
					Bundle bundle = new Bundle();
					bundle.putString("error", "�����룺" + Readflage + "������ʧ�ܣ�");
					message4.setData(bundle);
					handler.sendMessage(message4);
					thread.interrupt();
				}
				if (Readflage == -99)
				{
					Message message5 = new Message();
					message5.what = 11;
					Bundle bundle = new Bundle();
					bundle.putString("error", "�����룺" + Readflage + "�������쳣��");
					message5.setData(bundle);
					handler.sendMessage(message5);
					thread.interrupt();
				}
			}
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			timer.cancel();
			Message message8 = new Message();
			message8.what = 11;
			Bundle bundle = new Bundle();
			bundle.putString("error", "�����룺NumberFormatException���豸�����쳣��");
			message8.setData(bundle);
			handler.sendMessage(message8);
			thread.interrupt();
			// e.printStackTrace();
		}
		catch (InterruptedException e1)
		{
			// TODO Auto-generated catch block
			timer.cancel();
			Thread.currentThread().interrupt();
		}
	}

	private String decodeNation(int code)
	{
		String nation;
		switch (code)
		{
		case 1:
			nation = "��";
			break;
		case 2:
			nation = "�ɹ�";
			break;
		case 3:
			nation = "��";
			break;
		case 4:
			nation = "��";
			break;
		case 5:
			nation = "ά���";
			break;
		case 6:
			nation = "��";
			break;
		case 7:
			nation = "��";
			break;
		case 8:
			nation = "׳";
			break;
		case 9:
			nation = "����";
			break;
		case 10:
			nation = "����";
			break;
		case 11:
			nation = "��";
			break;
		case 12:
			nation = "��";
			break;
		case 13:
			nation = "��";
			break;
		case 14:
			nation = "��";
			break;
		case 15:
			nation = "����";
			break;
		case 16:
			nation = "����";
			break;
		case 17:
			nation = "������";
			break;
		case 18:
			nation = "��";
			break;
		case 19:
			nation = "��";
			break;
		case 20:
			nation = "����";
			break;
		case 21:
			nation = "��";
			break;
		case 22:
			nation = "�";
			break;
		case 23:
			nation = "��ɽ";
			break;
		case 24:
			nation = "����";
			break;
		case 25:
			nation = "ˮ";
			break;
		case 26:
			nation = "����";
			break;
		case 27:
			nation = "����";
			break;
		case 28:
			nation = "����";
			break;
		case 29:
			nation = "�¶�����";
			break;
		case 30:
			nation = "��";
			break;
		case 31:
			nation = "���Ӷ�";
			break;
		case 32:
			nation = "����";
			break;
		case 33:
			nation = "Ǽ";
			break;
		case 34:
			nation = "����";
			break;
		case 35:
			nation = "����";
			break;
		case 36:
			nation = "ë��";
			break;
		case 37:
			nation = "����";
			break;
		case 38:
			nation = "����";
			break;
		case 39:
			nation = "����";
			break;
		case 40:
			nation = "����";
			break;
		case 41:
			nation = "������";
			break;
		case 42:
			nation = "ŭ";
			break;
		case 43:
			nation = "���α��";
			break;
		case 44:
			nation = "����˹";
			break;
		case 45:
			nation = "���¿�";
			break;
		case 46:
			nation = "�°�";
			break;
		case 47:
			nation = "����";
			break;
		case 48:
			nation = "ԣ��";
			break;
		case 49:
			nation = "��";
			break;
		case 50:
			nation = "������";
			break;
		case 51:
			nation = "����";
			break;
		case 52:
			nation = "���״�";
			break;
		case 53:
			nation = "����";
			break;
		case 54:
			nation = "�Ű�";
			break;
		case 55:
			nation = "���";
			break;
		case 56:
			nation = "��ŵ";
			break;
		case 97:
			nation = "����";
			break;
		case 98:
			nation = "���Ѫͳ�й�����ʿ";
			break;
		default:
			nation = "";
			break;
		}
		return nation;
	}

	private void ReadCard()
	{
		try
		{
			if ((mmInStream == null) || (mmInStream == null))
			{
				Readflage = -2;// �����쳣
				return;
			}
			mmOutStream.write(cmd_find);
			Thread.sleep(200);
			int datalen = mmInStream.read(recData);
			if (recData[9] == -97)
			{
				mmOutStream.write(cmd_selt);
				Thread.sleep(200);
				datalen = mmInStream.read(recData);
				if (recData[9] == -112)
				{
					mmOutStream.write(cmd_read);
					Thread.sleep(1000);
					byte[] tempData = new byte[1500];
					if (mmInStream.available() > 0)
					{
						datalen = mmInStream.read(tempData);
					} else
					{
						Thread.sleep(500);
						if (mmInStream.available() > 0)
						{
							datalen = mmInStream.read(tempData);
						}
					}
					int flag = 0;
					if (datalen < 1294)
					{
						for (int i = 0; i < datalen; i++, flag++)
						{
							recData[flag] = tempData[i];
						}
						Thread.sleep(1000);
						if (mmInStream.available() > 0)
						{
							datalen = mmInStream.read(tempData);
						} else
						{
							Thread.sleep(500);
							if (mmInStream.available() > 0)
							{
								datalen = mmInStream.read(tempData);
							}
						}
						for (int i = 0; i < datalen; i++, flag++)
						{
							recData[flag] = tempData[i];
						}

					} else
					{
						for (int i = 0; i < datalen; i++, flag++)
						{
							recData[flag] = tempData[i];
						}
					}
					tempData = null;
					if (flag == 1295)
					{
						if (recData[9] == -112)
						{

							byte[] dataBuf = new byte[256];
							for (int i = 0; i < 256; i++)
							{
								dataBuf[i] = recData[14 + i];
							}
							String TmpStr = new String(dataBuf, "UTF16-LE");
							TmpStr = new String(TmpStr.getBytes("UTF-8"));
							person = new Person();
							person.setPeopleName(TmpStr.substring(0, 15));
							person.setPeopleSex(TmpStr.substring(15, 16).equals("1") ? "��" : "Ů");
							person.setPeopleNation(decodeNation(Integer.parseInt(TmpStr.substring(16, 18))));
							person.setPeopleBirthday(TmpStr.substring(18, 26));
							person.setPeopleAddress(TmpStr.substring(26, 61));
							person.setPeopleIDCode(TmpStr.substring(61, 79));
							person.setDepartment(TmpStr.substring(79, 94));
							person.setStartDate(TmpStr.substring(94, 102));
							person.setEndDate(TmpStr.substring(102, 110));

							// ��Ƭ����
							try
							{
								byte[] datawlt = new byte[1024];
								for (int i = 0; i < 1024; i++)
								{
									datawlt[i] = recData[i + 270];
								}
								person.setPhoto(datawlt);
								Readflage = 1;// �����ɹ�
							} catch (Exception e)
							{
								Readflage = 6;// ��Ƭ�����쳣
							}

						} else
						{
							Readflage = -5;// ����ʧ�ܣ�
						}
					} else
					{
						Readflage = -5;// ����ʧ��
					}
				} else
				{
					Readflage = -4;// ѡ��ʧ��
				}
			} else
			{
				Readflage = -3;// Ѱ��ʧ��
			}

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			Readflage = -99;// ��ȡ�����쳣
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			Readflage = -99;// ��ȡ�����쳣
		}
	}

}
