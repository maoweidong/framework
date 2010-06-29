package nds.io;

import nds.util.*;
import nds.io.scanner.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import javax.swing.event.EventListenerList;


import nds.control.web.WebUtils;
import nds.log.Logger;
import nds.log.LoggerManager;

/**
 * ����ServiceLoaderʵ��
 * 
 * ����һ��������classloader��Ŀ¼�����������л���Ŀ¼��jar���ļ���
 * 1��jar ���п����ж��plugin����һ��pluginmanager��ص�Ŀ¼�з�����jar �ļ��ĸı�
 * Ŀǰ��򵥵�ʵ�����ǽ�����plugin���������Ȼ�����¼���
 * 
 * ���õķ�ʽ���������ÿ��jar ������ȫ��jar���б����أ���ж�ر����jar�е�plugin
 * 
 * 
 * @author yfzhu@agilecontrol.com
 *
 */
public class PluginManager<S> implements DeploymentListener {
    private static Logger logger= LoggerManager.getInstance().getLogger(PluginManager.class.getName());
	

	private Class<S> clazz;
	private PluginScanner scanner;
	private ServiceLoader<S> serviceLoader; 
	private Hashtable<String, S> plugins;
	/**
	 * 
	 * @param clz the plugin class 
	 */
	public PluginManager(Class<S> clz, PluginScanner scanner){
		this.clazz=clz;
		this.scanner=scanner;
		plugins=new Hashtable();
	}
	/**
	 * Call this when plugin manager no longer used 
	 */
	public void destroy() throws Exception{
		scanner.removeDeploymentListener(this);
		plugins.clear();
		serviceLoader.reload();
		serviceLoader=null;
		logger.debug("PluginManager for "+ clazz.getName()+" destroied");
	}
	/**
	 * create thread to monitor scan paths
	 */
	public synchronized void init() {
		if(serviceLoader!=null){
			throw new NDSRuntimeException("Plugin manager already initialized");
		}
		
		scanner.addDeploymentListener(this);
		
		reload();
	}
	
	public void urlDeployed(DeploymentEvent det){reload();}
    public void urlModified(DeploymentEvent de){reload();}
    public void urlRemoved(DeploymentEvent de){reload();}
	
	
	/**
	 * clear all cache and reload from disk, when
	 * scaner find any jar changes, this method will be called
	 * 
	 */
	public void reload(){
		
		try{
		synchronized(plugins){
			plugins.clear();

			if(serviceLoader!=null){
				serviceLoader.reload();
			}
			serviceLoader =ServiceLoader.load(clazz, scanner.getPluginClassLoader());
			
			for(S plugin:serviceLoader){
				plugins.put(plugin.getClass().getName(), plugin);
				logger.debug("load "+ plugin.getClass().getName());
			}
			
		}
		}catch(Throwable t){
			logger.error("Fail to reload plugin manager of class "+ this.clazz.getName(), t);
		}
	}
	/**
	 * Find plugin object of specfied class. 
	 * @param name class name
	 * @return null if not found
	 */
	public S findPlugin(String name){
		return plugins.get(name);
	}

	
}
