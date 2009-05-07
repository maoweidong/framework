
package nds.mail;



/**
 * �����Ϣ֪ͨ�Ĳ�������
 */
public class NotifyParams
{

   /**
    * �����ű��ϲ���������һ�ʴ�д
    */
   private String tableName;

   /**
    * SQL ������������������ĵ��ݻ���󽫴���֪ͨ����
    * ����洢��sqlConditionΪ
    * sqlCondtion= select id from <table> , <table2> where <table>.<xxx> like
    * '$Operator' or <table2>.<yyy> = sysdate
    * �����������Խ��������ж�
    * select count(*) from table where id= xxxx and id in (sqlCondition)
    * �������0 ��ʾ��ǰ��������
    */
   private String sqlCondition;

   /**
    * �ڵ�ǰ��ɵĶ�������create, modify, delete, submit,rollback,permit,all, ��ΪСд
    */
   private String tableAction;

   /**
    * �Զ�������ɵĶ�����Ŀǰ��֧��mail
    */
   private String robotAction;

   /**
    * �����������Զ���������չ�֣��� MailRobotSession.robotParam="$me"
    * ��ʾ�ʼ���������
    */
   private String robotParam;

   /**
    * ������¼������������
    */
   private int ownerId;

   /**
    * @roseuid 3E698FF003BA
    */
   public NotifyParams()
   {

   }
}
