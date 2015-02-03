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
import android.content.Context;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.Toast;
import android_serialport_api.SerialPort;

import com.authentication.utils.ContextUtils;
import com.mega.credit.BlueToothReadCardThread;
import com.mega.credit.ElectronWriteName;
import com.mega.credit.GTReadCardThread;
import com.mega.credit.MyConstants;
import com.mega.credit.MyHandler;
import com.mega.credit.R;
import com.mega.credit.ReadCardThread;

public class HXiMateDeviceSDK extends CordovaPlugin implements OnClickListener
{
	private Thread thread;
	private ProgressDialog progressDialog;
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
	private CallbackContext callbackContext;
	static
	{
		spf = PreferenceManager.getDefaultSharedPreferences(ContextUtils.getInstance().getApplicationContext());
		editor = spf.edit();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	/* Զӯ���� */
	private SerialPort mserialPort;

	/* Զӯ���� */
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
		Log.i("���ز����־", action);
		// action = "signName";
		this.callbackContext = callbackContext;
		if (action.equals("readIdCard"))//������֤
		{
			if (MyConstants.spf.getInt("cardreader_type", 0) == 100)
			{
				/****** �������� *****/
				progressDialog = new ProgressDialog(cordova.getActivity());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle("��ʾ");
				progressDialog.setMessage("���ڶ�ȡ����......");
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
				/****** �������� *****/
			} else if (MyConstants.spf.getInt("cardreader_type", 0) == 1)
			{
				/***** Զӯ������֤ *****/
				progressDialog = new ProgressDialog(cordova.getActivity());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setTitle("��ʾ");
				progressDialog.setMessage("���ڶ�ȡ����......");
				progressDialog.setIndeterminate(false);
				progressDialog.setCancelable(true);
				myHandler = new MyHandler(callbackContext, progressDialog);
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
				/***** Զӯ������֤ *****/

			} else
			{
				timer = new Timer();
				final CallbackContext context = callbackContext;
				if (mBluetoothAdapter == null)
				{
					Toast.makeText(cordova.getActivity(), "���豸���������޷���ȡ����֤��Ϣ", Toast.LENGTH_SHORT).show();
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
							mItems[i] = device.getName() + " (�����)\n" + device.getAddress();
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

					/***** ���ڶ�����֤ *****/
					if (MyConstants.spf.getInt("cardreader_type", 0) == 3)
					{
						read.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								// TODO Auto-generated method stub
								if (!mBluetoothAdapter.isEnabled())
								{
									Toast.makeText(cordova.getActivity(), "����δ�򿪣�", Toast.LENGTH_SHORT).show();
								} else
								{
									progressDialog = new ProgressDialog(cordova.getActivity());
									progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									progressDialog.setTitle("��ʾ");
									progressDialog.setMessage("���������豸......");
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
												// TODO Auto-generated method
												// stub
												try
												{
													Thread.sleep(100);
													new GTReadCardThread(device, myHandler).start();
												} catch (SecurityException e)
												{
													// TODO Auto-generated catch
													// block
													Message message = new Message();
													message.what = 11;
													Bundle bundle = new Bundle();
													bundle.putString("error", "�����룺SecurityException�������豸�쳣��");
													message.setData(bundle);
													myHandler.sendMessage(message);
												} catch (NoSuchMethodException e)
												{
													// TODO Auto-generated catch
													// block
													Message message = new Message();
													message.what = 11;
													Bundle bundle = new Bundle();
													bundle.putString("error", "�����룺NoSuchMethodException�������豸�쳣��");
													message.setData(bundle);
													myHandler.sendMessage(message);
												} catch (InterruptedException e)
												{
													// TODO Auto-generated catch
													// block
													Message message = new Message();
													message.what = 11;
													Bundle bundle = new Bundle();
													bundle.putString("error", "�����룺InterruptedException�������豸�쳣��");
													message.setData(bundle);
													myHandler.sendMessage(message);
												} catch (IOException e)
												{
													// TODO Auto-generated catch
													// block
													Message message = new Message();
													message.what = 11;
													Bundle bundle = new Bundle();
													bundle.putString("error", "�����룺IOException�������豸�쳣��");
													message.setData(bundle);
													myHandler.sendMessage(message);
												}

											}
										}).start();
									} else
									{
										Toast.makeText(cordova.getActivity(), "δѡ��Ĭ�������豸", Toast.LENGTH_SHORT).show();
									}

								}
							}
						});
					}
					/***** ���ڶ�����֤ *****/

					/*** ��˹̩��������֤ ***/
					if (MyConstants.spf.getInt("cardreader_type", 0) == 2)
					{
						read.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v)
							{
								// TODO Auto-generated method stub
								if (!mBluetoothAdapter.isEnabled())
								{
									Toast.makeText(cordova.getActivity(), "����δ�򿪣�", Toast.LENGTH_SHORT).show();
								} else
								{
									progressDialog = new ProgressDialog(cordova.getActivity());
									progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									progressDialog.setTitle("��ʾ");
									progressDialog.setMessage("���������豸......");
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
												// TODO Auto-generated method
												// stub
												try
												{
													Thread.sleep(100);
													TimerTask conntask = new TimerTask()
													{

														@Override
														public void run()
														{
															// TODO
															// Auto-generated
															// method stub
															if (mmInStream == null || mmOutStream == null)
															{
																Message message = new Message();
																message.what = 11;
																Bundle bundle = new Bundle();
																bundle.putString("error", "�����豸��ʱ�쳣��");
																message.setData(bundle);
																myHandler.sendMessage(message);
																Thread.currentThread().interrupt();
															}
														}
													};
													if (mmInStream != null && mmInStream != null)
													{
														System.out.println("����");
														new BlueToothReadCardThread(mmInStream, mmOutStream, myHandler).start();
													} else
													{
														System.out.println("����1");
														timer.schedule(conntask, 15000);
														// if (mBTHSocket==null)
														// {
														// mBTHSocket =
														// device.createRfcommSocketToServiceRecord(MY_UUID);
														// }
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
															new BlueToothReadCardThread(mmInStream, mmOutStream, myHandler).start();
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
													bundle.putString("error", "�����룺NumberFormatException�������豸�쳣��");
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
													bundle.putString("error", "�����룺IOException�������豸�쳣��");
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
										Toast.makeText(cordova.getActivity(), "δѡ��Ĭ�������豸", Toast.LENGTH_SHORT).show();
									}

								}
							}
						});
					}
					/*** ��˹̩��������֤ ***/
					readboarddialog.show();
				}

			}
		} else if (action.equals("signName"))//����ǩ��
		{
			DisplayMetrics dm = new DisplayMetrics();
			cordova.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			int screenHeigh = dm.heightPixels;
			View view = LayoutInflater.from(cordova.getActivity()).inflate(R.layout.handsign, null);
			cancel = (Button) view.findViewById(R.id.btQuit);
			clear = (Button) view.findViewById(R.id.btClear);
			save = (Button) view.findViewById(R.id.btSave);
			cancel.setOnClickListener(this);
			clear.setOnClickListener(this);
			save.setOnClickListener(this);
			lname = (LinearLayout) view.findViewById(R.id.WriteUserNameSpace);
			LayoutParams params;
			if (getScreenInches() > 7.0)
			{
				params = new LayoutParams(screenWidth * 7 / 10, screenHeigh * 5 / 10);// 10��
			} else
			{
				params = new LayoutParams(screenWidth * 8 / 10, screenHeigh * 6 / 10);// 7��
			}
			lname.setLayoutParams(params);
			eName = new ElectronWriteName(cordova.getActivity());
			lname.addView(eName);
			builder = new AlertDialog.Builder(cordova.getActivity());
			dialog = builder.setCancelable(false).setView(view).create();
			dialog.show();
			if (getScreenInches() > 7.0)
			{
				dialog.getWindow().setLayout(screenWidth * 6 / 10, screenHeigh * 7 / 10);// 10��
			} else
			{
				dialog.getWindow().setLayout(screenWidth * 7 / 10, screenHeigh * 8 / 10);// 7��
			}
		} else if (action.equals(""))
		{
			
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
				takeScreenShot(eName, callbackContext);
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
				Toast.makeText(cordova.getActivity(), "�����Ѵ򿪣�", Toast.LENGTH_SHORT).show();
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
						mItems[i] = device.getName() + " (�����)\n" + device.getAddress();
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

	public Uri takeScreenShot(View view, CallbackContext context) throws Exception
	{
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b = view.getDrawingCache();
		FileOutputStream fos = null;
		try
		{
			String PIC_DIR = Environment.getExternalStorageDirectory() + "/mega";
			String fileName = Environment.getExternalStorageDirectory() + "/mega/" + "sign.png";
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

		// invalidateLayoout();
	}

	private double getScreenInches()
	{
		WindowManager wm = (WindowManager) cordova.getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		// ��Ļ�ߴ�
		double screenInches = Math.sqrt(x + y);
		return screenInches;
	}

}
