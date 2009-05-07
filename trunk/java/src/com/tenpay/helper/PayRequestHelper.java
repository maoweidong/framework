package com.tenpay.helper;

import com.tenpay.bean.PayRequest;
import com.tenpay.util.MD5Util;
import com.tenpay.util.TenpayUtil;

public class PayRequestHelper {

	/** �̼���Կ */
	private String key;
	
	/** ֧������bean */
	private PayRequest payRequest;
	
	/** ǩ����Ϣ */
	private String sign;
	
	/** Ŀ��URL */
	private final String targetURL = 
		"https://www.tenpay.com/cgi-bin/v1.0/pay_gate.cgi";
	
//	private final String targetURL = 
//		"https://www.tenpay.com/cgi-bin/v1.0/vast_pay_gate.cgi";
	
	/** ��������� */
	private String requestParameters;
	
	/**
	 * ���캯��
	 * @param key �̼ҽ�����Կ
	 * @param payRequest ֧������bean
	 */
	public PayRequestHelper(String key, PayRequest payRequest) {
		this.key = key;
		this.payRequest = payRequest;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public PayRequest getPayRequest() {
		return payRequest;
	}

	public void setPayRequest(PayRequest payRequest) {
		this.payRequest = payRequest;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTargetURL() {
		return targetURL;
	}
	
	/**
	 * ��ȡ���͵�URL(��������)
	 * @return String
	 */
	public String getSendUrl() {
		this.createRequestParameters();
		return this.targetURL + "?" + this.requestParameters;
	}
	
	protected void createSign() {
		StringBuffer buf = new StringBuffer();
		TenpayUtil.addParameter(buf, "cmdno", payRequest.getCmdno());
		TenpayUtil.addParameter(buf, "date", payRequest.getDate());
		TenpayUtil.addParameter(buf, "bargainor_id", payRequest.getBargainor_id());
		TenpayUtil.addParameter(buf, "transaction_id", payRequest.getTransaction_id());
		TenpayUtil.addParameter(buf, "sp_billno", payRequest.getSp_billno());
		TenpayUtil.addParameter(buf, "total_fee", payRequest.getTotal_fee() + "");
		TenpayUtil.addParameter(buf, "fee_type", payRequest.getFee_type());
		TenpayUtil.addParameter(buf, "return_url", payRequest.getReturn_url());
		TenpayUtil.addParameter(buf, "attach", payRequest.getAttach());
		TenpayUtil.addBusParameter(buf, "spbill_create_ip", payRequest.getSpbill_create_ip());
		
		this.requestParameters = buf.toString();
		
		TenpayUtil.addParameter(buf, "key", this.key);
		
		//���ɴ�дǩ����
		this.sign = MD5Util.MD5Encode(buf.toString()).toUpperCase();
		
	}
	
	protected void createRequestParameters() {
		this.createSign();
		StringBuffer buf = new StringBuffer(this.requestParameters);
		TenpayUtil.addParameter(buf, "bank_type", payRequest.getBank_type());
		TenpayUtil.addParameter(buf, "purchaser_id", payRequest.getPurchaser_id());
		
		//��Ʒ����encode���ύ,�������Խ����������
		String encodeDesc = TenpayUtil.URLEncode2GBK(payRequest.getDesc());
		TenpayUtil.addParameter(buf, "desc", encodeDesc);
		TenpayUtil.addParameter(buf, "sign", this.sign);
		
		//��ɲ�������
		this.requestParameters = buf.toString();
		
	}
	
	public static void main(String args[]) {
		
		//System.out.println("--------------------");
		
		String bargainor_id = "1202437801";
		String key = "tenpaytesttenpaytesttenpaytest12";
		String currTime = TenpayUtil.getCurrTime();
		String strRandom = TenpayUtil.buildRandom(4) + "";
		
		PayRequest payRequest = new PayRequest();
		payRequest.setAttach("����:����");
		payRequest.setBank_type("0");
		payRequest.setBargainor_id(bargainor_id);
		payRequest.setCmdno("1");
		payRequest.setDate(currTime.substring(0,8));
		payRequest.setDesc("��Ʒ����");
		payRequest.setFee_type("1");
		payRequest.setPurchaser_id("");
//		payRequest.setReturn_url("http://localhost:8080/tenpay/notify_handler.jsp");
		payRequest.setReturn_url("http://localhost:8780/szair/servlet/com.iss.szair.bank.tenpay.TenPayB2CServlet");
//		payRequest.setSp_billno("20080523095542000016");
		payRequest.setSp_billno("004913");
		payRequest.setTotal_fee(1);
		payRequest.setTransaction_id(bargainor_id + currTime + strRandom);
//		payRequest.setTransaction_id("1203292301200808140049131234");
		payRequest.setSpbill_create_ip("219.33.62.73");
		
		PayRequestHelper helper = new PayRequestHelper(key,payRequest);
		
		System.out.println(helper.getSendUrl());
		
		//System.out.println("--------------------");
	}

	
}
