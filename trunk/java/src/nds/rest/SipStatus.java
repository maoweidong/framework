package nds.rest;

import java.util.Map;
import java.util.HashMap;

/**
 * Desc:
 * <p/>
 */
public enum SipStatus
{
    ERROR( "error", 							"0000"),//��������ʧ��
    SUCCESS( "success", 						"9999"),//��������ɹ�
    SIGNATURE_INVALID( "signatureInvalid", 		"1001"),//ǩ����Ч
    REQ_TIMEOUT( "reqTimeout", 					"1002"),//�������
    BINDUSER_FAILD( "binduserFaild", 			"1003"),//�û���ʧ��
    NEED_BINDUSER( "needBinduser", 				"1004"),//��Ҫ���û�
    NEED_APPKEY( "needAppKey", 					"1005"),//��Ҫ�ṩAppKey
    NEED_APINAME( "needApiName", 				"1006"),//��Ҫ�ṩ������
    NEED_SIGN( "needSign", 						"1007"),//��Ҫ�ṩǩ��
    NEED_TIMESTAMP( "needTimeStamp", 			"1008"),//��Ҫ�ṩʱ���
    AUTH_FAILD( "authFaild", 					"1009"),//�û���֤ʧ��
    NORIGHT_CALLSERVICE( "noRightCallService", 	"1010"),//��Ȩ���ʷ���
    SERVICE_NOTEXIST( "service", 				"1011"),//���񲻴���
    NEED_SESSIONID( "sessionid",				"1012"),//��Ҫ�ṩSessionId
    NEED_USERNAME( "username",					"1013");//��Ҫ�ṩ�û���

    /*
{"0000":"��������ʧ��","9999":"��������ɹ�","1001":"ǩ����Ч","1002":"�������","1003":"�û���ʧ��","1004":"��Ҫ���û�","1005":"/��Ҫ�ṩAppKey","1006":"��Ҫ�ṩ������","1007":"��Ҫ�ṩǩ��","1008":"��Ҫ�ṩʱ���","1009":"�û���֤ʧ��","1010":"��Ȩ���ʷ���","1011":"���񲻴���","1012":"��Ҫ�ṩSessionId","1013":"��Ҫ�ṩ�û���"}
     */
    
    private String v;
    private String c;

    private static Map<String,SipStatus> status ;

    SipStatus(String value, String code)
    {
        v = value;
        c = code;
    }

    @Override
    public String toString() {
        return v;
    }

    public String getCode() {
        return c;
    }

    public static SipStatus getStatus(String code) {
        if(status == null) {
            status = new HashMap<String,SipStatus>();
            status.put("0000",ERROR);
            status.put("9999",SUCCESS);

            status.put("1001",SIGNATURE_INVALID);
            status.put("1002",REQ_TIMEOUT);
            status.put("1003",BINDUSER_FAILD);
            status.put("1004",NEED_BINDUSER);
            status.put("1005",NEED_APPKEY);
            status.put("1006",NEED_APINAME);
            status.put("1007",NEED_SIGN);
            status.put("1008",NEED_TIMESTAMP);
            status.put("1009",AUTH_FAILD);
            status.put("1010",NORIGHT_CALLSERVICE);
            status.put("1011",SERVICE_NOTEXIST);
            status.put("1012",NEED_SESSIONID);
            status.put("1013",NEED_USERNAME);
        }

        return status.get(code);
    }
}