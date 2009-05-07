package nds.model.base;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import nds.model.dao.SpoExportLogDAO;

/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an automatically generated DAO class which should not be edited.
 */
public abstract class BaseSpoExportLogDAO extends nds.model.dao._RootDAO {

	public static SpoExportLogDAO instance;

	/**
	 * Return a singleton of the DAO
	 */
	public static SpoExportLogDAO getInstance () {
		if (null == instance) instance = new SpoExportLogDAO();
		return instance;
	}

	/**
	 * nds.model.dao._RootDAO _RootDAO.getReferenceClass()
	 */
	public Class getReferenceClass () {
		return nds.model.SpoExportLog.class;
	}
	
	public nds.model.SpoExportLog load(java.lang.Integer key)
		throws org.hibernate.HibernateException {
		return (nds.model.SpoExportLog) load(getReferenceClass(), key);
	}

	public nds.model.SpoExportLog load(java.lang.Integer key, Session s)
		throws org.hibernate.HibernateException {
		return (nds.model.SpoExportLog) load(getReferenceClass(), key, s);
	}

	public nds.model.SpoExportLog loadInitialize(java.lang.Integer key, Session s) 
			throws org.hibernate.HibernateException { 
		nds.model.SpoExportLog obj = load(key, s); 
		if (!Hibernate.isInitialized(obj)) {
			Hibernate.initialize(obj);
		} 
		return obj; 
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param spoExportLog a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(nds.model.SpoExportLog spoExportLog)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) super.save(spoExportLog);
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * Use the Session given.
	 * @param spoExportLog a transient instance of a persistent class
	 * @param s the Session
	 * @return the class identifier
	 */
	public java.lang.Integer save(nds.model.SpoExportLog spoExportLog, Session s)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) super.save(spoExportLog, s);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default
	 * the instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the
	 * identifier property mapping. 
	 * @param spoExportLog a transient instance containing new or updated state 
	 */
	public void saveOrUpdate(nds.model.SpoExportLog spoExportLog)
		throws org.hibernate.HibernateException {
		super.saveOrUpdate(spoExportLog);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default the
	 * instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the identifier
	 * property mapping. 
	 * Use the Session given.
	 * @param spoExportLog a transient instance containing new or updated state.
	 * @param s the Session.
	 */
	public void saveOrUpdate(nds.model.SpoExportLog spoExportLog, Session s)
		throws org.hibernate.HibernateException {
		super.saveOrUpdate(spoExportLog, s);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param spoExportLog a transient instance containing updated state
	 */
	public void update(nds.model.SpoExportLog spoExportLog) 
		throws org.hibernate.HibernateException {
		super.update(spoExportLog);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * Use the Session given.
	 * @param spoExportLog a transient instance containing updated state
	 * @param the Session
	 */
	public void update(nds.model.SpoExportLog spoExportLog, Session s)
		throws org.hibernate.HibernateException {
		super.update(spoExportLog, s);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param id the instance ID to be removed
	 */
	public void delete(java.lang.Integer id)
		throws org.hibernate.HibernateException {
		super.delete(load(id));
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * Use the Session given.
	 * @param id the instance ID to be removed
	 * @param s the Session
	 */
	public void delete(java.lang.Integer id, Session s)
		throws org.hibernate.HibernateException {
		super.delete(load(id, s), s);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * @param spoExportLog the instance to be removed
	 */
	public void delete(nds.model.SpoExportLog spoExportLog)
		throws org.hibernate.HibernateException {
		super.delete(spoExportLog);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * Use the Session given.
	 * @param spoExportLog the instance to be removed
	 * @param s the Session
	 */
	public void delete(nds.model.SpoExportLog spoExportLog, Session s)
		throws org.hibernate.HibernateException {
		super.delete(spoExportLog, s);
	}
	
	/**
	 * Re-read the state of the given instance from the underlying database. It is inadvisable to use this to implement
	 * long-running sessions that span many business tasks. This method is, however, useful in certain special circumstances.
	 * For example 
	 * <ul> 
	 * <li>where a database trigger alters the object state upon insert or update</li>
	 * <li>after executing direct SQL (eg. a mass update) in the same session</li>
	 * <li>after inserting a Blob or Clob</li>
	 * </ul>
	 */
	public void refresh (nds.model.SpoExportLog spoExportLog, Session s)
		throws org.hibernate.HibernateException {
		super.refresh(spoExportLog, s);
	}

    public String getDefaultOrderProperty () {
		return null;
    }

}