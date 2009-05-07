package nds.export;

import java.util.NoSuchElementException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import nds.control.web.test.RandomGen;
import nds.log.Logger;
import nds.log.LoggerManager;
import nds.net.Scheduler;
import nds.net.ThreadProcess;
import nds.query.QueryEngine;
import nds.util.*;
/**
����������ƣ�
�첽����ʽ������������
��������е�δ��������������ָ���������׳��쳣�ܾ���Ӧ������ǰ������������̫�࣬���Ժ����ԣ������˴����ж��й���
������еĹ������Է���ķ�ʽ�����ں�̨��ÿ���һ��ʱ�����Ƿ��еȴ���������У�������ִ�����񡣹���Ա�������ü��ʱ��
��������ҿ���������������ж��ٸ�����ͬʱ��ִ�С���һ������ͬʱ����������һ�����񽫼������ʱ�䱻������
���磬��������ϵͳÿ��5���Ӽ��һ�ζ��У���ÿ�����������Ϊ3��������Ϊ2�롣����ϵͳ����20������δִ�У�����ÿ�ν�ִ��3��
����ÿ�����������ϵͳ���ȴ�2�룬Ȼ��������һ����������������ʹ��ϵͳ�ĵ������ز�����̫�ߣ��Ա���������ҵ������ִ�С�
 */
public class ExportManager implements Runnable {
    private static final String GET_ONE_JOB="select id,no from (select id,no from spo_export order by priority desc, creationdate asc) where rownum<2";
    private Logger logger= LoggerManager.getInstance().getLogger(this.getClass().getName());
    private ObjectQueue idleWorkerQueue; //elements are ExportWorker
    private ExportMBean bean;
    private boolean stop=true;
    public ExportManager(ExportMBean bean) {
    	this.bean=bean; 
    	idleWorkerQueue=new ObjectQueue(bean.getWorkerCount());
    	idleWorkerQueue.setInDataPreparing(true);
    	//put workers in
    	for(int i=0;i< bean.getWorkerCount();i++)
    		idleWorkerQueue.addElement(new ExportWorker(idleWorkerQueue, Sequences.getNextID("ExportWorker")+""));
    	
    }
    
    /**
     * 
     * @return -1 if current no waiting job
     */
    private int getWaitJobId(){
    	//int[] is=new int[]{-1, 3, 2,4};
    	//if(true)return nds.control.web.test.RandomGen.getInt(is);
        int jobId=-1;
    	Connection conn=null;
        PreparedStatement stmt=null;
        ResultSet rs=null;
        try{
            conn= QueryEngine.getInstance().getConnection();
            stmt= conn.prepareStatement(GET_ONE_JOB);
            rs=stmt.executeQuery();
            if( !rs.next()) {
            	//logger.debug("Found no jobs waiting");
            }else
            	jobId= rs.getInt(1);
    	
        }catch(Exception e){
            logger.error("Error exporting data.", e);
        }finally{
        	try{if( rs !=null) rs.close();}catch(Exception e2){}
            try{if( stmt !=null) stmt.close();}catch(Exception e2){}
            try{if( conn!=null)conn.close();}catch(Exception  ee){}
        }
        return jobId;
    }
    /*
     * Wait every time interval to check for unhandling jobs
     */
    public void run(){
    	this.stop=false;
        while(stop==false){
        	execute();
        	try{
        		Thread.sleep(bean.getCheckInterval()*1000);
        	}catch(Exception e){
        		
        	}
        }
        logger.info("ExportManager stopped.");
    }
    public void stop(){
    	this.stop=true;
    }
    /**
     * Handle all jobs currently in job queue
     */
    public void execute() {
        int jobId, queueLength;
        ExportWorker worker=null;
	        while(!stop){
	        	//MBean.workerCount may be changed during runtime
	        	//if idleWorkerQueue's maxLength is not equal to MBean.workerCount,
	        	//then the queue size should be changed.
	        	queueLength=idleWorkerQueue.getMaxLength();
	        	if(queueLength != bean.getWorkerCount()){
	        		if( queueLength> bean.getWorkerCount()){
	        			// discard some workers
	        			for(int i=0;i<queueLength-bean.getWorkerCount();i++)
	        				idleWorkerQueue.nextElement();
	        		}else{
	        			// add some workers
	        			for(int i=0;i<bean.getWorkerCount()-queueLength;i++)
	        				idleWorkerQueue.addElement(new ExportWorker(idleWorkerQueue, Sequences.getNextID("ExportWorker")+""));
	        		}
	        	}
	            // get idle worker
	        	worker=(ExportWorker)idleWorkerQueue.nextElement();
	        	
	        	// sleep job interval seconds before work
	            //logger.debug("Sleep for " +jobInterval +" seconds between jobs");
	        	try{
	        		Thread.sleep(bean.getJobInterval()*1000);
	        	}catch(Exception e){}
	        	jobId= getWaitJobId();
	        	if(jobId ==-1) {
	        		//logger.debug("Found no job waiting");
	        		// put idle worker back to queue
	        		idleWorkerQueue.addElement(worker);
	        		break;
	        	}
	            logger.debug("Found job(id="+jobId+"), current idle workers:"+(idleWorkerQueue.size()+1) );
	            // after worker finished the job, it will return to idle queue for next job request
	        	worker.handleJob(jobId);
	        }
        
    }
	
}
