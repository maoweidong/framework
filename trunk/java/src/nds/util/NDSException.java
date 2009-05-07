/******************************************************************
*
*$RCSfile: NDSException.java,v $ $Revision: 1.2 $ $Author: Administrator $ $Date: 2006/01/31 03:01:58 $
*
********************************************************************/

package nds.util;

/**
 * Allow for nested exception
 */
public class NDSException extends Exception
{
    public NDSException()
    {
        
    }

    public NDSException(String s)
    {
        super(s);
    }

    public NDSException(String s, Throwable exception)
    {
        super(s, exception);
    }

    public Throwable getNextException()
    {
        return  this.getCause();
    }

    public synchronized boolean setNextException(Throwable exception)
    {
       this.initCause(exception);
       return true;
    }
    /**
     * ��NDSException �ɷ�����EJB�˷��������ݵ�web��ʱ��weblogic�����һ�δ������stack trace,
     * ��ʱ����ʹ�ñ��������ԭʼ�ļ�message,����stackTrace��message ����ͨ��getMessage()��á�
     */
    public String getSimpleMessage(){
        Throwable e = this;
        String s=this.getMessage();
        while (e != null) {
          if(Validator.isNotNull(e.getMessage())) s= e.getMessage();
          Throwable prev = e;
          e = e.getCause();
          if (e == prev)
            break;
        }
        return s;
    }

    
}
