package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the C_TAX_ACCT table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="C_TAX_ACCT"
 */
public abstract class BaseCTaxAcct  implements Serializable {

	public static String PROP_C_ACCT_SCHEMA_ID = "CAcctSchemaId";
	public static String PROP_T_DUE_ACCT = "TDueAcct";
	public static String PROP_T_CREDIT_ACCT = "TCreditAcct";
	public static String PROP_T_EXPENSE_ACCT = "TExpenseAcct";
	public static String PROP_T_EXPENSE_ACCT_OBJ = "TExpenseAcctObj";
	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_AD_ORG = "AdOrg";
	public static String PROP_T_RECEIVABLES_ACCT = "TReceivablesAcct";
	public static String PROP_T_DUE_ACCT_OBJ = "TDueAcctObj";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_T_RECEIVABLES_ACCT_OBJ = "TReceivablesAcctObj";
	public static String PROP_T_CREDIT_ACCT_OBJ = "TCreditAcctObj";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_C_ACCT_SCHEMA = "CAcctSchema";
	public static String PROP_T_LIABILITY_ACCT_OBJ = "TLiabilityAcctObj";
	public static String PROP_T_LIABILITY_ACCT = "TLiabilityAcct";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_ID = "Id";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.Integer _cAcctSchemaId;
	private java.lang.Integer _tDueAcct;
	private java.lang.Integer _tLiabilityAcct;
	private java.lang.Integer _tCreditAcct;
	private java.lang.Integer _tReceivablesAcct;
	private java.lang.Integer _tExpenseAcct;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;

	// many to one
	private nds.model.AdOrg _adOrg;
	private nds.model.CAcctSchema _cAcctSchema;
	private nds.model.CValidCombination _tDueAcctObj;
	private nds.model.CValidCombination _tLiabilityAcctObj;
	private nds.model.CValidCombination _tCreditAcctObj;
	private nds.model.CValidCombination _tReceivablesAcctObj;
	private nds.model.CValidCombination _tExpenseAcctObj;


	// constructors
	public BaseCTaxAcct () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseCTaxAcct (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseCTaxAcct (
		java.lang.Integer _id,
		java.lang.String _isActive) {

		this.setId(_id);
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
	 * Return the value associated with the column: C_ACCTSCHEMA_ID
	 */
	public java.lang.Integer getCAcctSchemaId () {
		return _cAcctSchemaId;
	}

	/**
	 * Set the value related to the column: C_ACCTSCHEMA_ID
	 * @param _cAcctSchemaId the C_ACCTSCHEMA_ID value
	 */
	public void setCAcctSchemaId (java.lang.Integer _cAcctSchemaId) {
		this._cAcctSchemaId = _cAcctSchemaId;
	}


	/**
	 * Return the value associated with the column: T_DUE_ACCT
	 */
	public java.lang.Integer getTDueAcct () {
		return _tDueAcct;
	}

	/**
	 * Set the value related to the column: T_DUE_ACCT
	 * @param _tDueAcct the T_DUE_ACCT value
	 */
	public void setTDueAcct (java.lang.Integer _tDueAcct) {
		this._tDueAcct = _tDueAcct;
	}


	/**
	 * Return the value associated with the column: T_LIABILITY_ACCT
	 */
	public java.lang.Integer getTLiabilityAcct () {
		return _tLiabilityAcct;
	}

	/**
	 * Set the value related to the column: T_LIABILITY_ACCT
	 * @param _tLiabilityAcct the T_LIABILITY_ACCT value
	 */
	public void setTLiabilityAcct (java.lang.Integer _tLiabilityAcct) {
		this._tLiabilityAcct = _tLiabilityAcct;
	}


	/**
	 * Return the value associated with the column: T_CREDIT_ACCT
	 */
	public java.lang.Integer getTCreditAcct () {
		return _tCreditAcct;
	}

	/**
	 * Set the value related to the column: T_CREDIT_ACCT
	 * @param _tCreditAcct the T_CREDIT_ACCT value
	 */
	public void setTCreditAcct (java.lang.Integer _tCreditAcct) {
		this._tCreditAcct = _tCreditAcct;
	}


	/**
	 * Return the value associated with the column: T_RECEIVABLES_ACCT
	 */
	public java.lang.Integer getTReceivablesAcct () {
		return _tReceivablesAcct;
	}

	/**
	 * Set the value related to the column: T_RECEIVABLES_ACCT
	 * @param _tReceivablesAcct the T_RECEIVABLES_ACCT value
	 */
	public void setTReceivablesAcct (java.lang.Integer _tReceivablesAcct) {
		this._tReceivablesAcct = _tReceivablesAcct;
	}


	/**
	 * Return the value associated with the column: T_EXPENSE_ACCT
	 */
	public java.lang.Integer getTExpenseAcct () {
		return _tExpenseAcct;
	}

	/**
	 * Set the value related to the column: T_EXPENSE_ACCT
	 * @param _tExpenseAcct the T_EXPENSE_ACCT value
	 */
	public void setTExpenseAcct (java.lang.Integer _tExpenseAcct) {
		this._tExpenseAcct = _tExpenseAcct;
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
     *  column=C_ACCTSCHEMA_ID
	 */
	public nds.model.CAcctSchema getCAcctSchema () {
		return this._cAcctSchema;
	}

	/**
	 * Set the value related to the column: C_ACCTSCHEMA_ID
	 * @param _cAcctSchema the C_ACCTSCHEMA_ID value
	 */
	public void setCAcctSchema (nds.model.CAcctSchema _cAcctSchema) {
		this._cAcctSchema = _cAcctSchema;
	}


	/**
     * @hibernate.property
     *  column=T_DUE_ACCT
	 */
	public nds.model.CValidCombination getTDueAcctObj () {
		return this._tDueAcctObj;
	}

	/**
	 * Set the value related to the column: T_DUE_ACCT
	 * @param _tDueAcctObj the T_DUE_ACCT value
	 */
	public void setTDueAcctObj (nds.model.CValidCombination _tDueAcctObj) {
		this._tDueAcctObj = _tDueAcctObj;
	}


	/**
     * @hibernate.property
     *  column=T_LIABILITY_ACCT
	 */
	public nds.model.CValidCombination getTLiabilityAcctObj () {
		return this._tLiabilityAcctObj;
	}

	/**
	 * Set the value related to the column: T_LIABILITY_ACCT
	 * @param _tLiabilityAcctObj the T_LIABILITY_ACCT value
	 */
	public void setTLiabilityAcctObj (nds.model.CValidCombination _tLiabilityAcctObj) {
		this._tLiabilityAcctObj = _tLiabilityAcctObj;
	}


	/**
     * @hibernate.property
     *  column=T_CREDIT_ACCT
	 */
	public nds.model.CValidCombination getTCreditAcctObj () {
		return this._tCreditAcctObj;
	}

	/**
	 * Set the value related to the column: T_CREDIT_ACCT
	 * @param _tCreditAcctObj the T_CREDIT_ACCT value
	 */
	public void setTCreditAcctObj (nds.model.CValidCombination _tCreditAcctObj) {
		this._tCreditAcctObj = _tCreditAcctObj;
	}


	/**
     * @hibernate.property
     *  column=T_RECEIVABLES_ACCT
	 */
	public nds.model.CValidCombination getTReceivablesAcctObj () {
		return this._tReceivablesAcctObj;
	}

	/**
	 * Set the value related to the column: T_RECEIVABLES_ACCT
	 * @param _tReceivablesAcctObj the T_RECEIVABLES_ACCT value
	 */
	public void setTReceivablesAcctObj (nds.model.CValidCombination _tReceivablesAcctObj) {
		this._tReceivablesAcctObj = _tReceivablesAcctObj;
	}


	/**
     * @hibernate.property
     *  column=T_EXPENSE_ACCT
	 */
	public nds.model.CValidCombination getTExpenseAcctObj () {
		return this._tExpenseAcctObj;
	}

	/**
	 * Set the value related to the column: T_EXPENSE_ACCT
	 * @param _tExpenseAcctObj the T_EXPENSE_ACCT value
	 */
	public void setTExpenseAcctObj (nds.model.CValidCombination _tExpenseAcctObj) {
		this._tExpenseAcctObj = _tExpenseAcctObj;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseCTaxAcct)) return false;
		else {
			nds.model.base.BaseCTaxAcct mObj = (nds.model.base.BaseCTaxAcct) obj;
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