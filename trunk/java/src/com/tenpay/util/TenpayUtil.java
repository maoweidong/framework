package com.tenpay.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TenpayUtil {
	
	/**
	 * �Ѷ���ת��Ϊint��ֵ.
	 * 
	 * @param obj
	 *            �������ֵĶ���.
	 * @return int ת�������ֵ,�Բ���ת���Ķ��󷵻�0��
	 */
	public static int toInt(Object obj) {
		int a = 0;
		try {
			if (obj != null)
				a = Integer.parseInt(obj.toString());
		} catch (Exception e) {

		}
		return a;
	}
	
	/**
	 * �Ѷ���ת��Ϊlong��ֵ.
	 * 
	 * @param obj
	 *            �������ֵĶ���.
	 * @return long ת�������ֵ,�Բ���ת���Ķ��󷵻�0��
	 */
	public static long toLong(Object obj) {
		long a = 0;
		try {
			if(obj != null)
				a = Long.parseLong(obj.toString());
		} catch (Exception e) {
			
		}
		return a;
	}
	
	/**
	 * �Ѷ���ת�����ַ���
	 * @param obj
	 * @return String ת�����ַ���,������Ϊnull,�򷵻ؿ��ַ���.
	 */
	public static String toString(Object obj) {
		if(obj == null)
			return "";
		
		return obj.toString();
	}
	
	/**
	 * Ԫת����Ϊ�� 1Ԫ==100��
	 * ����0.011Ԫת����Ϊ1��,С��������3λ�Ժ��(��������λ)������.
	 * @param money
	 * @return long
	 */
	public static long yuan2Fen(String money) {
		return TenpayUtil.yuan2Fen(Double.valueOf(money));
	}
	
	/**
	 * Ԫת���ɷ� 1Ԫ==100��
	 * ����0.011Ԫת����Ϊ1��,С��������3λ�Ժ��(��������λ)������.
	 * @param money
	 * @return long
	 */
	public static long yuan2Fen(double money) {		
		String strFen = (money * 100) + "";

		return Long.parseLong(strFen.substring(0,strFen.indexOf(".")));

	}
	
	/**
	 * ��ת����Ԫ 100��==1Ԫ
	 * @param money
	 * @return double
	 */
	public static double fen2Yuan(String money) {
		return TenpayUtil.fen2Yuan(Long.parseLong(money));
	}
	
	/**
	 * ��ת����Ԫ 100��==1Ԫ
	 * @param money
	 * @return double
	 */
	public static double fen2Yuan(long money) {
		return (double)money / 100;
	}
	
	/**
	 * ��ȡ��ǰʱ�� yyyyMMddHHmmss
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}
	
	/**
	 * ��ȡ��ǰ���� yyyyMMdd
	 * @param date
	 * @return String
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String strDate = formatter.format(date);
		return strDate;
	}
	
	/**
	 * 
	 * @param str
	 * @return String
	 * @see java.net.URLEncoder.encode(String s, String enc)
	 */
	public static String URLEncode2GBK(String str) {
		
		if( null == str )
			return null;
		
		String strRet = "";
		try {
			strRet = java.net.URLEncoder.encode(str,"GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strRet;
	}
	
	public static String URLDecode2GBK(String str) {
		if( null == str ) 
			return null;
		
		String strRet = "";
		try {
			strRet = java.net.URLDecoder.decode(str, "GBK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strRet;
	}
	
	/**
	 * ��GBK����encode������ֵ
	 * @param parameters
	 * @return
	 */
	public static String encodeParameters2GBK(String parameters) {
		if (null == parameters || "".equals(parameters))
			return parameters;

		String[] parArray = parameters.split("&");
		if (null == parArray || parArray.length == 0)
			return parameters;

		StringBuffer bufRet = new StringBuffer();
		for (int i = 0; i < parArray.length; i++) {
			String[] keyAndValue = parArray[i].split("=");
			if (null != keyAndValue && keyAndValue.length == 2) {
				String key = keyAndValue[0];
				String value = keyAndValue[1];
				String encodeValue = URLEncode2GBK(value);
				TenpayUtil.addParameter(bufRet, key, encodeValue);
			} else {
				bufRet.append("&" + parArray[i]);
			}
		}

		return bufRet.toString();
	}
	
	/**
	 * ȡ��һ��ָ�����ȴ�С�����������.
	 * 
	 * @param length
	 *            int �趨��ȡ��������ĳ��ȡ�lengthС��11
	 * @return int �������ɵ��������
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}
	
	/**
	 * ��Ӳ���
	 * @param buf
	 * @param parameterName ������
	 * @param parameterValue ����ֵ
	 * @return StringBuffer
	 */
	public static StringBuffer addParameter(StringBuffer buf, 
			String parameterName,
			String parameterValue) {
		
		if("".equals(buf.toString())) {
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		} else {
			buf.append("&");
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		}
		
		return buf;
	}
	
	
	public static StringBuffer addParameter(StringBuffer buf, 
			String parameterName,
			int parameterValue) {
		
		if("".equals(buf.toString())) {
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		} else {
			buf.append("&");
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		}
		
		return buf;
	}
	/**
	 * ��Ӳ���,������ֵ��Ϊ�մ�,����ӡ���֮,����ӡ�
	 * @param buf
	 * @param parameterName
	 * @param parameterValue
	 * @return StringBuffer
	 */
	public static StringBuffer addBusParameter(StringBuffer buf,
			String parameterName,
			String parameterValue) {
		if( null == parameterValue || "".equals(parameterValue)) {
			return buf;
		}
		
		if("".equals(buf.toString())) {
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		} else {
			buf.append("&");
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		}
		return buf;
	}
	
	public static StringBuffer addBusParameter(StringBuffer buf,
			String parameterName,
			int parameterValue) {
		
			buf.append("&");
			buf.append(parameterName);
			buf.append("=");
			buf.append(parameterValue);
		return buf;
	}
	
	/**
	 * ��ת����ʾ��ҳ��
	 * @param url ��ʾ��ҳ��,�Ծ��Ե�ַ����
	 * @param name ��������
	 * @param value ����ֵ
	 * @return String
	 * 		������ת��js�ַ�������
	 */
	public static String gotoShow(String url, 
			String name, 
			String value) {
		String strScript = "<script language='javascript'>";
		strScript += "window.location.href='";
		strScript += url + "?" + name + "=" + value;
		strScript += "'</script>";
		
		return strScript;
	}
	
	/**
	 * ��������
	 * @param parameters ������
	 * @return Map<String,String>
	 */
	public static Map<String,String> parseParameters(String parameters) {
		if(null == parameters || "".equals(parameters)) return null;
		
		String[] parametersArray = parameters.split("&");
		if(null == parametersArray) return null;
		
		Map<String,String> m = new HashMap<String,String> ();
		for(int i = 0; i < parametersArray.length; i++) {
			String keyAndValue = parametersArray[i];
			String[] tempArray = keyAndValue.split("=");
			if(tempArray != null  &&  tempArray.length == 2) {
				String key = tempArray[0];
				String value = URLDecode2GBK(tempArray[1]);
				m.put(key, value);
			}
		}
		
		return m;
	}
	
	public static void main(String args[]) {
		System.out.println("----------");
		
		String fen = "100";
		System.out.println("100 fen is " + TenpayUtil.fen2Yuan(fen) + " yuan" );
		
		String yun = "2";
		System.out.println("fen=" + TenpayUtil.yuan2Fen(yun));
		
		String currTime = TenpayUtil.getCurrTime();
		System.out.println("currTime:" + currTime);
		
		String currDate = TenpayUtil.formatDate(new Date());
		System.out.println("currDate:" + currDate);
		
		String str = "88881491^1^2|68084040^1^1|468478488^1^4";
		String encodeGBK = TenpayUtil.URLEncode2GBK(str);
		System.out.println("encodeGBK:" + encodeGBK);
		
		int iRandom = TenpayUtil.buildRandom(1);
		System.out.println("iRandom:" + iRandom);
		
		String strShow = TenpayUtil.gotoShow("http://localhost:8080/tenpay_b2c_jsp",
				"msg","֧���ɹ�");
		System.out.println("strShow:" + strShow);
		
		String html = "<html><script language=\"javascript\">window.location.href='http://miklchen-pc:8080/tenpay_b2c_jsp/busSplitResponse.jsp?bargainor_id=1202952101&bus_args=88881491%5E1%5E2%7C68084040%5E1%5E1%7C468478488%5E1%5E4&bus_type=33&cmdno=3&fee_type=1&pay_info=ok&pay_result=0&sign=3EB3790277B5582B8EB212D8624C0C89&sp_billno=200808211431051471&total_fee=3&transaction_id=1202952101200808211431051471&version=4';</script></html>";
		String[] resArray = html.split("window.location.href='");
		if(null != resArray && resArray.length == 2) {
			String temp = resArray[1];
			String[] tempArray = temp.split("'");
			
		}
		
		String t = "a=����&b=2&c";
		System.out.println(encodeParameters2GBK(t));
		
		System.out.println("----------");
	}
	
}
