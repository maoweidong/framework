package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the U_GROUPUSER table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="U_GROUPUSER"
 */
public abstract class BaseUGroupuser  implements Serializable {

	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_AD_ORG = "AdOrg";
	public static String PROP_USER_ID = "UserId";
	public static String PROP_U_GROUP = "UGroup";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_USER = "User";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_U_GROUP_ID = "UGroupId";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_ID = "Id";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.Integer _userId;
	private java.lang.Integer _uGroupId;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;

	// many to one
	private nds.model.AdOrg _adOrg;
	private nds.model.Users _user;
	private nds.model.UGroup _uGroup;


	// constructors
	public BaseUGroupuser () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUGroupuser (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseUGroupuser (
		java.lang.Integer _id,
		nds.model.Users _user,
		nds.model.UGroup _uGroup,
		java.lang.Integer _userId,
		java.lang.Integer _uGroupId,
		java.lang.String _isActive) {

		this.setId(_id);
		this.setUser(_user);
		this.setUGroup(_uGroup);
		this.setUserId(_userId);
		this.setUGroupId(_uGroupId);
		this.setIsActive(_isActive);
		initialize();
	}

	protected void initialize () {}



	/**
	 * Return the unique identifier of this class
     * @hibernate.id
     *  generator-class="sequence"
     *  column="ID"
     */
	public java.lang.Integer getId () {
		return _id;
	}

	/**
	 * Set the unique identifier of this class
	 * @param _id the new ID
	 */
	public void setId (java.lang.Integer _id) {
		this._id = _id;
		this.hashCode = Integer.MIN_VALUE;
	}


	/**
	 * Return the value associated with the column: AD_CLIENT_ID
	 */
	public java.lang.Integer getAdClientId () {
		return _adClientId;
	}

	/**
	 * Set the value related to the column: AD_CLIENT_ID
	 * @param _adClientId the AD_CLIENT_ID value
	 */
	public void setAdClientId (java.lang.Integer _adClientId) {
		this._adClientId = _adClientId;
	}


	/**
	 * Return the value associated with the column: AD_ORG_ID
	 */
	public java.lang.Integer getAdOrgId () {
		return _adOrgId;
	}

	/**
	 * Set the value related to the column: AD_ORG_ID
	 * @param _adOrgId the AD_ORG_ID value
	 */
	public void setAdOrgId (java.lang.Integer _adOrgId) {
		this._adOrgId = _adOrgId;
	}


	/**
	 * Return the value associated with the column: USER_ID
	 */
	public java.lang.Integer getUserId () {
		return _userId;
	}

	/**
	 * Set the value related to the column: USER_ID
	 * @param _userId the USER_ID value
	 */
	public void setUserId (java.lang.Integer _userId) {
		this._userId = _userId;
	}


	/**
	 * Return the value associated with the column: U_GROUP_ID
	 */
	public java.lang.Integer getUGroupId () {
		return _uGroupId;
	}

	/**
	 * Set the value related to the column: U_GROUP_ID
	 * @param _uGroupId the U_GROUP_ID value
	 */
	public void setUGroupId (java.lang.Integer _uGroupId) {
		this._uGroupId = _uGroupId;
	}


	/**
	 * Return the value associated with the column: OWNERID
	 */
	public java.lang.Integer getOwnerId () {
		return _ownerId;
	}

	/**
	 * Set the value related to the column: OWNERID
	 * @param _ownerId the OWNERID value
	 */
	public void setOwnerId (java.lang.Integer _ownerId) {
		this._ownerId = _ownerId;
	}


	/**
	 * Return the value associated with the column: MODIFIERID
	 */
	public java.lang.Integer getModifierId () {
		return _modifierId;
	}

	/**
	 * Set the value related to the column: MODIFIERID
	 * @param _modifierId the MODIFIERID value
	 */
	public void setModifierId (java.lang.Integer _modifierId) {
		this._modifierId = _modifierId;
	}


	/**
	 * Return the value associated with the column: CREATIONDATE
	 */
	public java.util.Date getCreationDate () {
		return _creationDate;
	}

	/**
	 * Set the value related to the column: CREATIONDATE
	 * @param _creationDate the CREATIONDATE value
	 */
	public void setCreationDate (java.util.Date _creationDate) {
		this._creationDate = _creationDate;
	}


	/**
	 * Return the value associated with the column: MODIFIEDDATE
	 */
	public java.util.Date getModifiedDate () {
		return _modifiedDate;
	}

	/**
	 * Set the value related to the column: MODIFIEDDATE
	 * @param _modifiedDate the MODIFIEDDATE value
	 */
	public void setModifiedDate (java.util.Date _modifiedDate) {
		this._modifiedDate = _modifiedDate;
	}


	/**
	 * Return the value associated with the column: ISACTIVE
	 */
	public java.lang.String getIsActive () {
		return _isActive;
	}

	/**
	 * Set the value related to the column: ISACTIVE
	 * @param _isActive the ISACTIVE value
	 */
	public void setIsActive (java.lang.String _isActive) {
		this._isActive = _isActive;
	}


	/**
     * @hibernate.property
     *  column=AD_ORG_ID
	 */
	public nds.model.AdOrg getAdOrg () {
		return this._adOrg;
	}

	/**
	 * Set the value related to the column: AD_ORG_ID
	 * @param _adOrg the AD_ORG_ID value
	 */
	public void setAdOrg (nds.model.AdOrg _adOrg) {
		this._adOrg = _adOrg;
	}


	/**
     * @hibernate.property
     *  column=USER_ID
	 * not-null=true
	 */
	public nds.model.Users getUser () {
		return this._user;
	}

	/**
	 * Set the value related to the column: USER_ID
	 * @param _user the USER_ID value
	 */
	public void setUser (nds.model.Users _user) {
		this._user = _user;
	}


	/**
     * @hibernate.property
     *  column=U_GROUP_ID
	 * not-null=true
	 */
	public nds.model.UGroup getUGroup () {
		return this._uGroup;
	}

	/**
	 * Set the value related to the column: U_GROUP_ID
	 * @param _uGroup the U_GROUP_ID value
	 */
	public void setUGroup (nds.model.UGroup _uGroup) {
		this._uGroup = _uGroup;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseUGroupuser)) return false;
		else {
			nds.model.base.BaseUGroupuser mObj = (nds.model.base.BaseUGroupuser) obj;
			if (null == this.getId() || null == mObj.getId()) return false;
			else return (this.getId().equals(mObj.getId()));
		}
	}


	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}

}