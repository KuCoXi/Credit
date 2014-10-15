package org.apache.cordova.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.authentication.utils.ContextUtils;
import com.authentication.utils.DataUtils;
import com.mega.cardreader.AsyncParseSFZ;
import com.mega.credit.BlueToothReadCardThread;
import com.mega.credit.ElectronWriteName;
import com.mega.credit.MyConstants;
import com.mega.credit.MyHandler;
import com.mega.credit.Person;
import com.mega.credit.R;
import com.mega.credit.ReadCardThread;

public class HXiMateDeviceSDK extends CordovaPlugin implements OnClickListener
{
	private Thread thread;
	private ProgressDialog progressDialog;
	private AsyncParseSFZ asyncParseSFZ;
	private MyHandler myHandler;
	private ElectronWriteName eName;
	private LinearLayout lname;
	private Button cancel;
	private Button clear;
	private Button save;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	private AlertDialog.Builder readboard;
	private AlertDialog readboarddialog;
	private Button open;
	private Button setting;
	private Button read;
	private Button reflash;
	private Spinner readerlist;
	public static BluetoothAdapter mBluetoothAdapter = null;
	private Set<BluetoothDevice> pairedDevices;
	private ArrayAdapter<String> adapter;
	private String[] mItems;
	public static SharedPreferences spf;
	public static Editor editor;
	private BluetoothDevice device;
	public static BluetoothSocket mBTHSocket = null;
	public static InputStream mmInStream = null;
	public static OutputStream mmOutStream = null;
	UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private Timer timer;
	private CallbackContext cContext;
	static
	{
		spf = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getInstance().getApplicationContext());
		editor = spf.edit();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	/*远盈参数*/
	private SerialPort mserialPort;
	private InputStream yyInStream;
	private OutputStream yyOutStream;
	private int Readflage;
	private byte[] recData;
	private byte[] datawlt;
	private String[] decodeInfo;
	byte[] cmd_find = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22 };
	byte[] cmd_selt = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21 };
	byte[] cmd_read = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32 };
	byte[] cmd_sleep = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02 };
	byte[] cmd_weak = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03 };
	private Person person;
	/*远盈参数*/
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView)
	{
		// TODO Auto-generated method stub
		super.initialize(cordova, webView);
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException
	{
		// TODO Auto-generated method stub
		Log.i("本地插件标志", action);
//		 action = "signName";
		if (action.equals("readIdCard"))
		{
			if (MyConstants.spf.getInt("cardreader_type", 0) == 1)
			{
				/****** 测试数据 *****/
				progressDialog = new ProgressDialog(cordova.getActivity());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle("提示");
				progressDialog.setMessage("正在读取数据......");
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(false);
				myHandler = new MyHandler(callbackContext);
				progressDialog.show();
				new Thread(new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						try
						{
							Thread.sleep(1000);
							progressDialog.dismiss();
							Message msg = new Message();
							msg.what = 100;
							myHandler.sendMessage(msg);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				/****** 测试数据 *****/
			} else if (MyConstants.spf.getInt("cardreader_type", 0) == 2)
			{
				/****** 肯麦斯读二代证 *****
				try
				{
					progressDialog = new ProgressDialog(cordova.getActivity());
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.setTitle("提示");
					progressDialog.setMessage("正在读取数据......");
					progressDialog.setIndeterminate(false);
					progressDialog.setCancelable(false);
					myHandler = new MyHandler(callbackContext);
					asyncParseSFZ = new AsyncParseSFZ(ContextUtils.getInstance().getHandlerThread().getLooper(), ContextUtils.getInstance().getRootPath());
					asyncParseSFZ.setOnReadSFZListener(new OnReadSFZListener()
					{
						@Override
						public void onReadSuccess(People people)
						{
							progressDialog.dismiss();
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putSerializable("people", people);
							msg.setData(bundle);
							msg.what = 1;
							myHandler.sendMessage(msg);
						}

						@Override
						public void onReadFail(int confirmationCode)
						{
							progressDialog.dismiss();
							if (confirmationCode == ParseSFZAPI.Result.FIND_FAIL)
							{
								Message msg = new Message();
								msg.what = confirmationCode;
								myHandler.sendMessage(msg);
							} else if (confirmationCode == ParseSFZAPI.Result.TIME_OUT)
							{
								Message msg = new Message();
								msg.what = confirmationCode;
								myHandler.sendMessage(msg);
							} else if (confirmationCode == ParseSFZAPI.Result.OTHER_EXCEPTION)
							{
								Message msg = new Message();
								msg.what = confirmationCode;
								myHandler.sendMessage(msg);
							}
						}
					});
					SerialPortManager.getInstance().openSerialPort();
					progressDialog.show();
					asyncParseSFZ.readSFZ(ParseSFZAPI.SECOND_GENERATION_CARD);

				} catch (InvalidParameterException e)
				{
					// TODO Auto-generated catch block
					Log.e("readidcard", e.toString());
					Message msg = new Message();
					msg.what = 5;
					myHandler.sendMessage(msg);
				} catch (SecurityException e)
				{
					// TODO Auto-generated catch block
					Log.e("readidcard", e.toString());
					Message msg = new Message();
					msg.what = 5;
					myHandler.sendMessage(msg);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					Log.e("readidcard", e.toString());
					Message msg = new Message();
					msg.what = 5;
					myHandler.sendMessage(msg);
				}
				****** 肯麦斯读二代证 *****/
				
				/***** 远盈读二代证 *****/
				person = new Person();
				Readflage = -99;
				recData = new byte[1500];
				decodeInfo = new String[10];
				progressDialog = new ProgressDialog(cordova.getActivity());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle("提示");
				progressDialog.setMessage("正在读取数据......");
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(true);
				myHandler = new MyHandler(callbackContext,progressDialog);
				progressDialog.show();
				try
				{

					if (mserialPort == null)
					{
						mserialPort = new SerialPort(new File("/dev/ttySAC3"), 115200, 0);
					}
					if (mmInStream == null)
					{
						mmInStream = mserialPort.getInputStream();
					}
					if (mmOutStream == null)
					{
						mmOutStream = mserialPort.getOutputStream();
					}
					if (mmInStream != null && mmOutStream != null)
					{
						thread = new ReadCardThread(mmInStream, mmOutStream, myHandler);
						thread.start();
					} else
					{
						Message message = new Message();
						message.what = 14;
						myHandler.sendMessage(message);
					}

				} catch (SecurityException e)
				{
					// TODO Auto-generated catch block
					Message message = new Message();
					message.what = 14;
					myHandler.sendMessage(message);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					Message message = new Message();
					message.what = 14;
					myHandler.sendMessage(message);
				}
//				try
//				{
//					new Thread(new Runnable()
//					{
//						
//						@Override
//						public void run()
//						{
//							// TODO Auto-generated method stub
//							if (mserialPort==null)
//							{
//								
//								try
//								{
//									mserialPort = new SerialPort(new File("/dev/ttySAC3"), 115200, 0);
//									if (yyInStream==null)
//									{
//										yyInStream = mserialPort.getInputStream();
//									}
//									if (yyOutStream==null)
//									{
//										yyOutStream = mserialPort.getOutputStream();
//									}
//								} catch (SecurityException e)
//								{
//									// TODO Auto-generated catch block
//									Message message = new Message();
//									message.what = 14;
//									myHandler.sendMessage(message);
//									
//								} catch (IOException e)
//								{
//									// TODO Auto-generated catch block
//									Message message = new Message();
//									message.what = 14;
//									myHandler.sendMessage(message);
//								}
//							}
//							if (yyInStream!=null&&yyOutStream!=null)
//							{
//								person = getData();
//								if (person.getErrcode()==-1)
//								{
//									Message message = new Message();
//									Bundle bundle = new Bundle();
//									bundle.putSerializable("human", person);
//									message.setData(bundle);
//									message.what = 15;
//									myHandler.sendMessage(message);
//								}
//								else {
//									Message message = new Message();
//									Bundle bundle = new Bundle();
//									bundle.putSerializable("human", person);
//									message.setData(bundle);
//									message.what = 16;
//									myHandler.sendMessage(message);
//								}
//							}
//							
//						}
//					}).start();
//					
//				} catch (SecurityException e)
//				{
//					// TODO Auto-generated catch block
//					Message message = new Message();
//					message.what = 1;
//					myHandler.sendMessage(message);
//				}
			
				/***** 远盈读二代证 *****/

			} else
			{
				/*** 凯斯泰尔读二代证 ***/
				timer = new Timer();
				final CallbackContext context = callbackContext;
				if (mBluetoothAdapter == null)
				{
					Toast.makeText(cordova.getActivity(), "该设备无蓝牙，无法读取二代证信息", Toast.LENGTH_SHORT).show();
				} else
				{
					pairedDevices = mBluetoothAdapter.getBondedDevices();
					mItems = new String[pairedDevices.size()];
					int i = 0;
					if (pairedDevices.size() > 0)
					{
						// findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
						for (BluetoothDevice device : pairedDevices)
						{
							mItems[i] = device.getName() + " (已配对)\n" + device.getAddress();
							i++;
						}
					}
					View view = LayoutInflater.from(cordova.getActivity()).inflate(R.layout.readboard, null);
					open = (Button) view.findViewById(R.id.bt_open_bluetooth);
					setting = (Button) view.findViewById(R.id.bt_bluetooth_setting);
					read = (Button) view.findViewById(R.id.bt_read);
					reflash = (Button) view.findViewById(R.id.bt_reflash);
					readerlist = (Spinner) view.findViewById(R.id.sp_default_reader);
					adapter = new ArrayAdapter<String>(cordova.getActivity(), android.R.layout.simple_spinner_item, mItems);
					adapter.setDropDownViewResource(R.drawable.dropdown_style);
					readerlist.setAdapter(adapter);
					int position = getPosition(mItems, spf.getString("default_reader", ""));
					if (position != -1)
					{
						readerlist.setSelection(position, true);
					}
					readerlist.setOnItemSelectedListener(new OnItemSelectedListener()
					{

						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
						{
							// TODO Auto-generated method stub
							String address = adapter.getItem(position).toString();
							editor.putString("default_reader", address);
							editor.commit();
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent)
						{
							// TODO Auto-generated method stub

						}
					});
					readboard = new AlertDialog.Builder(cordova.getActivity());
					readboarddialog = readboard.setView(view).create();
					open.setOnClickListener(this);
					setting.setOnClickListener(this);
					reflash.setOnClickListener(this);
					read.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							// TODO Auto-generated method stub
							if (!mBluetoothAdapter.isEnabled())
							{
								Toast.makeText(cordova.getActivity(), "蓝牙未打开！", Toast.LENGTH_SHORT).show();
							} else
							{
								progressDialog = new ProgressDialog(cordova.getActivity());
								progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								progressDialog.setTitle("提示");
								progressDialog.setMessage("正在连接设备......");
								progressDialog.setIndeterminate(false);
								progressDialog.setCancelable(false);
								myHandler = new MyHandler(context, progressDialog, readboarddialog);
								if (readerlist.getSelectedItem() != null)
								{
									String add = readerlist.getSelectedItem().toString();
									editor.putString("default_reader", add);
									editor.commit();
									add = add.substring(add.length() - 17);
									device = mBluetoothAdapter.getRemoteDevice(add);
									progressDialog.show();
									new Thread(new Runnable()
									{

										@Override
										public void run()
										{
											// TODO Auto-generated method stub
											try
											{
												Thread.sleep(100);
												TimerTask conntask = new TimerTask()
												{

													@Override
													public void run()
													{
														// TODO Auto-generated
														// method stub
														if (mmInStream == null || mmOutStream == null)
														{
															Message message = new Message();
															message.what = 11;
															Bundle bundle = new Bundle();
															bundle.putString("error", "连接设备超时异常！");
															message.setData(bundle);
															myHandler.sendMessage(message);
															Thread.currentThread().interrupt();
														}
													}
												};
												if (mmInStream != null && mmInStream != null)
												{
													System.out.println("哈哈");
													new BlueToothReadCardThread(mmInStream, mmOutStream,myHandler).start();
												}else {
													System.out.println("哈哈1");
													timer.schedule(conntask, 15000);
//													if (mBTHSocket==null)
//													{
//														mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//													}
													int sdk = Build.VERSION.SDK_INT;
													if (sdk >= 10)
													{
														mBTHSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
													} else
													{
														mBTHSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
													}
													mBTHSocket.connect();
													mmInStream = mBTHSocket.getInputStream();
													mmOutStream = mBTHSocket.getOutputStream();
													if (mmInStream != null && mmInStream != null)
													{
														new BlueToothReadCardThread(mmInStream, mmOutStream,myHandler).start();
													}
												}

											} catch (NumberFormatException e)
											{
												// TODO Auto-generated catch
												// block
												timer.cancel();
												Message message = new Message();
												message.what = 11;
												Bundle bundle = new Bundle();
												bundle.putString("error", "错误码：NumberFormatException，连接设备异常！");
												message.setData(bundle);
												myHandler.sendMessage(message);
											} catch (IOException e)
											{
												// TODO Auto-generated catch
												// block
												timer.cancel();
												Message message = new Message();
												message.what = 11;
												Bundle bundle = new Bundle();
												bundle.putString("error", "错误码：IOException，连接设备异常！");
												message.setData(bundle);
												myHandler.sendMessage(message);
											} catch (InterruptedException e)
											{
												// TODO Auto-generated catch
												// block
												timer.cancel();
												Thread.currentThread().interrupt();
											}
										}
									}).start();
								} else
								{
									Toast.makeText(cordova.getActivity(), "未选择默认蓝牙设备", Toast.LENGTH_SHORT).show();
								}

							}
						}
					});
					readboarddialog.show();
				}

				/*** 凯斯泰尔读二代证 ***/
			}

		} else if (action.equals("signName"))
		{
			cContext = callbackContext;
			View view = LayoutInflater.from(cordova.getActivity()).inflate(R.layout.handsign, null);
			cancel = (Button) view.findViewById(R.id.btQuit);
			clear = (Button) view.findViewById(R.id.btClear);
			save = (Button) view.findViewById(R.id.btSave);
			cancel.setOnClickListener(this);
			clear.setOnClickListener(this);
			save.setOnClickListener(this);
			lname = (LinearLayout) view.findViewById(R.id.WriteUserNameSpace);
			eName = new ElectronWriteName(cordova.getActivity());
			lname.addView(eName);
			builder = new AlertDialog.Builder(cordova.getActivity());
			dialog = builder.setCancelable(false).setView(view).create();
			dialog.show();
		} else if (action.equals("")) {
			
		} else
		{
			return false;
		}
		return true;
	}


	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btClear:
			if (eName != null)
			{
				eName = null;
				lname.removeAllViews();
				eName = new ElectronWriteName(cordova.getActivity());
				lname.addView(eName);
				lname.invalidate();
			}
			break;
		case R.id.btQuit:
			dialog.cancel();
			break;
		case R.id.btSave:
			try
			{
				takeScreenShot(eName, cContext);
				dialog.cancel();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.bt_bluetooth_setting:
			Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
			cordova.getActivity().startActivity(intent);
			break;
		case R.id.bt_open_bluetooth:
			if (mBluetoothAdapter.isEnabled())
			{
				Toast.makeText(cordova.getActivity(), "蓝牙已打开！", Toast.LENGTH_SHORT).show();
			}
			mBluetoothAdapter.enable();
			break;
		case R.id.bt_reflash:
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what)
			{
			case 1:
				pairedDevices = mBluetoothAdapter.getBondedDevices();
				mItems = new String[pairedDevices.size()];
				int i = 0;
				if (pairedDevices.size() > 0)
				{
					// findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
					for (BluetoothDevice device : pairedDevices)
					{
						mItems[i] = device.getName() + " (已配对)\n" + device.getAddress();
						i++;
					}
				}
				adapter = new ArrayAdapter<String>(cordova.getActivity(), android.R.layout.simple_spinner_item, mItems);
				adapter.setDropDownViewResource(R.drawable.dropdown_style);
				readerlist.setAdapter(adapter);
				int position = getPosition(mItems, spf.getString("default_reader", ""));
				if (position != -1)
				{
					readerlist.setSelection(position, true);
				}
				readerlist.setOnItemSelectedListener(new OnItemSelectedListener()
				{

					@Override
					public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
					{
						// TODO Auto-generated method stub
						String address = adapter.getItem(position).toString();
						editor.putString("default_reader", address);
						editor.commit();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent)
					{
						// TODO Auto-generated method stub

					}
				});
				break;

			default:
				break;
			}
		}

	};

	private int getPosition(String[] data, String item)
	{
		int p = 0;
		int count = 0;
		for (int i = 0; i < data.length; i++)
		{
			if (data[i].equals(item))
			{
				p = i;
				count++;
			}
		}
		if (count == 0)
		{
			return -1;
		}
		return p;
	}
	
	public Uri takeScreenShot(View view,CallbackContext context) throws Exception
	{
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();
		FileOutputStream fos = null;
		try
		{
			String PIC_DIR = Environment.getExternalStorageDirectory()+"/mega";
			String fileName = Environment.getExternalStorageDirectory()+"/mega/"+"sign.png";
			if (!new File(PIC_DIR).exists())
			{
				new File(PIC_DIR).mkdir();
			}
			fos = new FileOutputStream(fileName);
			if (fos != null)
			{
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
			JSONObject r = new JSONObject();
			r.put("signURL", Uri.fromFile(new File(fileName)));
			String string = r.toString();
			string = string.substring(1, string.length() - 1);
			context.sendPluginResult(new PluginResult(PluginResult.Status.OK, string));
			context.success();
		} catch (Exception e)
		{
			// e.printStackTrace();
			System.out.println(e);
		}
		return null;

//		invalidateLayoout();
	}
	
	private Person getData()
	{
		Person person = new Person();
		int readcount = 15;
		try
		{
			while (readcount > 1)
			{
				ReadCard();
				readcount = readcount - 1;
				if (Readflage > 0)
				{
					readcount = 0;
					person.setPeopleName(decodeInfo[0]);
					person.setPeopleSex(decodeInfo[1]);
					person.setPeopleNation(decodeInfo[2]);
					person.setPeopleBirthday(decodeInfo[3]);
					person.setPeopleAddress(decodeInfo[4]);
					person.setPeopleIDCode(decodeInfo[5]);
					person.setDepartment(decodeInfo[6]);
					person.setStartDate(decodeInfo[7]);
					person.setEndDate(decodeInfo[8]);
					person.setPhoto(datawlt);

				} else
				{
//					image.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
					if (Readflage == -2)
					{
						person.setErrcode(0);
						person.setErrmsg("蓝牙连接异常");
					}
					if (Readflage == -3)
					{
						person.setErrcode(0);
						person.setErrmsg("无卡或卡片已读过");
					}
					if (Readflage == -4)
					{
						person.setErrcode(0);
						person.setErrmsg("无卡或卡片已读过");
					}
					if (Readflage == -5)
					{
						person.setErrcode(0);
						person.setErrmsg("读卡失败");
					}
					if (Readflage == -99)
					{
						person.setErrcode(0);
						person.setErrmsg("操作异常");
					}
				}
				Thread.sleep(100);
			}

		} catch (InterruptedException e)
		{
			// CDialog.dismissDialog();
		}
		// CDialog.dismissDialog();
		return person;
	}

	private void ReadCard()
	{
		try
		{
			if ((yyInStream == null) || (yyOutStream == null))
			{
				Readflage = -2;// 连接异常
				return;
			}
			yyOutStream.write(cmd_find);
			Thread.sleep(200);
			int datalen = yyInStream.read(recData);
			System.out.println(">>>>>datalen111--->" + datalen);
			if (recData[9] == -97)
			{
				yyOutStream.write(cmd_selt);
				Thread.sleep(200);
				datalen = yyInStream.read(recData);
				System.out.println(">>>>>datalen222--->" + datalen);
				if (recData[9] == -112)
				{
					yyOutStream.write(cmd_read);
					Thread.sleep(1000);
					byte[] tempData = new byte[1500];
					if (yyInStream.available() > 0)
					{
						datalen = yyInStream.read(tempData);
						System.out.println(">>>>>datalen333--->" + datalen);
					} else
					{
						Thread.sleep(500);
						if (yyInStream.available() > 0)
						{
							datalen = yyInStream.read(tempData);
							System.out.println(">>>>>datalen333>>>--->>>" + datalen);
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
						if (yyInStream.available() > 0)
						{
							datalen = yyInStream.read(tempData);
						} else
						{
							Thread.sleep(500);
							if (yyInStream.available() > 0)
							{
								datalen = yyInStream.read(tempData);
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
							// System.out.println("readData"+flag+">>>>"+recData[flag]);
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
							decodeInfo[0] = TmpStr.substring(0, 15);
							decodeInfo[1] = TmpStr.substring(15, 16);
							decodeInfo[2] = TmpStr.substring(16, 18);
							decodeInfo[3] = TmpStr.substring(18, 26);
							decodeInfo[4] = TmpStr.substring(26, 61);
							decodeInfo[5] = TmpStr.substring(61, 79);
							decodeInfo[6] = TmpStr.substring(79, 94);
							decodeInfo[7] = TmpStr.substring(94, 102);
							decodeInfo[8] = TmpStr.substring(102, 110);
							decodeInfo[9] = TmpStr.substring(110, 128);
							if (decodeInfo[1].equals("1"))
								decodeInfo[1] = "男";
							else
								decodeInfo[1] = "女";
							try
							{
								int code = Integer.parseInt(decodeInfo[2].toString());
								decodeInfo[2] = DataUtils.decodeNation(code);
							} catch (Exception e)
							{
								decodeInfo[2] = "";
							}
							for (int i = 0; i < decodeInfo.length; i++)
							{
								System.out.println(decodeInfo[i]);
							}
							try
							{
								datawlt = new byte[1024];
								for (int i = 0; i < 1024; i++)
								{
									datawlt[i] = recData[i + 270];
								}
							} catch (Exception e)
							{
								// TODO Auto-generated catch block
								Readflage = 6;// 照片解码异常
							}
							Readflage = 1;

						} else
						{
							Readflage = -5;// 读卡失败！
						}
					} else
					{
						Readflage = -5;// 读卡失败
					}
				} else
				{
					Readflage = -4;// 选卡失败
				}
			} else
			{
				Readflage = -3;// 寻卡失败
			}

		} catch (Exception e)
		{
			Readflage = -99;// 读取数据异常
			// CDialog.dismissDialog();
		}
		// CDialog.dismissDialog();
	}
}
