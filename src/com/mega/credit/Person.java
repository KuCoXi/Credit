package com.mega.credit;

import java.io.Serializable;

public class Person implements Serializable
{
	private String msg;
	public Person(String msg)
	{
		this.setMsg(msg);
	}
	
	public Person(){}
	/**
	 * ����
	 */
	private String peopleName;

	/**
	 * �Ա�
	 */
	private String peopleSex;

	/**
	 * ����
	 */
	private String peopleNation;

	/**
	 * ��������
	 */
	private String peopleBirthday;

	/**
	 * סַ
	 */
	private String peopleAddress;

	/**
	 * ���֤��
	 */
	private String peopleIDCode;

	/**
	 * ǩ������
	 */
	private String department;

	/**
	 * ��Ч���ޣ���ʼ
	 */
	private String startDate;

	/**
	 * ��Ч���ޣ�����
	 */
	private String endDate;

	/**
	 * ���֤ͷ��
	 */
	private byte[] photo;
	private String base64;

	/**
	 * û�н�����ͼƬ�����ݴ�Сһ��Ϊ1024�ֽ�
	 */
	private byte[] headImage;

	/**
	 * ����ָ֤��ģ�����ݣ�����λ1024�����Ϊnull��˵��Ϊ����֤��û��ָ��ģ������
	 */
	private byte[] model;
	
	private int errcode = -1;
	
	private String errmsg;

	public String getErrmsg()
	{
		return errmsg;
	}

	public void setErrmsg(String errmsg)
	{
		this.errmsg = errmsg;
	}

	public String getPeopleName() {
		return peopleName;
	}

	public void setPeopleName(String peopleName) {
		this.peopleName = peopleName;
	}

	public String getPeopleSex() {
		return peopleSex;
	}

	public void setPeopleSex(String peopleSex) {
		this.peopleSex = peopleSex;
	}

	public String getPeopleNation() {
		return peopleNation;
	}

	public void setPeopleNation(String peopleNation) {
		this.peopleNation = peopleNation;
	}

	public String getPeopleBirthday() {
		return peopleBirthday;
	}

	public void setPeopleBirthday(String peopleBirthday) {
		this.peopleBirthday = peopleBirthday;
	}

	public String getPeopleAddress() {
		return peopleAddress;
	}

	public void setPeopleAddress(String peopleAddress) {
		this.peopleAddress = peopleAddress;
	}

	public String getPeopleIDCode() {
		return peopleIDCode;
	}

	public void setPeopleIDCode(String peopleIDCode) {
		this.peopleIDCode = peopleIDCode;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public byte[] getHeadImage() {
		return headImage;
	}

	public void setHeadImage(byte[] headImage) {
		this.headImage = headImage;
	}

	public byte[] getModel() {
		return model;
	}

	public void setModel(byte[] model) {
		this.model = model;
	}

	public String getBase64()
	{
		return base64;
	}

	public void setBase64(String base64)
	{
		this.base64 = base64;
	}

	public int getErrcode()
	{
		return errcode;
	}

	public void setErrcode(int errcode)
	{
		this.errcode = errcode;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}


}
