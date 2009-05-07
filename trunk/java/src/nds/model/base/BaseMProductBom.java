package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the M_PRODUCT_BOM table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="M_PRODUCT_BOM"
 */
public abstract class BaseMProductBom  implements Serializable {

	public static String PROP_M_PRODUCT_ID = "MProductId";
	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_M_PRODUCT = "MProduct";
	public static String PROP_AD_ORG = "AdOrg";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_M_PRODUCTBOM = "MProductbom";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_LINE = "Line";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_BOM_TYPE = "BomType";
	public static String PROP_M_PRODUCTBOM_ID = "MProductbomId";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_ID = "Id";
	public static String PROP_BOMQTY = "Bomqty";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.Integer _mProductId;
	private java.lang.Integer _mProductbomId;
	private java.lang.Integer _line;
	private java.lang.Integer _bomqty;
	private java.lang.String _description;
	private java.lang.String _bomType;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;

	// many to one
	private nds.model.AdOrg _adOrg;
	private nds.model.MProduct _mProduct;
	private nds.model.MProduct _mProductbom;


	// constructors
	public BaseMProductBom () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseMProductBom (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseMProductBom (
		java.lang.Integer _id,
		java.lang.String _bomType,
		java.lang.String _isActive) {

		this.setId(_id);
		this.setBomType(_bomType);
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
	 * Return the value associated with the column: M_PRODUCT_ID
	 */
	public java.lang.Integer getMProductId () {
		return _mProductId;
	}

	/**
	 * Set the value related to the column: M_PRODUCT_ID
	 * @param _mProductId the M_PRODUCT_ID value
	 */
	public void setMProductId (java.lang.Integer _mProductId) {
		this._mProductId = _mProductId;
	}


	/**
	 * Return the value associated with the column: M_PRODUCTBOM_ID
	 */
	public java.lang.Integer getMProductbomId () {
		return _mProductbomId;
	}

	/**
	 * Set the value related to the column: M_PRODUCTBOM_ID
	 * @param _mProductbomId the M_PRODUCTBOM_ID value
	 */
	public void setMProductbomId (java.lang.Integer _mProductbomId) {
		this._mProductbomId = _mProductbomId;
	}


	/**
	 * Return the value associated with the column: LINE
	 */
	public java.lang.Integer getLine () {
		return _line;
	}

	/**
	 * Set the value related to the column: LINE
	 * @param _line the LINE value
	 */
	public void setLine (java.lang.Integer _line) {
		this._line = _line;
	}


	/**
	 * Return the value associated with the column: BOMQTY
	 */
	public java.lang.Integer getBomqty () {
		return _bomqty;
	}

	/**
	 * Set the value related to the column: BOMQTY
	 * @param _bomqty the BOMQTY value
	 */
	public void setBomqty (java.lang.Integer _bomqty) {
		this._bomqty = _bomqty;
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
	 * Return the value associated with the column: BOMTYPE
	 */
	public java.lang.String getBomType () {
		return _bomType;
	}

	/**
	 * Set the value related to the column: BOMTYPE
	 * @param _bomType the BOMTYPE value
	 */
	public void setBomType (java.lang.String _bomType) {
		this._bomType = _bomType;
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
     *  column=M_PRODUCT_ID
	 */
	public nds.model.MProduct getMProduct () {
		return this._mProduct;
	}

	/**
	 * Set the value related to the column: M_PRODUCT_ID
	 * @param _mProduct the M_PRODUCT_ID value
	 */
	public void setMProduct (nds.model.MProduct _mProduct) {
		this._mProduct = _mProduct;
	}


	/**
     * @hibernate.property
     *  column=M_PRODUCTBOM_ID
	 */
	public nds.model.MProduct getMProductbom () {
		return this._mProductbom;
	}

	/**
	 * Set the value related to the column: M_PRODUCTBOM_ID
	 * @param _mProductbom the M_PRODUCTBOM_ID value
	 */
	public void setMProductbom (nds.model.MProduct _mProductbom) {
		this._mProductbom = _mProductbom;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseMProductBom)) return false;
		else {
			nds.model.base.BaseMProductBom mObj = (nds.model.base.BaseMProductBom) obj;
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