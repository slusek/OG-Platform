/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.security.db;

import com.opengamma.OpenGammaRuntimeException;
import com.opengamma.financial.security.option.PayoffStyleVisitor;

/**
 * Payoff style of the option.
 */
public enum OptionPayoffStyle {

  /** Asymmetric powered */
  ASYMMETRIC_POWERED,
  /** Barrier */
  BARRIER,
  /** Capped Powered*/
  CAPPED_POWERED,
  /** Fixed Strike */
  FIXED_STRIKE,
  /** Powered */
  POWERED,
  /** Vanilla */
  VANILLA;

  public <T> T accept(final PayoffStyleVisitor<T> visitor) {
    switch (this) {
      case ASYMMETRIC_POWERED:
        return visitor.visitAsymmetricPoweredPayoffStyle(null);
      case BARRIER:
        return visitor.visitBarrierPayoffStyle(null);
      case CAPPED_POWERED:
        return visitor.visitCappedPoweredPayoffStyle(null);
      case FIXED_STRIKE:
        return visitor.visitFixedStrikePayoffStyle(null);
      case POWERED:
        return visitor.visitPoweredPayoffStyle(null);
      case VANILLA:
        return visitor.visitVanillaPayoffStyle(null);
      default:
        throw new OpenGammaRuntimeException("unexpected enum value " + this);
    }
  }

}
