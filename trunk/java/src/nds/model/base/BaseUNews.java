package nds.model.base;

import java.io.Serializable;


/**
 * This class has been automatically generated by Hibernate Synchronizer.
 * For more information or documentation, visit The Hibernate Synchronizer page
 * at http://www.binamics.com/hibernatesync or contact Joe Hudson at joe@binamics.com.
 *
 * This is an object that contains data related to the U_NEWS table.
 * Do not modify this class because it will be overwritten if the configuration file
 * related to this class is modified.
 *
 * @hibernate.class
 *  table="U_NEWS"
 */
public abstract class BaseUNews  implements Serializable {

	public static String PROP_IS_ACTIVE = "IsActive";
	public static String PROP_STYLE = "Style";
	public static String PROP_PARENT_NEWS = "ParentNews";
	public static String PROP_MODIFIER_ID = "ModifierId";
	public static String PROP_AUTHOR = "Author";
	public static String PROP_KEYWORDS = "Keywords";
	public static String PROP_DOCTYPE = "Doctype";
	public static String PROP_OWNER_ID = "OwnerId";
	public static String PROP_PARENT_ID = "ParentId";
	public static String PROP_NO = "No";
	public static String PROP_DESCRIPTION = "Description";
	public static String PROP_AD_ORG_ID = "AdOrgId";
	public static String PROP_AD_CLIENT_ID = "AdClientId";
	public static String PROP_C_CITY_ID = "CCityId";
	public static String PROP_SUBJECT = "Subject";
	public static String PROP_MODIFIED_DATE = "ModifiedDate";
	public static String PROP_PUBLISHER = "Publisher";
	public static String PROP_U_NEWS_CATEGORY_ID = "UNewsCategoryId";
	public static String PROP_PUBLISH_DATE = "PublishDate";
	public static String PROP_AD_CLIENT = "AdClient";
	public static String PROP_IS_PUBLIC = "IsPublic";
	public static String PROP_ID = "Id";
	public static String PROP_CREATION_DATE = "CreationDate";


	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer _id;

	// fields
	private java.lang.Integer _adClientId;
	private java.lang.Integer _adOrgId;
	private java.lang.String _no;
	private java.lang.Integer _uNewsCategoryId;
	private java.lang.String _subject;
	private java.lang.String _description;
	private java.lang.String _publisher;
	private java.lang.String _author;
	private java.lang.Integer _publishDate;
	private java.lang.Integer _parentId;
	private java.lang.String _style;
	private java.lang.Integer _cCityId;
	private java.lang.String _isPublic;
	private java.lang.Integer _ownerId;
	private java.lang.Integer _modifierId;
	private java.util.Date _creationDate;
	private java.util.Date _modifiedDate;
	private java.lang.String _isActive;
	private java.lang.String _keywords;
	private java.lang.String _doctype;

	// many to one
	private nds.model.UNews _parentNews;
	private nds.model.AdClient _adClient;


	// constructors
	public BaseUNews () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public BaseUNews (java.lang.Integer _id) {
		this.setId(_id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public BaseUNews (
		java.lang.Integer _id,
		java.lang.String _no,
		java.lang.String _subject,
		java.lang.String _isPublic,
		java.lang.String _isActive) {

		this.setId(_id);
		this.setNo(_no);
		this.setSubject(_subject);
		this.setIsPublic(_isPublic);
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
	 * Return the value associated with the column: NO
	 */
	public java.lang.String getNo () {
		return _no;
	}

	/**
	 * Set the value related to the column: NO
	 * @param _no the NO value
	 */
	public void setNo (java.lang.String _no) {
		this._no = _no;
	}


	/**
	 * Return the value associated with the column: U_NEWSCATEGORY_ID
	 */
	public java.lang.Integer getUNewsCategoryId () {
		return _uNewsCategoryId;
	}

	/**
	 * Set the value related to the column: U_NEWSCATEGORY_ID
	 * @param _uNewsCategoryId the U_NEWSCATEGORY_ID value
	 */
	public void setUNewsCategoryId (java.lang.Integer _uNewsCategoryId) {
		this._uNewsCategoryId = _uNewsCategoryId;
	}


	/**
	 * Return the value associated with the column: SUBJECT
	 */
	public java.lang.String getSubject () {
		return _subject;
	}

	/**
	 * Set the value related to the column: SUBJECT
	 * @param _subject the SUBJECT value
	 */
	public void setSubject (java.lang.String _subject) {
		this._subject = _subject;
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
	 * Return the value associated with the column: PUBLISHER
	 */
	public java.lang.String getPublisher () {
		return _publisher;
	}

	/**
	 * Set the value related to the column: PUBLISHER
	 * @param _publisher the PUBLISHER value
	 */
	public void setPublisher (java.lang.String _publisher) {
		this._publisher = _publisher;
	}


	/**
	 * Return the value associated with the column: AUTHOR
	 */
	public java.lang.String getAuthor () {
		return _author;
	}

	/**
	 * Set the value related to the column: AUTHOR
	 * @param _author the AUTHOR value
	 */
	public void setAuthor (java.lang.String _author) {
		this._author = _author;
	}


	/**
	 * Return the value associated with the column: PUBLISHDATE
	 */
	public java.lang.Integer getPublishDate () {
		return _publishDate;
	}

	/**
	 * Set the value related to the column: PUBLISHDATE
	 * @param _publishDate the PUBLISHDATE value
	 */
	public void setPublishDate (java.lang.Integer _publishDate) {
		this._publishDate = _publishDate;
	}


	/**
	 * Return the value associated with the column: PARENT_ID
	 */
	public java.lang.Integer getParentId () {
		return _parentId;
	}

	/**
	 * Set the value related to the column: PARENT_ID
	 * @param _parentId the PARENT_ID value
	 */
	public void setParentId (java.lang.Integer _parentId) {
		this._parentId = _parentId;
	}


	/**
	 * Return the value associated with the column: STYLE
	 */
	public java.lang.String getStyle () {
		return _style;
	}

	/**
	 * Set the value related to the column: STYLE
	 * @param _style the STYLE value
	 */
	public void setStyle (java.lang.String _style) {
		this._style = _style;
	}




	/**
	 * Return the value associated with the column: C_CITY_ID
	 */
	public java.lang.Integer getCCityId () {
		return _cCityId;
	}

	/**
	 * Set the value related to the column: C_CITY_ID
	 * @param _cCityId the C_CITY_ID value
	 */
	public void setCCityId (java.lang.Integer _cCityId) {
		this._cCityId = _cCityId;
	}


	/**
	 * Return the value associated with the column: ISPUBLIC
	 */
	public java.lang.String getIsPublic () {
		return _isPublic;
	}

	/**
	 * Set the value related to the column: ISPUBLIC
	 * @param _isPublic the ISPUBLIC value
	 */
	public void setIsPublic (java.lang.String _isPublic) {
		this._isPublic = _isPublic;
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
	 * Return the value associated with the column: KEYWORDS
	 */
	public java.lang.String getKeywords () {
		return _keywords;
	}

	/**
	 * Set the value related to the column: KEYWORDS
	 * @param _keywords the KEYWORDS value
	 */
	public void setDoctype (java.lang.String _dc) {
		this._doctype = _dc;
	}
	/**
	 * Return the value associated with the column: KEYWORDS
	 */
	public java.lang.String getDoctype () {
		return _doctype;
	}

	/**
	 * Set the value related to the column: KEYWORDS
	 * @param _keywords the KEYWORDS value
	 */
	public void setKeywords (java.lang.String _keywords) {
		this._keywords = _keywords;
	}

	/**
     * @hibernate.property
     *  column=PARENT_ID
	 */
	public nds.model.UNews getParentNews () {
		return this._parentNews;
	}

	/**
	 * Set the value related to the column: PARENT_ID
	 * @param _parentNews the PARENT_ID value
	 */
	public void setParentNews (nds.model.UNews _parentNews) {
		this._parentNews = _parentNews;
	}


	/**
     * @hibernate.property
     *  column=AD_CLIENT_ID
	 */
	public nds.model.AdClient getAdClient () {
		return this._adClient;
	}

	/**
	 * Set the value related to the column: AD_CLIENT_ID
	 * @param _adClient the AD_CLIENT_ID value
	 */
	public void setAdClient (nds.model.AdClient _adClient) {
		this._adClient = _adClient;
	}


	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof nds.model.base.BaseUNews)) return false;
		else {
			nds.model.base.BaseUNews mObj = (nds.model.base.BaseUNews) obj;
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