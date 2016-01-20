/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.function.calculation.future;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.calc.config.Measure;
import com.opengamma.strata.calc.marketdata.CalculationMarketData;
import com.opengamma.strata.calc.marketdata.FunctionRequirements;
import com.opengamma.strata.calc.runner.function.CalculationFunction;
import com.opengamma.strata.calc.runner.function.result.ScenarioResult;
import com.opengamma.strata.collect.result.FailureReason;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.market.key.QuoteKey;
import com.opengamma.strata.product.future.GenericFutureOptionTrade;

/**
 * Perform calculations on a single {@code GenericFutureOptionTrade} for each of a set of scenarios.
 * <p>
 * The supported built-in measures are:
 * <ul>
 *   <li>{@linkplain Measure#PRESENT_VALUE Present value}
 * </ul>
 */
public class GenericFutureOptionCalculationFunction
    implements CalculationFunction<GenericFutureOptionTrade> {

  /**
   * The calculations by measure.
   */
  private static final ImmutableMap<Measure, SingleMeasureCalculation> CALCULATORS =
      ImmutableMap.<Measure, SingleMeasureCalculation>builder()
          .put(Measure.PRESENT_VALUE, GenericFutureOptionMeasureCalculations::presentValue)
          .build();

  /**
   * Creates an instance.
   */
  public GenericFutureOptionCalculationFunction() {
  }

  //-------------------------------------------------------------------------
  @Override
  public Set<Measure> supportedMeasures() {
    return CALCULATORS.keySet();
  }

  @Override
  public Optional<Currency> defaultReportingCurrency(GenericFutureOptionTrade target) {
    return Optional.of(target.getProduct().getCurrency());
  }

  //-------------------------------------------------------------------------
  @Override
  public FunctionRequirements requirements(GenericFutureOptionTrade trade, Set<Measure> measures) {
    QuoteKey key = QuoteKey.of(trade.getSecurity().getStandardId());

    return FunctionRequirements.builder()
        .singleValueRequirements(ImmutableSet.of(key))
        .outputCurrencies(trade.getProduct().getCurrency())
        .build();
  }

  //-------------------------------------------------------------------------
  @Override
  public Map<Measure, Result<ScenarioResult<?>>> calculate(
      GenericFutureOptionTrade trade,
      Set<Measure> measures,
      CalculationMarketData scenarioMarketData) {

    // loop around measures, calculating all scenarios for one measure
    Map<Measure, Result<ScenarioResult<?>>> results = new HashMap<>();
    for (Measure measure : measures) {
      results.put(measure, calculate(measure, trade, scenarioMarketData));
    }
    return results;
  }

  // calculate one measure
  private Result<ScenarioResult<?>> calculate(
      Measure measure,
      GenericFutureOptionTrade trade,
      CalculationMarketData scenarioMarketData) {

    SingleMeasureCalculation calculator = CALCULATORS.get(measure);
    if (calculator == null) {
      return Result.failure(FailureReason.INVALID_INPUT, "Unsupported measure: {}", measure);
    }
    return Result.of(() -> calculator.calculate(trade, scenarioMarketData));
  }

  //-------------------------------------------------------------------------
  @FunctionalInterface
  interface SingleMeasureCalculation {
    public abstract ScenarioResult<?> calculate(
        GenericFutureOptionTrade trade,
        CalculationMarketData marketData);
  }

}
