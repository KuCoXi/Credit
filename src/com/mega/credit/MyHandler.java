package com.mega.credit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import android_serialport_api.ParseSFZAPI;
import android_serialport_api.ParseSFZAPI.People;

import com.authentication.utils.ContextUtils;

public class MyHandler extends Handler
{
	private CallbackContext callbackContext;
	private ProgressDialog progressDialog;
	private AlertDialog dialog;

	public MyHandler(CallbackContext callbackContext)
	{
		this.callbackContext = callbackContext;
	}
	
	public MyHandler(CallbackContext callbackContext,ProgressDialog progressDialog)
	{
		this.callbackContext = callbackContext;
		this.progressDialog = progressDialog;
	}
	
	public MyHandler(CallbackContext callbackContext,ProgressDialog progressDialog,AlertDialog dialog)
	{
		this.callbackContext = callbackContext;
		this.progressDialog = progressDialog;
		this.dialog = dialog;
	}
	
	@Override
	public void handleMessage(Message msg)
	{
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch (msg.what)
		{
		/**����˹**/
		case 1:
			try
			{
				Bundle bundle = msg.getData();
				People people = (People) bundle.getSerializable("people");
				JSONObject r = new JSONObject();
				r.put("idnumber", people.getPeopleIDCode());
				r.put("name", people.getPeopleName());
				r.put("address", people.getPeopleAddress());
				r.put("sex", people.getPeopleSex());
				r.put("birthday_year", people.getPeopleBirthday().substring(0, 4));
				r.put("birthday_month", people.getPeopleBirthday().substring(4, 6));
				r.put("birthday_day", people.getPeopleBirthday().substring(6, 8));
				String validdate = people.getStartDate().substring(0, 4) + "." + people.getStartDate().substring(4, 6) + "." + people.getStartDate().substring(6, 8) + "-" + people.getEndDate().substring(0, 4) + "." + people.getEndDate().substring(4, 6) + "." + people.getEndDate().substring(6, 8);
				r.put("validdate", validdate);
				r.put("issuing", people.getDepartment());
				r.put("nation", people.getPeopleNation());
				r.put("photodata", Base64.encodeToString(people.getHeadImage(), Base64.DEFAULT));
				String string = r.toString();
				string = string.substring(1, string.length() - 1);
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, string));
				callbackContext.success();
				Toast.makeText(ContextUtils.getInstance(), "������֤�ɹ���", Toast.LENGTH_SHORT).show();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				callbackContext.error("JSON��������");
			}
			break;
		case ParseSFZAPI.Result.FIND_FAIL:
			callbackContext.error("δѰ����,�з������ݣ�");
			break;
		case ParseSFZAPI.Result.TIME_OUT:
			callbackContext.error("δѰ����,�޷������ݣ���ʱ��");
			break;
		case ParseSFZAPI.Result.OTHER_EXCEPTION:
			callbackContext.error("�����Ǵ��ڴ�ʧ�ܻ������쳣��");
			break;
		case 5:
			callbackContext.error("�����Ǵ��ڴ�ʧ�ܻ������쳣��");
			break;
		/**����˹**/
		case 100:
			/***** ���� ****/
			try
			{
				JSONObject r = new JSONObject();
				r.put("idnumber", "35220319860102105X");
				r.put("name", "��ï��");
				r.put("address", "����ʡ�����й��������Ѵ�ţ��19��");
				r.put("sex", "��");
				r.put("birthday_year", "1986");
				r.put("birthday_month", "01");
				r.put("birthday_day", "02");
				r.put("validdate", "2011.02.23-2021.02.23");
				r.put("issuing", "�����й�����");
				r.put("photodata", getAssetsFileString("filename.txt"));
				r.put("nation", "��");
				String string = r.toString();
				System.out.println(string);
				string = string.substring(1, string.length() - 1);
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, string));
				callbackContext.success();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				callbackContext.error("JSON�쳣��");
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
				callbackContext.error("JSON�쳣��");
			}
			break;
		/***** ���� ****/
			
		/***** ��˹̩�� ****/
		case 11:
			progressDialog.dismiss();
			dialog.cancel();
			Bundle error_msg = msg.getData();
			callbackContext.error(error_msg.getString("error"));
			break;
		case 12:
			progressDialog.setMessage("�豸���ӳɹ������ڶ�����֤......");
			break;
		case 13:
			progressDialog.dismiss();
			dialog.cancel();
			try
			{
				Bundle bundle = msg.getData();
				Person person = (Person) bundle.getSerializable("person");
				JSONObject r = new JSONObject();
				r.put("idnumber", person.getPeopleIDCode());
				r.put("name", person.getPeopleName());
				r.put("address", person.getPeopleAddress());
				r.put("sex", person.getPeopleSex());
				r.put("birthday_year", person.getPeopleBirthday().substring(0, 4));
				r.put("birthday_month", person.getPeopleBirthday().substring(4, 6));
				r.put("birthday_day", person.getPeopleBirthday().substring(6, 8));
				String validdate = person.getStartDate().substring(0, 4) + "." + person.getStartDate().substring(4, 6) + "." + person.getStartDate().substring(6, 8) + "-" + person.getEndDate().substring(0, 4) + "." + person.getEndDate().substring(4, 6) + "." + person.getEndDate().substring(6, 8);
				r.put("validdate", validdate);
				r.put("issuing", person.getDepartment());
				r.put("nation", person.getPeopleNation());
				r.put("photodata", Base64.encodeToString(person.getPhoto(), Base64.DEFAULT));
				String string = r.toString();
				string = string.substring(1, string.length() - 1);
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, string));
				callbackContext.success();
				Toast.makeText(ContextUtils.getInstance(), "������֤�ɹ���", Toast.LENGTH_SHORT).show();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				callbackContext.error("JSON�쳣��");
			}
			break;
		/***** ��˹̩�� ****/
			
		/***** Զӯ ****/
		case 14:
			progressDialog.dismiss();
			callbackContext.error("���ڴ�ʧ�ܣ�");
			break;
		case 15:
			progressDialog.dismiss();
			try
			{
				Bundle bundle = msg.getData();
				Person person = (Person) bundle.getSerializable("human");
				JSONObject r = new JSONObject();
				r.put("idnumber", person.getPeopleIDCode());
				r.put("name", person.getPeopleName());
				r.put("address", person.getPeopleAddress());
				r.put("sex", person.getPeopleSex());
				r.put("birthday_year", person.getPeopleBirthday().substring(0, 4));
				r.put("birthday_month", person.getPeopleBirthday().substring(4, 6));
				r.put("birthday_day", person.getPeopleBirthday().substring(6, 8));
				String validdate = person.getStartDate().substring(0, 4) + "." + person.getStartDate().substring(4, 6) + "." + person.getStartDate().substring(6, 8) + "-" + person.getEndDate().substring(0, 4) + "." + person.getEndDate().substring(4, 6) + "." + person.getEndDate().substring(6, 8);
				r.put("validdate", validdate);
				r.put("issuing", person.getDepartment());
				r.put("nation", person.getPeopleNation());
				r.put("photodata", Base64.encodeToString(person.getPhoto(), Base64.DEFAULT));
				String string = r.toString();
				string = string.substring(1, string.length() - 1);
				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, string));
				callbackContext.success();
				Toast.makeText(ContextUtils.getInstance(), "������֤�ɹ���", Toast.LENGTH_SHORT).show();
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				callbackContext.error("JSON�쳣��");
			}
			break;
		case 16:
			progressDialog.dismiss();
			Bundle bundle = msg.getData();
			String error_str = bundle.getString("error");
//			Person person = (Person) bundle.getSerializable("human");
			callbackContext.error(error_str);
			break;
		/***** Զӯ ****/
		default:
			break;
		}
	}

	public static String ReadTxtFile(String strFilePath)
	{
		String path = strFilePath;
		String content = ""; // �ļ������ַ���
		// ���ļ�
		File file = new File(path);
		// ���path�Ǵ��ݹ����Ĳ�����������һ����Ŀ¼���ж�
		if (file.isDirectory())
		{
			Log.d("TestFile", "The File doesn't not exist.");
		} else
		{
			try
			{
				InputStream instream = new FileInputStream(file);
				if (instream != null)
				{
					InputStreamReader inputreader = new InputStreamReader(instream);
					BufferedReader buffreader = new BufferedReader(inputreader);
					String line;
					// ���ж�ȡ
					while ((line = buffreader.readLine()) != null)
					{
						content += line + "\n";
					}
					instream.close();
				}
			} catch (java.io.FileNotFoundException e)
			{
				Log.d("TestFile", "The File doesn't not exist.");
			} catch (IOException e)
			{
				Log.d("TestFile", e.getMessage());
			}
		}
		return content;
	}
	
	private String getAssetsFileString(String fileName)
	{
		String res="";   
		try{   
		  
		   //�õ���Դ�е�asset������  
		   InputStream in = ContextUtils.getInstance().getResources().getAssets().open(fileName);   
		  
		   int length = in.available();           
		   byte [] buffer = new byte[length];          
		  
		   in.read(buffer);              
		   in.close();  
		   res = EncodingUtils.getString(buffer, "UTF-8");       
		  
		  }catch(Exception e){   
		  
		      e.printStackTrace();           
		  
		   }  
		return res;
	}

}
