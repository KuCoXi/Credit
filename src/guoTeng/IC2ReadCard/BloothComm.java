package guoTeng.IC2ReadCard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;

public class BloothComm{
//private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
private BluetoothSocket mSocket=null;    	
private BluetoothAdapter btAdapt=null; 
private OutputStream os=null;
private InputStream is=null;
private byte[] mData = new byte[2048];
private byte[] mDesWlt = new byte[1024];
private byte[] mSrcWlt = new byte[1024];
private byte[] mBmp = new byte[38862];

public String mName;
public String mSex;
public String mNation;
public String mBirth;
public String mAddress;
public String mIdNo;
public String mPolice;
public String mStart;
public String mEnd;
public String mBmpFilePath;
public String mBase64data;

private String Nation[] = { "未知",
		  "汉", "蒙古","回","藏","维吾尔","苗","彝","壮","布依","朝鲜",
		  "满", "侗","瑶","白","土家","哈尼","哈萨克","傣","黎","傈僳",
		  "佤","畲","高山","拉祜","水","东乡","纳西","景颇","柯尔克孜","土",
		  "达斡尔","仫佬","羌","布朗","撒拉","毛难","仡佬","锡伯","阿昌","普米",
		  "塔吉克","怒","乌孜别克","俄罗斯","鄂温克","德昂","保安","裕固","京","塔塔尔",
		  "独龙","鄂伦春","赫哲","门巴","珞巴","基诺","未知"
		};

private String Nation1[] = {"芒人","摩梭人","革家人","穿青人","入籍","其他","外国血统"};    	
//2.UCS2   ->   gb2312 
private String byte2Ucs2(byte[] data, int iPos, int iLen){
    //byte[] ucs2Bytes = new byte[]{0x4e,   0x2d,   0x65,   (byte)0x87};   //UCS2   bytes 
    try{
    	return  (new String(data, iPos, iLen, "UTF-16LE")).trim();//UTF-16LE,UTF-16BE
    }catch(UnsupportedEncodingException e){    	    
    }
    return "";
    //String result = new String(s.getBytes("UTF-16BE"),   "GBK ");  
}	

private static File writeBytesToFile(byte[] inByte, String pathAndNameString) throws IOException {
    File file = null;
    try {
    	//if (android.os.Environment.getExternalStorageState().
    	//		equals(android.os.Environment.MEDIA_MOUNTED)){
    		
            file = new File(pathAndNameString);
            if (!file.getParentFile().exists()) {   
                file.getParentFile().mkdirs(); 
            }   
            file.delete();
            if(!file.exists()) { 
             	file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(inByte);
            fos.close();       		
    	//}
    } catch (IOException e) {    
    	//Log.w(TAG, "createNewFile IOException :" + e.toString());	
    	//throw new IOException("Could not completely write file "+file.getName());
    	return null;
    }

    return file;
}  

private boolean IdCardByte2String(byte[] data){
	//String szIdNo = mIdNo;
	
	mName = byte2Ucs2(data, 14, 30);
	mSex = byte2Ucs2(data, 14+30, 2);
	
	switch (Integer.parseInt(mSex)) {
	case 0:
		mSex = "未知";
		break;
	case 1:
		mSex =  "男";
		break;
	case 2:
		mSex = "女";
		break;
	case 9:
		mSex = "未明";
		break;
	default:
		break;
	}   
	
	mNation = byte2Ucs2(data, 14+32, 4);
	int iNation = Integer.parseInt(mNation);
	switch(iNation) {
	case 94:
	case 95:
	case 96:
	case 97:
	case 98:
		mNation = Nation1[iNation-94];
		break;
	default:
		if (iNation<1 || iNation>56){
			iNation = 0;			
		}
		mNation = Nation[iNation];
		break;
	}  
	
	mBirth = byte2Ucs2(data, 14+36, 16);
	mAddress = byte2Ucs2(data, 14+52, 70);			
	mPolice = byte2Ucs2(data, 14+158, 30);
	mStart = byte2Ucs2(data, 14+188, 16);
	mEnd = byte2Ucs2(data, 14+204, 16);
	
	byte arIdNo[] = new byte[18];
	if (mIdNo != null){
		System.arraycopy(mIdNo.getBytes(), 0, arIdNo, 0, 18);
	}
	mIdNo  = byte2Ucs2(data, 14+122, 36);
		
	System.arraycopy(data, 14+256, mDesWlt, 0, 1024);	
	byte[] szBmp = Wlt2Bmp(mDesWlt);
    try{    	
    	if ((szBmp != null) && (szBmp.length == (38862+1024))){
    		System.arraycopy(szBmp, 38862, mSrcWlt, 0, 1024);	
    		System.arraycopy(szBmp, 0, mBmp, 0, 38862);
//    		writeBytesToFile(mBmp, mBmpFilePath); 
    		mBase64data = Base64.encodeToString(mSrcWlt, Base64.DEFAULT);
    	}else{
    		return false;
    	}          	    
    }catch(Exception e){
    	return false;
    } 	
	return true; 
}

private boolean SendData(byte[] data, int len){
	try {
		Thread.sleep(50);
		os.write(data, 0, len);
		//os.write(data);
    } catch (Exception e) {
    	ClearBloothComm();
        return false;
    }     		
	return true;
}

private int RecvData(byte[] data, int pos, int len){
	try {    	    			
		return is.read(data, pos, len);
    } catch (Exception e) { 
    	ClearBloothComm();
        return 0;
    }      		
}


private byte ChkSum(byte[] Data, int iPos, int iLen)
{
	if (iLen == 0)
		return 0;
	
	byte cSum = 0;
	int i=0;
	while ( i<iLen) {
		cSum ^= Data[i+iPos];
		i++;
	}
	return cSum;
}

private boolean SendCmdData(byte[] pData, int iLen)
{
	byte[] Data = new byte[50];
	Data[0] = (byte)0xaa;
	Data[1] = (byte)0xaa;
	Data[2] = (byte)0xaa;
	Data[3] = (byte)0x96;
	Data[4] = (byte)0x69;
	
	if (iLen + 7 + 1 > 50)
		return false;
	
	//长度数据
	Data[5] = (byte)(((iLen+1) >> 8) & 0x0ff);
	Data[6] = (byte)((iLen+1) & 0x0ff);
	
	System.arraycopy(pData, 0, Data, 7, iLen);
	
	Data[7+iLen] = ChkSum(Data,5,iLen+2);	
	return SendData(Data, 7 + iLen + 1);
}

private int RecvCmdResult()//命令全部以小写方式发送
{
	int	iDataLen = 0;
	int iPos = 0;
	boolean bHead = false;
	byte[] head = new byte[]{(byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0x96, (byte)0x69};  
	    		
	while (true){
		int iLen = RecvData(mData, iPos, mData.length - iPos);
		if (iLen == 0){//超时
			return 0;
		}

		iPos += iLen;
		if (iPos < head.length)
			continue;
		
		if (!bHead){    				
    			int i;
				for (i=0; i<head.length; i++){
					if (mData[i] != head[i])
						break;
				}
				if (i != head.length)
					return 0;
				bHead = true;
		}
		
		if (iDataLen == 0){
			if (iPos > 7){
				int iLow = mData[6]&0xff;
				int iHight = (mData[5]&0xff) << 8;
				iDataLen = iHight+iLow;
			}
		}
		
		if (iPos-7 < iDataLen)
			continue;
			
		if (ChkSum(mData, 5, iDataLen+2-1) != mData[iDataLen+6]){
			return 0;
		}

		//return c2i(0, pStart[7], pStart[8], pStart[9]);
		return (0xff & mData[9]);
	}
}

private int SendCmd(byte[] szData, int iLen, int iDelay) //
{
	if (!SendCmdData(szData, iLen)) {
		return 0;
	}

	if (iDelay > 0)
	try{
		Thread.sleep(iDelay);
	}catch(InterruptedException e){    			
	}    		
	
	return RecvCmdResult();
}

private byte[] mSamId = new byte[16];
//05 00 01 00    05-01-
//AB F2 31 01    20050603-
//21 12 01 00    70177-
//27 84 11 14    336692263
private String SamID_Caculate(byte []SamID, int iPos)
{
	String szResult = "";
	long iLow = SamID[0+iPos] & 0xff;
	long iHight = ((long)(SamID[1+iPos] & 0xff)) << 8;
	long iTmp = iHight + iLow;
	szResult = Long.toString(iTmp);
	szResult += "-";
	iLow = SamID[2+iPos] & 0xff;
	iHight = ((long)(SamID[3+iPos] & 0xff)) << 8;    		
	iTmp = iHight + iLow;
	szResult += Long.toString(iTmp) + "-";
	
	iLow = SamID[4+iPos] & 0xff;
	iHight = ((long)(SamID[5+iPos] & 0xff)) << 8;    
	iTmp = iHight + iLow;
	iLow = ((long)(SamID[6+iPos] & 0xff)) << 16;
	iHight = ((long)(SamID[7+iPos] & 0xff)) << 24;    
	iTmp = iTmp+ iHight + iLow;    		
	szResult += Long.toString(iTmp) + "-";

	iLow = SamID[8+iPos] & 0xff;
	iHight = ((long)(SamID[9+iPos] & 0xff)) << 8;    
	iTmp = iHight + iLow;
	iLow = ((long)(SamID[10+iPos] & 0xff)) << 16;
	iHight = ((long)(SamID[11+iPos] & 0xff)) << 24;    
	iTmp = iTmp+ iHight + iLow;      		    		
	szResult += Long.toString(iTmp) + "-";
	
	iLow = SamID[12+iPos] & 0xff;
	iHight = ((long)(SamID[13+iPos] & 0xff)) << 8;    
	iTmp = iHight + iLow;
	iLow = ((long)(SamID[14+iPos] & 0xff)) << 16;
	iHight = ((long)(SamID[15+iPos] & 0xff)) << 24;
	iTmp = iTmp+ iHight + iLow;      		    		
	szResult += Long.toString(iTmp);    		
	
	return szResult;
}

private int m_iCmd = 0;
private String msKey = "000000000000000000000000000000";

public boolean InitBloothComm(BluetoothDevice device) throws SecurityException, NoSuchMethodException, IOException{
//	mBmpFilePath = szBmpFilePath;
	ClearBloothComm();
	
//	btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能
//	if (btAdapt == null) {
//		return false;
//	}else {
//        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF)// 开蓝牙   
//            btAdapt.enable();  
//	}    		
    	
//    BluetoothDevice device = btAdapt.getRemoteDevice(szAddress);    		   

    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
	
    Boolean bOk = false;
    try {
    	mSocket = (BluetoothSocket) m.invoke(device, 1);
    	mSocket.connect();  
		os = mSocket.getOutputStream(); 
		is = mSocket.getInputStream();  
    	bOk = true;
    } catch (IllegalArgumentException e){
    	e.printStackTrace();
    } catch (IllegalAccessException e){
    	e.printStackTrace();
    } catch (InvocationTargetException e){
    	e.printStackTrace();
    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
    if (!bOk){
    	ClearBloothComm();  	
    }
    
    return bOk;    
}

public boolean LinkOk(){
	return mSocket != null;
}


public String ReadSamidCmd()
{
	byte[] szData = new byte[]{(byte)0xa0, (byte)0x04};
	//byte[] szData = new byte[]{(byte)0x12, (byte)0xff};

	m_iCmd = SendCmd(szData, 2, 1500);    		

	if (m_iCmd != 0x90){
		return "";
	}
	System.arraycopy(mData, 10, mSamId, 0, 16);
	msKey = SamID_Caculate(mData, 10);//5-1-20110215-1312544-3908317450	
	
	return msKey;
}   

public int FindCardCmd()//找卡
{
	byte[] szData = new byte[]{(byte)0xa0, (byte)0x01};
	//byte[] szData = new byte[]{(byte)0x20, (byte)0x01};

	m_iCmd = SendCmd(szData, 2, 100);

	if (m_iCmd != 0x80 && m_iCmd != 0x9F){
		return m_iCmd;
	}
	System.out.println("找卡结果："+m_iCmd);
	return m_iCmd;
}

public int SelCardCmd()//选卡
{
	byte[] szData = new byte[]{(byte)0xa0, (byte)0x02};
	//byte[] szData = new byte[]{(byte)0x20, (byte)0x02};
	m_iCmd = SendCmd(szData, 2, 50);        		

	if (m_iCmd != 0x81 && m_iCmd != 0x90){
		return m_iCmd;
	}
	System.out.println("选卡结果："+m_iCmd);
	return m_iCmd;		
}

public int ReadCardCmd()//读卡
{
	byte[] szData = new byte[]{(byte)0xa0, (byte)0x03};
	//byte[] szData = new byte[]{(byte)0x30, (byte)0x01};
	m_iCmd = SendCmd(szData, 2, 1250);      		

	if (m_iCmd != 0x90){
		return m_iCmd;
	}  
	
	if (IdCardByte2String(mData)) 
	{	
		System.out.println("读卡结果："+m_iCmd);
		return m_iCmd;
	}
	System.out.println("读卡结果：0x41");
	return 0x41;
}

public int SetTimeCmd(int iTime)
{
	byte[] szData = new byte[3];
	szData[0] = (byte)0x81;
	szData[1] = (byte)0x04;
	szData[2] = (byte)iTime;
	return SendCmd(szData, 3, 500);      		
}

public void ClearBloothComm(){
	if (mSocket != null){
		try {
			os.close();
			is.close();			
			mSocket.close();
        } catch (Exception e) {            	                    
        }   
		os=null;
		is=null;		
		mSocket = null;			
		btAdapt = null;
	}    		
}

static{ 
	try{ 
		System.loadLibrary("wlt"); 
	}catch(Exception e){ 
	
	} 
} 

private native byte[] Wlt2Bmp(byte[] szWlt);   
//private native byte[] GetRight();   
};