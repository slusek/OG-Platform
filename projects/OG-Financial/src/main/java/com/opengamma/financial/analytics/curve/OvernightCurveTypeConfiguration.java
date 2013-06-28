/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.analytics.curve;

import java.util.Map;

import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.util.time.Tenor;

/**
 * Configuration object for curves that are to be used as an overnight curve.
 */
@BeanDefinition
public class OvernightCurveTypeConfiguration extends CurveTypeConfiguration {

  /** Serialization version */
  private static final long serialVersionUID = 1L;

  /**
   * The convention name of the index.
   */
  @PropertyDefinition(validate = "notNull")
  private String _conventionName;

  /**
   * For the fudge builder
   */
  /* package */ OvernightCurveTypeConfiguration() {
    super();
  }

  /**
   * @param conventionName The name of the index convention, not null
   */
  public OvernightCurveTypeConfiguration(final String conventionName) {
    super();
    setConventionName(conventionName);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code OvernightCurveTypeConfiguration}.
   * @return the meta-bean, not null
   */
  public static OvernightCurveTypeConfiguration.Meta meta() {
    return OvernightCurveTypeConfiguration.Meta.INSTANCE;
  }
  static {
    JodaBeanUtils.registerMetaBean(OvernightCurveTypeConfiguration.Meta.INSTANCE);
  }

  @Override
  public OvernightCurveTypeConfiguration.Meta metaBean() {
    return OvernightCurveTypeConfiguration.Meta.INSTANCE;
  }

  @Override
  protected Object propertyGet(String propertyName, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1372137884:  // conventionName
        return getConventionName();
    }
    return super.propertyGet(propertyName, quiet);
  }

  @Override
  protected void propertySet(String propertyName, Object newValue, boolean quiet) {
    switch (propertyName.hashCode()) {
      case 1372137884:  // conventionName
        setConventionName((String) newValue);
        return;
    }
    super.propertySet(propertyName, newValue, quiet);
  }

  @Override
  protected void validate() {
    JodaBeanUtils.notNull(_conventionName, "conventionName");
    super.validate();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      OvernightCurveTypeConfiguration other = (OvernightCurveTypeConfiguration) obj;
      return JodaBeanUtils.equal(getConventionName(), other.getConventionName()) &&
          super.equals(obj);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash += hash * 31 + JodaBeanUtils.hashCode(getConventionName());
    return hash ^ super.hashCode();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the convention name of the index.
   * @return the value of the property, not null
   */
  public String getConventionName() {
    return _conventionName;
  }

  /**
   * Sets the convention name of the index.
   * @param conventionName  the new value of the property, not null
   */
  public void setConventionName(String conventionName) {
    JodaBeanUtils.notNull(conventionName, "conventionName");
    this._conventionName = conventionName;
  }

  /**
   * Gets the the {@code conventionName} property.
   * @return the property, not null
   */
  public final Property<String> conventionName() {
    return metaBean().conventionName().createProperty(this);
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code OvernightCurveTypeConfiguration}.
   */
  public static class Meta extends CurveTypeConfiguration.Meta {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code conventionName} property.
     */
    private final MetaProperty<String> _conventionName = DirectMetaProperty.ofReadWrite(
        this, "conventionName", OvernightCurveTypeConfiguration.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, (DirectMetaPropertyMap) super.metaPropertyMap(),
        "conventionName");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 1372137884:  // conventionName
          return _conventionName;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends OvernightCurveTypeConfiguration> builder() {
      return new DirectBeanBuilder<OvernightCurveTypeConfiguration>(new OvernightCurveTypeConfiguration());
    }

    @Override
    public Class<? extends OvernightCurveTypeConfiguration> beanType() {
      return OvernightCurveTypeConfiguration.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code conventionName} property.
     * @return the meta-property, not null
     */
    public final MetaProperty<String> conventionName() {
      return _conventionName;
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
