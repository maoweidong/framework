/******************************************************************
*
*$RCSfile: ClientControllerWebImpl.java,v $ $Revision: 1.5 $ $Author: Administrator $ $Date: 2006/01/31 02:59:20 $
*
*$Log: ClientControllerWebImpl.java,v $
*Revision 1.5  2006/01/31 02:59:20  Administrator
*no message
*
*Revision 1.4  2006/01/17 10:34:43  Administrator
*no message
*
*Revision 1.3  2005/08/28 00:27:03  Administrator
*no message
*
*Revision 1.2  2005/05/27 05:01:47  Administrator
*no message
*
*Revision 1.1.1.1  2005/03/15 11:23:15  Administrator
*init
*
*Revision 1.4  2004/02/02 10:42:36  yfzhu
*<No Comment Entered>
*
*Revision 1.3  2003/04/03 09:28:18  yfzhu
*<No Comment Entered>
*
*Revision 1.2  2002/12/17 05:53:44  yfzhu
*no message
*
*Revision 1.7  2001/12/28 14:20:02  yfzhu
*no message
*
*Revision 1.6  2001/12/09 03:43:32  yfzhu
*no message
*
*Revision 1.5  2001/11/29 00:48:31  yfzhu
*no message
*
*Revision 1.4  2001/11/20 22:36:09  yfzhu
*no message
*
*Revision 1.3  2001/11/16 11:42:40  yfzhu
*no message
*
*Revision 1.2  2001/11/07 20:58:46  yfzhu
*no message
*
*
********************************************************************/
//Source file: F:\\work2\\tmp\\nds\\control\\web\\ClientControllerWebImpl.java

package nds.control.web;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.ejb.Handle;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.servlet.ServletContext;
import java.util.*;
import nds.control.ejb.ClientController;
import nds.control.ejb.ClientControllerBean;
import nds.control.ejb.ClientControllerHome;
import nds.control.event.*;
import nds.control.util.EJBUtils;
import nds.control.util.ValueHolder;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.util.Configurations;
import nds.util.Director;
import nds.util.JNDINames;
import nds.util.NDSException;
import nds.util.NDSRuntimeException;
import nds.util.ObjectQueue;
import nds.util.ServletContextActor;
import nds.util.TimeLog;
import nds.util.*;
import nds.util.threadpool.DefaultThreadPool;
import nds.util.threadpool.ThreadPool;

public class ClientControllerWebImpl implements java.io.Serializable,ServletContextActor,
    nds.util.DestroyListener {
	private final static int DEFAULT_HANDLE_QUEUE_LOCKS =-1; // default to unlimited concurrent execution
	private final static int DEFAULT_HANDLE_QUEUE_TIMEOUT =0; // default to forever  
    
    /**
     *  Only hold plain class object of ClientControllerBean
     */
    private static ClientController ccInstance=null;
    private boolean isUsingUserTransaction= false;
    private Logger logger= LoggerManager.getInstance().getLogger(ClientControllerWebImpl.class.getName());
    private Handle controllerEjbHandle=null;
    private Configurations config;
    private Hashtable lockManagers;// key: String, command, value: LockManager Object
    private Director dir;
    private ArrayList wildcardQueues; // elements are String, such as "submit","create"
    private static ThreadPool threadPool=  new DefaultThreadPool(5); // this pool is only for background event
    
    public ClientControllerWebImpl() {
    	lockManagers=new Hashtable();
    }
    public void init(Director dir) {
        this.dir=dir;
        
    }
    /**
     * We do not put the body of this method in Constructor, because sometimes the
     * ModelManager will be reinitialized, so the controller bean should also be reinstalled.
     *
     */
    public void init(ServletContext context) {
        Configurations conf=(Configurations)WebUtils.getServletContextManager().getActor(WebKeys.CONFIGURATIONS);
        try{
            isUsingUserTransaction=
                (new Boolean(conf.getProperty("controller.usertransaction",
                "false"))).booleanValue() ;
        }catch(Exception e){
        }
        config= conf.getConfigurations("controller.handlequeue");
        // wildcard queues
        wildcardQueues=new ArrayList();
        String wqs=conf.getProperty("controller.queue.wildcardqueues");
        if(wqs !=null){
        	StringTokenizer st=new StringTokenizer( wqs, ",");
        	while(st.hasMoreTokens()){
        		wildcardQueues.add(st.nextToken());
        	}
        }
        
        //this.session=session.getId();
        if(isUsingUserTransaction)
            getClientControllerPlainClass();
        else
            getClientControllerBean();
        logger.debug("ClientControllerWebImpl initialized.");
    }
    public void destroy() {
    	for( Iterator it=lockManagers.values().iterator();it.hasNext();){
    		LockManager lm= (LockManager)it.next();
    		lm.destroy();
    	}
    	lockManagers.clear();
        remove();
        logger.debug("ClientControllerWebImpl destroied.");
    }
    private ClientController getClientControllerBean() {
        // use controllerEjbHandle to cache controller
        ClientController controller =null;
        int tid=TimeLog.requestTimeLog("ClientControllerWebImpl.getClientControllerBean");
        try {
            if( controllerEjbHandle !=null) {
                controller=(ClientController)controllerEjbHandle.getEJBObject();
            }
            if(controller ==null) {
                controller = getClientControllerHome().create();
                logger.debug("ClientController created for context");
            }
//            controllerEjbHandle = controller.getHandle();
        } catch (Exception ce) {
            logger.error("Error getting ClientController",ce);
            throw new NDSRuntimeException("Error getting ClientController",ce);
        }finally{
            TimeLog.endTimeLog(tid);
        }
        return controller;
    }
    /**
     * Get bean as normal class, no ejb called
     */
    private ClientController getClientControllerPlainClass(){
        if( ccInstance!=null) return ccInstance;
        logger.debug("Get client controller as plain class");
        // singleton is ok
        ClientControllerBean ccb=new ClientControllerBean();
        ccb.ejbCreate();
        ccInstance= ccb;
        return ccb;
    }
    public static ClientControllerHome getClientControllerHome() throws javax.naming.NamingException {
        InitialContext initial = new InitialContext();
        Object objref = initial.lookup(JNDINames.CLIENTCONTROLLER_EJBHOME);
        return (ClientControllerHome)
               PortableRemoteObject.narrow(objref, ClientControllerHome.class);
    }
    /**
     * Handle event asynchronous, will start a new thread
     * 
     */
    public void handleEventBackground(final NDSEvent event) throws NDSException {
        threadPool.invokeLater(
                new Runnable() {
                  public void run() {
                      try{
                          ClientControllerWebImpl.this.handleEvent(event);
                      }catch(Exception ex) {
                        logger.error("Errors found on handling event(background):"+ event, ex);
                      }

                  }
                }
              );
    	
    }

    /**
     * feeds the specified event to the state machine of the business logic.
     *
     * 2005-05-21 �Ķ�����:
     * 
     * ����ϵͳ�����ܿ������ڲ����������Ӷ��������ܼ����½�,����:�󲢷������ݵ���, ���ڴ˴����
     * ��������. ���� Event�Ĳ���, Ŀǰ�����DefaultWebEvent �� "command" ���������ж�.
     * ����ϵͳ���ö���Ѱ�Ҷ�Ӧ�Ķ���, �����ܹ��Ӷ����л�ȡ������ʱ��,�Ž���ʵ�ʵĴ�����. ����
     * һֱ�ȵ������п��е����ͷų��������е�����������ϵͳ����ָ����
     * 
     * ������ԣ���nds.properties �ļ��ж���������У�ȱʡʹ�� handlequeue.default=DEFAULT_HANDLE_QUEUE
     * DEFAULT_HANDLE_QUEUE ��ȱʡ�������Ƶ�����������queue��������������
     * handlequeue.<Queue Name>.locks=n (n <0 ȡ���޴��ֵ)
     * handlequeue.<Queue Name>.timeout=n  �������������ȴ�ʱ�䣩
     * �ͻ�������ȴ���ʱ�����յ���Ӧ�Ĵ��󱨸档ȱʡ��ֵ������
     * handlequeue.DEFAULT_HANDLE_QUEUE.locks = -1 ��
     * handlequeue.DEFAULT_HANDLE_QUEUE.timeout = 10
     * 
     * ��ÿһ��Command, 
     * �������� handlequeue.<Command Name>.queue=<queue name>
     * ע�� <Command Name> һ��Сд
     * ��ʹ�ö�Ӧ��queue ��Ϊ���Ĺ������ 
     * 
     * @param ese is the current event
     * @return ValueHolder getting from ejb
     */
    public ValueHolder handleEvent(NDSEvent ese)
    throws NDSException {
        Locale locale;
    	/*Object lock=null;
    	try{
    		lock= waitForLock(ese);
    	}catch(NoSuchElementException e){
    		logger.error(e.getMessage());
    		throw new NDSException("@system-timeout-please-try-later@");
    	}*/
        try {
            ClientController controllerEjb;
            if(isUsingUserTransaction)
                controllerEjb=getClientControllerPlainClass();
            else
                controllerEjb=getClientControllerBean();

            ValueHolder v=controllerEjb.handleEvent(ese);

            return v;
        } catch (Throwable re) {
        	if(re instanceof NDSException) throw (NDSException)re;
            //logger.error("Error handling event "+ese, re);
            if( ese instanceof DefaultWebEvent){
            	locale=( (DefaultWebEvent)ese).getLocale();
            }else{
            	locale= nds.schema.TableManager.getInstance().getDefaultLocale();
            }
            throw new NDSException( WebUtils.getExceptionMessage(re, locale));
        } finally{
           // releaseLock(lock);
        }
    }
    /**
     * ����event ��λ LockManager�� ������Lock 
     * @param e
     * @return Object[]{LockManager, Lock}
     */
    private Object waitForLock(NDSEvent e){
    	// currently handle command event only
    	if(! (e instanceof DefaultWebEvent)) return null;
    	DefaultWebEvent dwe= (DefaultWebEvent)e;
    	String command= (String)dwe.getParameterValue("command");
    	if( command ==null) return null;
    	command= command.toLowerCase();
    	// find LockManager for command
    	LockManager lm= getLockManager(command);
    	if( lm==null) return null;
    	
    	return new Object[]{lm, lm.waitForLock()};
    	
    }
    /**
     * @param command
     * @return null if no need to use LockManager, (locksCount=-1)
     */
    private LockManager getLockManager(String command){
    	// lms is the queue name for command
    	// if no queue for the <param>command</param>
    	// DEFAULT_HANDLE_QUEUE will be used
    	String lms ;
    	if ("DEFAULT_HANDLE_QUEUE".equals(command)) lms="DEFAULT_HANDLE_QUEUE";
    	else lms= config.getProperty( command+".queue");
    	if(lms ==null) {
    		// check for wildcard queues
    		for(int i=0;i< wildcardQueues.size();i++){
    			String wq= (String) wildcardQueues.get(i);
    			if( command.indexOf(wq)>-1 ){
    				lms = command+"_queue";
    				break;
    			}
    		}
    	}
    	if(lms ==null ) return getLockManager("DEFAULT_HANDLE_QUEUE");

    	LockManager lm = (LockManager)lockManagers.get(command);
    	if(lm ==null){
    		// create a new one
        	String lcs= config.getProperty( lms+".locks",
        			config.getProperty("TEMPLATE_QUEUE.locks",""+DEFAULT_HANDLE_QUEUE_LOCKS));
        	
        	String lct= config.getProperty( lms+".timeout",
        			config.getProperty("TEMPLATE_QUEUE.timeout",""+DEFAULT_HANDLE_QUEUE_TIMEOUT));
        	int locksCount= Tools.getInt( lcs, DEFAULT_HANDLE_QUEUE_LOCKS );
        	
        	// -1 means no need to do lock
        	if( locksCount ==-1) return null;
        	
        	int timeout= Tools.getInt(lct, DEFAULT_HANDLE_QUEUE_TIMEOUT);
        	lm= new LockManager(locksCount, timeout, command);
    		lockManagers.put(command, lm);
    		logger.info("Created LockManager("+command+", "+ locksCount+", "+ timeout+")");
    	}
    	return lm;
    }
    /**
     * 
     * @param lock should be the one returned by waitForLock(NDSEvent)
     * if null, will do nothing
     */
    private void releaseLock(Object l) throws NDSException{
    	if(l ==null) return;
    	try{
	    	LockManager lm=  (LockManager)((Object[])l)[0];
	    	lm.releaseLock(((Object[])l)[1]);
    	}catch(Throwable t){
    		throw new NDSException("Error release lock:"+ t);
    	}
    }
    /**
    * frees up all the resources associated with this controller and
    * destroys itself.
    */
    public synchronized void remove() {
        try{
        	this.ccInstance.remove();
        }catch(Throwable t){
        	logger.error("Unable to remove controller ", t);
        }
        ccInstance=null;
        // call ejb remove on self/shopping cart/etc.
        /*try {
            if(controllerEjbHandle !=null ) {
                EJBObject obj=controllerEjbHandle.getEJBObject();
                obj.remove();
            }
        } catch(RemoveException re) {
            // ignore, after all its only a remove() call!
            logger.error("Unable to remove controller ejb", re);
        }
        catch(RemoteException re) {
            // ignore, after all its only a remove() call!
            logger.error("Unable to remove controller ejb", re);
        }*/
        controllerEjbHandle=null;
    }
    public String toDebugInfo(){
    	StringBuffer sb=new StringBuffer();
    	for(Iterator it= lockManagers.values().iterator();it.hasNext();){
    		sb.append("["+it.next()+"]");
    	}
    	return sb.toString();
    }
}


