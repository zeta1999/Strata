/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.finance.fx;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableValidator;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.opengamma.strata.basics.BuySell;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.CurrencyPair;
import com.opengamma.strata.basics.currency.FxRate;
import com.opengamma.strata.basics.index.FxIndex;
import com.opengamma.strata.collect.ArgChecker;

/**
 * A Non-Deliverable Forward (NDF).
 * <p>
 * An NDF is a financial instrument that returns the difference between the spot FX rate
 * at the inception of the trade and the FX rate at maturity.
 * It is primarily used to handle FX requirements for currencies that cannot be easily traded.
 * For example, the forward may be between USD and CNY (Chinese Yuan).
 */
@BeanDefinition
public final class FxNonDeliverableForward
    implements FxNonDeliverableForwardProduct, ImmutableBean, Serializable {

  /**
   * Whether the NDF is buy or sell.
   */
  @PropertyDefinition(validate = "notNull")
  private final BuySell buySell;
  /**
   * The settlement currency.
   * <p>
   * The settlement currency is the currency that payment will be made in.
   * It must be one of the two currencies of the forward.
   */
  @PropertyDefinition(validate = "notNull")
  private final Currency settlementCurrency;
  /**
   * The notional amount.
   * <p>
   * The notional expressed here must be positive.
   * The currency of the notional is specified by {@code currency}.
   */
  @PropertyDefinition(validate = "ArgChecker.notNegative")
  private final double notional;
  /**
   * The FX rate agreed for the value date at the inception of the trade.
   * <p>
   * The settlement amount is based on the difference between this rate and the
   * rate observed on the fixing date using the {@code index}.
   * <p>
   * The forward is between the two currencies defined by the rate.
   */
  @PropertyDefinition(validate = "notNull")
  private final FxRate agreedFxRate;
  /**
   * The date that the forward settles.
   * <p>
   * On this date, the settlement amount will be exchanged.
   * This date should be a valid business day.
   */
  @PropertyDefinition(validate = "notNull")
  private final LocalDate paymentDate;
  /**
   * The index defining the FX rate to observe on the fixing date.
   * <p>
   * The index is used to settle the trade by providing the actual FX rate on the fixing date.
   * The value of the trade is based on the difference between the actual rate and the agreed rate.
   * <p>
   * The forward is between the two currencies defined by the index.
   */
  @PropertyDefinition(validate = "notNull")
  private final FxIndex index;

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    CurrencyPair pair = index.getCurrencyPair();
    if (!pair.contains(settlementCurrency)) {
      throw new IllegalArgumentException("FxIndex and settlement currency are incompatible");
    }
    if (!(pair.equals(agreedFxRate.getPair()) || pair.isInverse(agreedFxRate.getPair()))) {
      throw new IllegalArgumentException("FxIndex and agreed FX rate are incompatible");
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the non-deliverable currency.
   * <p>
   * Returns the currency that is not the settlement currency.
   * 
   * @return the currency that is not to be settled
   */
  public Currency getNonDeliverableCurrency() {
    Currency base = agreedFxRate.getPair().getBase();
    return base.equals(settlementCurrency) ? agreedFxRate.getPair().getCounter() : base;
  }

  //-------------------------------------------------------------------------
  /**
   * Expands this FX forward into an {@code ExpandedFxNonDeliverableForward}.
   * 
   * @return the transaction
   */
  @Override
  public ExpandedFxNonDeliverableForward expand() {
    double signedNotional = buySell.normalize(notional);
    return ExpandedFxNonDeliverableForward.builder()
        .settlementCurrencyNotional(CurrencyAmount.of(settlementCurrency, signedNotional))
        .agreedFxRate(agreedFxRate)
        .paymentDate(paymentDate)
        .index(index)
        .build();
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code FxNonDeliverableForward}.
   * @return the meta-bean, not null
   */
  public static FxNonDeliverableForward.Meta meta() {
    return FxNonDeliverableForward.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(FxNonDeliverableForward.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static FxNonDeliverableForward.Builder builder() {
    return new FxNonDeliverableForward.Builder();
  }

  private FxNonDeliverableForward(
      BuySell buySell,
      Currency settlementCurrency,
      double notional,
      FxRate agreedFxRate,
      LocalDate paymentDate,
      FxIndex index) {
    JodaBeanUtils.notNull(buySell, "buySell");
    JodaBeanUtils.notNull(settlementCurrency, "settlementCurrency");
    ArgChecker.notNegative(notional, "notional");
    JodaBeanUtils.notNull(agreedFxRate, "agreedFxRate");
    JodaBeanUtils.notNull(paymentDate, "paymentDate");
    JodaBeanUtils.notNull(index, "index");
    this.buySell = buySell;
    this.settlementCurrency = settlementCurrency;
    this.notional = notional;
    this.agreedFxRate = agreedFxRate;
    this.paymentDate = paymentDate;
    this.index = index;
    validate();
  }

  @Override
  public FxNonDeliverableForward.Meta metaBean() {
    return FxNonDeliverableForward.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether the NDF is buy or sell.
   * @return the value of the property, not null
   */
  public BuySell getBuySell() {
    return buySell;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the settlement currency.
   * <p>
   * The settlement currency is the currency that payment will be made in.
   * It must be one of the two currencies of the forward.
   * @return the value of the property, not null
   */
  public Currency getSettlementCurrency() {
    return settlementCurrency;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the notional amount.
   * <p>
   * The notional expressed here must be positive.
   * The currency of the notional is specified by {@code currency}.
   * @return the value of the property
   */
  public double getNotional() {
    return notional;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the FX rate agreed for the value date at the inception of the trade.
   * <p>
   * The settlement amount is based on the difference between this rate and the
   * rate observed on the fixing date using the {@code index}.
   * <p>
   * The forward is between the two currencies defined by the rate.
   * @return the value of the property, not null
   */
  public FxRate getAgreedFxRate() {
    return agreedFxRate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the date that the forward settles.
   * <p>
   * On this date, the settlement amount will be exchanged.
   * This date should be a valid business day.
   * @return the value of the property, not null
   */
  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the index defining the FX rate to observe on the fixing date.
   * <p>
   * The index is used to settle the trade by providing the actual FX rate on the fixing date.
   * The value of the trade is based on the difference between the actual rate and the agreed rate.
   * <p>
   * The forward is between the two currencies defined by the index.
   * @return the value of the property, not null
   */
  public FxIndex getIndex() {
    return index;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      FxNonDeliverableForward other = (FxNonDeliverableForward) obj;
      return JodaBeanUtils.equal(getBuySell(), other.getBuySell()) &&
          JodaBeanUtils.equal(getSettlementCurrency(), other.getSettlementCurrency()) &&
          JodaBeanUtils.equal(getNotional(), other.getNotional()) &&
          JodaBeanUtils.equal(getAgreedFxRate(), other.getAgreedFxRate()) &&
          JodaBeanUtils.equal(getPaymentDate(), other.getPaymentDate()) &&
          JodaBeanUtils.equal(getIndex(), other.getIndex());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getBuySell());
    hash = hash * 31 + JodaBeanUtils.hashCode(getSettlementCurrency());
    hash = hash * 31 + JodaBeanUtils.hashCode(getNotional());
    hash = hash * 31 + JodaBeanUtils.hashCode(getAgreedFxRate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getPaymentDate());
    hash = hash * 31 + JodaBeanUtils.hashCode(getIndex());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(224);
    buf.append("FxNonDeliverableForward{");
    buf.append("buySell").append('=').append(getBuySell()).append(',').append(' ');
    buf.append("settlementCurrency").append('=').append(getSettlementCurrency()).append(',').append(' ');
    buf.append("notional").append('=').append(getNotional()).append(',').append(' ');
    buf.append("agreedFxRate").append('=').append(getAgreedFxRate()).append(',').append(' ');
    buf.append("paymentDate").append('=').append(getPaymentDate()).append(',').append(' ');
    buf.append("index").append('=').append(JodaBeanUtils.toString(getIndex()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code FxNonDeliverableForward}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code buySell} property.
     */
    private final MetaProperty<BuySell> buySell = DirectMetaProperty.ofImmutable(
        this, "buySell", FxNonDeliverableForward.class, BuySell.class);
    /**
     * The meta-property for the {@code settlementCurrency} property.
     */
    private final MetaProperty<Currency> settlementCurrency = DirectMetaProperty.ofImmutable(
        this, "settlementCurrency", FxNonDeliverableForward.class, Currency.class);
    /**
     * The meta-property for the {@code notional} property.
     */
    private final MetaProperty<Double> notional = DirectMetaProperty.ofImmutable(
        this, "notional", FxNonDeliverableForward.class, Double.TYPE);
    /**
     * The meta-property for the {@code agreedFxRate} property.
     */
    private final MetaProperty<FxRate> agreedFxRate = DirectMetaProperty.ofImmutable(
        this, "agreedFxRate", FxNonDeliverableForward.class, FxRate.class);
    /**
     * The meta-property for the {@code paymentDate} property.
     */
    private final MetaProperty<LocalDate> paymentDate = DirectMetaProperty.ofImmutable(
        this, "paymentDate", FxNonDeliverableForward.class, LocalDate.class);
    /**
     * The meta-property for the {@code index} property.
     */
    private final MetaProperty<FxIndex> index = DirectMetaProperty.ofImmutable(
        this, "index", FxNonDeliverableForward.class, FxIndex.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "buySell",
        "settlementCurrency",
        "notional",
        "agreedFxRate",
        "paymentDate",
        "index");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 244977400:  // buySell
          return buySell;
        case -1024875430:  // settlementCurrency
          return settlementCurrency;
        case 1585636160:  // notional
          return notional;
        case 1040357930:  // agreedFxRate
          return agreedFxRate;
        case -1540873516:  // paymentDate
          return paymentDate;
        case 100346066:  // index
          return index;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public FxNonDeliverableForward.Builder builder() {
      return new FxNonDeliverableForward.Builder();
    }

    @Override
    public Class<? extends FxNonDeliverableForward> beanType() {
      return FxNonDeliverableForward.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code buySell} property.
     * @return the meta-property, not null
     */
    public MetaProperty<BuySell> buySell() {
      return buySell;
    }

    /**
     * The meta-property for the {@code settlementCurrency} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Currency> settlementCurrency() {
      return settlementCurrency;
    }

    /**
     * The meta-property for the {@code notional} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Double> notional() {
      return notional;
    }

    /**
     * The meta-property for the {@code agreedFxRate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FxRate> agreedFxRate() {
      return agreedFxRate;
    }

    /**
     * The meta-property for the {@code paymentDate} property.
     * @return the meta-property, not null
     */
    public MetaProperty<LocalDate> paymentDate() {
      return paymentDate;
    }

    /**
     * The meta-property for the {@code index} property.
     * @return the meta-property, not null
     */
    public MetaProperty<FxIndex> index() {
      return index;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 244977400:  // buySell
          return ((FxNonDeliverableForward) bean).getBuySell();
        case -1024875430:  // settlementCurrency
          return ((FxNonDeliverableForward) bean).getSettlementCurrency();
        case 1585636160:  // notional
          return ((FxNonDeliverableForward) bean).getNotional();
        case 1040357930:  // agreedFxRate
          return ((FxNonDeliverableForward) bean).getAgreedFxRate();
        case -1540873516:  // paymentDate
          return ((FxNonDeliverableForward) bean).getPaymentDate();
        case 100346066:  // index
          return ((FxNonDeliverableForward) bean).getIndex();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code FxNonDeliverableForward}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<FxNonDeliverableForward> {

    private BuySell buySell;
    private Currency settlementCurrency;
    private double notional;
    private FxRate agreedFxRate;
    private LocalDate paymentDate;
    private FxIndex index;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(FxNonDeliverableForward beanToCopy) {
      this.buySell = beanToCopy.getBuySell();
      this.settlementCurrency = beanToCopy.getSettlementCurrency();
      this.notional = beanToCopy.getNotional();
      this.agreedFxRate = beanToCopy.getAgreedFxRate();
      this.paymentDate = beanToCopy.getPaymentDate();
      this.index = beanToCopy.getIndex();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 244977400:  // buySell
          return buySell;
        case -1024875430:  // settlementCurrency
          return settlementCurrency;
        case 1585636160:  // notional
          return notional;
        case 1040357930:  // agreedFxRate
          return agreedFxRate;
        case -1540873516:  // paymentDate
          return paymentDate;
        case 100346066:  // index
          return index;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 244977400:  // buySell
          this.buySell = (BuySell) newValue;
          break;
        case -1024875430:  // settlementCurrency
          this.settlementCurrency = (Currency) newValue;
          break;
        case 1585636160:  // notional
          this.notional = (Double) newValue;
          break;
        case 1040357930:  // agreedFxRate
          this.agreedFxRate = (FxRate) newValue;
          break;
        case -1540873516:  // paymentDate
          this.paymentDate = (LocalDate) newValue;
          break;
        case 100346066:  // index
          this.index = (FxIndex) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public FxNonDeliverableForward build() {
      return new FxNonDeliverableForward(
          buySell,
          settlementCurrency,
          notional,
          agreedFxRate,
          paymentDate,
          index);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code buySell} property in the builder.
     * @param buySell  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder buySell(BuySell buySell) {
      JodaBeanUtils.notNull(buySell, "buySell");
      this.buySell = buySell;
      return this;
    }

    /**
     * Sets the {@code settlementCurrency} property in the builder.
     * @param settlementCurrency  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder settlementCurrency(Currency settlementCurrency) {
      JodaBeanUtils.notNull(settlementCurrency, "settlementCurrency");
      this.settlementCurrency = settlementCurrency;
      return this;
    }

    /**
     * Sets the {@code notional} property in the builder.
     * @param notional  the new value
     * @return this, for chaining, not null
     */
    public Builder notional(double notional) {
      ArgChecker.notNegative(notional, "notional");
      this.notional = notional;
      return this;
    }

    /**
     * Sets the {@code agreedFxRate} property in the builder.
     * @param agreedFxRate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder agreedFxRate(FxRate agreedFxRate) {
      JodaBeanUtils.notNull(agreedFxRate, "agreedFxRate");
      this.agreedFxRate = agreedFxRate;
      return this;
    }

    /**
     * Sets the {@code paymentDate} property in the builder.
     * @param paymentDate  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder paymentDate(LocalDate paymentDate) {
      JodaBeanUtils.notNull(paymentDate, "paymentDate");
      this.paymentDate = paymentDate;
      return this;
    }

    /**
     * Sets the {@code index} property in the builder.
     * @param index  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder index(FxIndex index) {
      JodaBeanUtils.notNull(index, "index");
      this.index = index;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(224);
      buf.append("FxNonDeliverableForward.Builder{");
      buf.append("buySell").append('=').append(JodaBeanUtils.toString(buySell)).append(',').append(' ');
      buf.append("settlementCurrency").append('=').append(JodaBeanUtils.toString(settlementCurrency)).append(',').append(' ');
      buf.append("notional").append('=').append(JodaBeanUtils.toString(notional)).append(',').append(' ');
      buf.append("agreedFxRate").append('=').append(JodaBeanUtils.toString(agreedFxRate)).append(',').append(' ');
      buf.append("paymentDate").append('=').append(JodaBeanUtils.toString(paymentDate)).append(',').append(' ');
      buf.append("index").append('=').append(JodaBeanUtils.toString(index));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
