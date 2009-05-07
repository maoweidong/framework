package nds.model.base;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import nds.model.dao.CChargeDAO;

/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an automatically generated DAO class which should not be edited.
 */
public abstract class BaseCChargeDAO extends nds.model.dao._RootDAO {

	public static CChargeDAO instance;

	/**
	 * Return a singleton of the DAO
	 */
	public static CChargeDAO getInstance () {
		if (null == instance) instance = new CChargeDAO();
		return instance;
	}

	/**
	 * nds.model.dao._RootDAO _RootDAO.getReferenceClass()
	 */
	public Class getReferenceClass () {
		return nds.model.CCharge.class;
	}
	
	public nds.model.CCharge load(java.lang.Integer key)
		throws org.hibernate.HibernateException {
		return (nds.model.CCharge) load(getReferenceClass(), key);
	}

	public nds.model.CCharge load(java.lang.Integer key, Session s)
		throws org.hibernate.HibernateException {
		return (nds.model.CCharge) load(getReferenceClass(), key, s);
	}

	public nds.model.CCharge loadInitialize(java.lang.Integer key, Session s) 
			throws org.hibernate.HibernateException { 
		nds.model.CCharge obj = load(key, s); 
		if (!Hibernate.isInitialized(obj)) {
			Hibernate.initialize(obj);
		} 
		return obj; 
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * @param cCharge a transient instance of a persistent class 
	 * @return the class identifier
	 */
	public java.lang.Integer save(nds.model.CCharge cCharge)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) super.save(cCharge);
	}

	/**
	 * Persist the given transient instance, first assigning a generated identifier. (Or using the current value
	 * of the identifier property if the assigned generator is used.) 
	 * Use the Session given.
	 * @param cCharge a transient instance of a persistent class
	 * @param s the Session
	 * @return the class identifier
	 */
	public java.lang.Integer save(nds.model.CCharge cCharge, Session s)
		throws org.hibernate.HibernateException {
		return (java.lang.Integer) super.save(cCharge, s);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default
	 * the instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the
	 * identifier property mapping. 
	 * @param cCharge a transient instance containing new or updated state 
	 */
	public void saveOrUpdate(nds.model.CCharge cCharge)
		throws org.hibernate.HibernateException {
		super.saveOrUpdate(cCharge);
	}

	/**
	 * Either save() or update() the given instance, depending upon the value of its identifier property. By default the
	 * instance is always saved. This behaviour may be adjusted by specifying an unsaved-value attribute of the identifier
	 * property mapping. 
	 * Use the Session given.
	 * @param cCharge a transient instance containing new or updated state.
	 * @param s the Session.
	 */
	public void saveOrUpdate(nds.model.CCharge cCharge, Session s)
		throws org.hibernate.HibernateException {
		super.saveOrUpdate(cCharge, s);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * @param cCharge a transient instance containing updated state
	 */
	public void update(nds.model.CCharge cCharge) 
		throws org.hibernate.HibernateException {
		super.update(cCharge);
	}

	/**
	 * Update the persistent state associated with the given identifier. An exception is thrown if there is a persistent
	 * instance with the same identifier in the current session.
	 * Use the Session given.
	 * @param cCharge a transient instance containing updated state
	 * @param the Session
	 */
	public void update(nds.model.CCharge cCharge, Session s)
		throws org.hibernate.HibernateException {
		super.update(cCharge, s);
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
	 * @param cCharge the instance to be removed
	 */
	public void delete(nds.model.CCharge cCharge)
		throws org.hibernate.HibernateException {
		super.delete(cCharge);
	}

	/**
	 * Remove a persistent instance from the datastore. The argument may be an instance associated with the receiving
	 * Session or a transient instance with an identifier associated with existing persistent state. 
	 * Use the Session given.
	 * @param cCharge the instance to be removed
	 * @param s the Session
	 */
	public void delete(nds.model.CCharge cCharge, Session s)
		throws org.hibernate.HibernateException {
		super.delete(cCharge, s);
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
	public void refresh (nds.model.CCharge cCharge, Session s)
		throws org.hibernate.HibernateException {
		super.refresh(cCharge, s);
	}

    public String getDefaultOrderProperty () {
		return "Name";
    }

}