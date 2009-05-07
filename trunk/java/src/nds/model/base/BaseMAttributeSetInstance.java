package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the M_ATTRIBUTESETINSTANCE table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="M_ATTRIBUTESETINSTANCE"
 */
public abstract class BaseMAttributeSetInstance  implements Serializable {

	public static String PROP_GUARANTEE_DATE = "GuaranteeDate";
	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_AD_ORG = "AdOrg";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_SERNO = "Serno";
	public static String PROP_M_LOT = "MLot";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_M_LOT_ID = "MLotId";
	public static String PROP_LOT = "Lot";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_ID = "Id";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.String _serno;
	private java.lang.String _lot;
	private java.util.Date _guaranteeDate;
	private java.lang.String _description;
	private java.lang.Integer _mLotId;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;

	// many to one
	private nds.model.AdOrg _adOrg;
	private nds.model.MLot _mLot;


	// constructors
	public BaseMAttributeSetInstance () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMAttributeSetInstance (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMAttributeSetInstance (
		java.lang.Integer _id,
		java.lang.String _serno,
		java.lang.String _isActive) {

		this.setId(_id);
		this.setSerno(_serno);
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
	 * Return the value associated with the column: SERNO
	 */
	public java.lang.String getSerno () {
		return _serno;
	}

	/**
	 * Set the value related to the column: SERNO
	 * @param _serno the SERNO value
	 */
	public void setSerno (java.lang.String _serno) {
		this._serno = _serno;
	}


	/**
	 * Return the value associated with the column: LOT
	 */
	public java.lang.String getLot () {
		return _lot;
	}

	/**
	 * Set the value related to the column: LOT
	 * @param _lot the LOT value
	 */
	public void setLot (java.lang.String _lot) {
		this._lot = _lot;
	}


	/**
	 * Return the value associated with the column: GUARANTEEDATE
	 */
	public java.util.Date getGuaranteeDate () {
		return _guaranteeDate;
	}

	/**
	 * Set the value related to the column: GUARANTEEDATE
	 * @param _guaranteeDate the GUARANTEEDATE value
	 */
	public void setGuaranteeDate (java.util.Date _guaranteeDate) {
		this._guaranteeDate = _guaranteeDate;
	}


	/**
	 * Return the value associated with the column: DESCRIPTION
	 */
	public java.lang.String getDescription () {
		return _description;
	}

	/**
	 * Set the value related to the column: DESCRIPTION
	 * @param _description the DESCRIPTION value
	 */
	public void setDescription (java.lang.String _description) {
		this._description = _description;
	}


	/**
	 * Return the value associated with the column: M_LOT_ID
	 */
	public java.lang.Integer getMLotId () {
		return _mLotId;
	}

	/**
	 * Set the value related to the column: M_LOT_ID
	 * @param _mLotId the M_LOT_ID value
	 */
	public void setMLotId (java.lang.Integer _mLotId) {
		this._mLotId = _mLotId;
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
     *  column=M_LOT_ID
	 */
	public nds.model.MLot getMLot () {
		return this._mLot;
	}

	/**
	 * Set the value related to the column: M_LOT_ID
	 * @param _mLot the M_LOT_ID value
	 */
	public void setMLot (nds.model.MLot _mLot) {
		this._mLot = _mLot;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseMAttributeSetInstance)) return false;
		else {
			nds.model.base.BaseMAttributeSetInstance mObj = (nds.model.base.BaseMAttributeSetInstance) obj;
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