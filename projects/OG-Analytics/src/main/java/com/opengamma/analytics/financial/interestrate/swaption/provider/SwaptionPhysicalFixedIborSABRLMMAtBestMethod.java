/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.interestrate.swaption.provider;

import java.util.List;

import com.opengamma.analytics.financial.interestrate.PresentValueSABRSensitivityDataBundle;
import com.opengamma.analytics.financial.interestrate.swaption.derivative.SwaptionPhysicalFixedIbor;
import com.opengamma.analytics.financial.interestrate.swaption.method.SwaptionPhysicalFixedIborBasketMethod;
import com.opengamma.analytics.financial.model.interestrate.definition.LiborMarketModelDisplacedDiffusionParameters;
import com.opengamma.analytics.financial.provider.calculator.sabr.PresentValueCurveSensitivitySABRSwaptionCalculator;
import com.opengamma.analytics.financial.provider.calculator.sabr.PresentValueSABRSensitivitySABRSwaptionCalculator;
import com.opengamma.analytics.financial.provider.calculator.sabr.PresentValueSABRSwaptionCalculator;
import com.opengamma.analytics.financial.provider.description.LiborMarketModelDisplacedDiffusionProvider;
import com.opengamma.analytics.financial.provider.description.MulticurveProviderInterface;
import com.opengamma.analytics.financial.provider.description.SABRSwaptionProviderInterface;
import com.opengamma.analytics.financial.provider.method.SuccessiveLeastSquareLMMDDCalibrationEngine;
import com.opengamma.analytics.financial.provider.method.SuccessiveLeastSquareLMMDDCalibrationObjective;
import com.opengamma.analytics.financial.provider.sensitivity.multicurve.MultipleCurrencyMulticurveSensitivity;
import com.opengamma.analytics.math.matrix.CommonsMatrixAlgebra;
import com.opengamma.analytics.math.matrix.DoubleMatrix1D;
import com.opengamma.analytics.math.matrix.DoubleMatrix2D;
import com.opengamma.util.ArgumentChecker;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.Triple;

/**
 * Method to computes the present value and sensitivities of physical delivery European swaptions with a Libor Market Model calibrated exactly to SABR prices.
 * The LMM displacements and volatility weights are hard coded.
 * <p> Reference: M. Henrard, Algorithmic differentiation and calibration: optimization, September 2012.
 */
public class SwaptionPhysicalFixedIborSABRLMMAtBestMethod {

  /**
   * The SABR method used for European swaptions with physical delivery.
   */
  private static final PresentValueSABRSwaptionCalculator PVSSC = PresentValueSABRSwaptionCalculator.getInstance();
  private static final PresentValueCurveSensitivitySABRSwaptionCalculator PVCSSSC = PresentValueCurveSensitivitySABRSwaptionCalculator.getInstance();
  private static final PresentValueSABRSensitivitySABRSwaptionCalculator PVSSSSC = PresentValueSABRSensitivitySABRSwaptionCalculator.getInstance();
  /**
   * The LMM method used for European swaptions with physical delivery.
   */
  private static final SwaptionPhysicalFixedIborLMMDDMethod METHOD_SWAPTION_LMM = SwaptionPhysicalFixedIborLMMDDMethod.getInstance();
  /**
   * The method used to create the calibration basket.
   */
  private static final SwaptionPhysicalFixedIborBasketMethod METHOD_BASKET = SwaptionPhysicalFixedIborBasketMethod.getInstance();
  /**
   * The matrix algebra used.
   */
  private static final CommonsMatrixAlgebra ALGEBRA = new CommonsMatrixAlgebra();
  /**
   * The noneyness of strikes used in the calibration basket. Difference between the swaption rate and the basket rates.
   */
  private final double[] _strikeMoneyness;
  /**
   * The initial value of the LMM parameters for calibration. The initial parameters are not modified by the calibration but a new copy is created for each calibration.
   */
  private final LiborMarketModelDisplacedDiffusionParameters _parametersInit;

  /**
   * Constructor.
   * @param strikeMoneyness The noneyness of strikes used in the calibration basket. Difference between the swaption rate and the basket rates.
   * @param parametersInit The initial value of the LMM parameters for calibration. The initial parameters are not modified by the calibration but a new copy is created for each calibration.
   */
  public SwaptionPhysicalFixedIborSABRLMMAtBestMethod(double[] strikeMoneyness, final LiborMarketModelDisplacedDiffusionParameters parametersInit) {
    _strikeMoneyness = strikeMoneyness;
    _parametersInit = parametersInit;
  }

  /**
   * The method calibrates a LMM on a set of vanilla swaption priced with SABR. The set of vanilla swaptions is given by the CalibrationType.
   * The original swaption is priced with the calibrated LMM. 
   * This should not be used for vanilla swaptions (the price is equal to the SABR price with a longer computation type and some approximation).
   * This is useful for non-standard swaptions like amortized swaptions.
   * @param swaption The swaption.
   * @param sabrData The SABR and multi-curves provider.
   * @return The present value. 
   */
  public MultipleCurrencyAmount presentValue(final SwaptionPhysicalFixedIbor swaption, final SABRSwaptionProviderInterface sabrData) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(sabrData, "SABR swaption provider");
    Currency ccy = swaption.getCurrency();
    MulticurveProviderInterface multicurves = sabrData.getMulticurveProvider();
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SuccessiveLeastSquareLMMDDCalibrationObjective objective = new SuccessiveLeastSquareLMMDDCalibrationObjective(lmmParameters, ccy);
    SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface> calibrationEngine = new SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface>(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, PVSSC);
    calibrationEngine.calibrate(sabrData);
    LiborMarketModelDisplacedDiffusionProvider lmm = new LiborMarketModelDisplacedDiffusionProvider(multicurves, lmmParameters, ccy);
    MultipleCurrencyAmount pv = METHOD_SWAPTION_LMM.presentValue(swaption, lmm);
    return pv;
  }

  public PresentValueSABRSensitivityDataBundle presentValueSABRSensitivity(final SwaptionPhysicalFixedIbor swaption, final SABRSwaptionProviderInterface sabrData) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(sabrData, "SABR swaption provider");
    Currency ccy = swaption.getCurrency();
    MulticurveProviderInterface multicurves = sabrData.getMulticurveProvider();
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SuccessiveLeastSquareLMMDDCalibrationObjective objective = new SuccessiveLeastSquareLMMDDCalibrationObjective(lmmParameters, ccy);
    SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface> calibrationEngine = new SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface>(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, PVSSC);
    calibrationEngine.calibrate(sabrData);
    LiborMarketModelDisplacedDiffusionProvider lmm = new LiborMarketModelDisplacedDiffusionProvider(multicurves, lmmParameters, ccy);

    int nbCalibrations = swaptionCalibration.length;
    int nbPeriods = nbCalibrations / nbStrikes;
    int nbFact = lmmParameters.getNbFactor();
    List<Integer> instrumentIndex = calibrationEngine.getInstrumentIndex();
    double[] dPvdPhi = new double[2 * nbPeriods];
    // Implementation note: Derivative of the priced swaptions wrt the calibration parameters (multiplicative factor and additive term)
    // Implementation note: Phi is a vector with the multiplicative factors on the volatility and then the additive terms on the displacements.
    double[][] dPvdGamma = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaption, lmm);
    double[] dPvdDis = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaption, lmm);
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
        for (int loopfact = 0; loopfact < nbFact; loopfact++) {
          dPvdPhi[loopperiod] += dPvdGamma[loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
          dPvdPhi[nbPeriods + loopperiod] += dPvdDis[loopsub];
        }
      }
    }

    double[][] dPvCaldPhi = new double[nbCalibrations][2 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][][] dPvCaldGamma = new double[nbCalibrations][][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldGamma[loopcal] = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaptionCalibration[loopcal], lmm);
    }
    double[][] dPvCaldDis = new double[nbCalibrations][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldDis[loopcal] = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaptionCalibration[loopcal], lmm);
    }
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
        for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
          for (int loopfact = 0; loopfact < nbFact; loopfact++) {
            dPvCaldPhi[loopcal][loopperiod] += dPvCaldGamma[loopcal][loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
            dPvCaldPhi[loopcal][nbPeriods + loopperiod] += dPvCaldDis[loopcal][loopsub];
          }
        }
      }
    }

    double[][] dPvCaldTheta = new double[nbCalibrations][3 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the SABR parameters as a unique array.
    // Implementation note: Theta is vector with first the Alpha, the the Rho and finally the Nu.
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopstrike = 0; loopstrike < nbStrikes; loopstrike++) {
        PresentValueSABRSensitivityDataBundle dPvCaldSABR = swaptionCalibration[loopperiod * nbStrikes + loopstrike].accept(PVSSSSC, sabrData);
        DoublesPair[] keySet = dPvCaldSABR.getAlpha().getMap().keySet().toArray(new DoublesPair[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][loopperiod] += dPvCaldSABR.getAlpha().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][nbPeriods + loopperiod] = dPvCaldSABR.getRho().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][2 * nbPeriods + loopperiod] = dPvCaldSABR.getNu().getMap().get(keySet[0]);
      }
    }

    double[][] dfdTheta = new double[2 * nbPeriods][3 * nbPeriods];
    // Implementation note: Derivative of f wrt the SABR parameters.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      for (int loops = 0; loops < 3 * nbPeriods; loops++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdTheta[loopp][loops] += -2 * dPvCaldPhi[loopcal][loopp] * dPvCaldTheta[loopcal][loops];
        }
      }
    }
    double[][] dfdPhi = new double[2 * nbPeriods][2 * nbPeriods];
    // Implementation note: Derivative of f wrt the calibration parameters. This is an approximation: the second order derivative part are ignored.
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdPhi[loopp1][loopp2] += 2 * dPvCaldPhi[loopcal][loopp1] * dPvCaldPhi[loopcal][loopp2];
        }
      }
    }

    DoubleMatrix2D dfdThetaMat = new DoubleMatrix2D(dfdTheta);
    DoubleMatrix2D dfdPhiMat = new DoubleMatrix2D(dfdPhi);
    DoubleMatrix2D dPhidThetaMat = (DoubleMatrix2D) ALGEBRA.scale(ALGEBRA.multiply(ALGEBRA.getInverse(dfdPhiMat), dfdThetaMat), -1.0);
    DoubleMatrix1D dPvdPhiMat = new DoubleMatrix1D(dPvdPhi);
    DoubleMatrix2D dPvdThetaMat = ALGEBRA.getTranspose(ALGEBRA.multiply(ALGEBRA.getTranspose(dPhidThetaMat), dPvdPhiMat));
    double[] dPvdTheta = dPvdThetaMat.getData()[0];

    // Storage in PresentValueSABRSensitivityDataBundle
    PresentValueSABRSensitivityDataBundle sensi = new PresentValueSABRSensitivityDataBundle();
    for (int loopp = 0; loopp < nbPeriods; loopp++) {
      DoublesPair expiryMaturity = new DoublesPair(swaptionCalibration[loopp * nbStrikes].getTimeToExpiry(), swaptionCalibration[loopp * nbStrikes].getMaturityTime());
      sensi.addAlpha(expiryMaturity, dPvdTheta[loopp]);
      sensi.addRho(expiryMaturity, dPvdTheta[nbPeriods + loopp]);
      sensi.addNu(expiryMaturity, dPvdTheta[2 * nbPeriods + loopp]);
    }
    return sensi;
  }

  public Triple<MultipleCurrencyAmount, PresentValueSABRSensitivityDataBundle, MultipleCurrencyMulticurveSensitivity> presentValueAndSensitivity(final SwaptionPhysicalFixedIbor swaption,
      final SABRSwaptionProviderInterface sabrData) {
    ArgumentChecker.notNull(swaption, "Swaption");
    ArgumentChecker.notNull(sabrData, "SABR swaption provider");
    Currency ccy = swaption.getCurrency();
    MulticurveProviderInterface multicurves = sabrData.getMulticurveProvider();
    int nbStrikes = _strikeMoneyness.length;
    LiborMarketModelDisplacedDiffusionParameters lmmParameters = _parametersInit.copy();
    SuccessiveLeastSquareLMMDDCalibrationObjective objective = new SuccessiveLeastSquareLMMDDCalibrationObjective(lmmParameters, ccy);
    SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface> calibrationEngine = new SuccessiveLeastSquareLMMDDCalibrationEngine<SABRSwaptionProviderInterface>(objective, nbStrikes);
    SwaptionPhysicalFixedIbor[] swaptionCalibration = METHOD_BASKET.calibrationBasketFixedLegPeriod(swaption, _strikeMoneyness);
    calibrationEngine.addInstrument(swaptionCalibration, PVSSC);
    calibrationEngine.calibrate(sabrData);
    LiborMarketModelDisplacedDiffusionProvider lmm = new LiborMarketModelDisplacedDiffusionProvider(multicurves, lmmParameters, ccy);

    // 1. PV 

    MultipleCurrencyAmount pv = METHOD_SWAPTION_LMM.presentValue(swaption, lmm);

    int nbCalibrations = swaptionCalibration.length;
    int nbPeriods = nbCalibrations / nbStrikes;
    int nbFact = lmmParameters.getNbFactor();
    List<Integer> instrumentIndex = calibrationEngine.getInstrumentIndex();

    // 2. SABR sensitivities 

    double[] dPvdPhi = new double[2 * nbPeriods];
    // Implementation note: Derivative of the priced swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][] dPvdGamma = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaption, lmm);
    double[] dPvdDis = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaption, lmm);
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
        for (int loopfact = 0; loopfact < nbFact; loopfact++) {
          dPvdPhi[loopperiod] += dPvdGamma[loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
          dPvdPhi[nbPeriods + loopperiod] += dPvdDis[loopsub];
        }
      }
    }

    double[][] dPvCaldPhi = new double[nbCalibrations][2 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the calibration parameters (multiplicative factor and additive term)
    double[][][] dPvCaldGamma = new double[nbCalibrations][][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldGamma[loopcal] = METHOD_SWAPTION_LMM.presentValueLMMSensitivity(swaptionCalibration[loopcal], lmm);
    }
    double[][] dPvCaldDis = new double[nbCalibrations][];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCaldDis[loopcal] = METHOD_SWAPTION_LMM.presentValueDDSensitivity(swaptionCalibration[loopcal], lmm);
    }
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
        for (int loopsub = instrumentIndex.get(loopperiod * nbStrikes); loopsub < instrumentIndex.get((loopperiod + 1) * nbStrikes); loopsub++) {
          for (int loopfact = 0; loopfact < nbFact; loopfact++) {
            dPvCaldPhi[loopcal][loopperiod] += dPvCaldGamma[loopcal][loopsub][loopfact] * lmmParameters.getVolatility()[loopsub][loopfact];
            dPvCaldPhi[loopcal][nbPeriods + loopperiod] += dPvCaldDis[loopcal][loopsub];
          }
        }
      }
    }

    double[][] dPvCaldTheta = new double[nbCalibrations][3 * nbPeriods];
    // Implementation note: Derivative of the calibration swaptions wrt the SABR parameters as a unique array.
    for (int loopperiod = 0; loopperiod < nbPeriods; loopperiod++) {
      for (int loopstrike = 0; loopstrike < nbStrikes; loopstrike++) {
        PresentValueSABRSensitivityDataBundle dPvCaldSABR = swaptionCalibration[loopperiod * nbStrikes + loopstrike].accept(PVSSSSC, sabrData);
        DoublesPair[] keySet = dPvCaldSABR.getAlpha().getMap().keySet().toArray(new DoublesPair[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][loopperiod] += dPvCaldSABR.getAlpha().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][nbPeriods + loopperiod] = dPvCaldSABR.getRho().getMap().get(keySet[0]);
        dPvCaldTheta[loopperiod * nbStrikes + loopstrike][2 * nbPeriods + loopperiod] = dPvCaldSABR.getNu().getMap().get(keySet[0]);
      }
    }

    double[][] dfdTheta = new double[2 * nbPeriods][3 * nbPeriods];
    // Implementation note: Derivative of f wrt the SABR parameters.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      for (int loops = 0; loops < 3 * nbPeriods; loops++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdTheta[loopp][loops] += -2 * dPvCaldPhi[loopcal][loopp] * dPvCaldTheta[loopcal][loops];
        }
      }
    }
    double[][] dfdPhi = new double[2 * nbPeriods][2 * nbPeriods];
    // Implementation note: Derivative of f wrt the calibration parameters. This is an approximation: the second order derivative part are ignored.
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
          dfdPhi[loopp1][loopp2] += 2 * dPvCaldPhi[loopcal][loopp1] * dPvCaldPhi[loopcal][loopp2];
        }
      }
    }

    DoubleMatrix2D dfdThetaMat = new DoubleMatrix2D(dfdTheta);
    DoubleMatrix2D dfdPhiMat = new DoubleMatrix2D(dfdPhi);
    DoubleMatrix2D dfdPhiInvMat = ALGEBRA.getInverse(dfdPhiMat);
    DoubleMatrix2D dPhidThetaMat = (DoubleMatrix2D) ALGEBRA.scale(ALGEBRA.multiply(dfdPhiInvMat, dfdThetaMat), -1.0);
    DoubleMatrix1D dPvdPhiMat = new DoubleMatrix1D(dPvdPhi);
    DoubleMatrix2D dPvdThetaMat = ALGEBRA.getTranspose(ALGEBRA.multiply(ALGEBRA.getTranspose(dPhidThetaMat), dPvdPhiMat));
    double[] dPvdTheta = dPvdThetaMat.getData()[0];

    // Storage in PresentValueSABRSensitivityDataBundle
    PresentValueSABRSensitivityDataBundle sensiSABR = new PresentValueSABRSensitivityDataBundle();
    for (int loopp = 0; loopp < nbPeriods; loopp++) {
      DoublesPair expiryMaturity = new DoublesPair(swaptionCalibration[loopp * nbStrikes].getTimeToExpiry(), swaptionCalibration[loopp * nbStrikes].getMaturityTime());
      sensiSABR.addAlpha(expiryMaturity, dPvdTheta[loopp]);
      sensiSABR.addRho(expiryMaturity, dPvdTheta[nbPeriods + loopp]);
      sensiSABR.addNu(expiryMaturity, dPvdTheta[2 * nbPeriods + loopp]);
    }

    // 3. Curve sensitivities 

    MultipleCurrencyMulticurveSensitivity[] dPvCalBasedC = new MultipleCurrencyMulticurveSensitivity[nbCalibrations];
    MultipleCurrencyMulticurveSensitivity[] dPvCalLmmdC = new MultipleCurrencyMulticurveSensitivity[nbCalibrations];
    MultipleCurrencyMulticurveSensitivity[] dPvCalDiffdC = new MultipleCurrencyMulticurveSensitivity[nbCalibrations];
    for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
      dPvCalBasedC[loopcal] = swaptionCalibration[loopcal].accept(PVCSSSC, sabrData);
      dPvCalLmmdC[loopcal] = METHOD_SWAPTION_LMM.presentValueCurveSensitivity(swaptionCalibration[loopcal], lmm);
      dPvCalDiffdC[loopcal] = dPvCalBasedC[loopcal].plus(dPvCalLmmdC[loopcal].multipliedBy(-1.0)).cleaned();
    }
    MultipleCurrencyMulticurveSensitivity[] dfdC = new MultipleCurrencyMulticurveSensitivity[2 * nbPeriods];
    // Implementation note: Derivative of f wrt the curves. This is an approximation: the second order derivative part are ignored.
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      dfdC[loopp] = new MultipleCurrencyMulticurveSensitivity();
      for (int loopcal = 0; loopcal < nbCalibrations; loopcal++) {
        dfdC[loopp] = dfdC[loopp].plus(dPvCalDiffdC[loopcal].multipliedBy(-2 * dPvCaldPhi[loopcal][loopp])).cleaned();
      }
    }
    MultipleCurrencyMulticurveSensitivity[] dPhidC = new MultipleCurrencyMulticurveSensitivity[2 * nbPeriods];
    for (int loopp1 = 0; loopp1 < 2 * nbPeriods; loopp1++) {
      dPhidC[loopp1] = new MultipleCurrencyMulticurveSensitivity();
      for (int loopp2 = 0; loopp2 < 2 * nbPeriods; loopp2++) {
        dPhidC[loopp1] = dPhidC[loopp1].plus(dfdC[loopp2].multipliedBy(-dfdPhiInvMat.getEntry(loopp1, loopp2))).cleaned();
      }
    }
    MultipleCurrencyMulticurveSensitivity dPvdC = METHOD_SWAPTION_LMM.presentValueCurveSensitivity(swaption, lmm);
    for (int loopp = 0; loopp < 2 * nbPeriods; loopp++) {
      dPvdC = dPvdC.plus(dPhidC[loopp].multipliedBy(dPvdPhi[loopp])).cleaned();
    }
    //    System.out.println(Runtime.getRuntime().totalMemory());

    return new Triple<MultipleCurrencyAmount, PresentValueSABRSensitivityDataBundle, MultipleCurrencyMulticurveSensitivity>(pv, sensiSABR, dPvdC);
  }

}
