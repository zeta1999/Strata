/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.calc.config;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.CalculationTarget;
import com.opengamma.strata.calc.marketdata.CalculationMarketData;
import com.opengamma.strata.calc.marketdata.FunctionRequirements;
import com.opengamma.strata.calc.runner.function.CalculationFunction;
import com.opengamma.strata.calc.runner.function.result.ScenarioResult;
import com.opengamma.strata.collect.Messages;
import com.opengamma.strata.collect.result.Result;

/**
 * Function used when there is no function registered that can calculate a requested value.
 */
public class MissingConfigCalculationFunction
    implements CalculationFunction<CalculationTarget> {
  // this must be public so that the constructor can be invoked

  @Override
  public Set<Measure> supportedMeasures() {
    return ImmutableSet.of();
  }

  @Override
  public FunctionRequirements requirements(CalculationTarget target, Set<Measure> measures) {
    return FunctionRequirements.empty();
  }

  @Override
  public Map<Measure, Result<ScenarioResult<?>>> calculate(
      CalculationTarget target,
      Set<Measure> measures,
      CalculationMarketData marketData) {

    throw new IllegalStateException(Messages.format(
        "No rule configured for measure on '{}'", target.getClass().getSimpleName()));
  }

}
