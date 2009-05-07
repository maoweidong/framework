package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the C_CROSSORDERITEM table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="C_CROSSORDERITEM"
 */
public abstract class BaseCCrossorderItem  implements Serializable {

	public static String PROP_STATUS = "Status";
	public static String PROP_M_PRODUCT_ID = "MProductId";
	public static String PROP_AMT_PO = "AmtPo";
	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_M_PRODUCT = "MProduct";
	public static String PROP_REF_CROSSORDER_ITEM_ID = "RefCrossorderItemId";
	public static String PROP_C_UOM = "CUom";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_PRICE_PO = "PricePo";
	public static String PROP_LINE = "Line";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_M_ATTRIBUTE_SET_INSTANCE_ID = "MAttributeSetInstanceId";
	public static String PROP_C_UOM_ID = "CUomId";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_AMT_SO = "AmtSo";
	public static String PROP_QTY = "Qty";
	public static String PROP_C_TAX_ID = "CTaxId";
	public static String PROP_AD_ORG = "AdOrg";
	public static String PROP_C_CROSSORDER = "CCrossorder";
	public static String PROP_REF_CROSSORDER_ITEM = "RefCrossorderItem";
	public static String PROP_AMT_DIFF = "AmtDiff";
	public static String PROP_PRICE_DIFF = "PriceDiff";
	public static String PROP_M_ATTRIBUTE_SET_INSTANCE = "MAttributeSetInstance";
	public static String PROP_C_CROSSORDER_ID = "CCrossorderId";
	public static String PROP_C_TAX = "CTax";
	public static String PROP_PRICE_SO = "PriceSo";
	public static String PROP_ID = "Id";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.Integer _cCrossorderId;
	private java.lang.Integer _line;
	private java.lang.String _description;
	private java.lang.Integer _mProductId;
	private java.lang.Integer _mAttributeSetInstanceId;
	private java.lang.Integer _cUomId;
	private java.lang.Integer _refCrossorderItemId;
	private java.lang.Integer _qty;
	private double _pricePo;
	private double _priceSo;
	private double _priceDiff;
	private double _amtPo;
	private double _amtSo;
	private double _amtDiff;
	private java.lang.Integer _cTaxId;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;
	private java.lang.Integer _status;

	// many to one
	private nds.model.AdOrg _adOrg;
	private nds.model.CCrossorder _cCrossorder;
	private nds.model.MProduct _mProduct;
	private nds.model.MAttributeSetInstance _mAttributeSetInstance;
	private nds.model.CUom _cUom;
	private nds.model.CCrossorderItem _refCrossorderItem;
	private nds.model.CTax _cTax;


	// constructors
	public BaseCCrossorderItem () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCCrossorderItem (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCCrossorderItem (
		java.lang.Integer _id,
		java.lang.Integer _qty,
		java.lang.String _isActive) {

		this.setId(_id);
		this.setQty(_qty);
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
	 * Return the value associated with the column: C_CROSSORDER_ID
	 */
	public java.lang.Integer getCCrossorderId () {
		return _cCrossorderId;
	}

	/**
	 * Set the value related to the column: C_CROSSORDER_ID
	 * @param _cCrossorderId the C_CROSSORDER_ID value
	 */
	public void setCCrossorderId (java.lang.Integer _cCrossorderId) {
		this._cCrossorderId = _cCrossorderId;
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
	 * Return the value associated with the column: M_ATTRIBUTESETINSTANCE_ID
	 */
	public java.lang.Integer getMAttributeSetInstanceId () {
		return _mAttributeSetInstanceId;
	}

	/**
	 * Set the value related to the column: M_ATTRIBUTESETINSTANCE_ID
	 * @param _mAttributeSetInstanceId the M_ATTRIBUTESETINSTANCE_ID value
	 */
	public void setMAttributeSetInstanceId (java.lang.Integer _mAttributeSetInstanceId) {
		this._mAttributeSetInstanceId = _mAttributeSetInstanceId;
	}


	/**
	 * Return the value associated with the column: C_UOM_ID
	 */
	public java.lang.Integer getCUomId () {
		return _cUomId;
	}

	/**
	 * Set the value related to the column: C_UOM_ID
	 * @param _cUomId the C_UOM_ID value
	 */
	public void setCUomId (java.lang.Integer _cUomId) {
		this._cUomId = _cUomId;
	}


	/**
	 * Return the value associated with the column: REF_CROSSORDERITEM_ID
	 */
	public java.lang.Integer getRefCrossorderItemId () {
		return _refCrossorderItemId;
	}

	/**
	 * Set the value related to the column: REF_CROSSORDERITEM_ID
	 * @param _refCrossorderItemId the REF_CROSSORDERITEM_ID value
	 */
	public void setRefCrossorderItemId (java.lang.Integer _refCrossorderItemId) {
		this._refCrossorderItemId = _refCrossorderItemId;
	}


	/**
	 * Return the value associated with the column: QTY
	 */
	public java.lang.Integer getQty () {
		return _qty;
	}

	/**
	 * Set the value related to the column: QTY
	 * @param _qty the QTY value
	 */
	public void setQty (java.lang.Integer _qty) {
		this._qty = _qty;
	}


	/**
	 * Return the value associated with the column: PRICE_PO
	 */
	public double getPricePo () {
		return _pricePo;
	}

	/**
	 * Set the value related to the column: PRICE_PO
	 * @param _pricePo the PRICE_PO value
	 */
	public void setPricePo (double _pricePo) {
		this._pricePo = _pricePo;
	}


	/**
	 * Return the value associated with the column: PRICE_SO
	 */
	public double getPriceSo () {
		return _priceSo;
	}

	/**
	 * Set the value related to the column: PRICE_SO
	 * @param _priceSo the PRICE_SO value
	 */
	public void setPriceSo (double _priceSo) {
		this._priceSo = _priceSo;
	}


	/**
	 * Return the value associated with the column: PRICE_DIFF
	 */
	public double getPriceDiff () {
		return _priceDiff;
	}

	/**
	 * Set the value related to the column: PRICE_DIFF
	 * @param _priceDiff the PRICE_DIFF value
	 */
	public void setPriceDiff (double _priceDiff) {
		this._priceDiff = _priceDiff;
	}


	/**
	 * Return the value associated with the column: AMT_PO
	 */
	public double getAmtPo () {
		return _amtPo;
	}

	/**
	 * Set the value related to the column: AMT_PO
	 * @param _amtPo the AMT_PO value
	 */
	public void setAmtPo (double _amtPo) {
		this._amtPo = _amtPo;
	}


	/**
	 * Return the value associated with the column: AMT_SO
	 */
	public double getAmtSo () {
		return _amtSo;
	}

	/**
	 * Set the value related to the column: AMT_SO
	 * @param _amtSo the AMT_SO value
	 */
	public void setAmtSo (double _amtSo) {
		this._amtSo = _amtSo;
	}


	/**
	 * Return the value associated with the column: AMT_DIFF
	 */
	public double getAmtDiff () {
		return _amtDiff;
	}

	/**
	 * Set the value related to the column: AMT_DIFF
	 * @param _amtDiff the AMT_DIFF value
	 */
	public void setAmtDiff (double _amtDiff) {
		this._amtDiff = _amtDiff;
	}


	/**
	 * Return the value associated with the column: C_TAX_ID
	 */
	public java.lang.Integer getCTaxId () {
		return _cTaxId;
	}

	/**
	 * Set the value related to the column: C_TAX_ID
	 * @param _cTaxId the C_TAX_ID value
	 */
	public void setCTaxId (java.lang.Integer _cTaxId) {
		this._cTaxId = _cTaxId;
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
	 * Return the value associated with the column: STATUS
	 */
	public java.lang.Integer getStatus () {
		return _status;
	}

	/**
	 * Set the value related to the column: STATUS
	 * @param _status the STATUS value
	 */
	public void setStatus (java.lang.Integer _status) {
		this._status = _status;
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
     *  column=C_CROSSORDER_ID
	 */
	public nds.model.CCrossorder getCCrossorder () {
		return this._cCrossorder;
	}

	/**
	 * Set the value related to the column: C_CROSSORDER_ID
	 * @param _cCrossorder the C_CROSSORDER_ID value
	 */
	public void setCCrossorder (nds.model.CCrossorder _cCrossorder) {
		this._cCrossorder = _cCrossorder;
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
     *  column=M_ATTRIBUTESETINSTANCE_ID
	 */
	public nds.model.MAttributeSetInstance getMAttributeSetInstance () {
		return this._mAttributeSetInstance;
	}

	/**
	 * Set the value related to the column: M_ATTRIBUTESETINSTANCE_ID
	 * @param _mAttributeSetInstance the M_ATTRIBUTESETINSTANCE_ID value
	 */
	public void setMAttributeSetInstance (nds.model.MAttributeSetInstance _mAttributeSetInstance) {
		this._mAttributeSetInstance = _mAttributeSetInstance;
	}


	/**
     * @hibernate.property
     *  column=C_UOM_ID
	 */
	public nds.model.CUom getCUom () {
		return this._cUom;
	}

	/**
	 * Set the value related to the column: C_UOM_ID
	 * @param _cUom the C_UOM_ID value
	 */
	public void setCUom (nds.model.CUom _cUom) {
		this._cUom = _cUom;
	}


	/**
     * @hibernate.property
     *  column=REF_CROSSORDERITEM_ID
	 */
	public nds.model.CCrossorderItem getRefCrossorderItem () {
		return this._refCrossorderItem;
	}

	/**
	 * Set the value related to the column: REF_CROSSORDERITEM_ID
	 * @param _refCrossorderItem the REF_CROSSORDERITEM_ID value
	 */
	public void setRefCrossorderItem (nds.model.CCrossorderItem _refCrossorderItem) {
		this._refCrossorderItem = _refCrossorderItem;
	}


	/**
     * @hibernate.property
     *  column=C_TAX_ID
	 */
	public nds.model.CTax getCTax () {
		return this._cTax;
	}

	/**
	 * Set the value related to the column: C_TAX_ID
	 * @param _cTax the C_TAX_ID value
	 */
	public void setCTax (nds.model.CTax _cTax) {
		this._cTax = _cTax;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseCCrossorderItem)) return false;
		else {
			nds.model.base.BaseCCrossorderItem mObj = (nds.model.base.BaseCCrossorderItem) obj;
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