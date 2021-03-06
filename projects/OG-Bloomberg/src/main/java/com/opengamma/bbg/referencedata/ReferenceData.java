/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.bbg.referencedata;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeMsg;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.bloomberglp.blpapi.Element;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.PublicSPI;

/**
 * Reference data specific to a single identifier.
 * <p>
 * This class is mutable and not thread-safe.
 */
@PublicSPI
@BeanDefinition
public class ReferenceData extends DirectBean {

  /**
   * The identifier that this reference data is for.
   */
  @PropertyDefinition(validate = "notNull")
  private String _identifier;
  /**
   * The reference data that was obtained.
   * The key is the data field, the value is the reference data value.
   */
  @PropertyDefinition(validate = "notNull")
  private FudgeMsg _fieldValues = FudgeContext.EMPTY_MESSAGE;
  /**
   * The errors.
   * This includes errors related to the identifier and to a single field.
   */
  @PropertyDefinition
  private final List<ReferenceDataError> _errors = Lists.newArrayList();
  /**
   * The entitlement information.
   */
  private Element _entitlementInfo;  // should probably be handled in another way

  /**
   * Creates an instance.
   */
  protected ReferenceData() {
  }

  /**
   * Creates an instance.
   * 
   * @param identifier  the identifier, not null
   */
  public ReferenceData(String identifier) {
    setIdentifier(identifier);
  }

  /**
   * Creates an instance.
   * 
   * @param identifier  the identifier, not null
   * @param fieldValues  the field-value map, not null
   */
  public ReferenceData(String identifier, FudgeMsg fieldValues) {
    setIdentifier(identifier);
    setFieldValues(fieldValues);
  }

  //-------------------------------------------------------------------------
  /**
   * Adds an error to the list contained.
   * 
   * @param error  the reference data error to add, not null
   */
  public void addError(ReferenceDataError error) {
    ArgumentChecker.notNull(error, "error");
    getErrors().add(error);
  }

  /**
   * Removes all errors for the specified field.
   * 
   * @param field  the field to remove, null means the whole-identifier errors
   */
  public void removeErrors(String field) {
    for (Iterator<ReferenceDataError> it = getErrors().iterator(); it.hasNext(); ) {
      ReferenceDataError error = it.next();
      if (Objects.equal(field, error.getField())) {
        it.remove();
      }
    }
  }

  /**
   * Checks if the whole identifier was in error
   * 
   * @return true if the whole identifier was in error
   */
  public boolean isIdentifierError() {
    return isError(null);
  }

  /**
   * Checks if a field was in error.
   * 
   * @param field  the field to check, null for the whole identifier
   * @return true if the whole identifier was in error
   */
  public boolean isError(String field) {
    for (ReferenceDataError error : getErrors()) {
      if (Objects.equal(field, error.getField())) {
        return true;
      }
    }
    return false;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the entitlement information.
   * 
   * @return the value of the property
   */
  public Element getEntitlementInfo() {
    return _entitlementInfo;
  }

  /**
   * Sets the entitlement information.
   * 
   * @param entitlementInfo  the new value of the property
   */
  public void setEntitlementInfo(Element entitlementInfo) {
    this._entitlementInfo = entitlementInfo;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ReferenceData}.
   * @return the meta-bean, not null
   */
  public static ReferenceData.Meta meta() {
    return ReferenceData.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ReferenceData.Meta.INSTANCE);
  }

  @Override
  public ReferenceData.Meta metaBean() {
    return ReferenceData.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1618432855:  // identifier
        return getIdentifier();
      case 427230908:  // fieldValues
        return getFieldValues();
      case -1294635157:  // errors
        return getErrors();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case -1618432855:  // identifier
        setIdentifier((String) newValue);
        return;
      case 427230908:  // fieldValues
        setFieldValues((FudgeMsg) newValue);
        return;
      case -1294635157:  // errors
        setErrors((List<ReferenceDataError>) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_identifier, "identifier");
    JodaBeanUtils.notNull(_fieldValues, "fieldValues");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ReferenceData other = (ReferenceData) obj;
      return JodaBeanUtils.equal(getIdentifier(), other.getIdentifier()) &&
          JodaBeanUtils.equal(getFieldValues(), other.getFieldValues()) &&
          JodaBeanUtils.equal(getErrors(), other.getErrors());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getIdentifier());
    hash += hash * 31 + JodaBeanUtils.hashCode(getFieldValues());
    hash += hash * 31 + JodaBeanUtils.hashCode(getErrors());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the identifier that this reference data is for.
   * @return the value of the property, not null
   */
  public String getIdentifier() {
    return _identifier;
  }

  /**
   * Sets the identifier that this reference data is for.
   * @param identifier  the new value of the property, not null
   */
  public void setIdentifier(String identifier) {
    JodaBeanUtils.notNull(identifier, "identifier");
    this._identifier = identifier;
  }

  /**
   * Gets the the {@code identifier} property.
   * @return the property, not null
   */
  public final Property<String> identifier() {
    return metaBean().identifier().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the reference data that was obtained.
   * The key is the data field, the value is the reference data value.
   * @return the value of the property, not null
   */
  public FudgeMsg getFieldValues() {
    return _fieldValues;
  }

  /**
   * Sets the reference data that was obtained.
   * The key is the data field, the value is the reference data value.
   * @param fieldValues  the new value of the property, not null
   */
  public void setFieldValues(FudgeMsg fieldValues) {
    JodaBeanUtils.notNull(fieldValues, "fieldValues");
    this._fieldValues = fieldValues;
  }

  /**
   * Gets the the {@code fieldValues} property.
   * The key is the data field, the value is the reference data value.
   * @return the property, not null
   */
  public final Property<FudgeMsg> fieldValues() {
    return metaBean().fieldValues().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the errors.
   * This includes errors related to the identifier and to a single field.
   * @return the value of the property
   */
  public List<ReferenceDataError> getErrors() {
    return _errors;
  }

  /**
   * Sets the errors.
   * This includes errors related to the identifier and to a single field.
   * @param errors  the new value of the property
   */
  public void setErrors(List<ReferenceDataError> errors) {
    this._errors.clear();
    this._errors.addAll(errors);
  }

  /**
   * Gets the the {@code errors} property.
   * This includes errors related to the identifier and to a single field.
   * @return the property, not null
   */
  public final Property<List<ReferenceDataError>> errors() {
    return metaBean().errors().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ReferenceData}.
   */
  public static class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code identifier} property.
     */
    private final MetaProperty<String> _identifier = DirectMetaProperty.ofReadWrite(
        this, "identifier", ReferenceData.class, String.class);
    /**
     * The meta-property for the {@code fieldValues} property.
     */
    private final MetaProperty<FudgeMsg> _fieldValues = DirectMetaProperty.ofReadWrite(
        this, "fieldValues", ReferenceData.class, FudgeMsg.class);
    /**
     * The meta-property for the {@code errors} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<ReferenceDataError>> _errors = DirectMetaProperty.ofReadWrite(
        this, "errors", ReferenceData.class, (Class) List.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "identifier",
        "fieldValues",
        "errors");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1618432855:  // identifier
          return _identifier;
        case 427230908:  // fieldValues
          return _fieldValues;
        case -1294635157:  // errors
          return _errors;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends ReferenceData> builder() {
      return new DirectBeanBuilder<ReferenceData>(new ReferenceData());
    }

    @Override
    public Class<? extends ReferenceData> beanType() {
      return ReferenceData.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code identifier} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> identifier() {
      return _identifier;
    }

    /**
     * The meta-property for the {@code fieldValues} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<FudgeMsg> fieldValues() {
      return _fieldValues;
    }

    /**
     * The meta-property for the {@code errors} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<List<ReferenceDataError>> errors() {
      return _errors;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
