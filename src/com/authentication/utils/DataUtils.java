package com.authentication.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class DataUtils {

	private char[] getChar(int position) {
		String str = String.valueOf(position);
		if (str.length() == 1) {
			str = "0" + str;
		}
		char[] c = { str.charAt(0), str.charAt(1) };
		return c;
	}

	/**
	 * 16�����ַ���ת��������
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringTobyte(String hex) {
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		String temp = "";
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
			temp += result[i] + ",";
		}
		// uiHandler.obtainMessage(206, hex + "=read=" + new String(result))
		// .sendToTarget();
		return result;
	}

	public static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * ����ת��16�����ַ���
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * ʮ�������ַ���ת�����ַ���
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * �ַ���ת����ʮ�������ַ���
	 */
	public static String str2Hexstr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	public static String byte2Hexstr(byte b) {
		String temp = Integer.toHexString(0xFF & b);
		if (temp.length() < 2) {
			temp = "0" + temp;
		}
		temp = temp.toUpperCase();
		return temp;
	}

	public static String str2Hexstr(String str, int size) {
		byte[] byteStr = str.getBytes();
		byte[] temp = new byte[size];
		System.arraycopy(byteStr, 0, temp, 0, byteStr.length);
		temp[size - 1] = (byte) byteStr.length;
		String hexStr = toHexString(temp);
		return hexStr;
	}

	/**
	 * 16�����ַ����ָ�����ɿ飬ÿ��32��16�����ַ�����16�ֽ�
	 * 
	 * @param str
	 * @return
	 */
	public static String[] hexStr2StrArray(String str) {
		// 32��ʮ�������ַ�����ʾ16�ֽ�
		int len = 32;
		int size = str.length() % len == 0 ? str.length() / len : str.length()
				/ len + 1;
		String[] strs = new String[size];
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				String temp = str.substring(i * len);
				for (int j = 0; j < len - temp.length(); j++) {
					temp = temp + "0";
				}
				strs[i] = temp;
			} else {
				strs[i] = str.substring(i * len, (i + 1) * len);
			}
		}
		return strs;
	}

	/**
	 * ��16�����ַ���ѹ�����ֽ����飬�ڰ��ֽ�����ת����16�����ַ���
	 * 
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * ��16�����ַ�����ѹ��ѹ�����ֽ����飬�ڰ��ֽ�����ת����16�����ַ���
	 * 
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] data) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	public static byte[] short2byte(short s) {
		byte[] size = new byte[2];
		size[0] = (byte) (s >>> 8);
		short temp = (short) (s << 8);
		size[1] = (byte) (temp >>> 8);

		// size[0] = (byte) ((s >> 8) & 0xff);
		// size[1] = (byte) (s & 0x00ff);
		return size;
	}

	public static short[] hexStr2short(String hexStr) {
		byte[] data = hexStringTobyte(hexStr);
		short[] size = new short[4];
		for (int i = 0; i < size.length; i++) {
			size[i] = getShort(data[i * 2], data[i * 2 + 1]);
		}
		return size;
	}

	public static short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}
	
	public static String decodeNation(int code)
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

}
