/* Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew;

import static com.opengamma.financial.convention.businessday.BusinessDayDateUtils.addWorkDays;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import org.threeten.bp.LocalDate;

import com.opengamma.analytics.financial.model.BumpType;

/**
 * 
 */
public class CS01FromParSpreadTest extends ISDABaseTest {

  private static final LocalDate TODAY = LocalDate.of(2013, 6, 4);
  private static final LocalDate EFFECTIVE_DATE = TODAY.plusDays(1); // AKA stepin date
  private static final LocalDate CASH_SETTLE_DATE = addWorkDays(TODAY, 3, DEFAULT_CALENDAR); // AKA valuation date
  private static final LocalDate STARTDATE = LocalDate.of(2013, 2, 2);
  private static final LocalDate[] MATURITIES = new LocalDate[] {LocalDate.of(2013, 6, 20), LocalDate.of(2013, 9, 20), LocalDate.of(2013, 12, 20), LocalDate.of(2014, 3, 20),
    LocalDate.of(2014, 6, 20), LocalDate.of(2014, 9, 20), LocalDate.of(2014, 12, 20), LocalDate.of(2015, 3, 20), LocalDate.of(2015, 6, 20), LocalDate.of(2015, 9, 20), LocalDate.of(2015, 12, 20),
    LocalDate.of(2016, 3, 20), LocalDate.of(2016, 6, 20), LocalDate.of(2016, 9, 20), LocalDate.of(2016, 12, 20), LocalDate.of(2017, 3, 20), LocalDate.of(2017, 6, 20), LocalDate.of(2017, 9, 20),
    LocalDate.of(2017, 12, 20), LocalDate.of(2018, 3, 20), LocalDate.of(2018, 6, 20), LocalDate.of(2018, 9, 20), LocalDate.of(2018, 12, 20), LocalDate.of(2019, 3, 20), LocalDate.of(2019, 6, 20),
    LocalDate.of(2019, 9, 20), LocalDate.of(2019, 12, 20), LocalDate.of(2020, 3, 20), LocalDate.of(2020, 6, 20), LocalDate.of(2020, 9, 20), LocalDate.of(2020, 12, 20), LocalDate.of(2021, 3, 20),
    LocalDate.of(2021, 6, 20), LocalDate.of(2021, 9, 20), LocalDate.of(2021, 12, 20), LocalDate.of(2022, 3, 20), LocalDate.of(2022, 6, 20), LocalDate.of(2022, 9, 20), LocalDate.of(2022, 12, 20),
    LocalDate.of(2023, 3, 20), LocalDate.of(2023, 6, 20) };

  private static final double DEAL_SPREAD = 100.0;
  private static final double NOTIONAL = 1e6;

  //yield curve
  private static final LocalDate SPOT_DATE = LocalDate.of(2013, 6, 6);
  private static final String[] YIELD_CURVE_POINTS = new String[] {"1M", "2M", "3M", "6M", "1Y", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "12Y", "15Y", "20Y", "25Y", "30Y" };
  private static final String[] YIELD_CURVE_INSTRUMENTS = new String[] {"M", "M", "M", "M", "M", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S", "S" };
  private static final double[] YIELD_CURVE_RATES = new double[] {0.00194, 0.002292, 0.002733, 0.004153, 0.006902, 0.004575, 0.006585, 0.00929, 0.012175, 0.0149, 0.01745, 0.019595, 0.02144, 0.023045,
    0.02567, 0.02825, 0.03041, 0.031425, 0.03202 };
  private static final ISDACompliantYieldCurve YIELD_CURVE = makeYieldCurve(TODAY, SPOT_DATE, YIELD_CURVE_POINTS, YIELD_CURVE_INSTRUMENTS, YIELD_CURVE_RATES);

  //private static final double[] QUOTED_SPREADS = new double[] {8.97, 9.77, 10.7, 11.96, 13.17, 15.59, 17.8, 19.66, 21.35, 23.91, 26.54, 28.56, 30.63, 32.41, 34.08, 35.33, 36.74, 38.9, 40.88, 42.71,
  //44.49, 46.92, 49.2, 51.36, 53.5, 55.58, 57.59, 59.49, 61.4, 62.76, 64.11, 65.35, 66.55, 67.58, 68.81, 69.81, 70.79, 71.65, 72.58, 73.58, 74.2 };

  private static final LocalDate[] BUCKET_DATES = new LocalDate[] {LocalDate.of(2013, 12, 20), LocalDate.of(2014, 6, 20), LocalDate.of(2015, 6, 20), LocalDate.of(2016, 6, 20),
    LocalDate.of(2017, 6, 20), LocalDate.of(2018, 6, 20), LocalDate.of(2019, 6, 20), LocalDate.of(2020, 6, 20), LocalDate.of(2021, 6, 20), LocalDate.of(2022, 6, 20), LocalDate.of(2023, 6, 20),
    LocalDate.of(2028, 6, 20), LocalDate.of(2033, 6, 20), LocalDate.of(2043, 6, 20) };

  private static final double[] PAR_SPREADS_AT_BUCKET_DATES = new double[] {10.7, 13.17, 21.35, 30.63, 36.74, 44.49, 53.5, 61.4, 66.55, 70.79, 74.2, 74.2, 74.2, 74.2 };

  // These numbers come from The ISDA excel plugin
  private static final double[] PARELLEL_CS01 = new double[] {4.44270353942497, 30.0278505776398, 55.3868828464654, 80.4734104513718, 106.127260703191, 131.794539837356, 157.209927186215,
    182.372358231722, 208.12006951847, 233.7275289487, 258.987409407063, 284.178525610287, 309.575849697874, 334.809534792469, 359.644785347887, 384.086200723836, 408.946037869194, 433.442466121137,
    457.469236295568, 481.034666114259, 504.921733027089, 528.313650464244, 551.178748198784, 573.529094140796, 596.107540537752, 618.263785438271, 639.874499893493, 661.186271106971,
    682.431640940034, 703.467480283569, 723.983498025077, 743.993186594343, 764.162951254262, 784.014616982583, 803.361059556938, 822.215898645791, 841.207351388533, 859.917029630126,
    878.142241147122, 895.896360249962, 913.770089165433 };

  private static final double[][] BUCKETED_CS01 = new double[][] {
    {4.44270353942497, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {30.0278505776398, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {55.3868828464654, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {28.2126202539729, 52.2656333140862, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.420049420366345, 105.712077958918, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.409085625416306, 79.3768750722273, 52.0237104432632, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.398231019398468, 53.2960183187939, 103.536394075164, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.387485995508724, 27.4707421277567, 154.535904512417, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.376492972027631, 1.04035512787926, 206.721491059769, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.364277365036414, 1.00736139383231, 155.160594792942, 77.2425316789664, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.352228056648557, 0.974815752199237, 104.290175365007, 153.432646045761, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.340212021811281, 0.94235893839667, 53.5489984024411, 229.411372018457, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.328098313427672, 0.909637013414566, 2.38253795959917, 306.008595441942, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.319762723039602, 0.88721773347164, 2.32253315082115, 229.137926119657, 102.248465369641, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.311559263013739, 0.865153077941883, 2.26347897864729, 153.463743563974, 202.872911301638, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.3034863896266, 0.843438929438306, 2.20536430145871, 78.9728057994515, 301.892390060145, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.295276146297652, 0.821354257728862, 2.14625994265569, 3.18796793736739, 402.599069654102, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.285112892458445, 0.793683071133311, 2.07284279080122, 3.07345914933935, 300.807969676277, 126.597901243024, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.27514423106903, 0.766541525774878, 2.00083119574271, 2.961144258469, 200.944649514492, 250.747423838078, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.265366723616045, 0.739920269485217, 1.9302003276718, 2.85098434237019, 102.975735834621, 372.492613021757, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.255455757783296, 0.712935210221699, 1.85860504301957, 2.73932424164902, 3.64386067406089, 495.880867067471, 0, 0, 0, 0, 0, 0, 0, 0 },
    {0.243987042066601, 0.681421926090192, 1.77553866343924, 2.6124023510439, 3.46973254425689, 369.681438878295, 150.141811956609, 0, 0, 0, 0, 0, 0, 0 },
    {0.232776106105781, 0.650616991358655, 1.69433936206276, 2.48833295664677, 3.29951765460149, 246.290567331695, 296.867506653208, 0, 0, 0, 0, 0, 0, 0 },
    {0.221817061215462, 0.620504224297519, 1.61496448859016, 2.36705090380165, 3.13312645094238, 125.645012403336, 440.2564732977, 0, 0, 0, 0, 0, 0, 0 },
    {0.210745866036088, 0.590083063054664, 1.53477714002342, 2.24452952777079, 2.96503737079803, 3.7317284192484, 585.078129887007, 0, 0, 0, 0, 0, 0, 0 },
    {0.20082850085007, 0.562715840542183, 1.46285824423331, 2.13570681230346, 2.8169523851318, 3.54065487613575, 435.920629882597, 172.040720662965, 0, 0, 0, 0, 0, 0 },
    {0.191154935200166, 0.536021335983961, 1.39270728945207, 2.02955969084984, 2.67250885793019, 3.35428070324245, 290.386436035323, 339.79794705246, 0, 0, 0, 0, 0, 0 },
    {0.181614841776589, 0.509695057819559, 1.32352417134274, 1.92487787248341, 2.5300602626982, 3.17048158748195, 146.816313218001, 505.188723154838, 0, 0, 0, 0, 0, 0 },
    {0.172104374286663, 0.483450141648689, 1.25455558822735, 1.82052422473267, 2.38806231277894, 2.98726822234308, 3.63881896457696, 670.023591486532, 0, 0, 0, 0, 0, 0 },
    {0.165550528535841, 0.465377721442506, 1.20703826171631, 1.748504847493, 2.28992187773641, 2.86049371280711, 3.48040464839827, 498.602123988508, 193.209595676154, 0, 0, 0, 0, 0 },
    {0.159158447129992, 0.447751293944365, 1.16069371049188, 1.67826366986867, 2.19420537444365, 2.73685118312716, 3.32590477062494, 331.349011612664, 381.580604663208, 0, 0, 0, 0, 0 },
    {0.1529239381029, 0.430559305035494, 1.115491549665, 1.60975460597457, 2.10084996419418, 2.61625942148819, 3.17521781297708, 168.158291720462, 565.240820763466, 0, 0, 0, 0, 0 },
    {0.146639285832084, 0.413229037043994, 1.06992582603327, 1.54069457339312, 2.00674382306448, 2.4946979635676, 3.02331921294557, 3.59750818332977, 750.305883072622, 0, 0, 0, 0, 0 },
    {0.141288166395637, 0.398435711021494, 1.03110013004171, 1.48219058450361, 1.92741264735952, 2.39263278414836, 2.89617822375743, 3.4433815646745, 558.014874206557, 213.010509520807, 0, 0, 0, 0 },
    {0.136073134343606, 0.384018500991512, 0.99326175302672, 1.42517524573393, 1.85010113627759, 2.29316716630207, 2.77227654844892, 3.29318266452383, 370.528429416171, 420.517091874351, 0, 0, 0, 0 },
    {0.130990525752661, 0.369967283253558, 0.956384114562947, 1.36960845698031, 1.77475485964307, 2.19623102392513, 2.65152682595721, 3.14680553503005, 187.719990606766, 622.671000121366, 0, 0, 0, 0 },
    {0.125870883389834, 0.355813668237448, 0.919237757260327, 1.31363689061942, 1.69885986422286, 2.09858908154642, 2.52989805725468, 2.9993629038147, 3.50256435911744, 826.205942562788, 0, 0, 0, 0 },
    {0.121592273479021, 0.343963300300509, 0.888177032344617, 1.26703438302345, 1.63589867956809, 2.01782985907034, 2.42953440551114, 2.87788680081863, 3.35889040401632, 614.220225571366,
      231.657836866565, 0, 0, 0 },
    {0.117424292879476, 0.3324193223464, 0.857919410068853, 1.22163698248329, 1.57456580785614, 1.93915943743489, 2.33176679878544, 2.75955299069292, 3.21893348820962, 407.613956328538,
      457.208549452769, 0, 0, 0 },
    {0.113363892439988, 0.32117329029635, 0.828442755229641, 1.17741146385408, 1.51481634366735, 1.86252019980515, 2.23652361688864, 2.64427477417173, 3.08259060660676, 206.242346351429,
      676.824474037663, 0, 0, 0 },
    {0.109276238918765, 0.30985157893626, 0.798768104201542, 1.13289067206479, 1.45467003437316, 1.78537413728086, 2.14065271197939, 2.52823849759831, 2.94535236432247, 3.39918437135267,
      897.821618556768, 0, 0, 0 } };

  @Test
  public void parellelCS01FromParSpreadTest() {
    final double coupon = DEAL_SPREAD * ONE_BP;
    final double scale = NOTIONAL * ONE_BP;

    final int m = BUCKET_DATES.length;
    final double[] parSpreads = new double[m];
    final CDSAnalytic[] curveCDSs = new CDSAnalytic[m];
    for (int i = 0; i < m; i++) {
      parSpreads[i] = PAR_SPREADS_AT_BUCKET_DATES[i] * ONE_BP;
      curveCDSs[i] = new CDSAnalytic(TODAY, EFFECTIVE_DATE, CASH_SETTLE_DATE, TODAY, BUCKET_DATES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
    }

    final int n = MATURITIES.length;
    for (int i = 0; i < n; i++) {
      final CDSAnalytic cds = new CDSAnalytic(TODAY, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      final double cs01 = scale * CS01_CAL.parallelCS01FromParSpreads(cds, coupon, YIELD_CURVE, curveCDSs, parSpreads, ONE_BP, BumpType.ADDITIVE);
      // System.out.println(MATURITIES[i].toString() + "\t" + cs01);
      assertEquals(MATURITIES[i].toString(), PARELLEL_CS01[i], cs01, 1e-14 * NOTIONAL);
    }
  }

  @Test
  public void bucketedCS01TermStructureTest() {
    final double coupon = DEAL_SPREAD * ONE_BP;
    final double scale = NOTIONAL * ONE_BP;

    final int m = BUCKET_DATES.length;
    final double[] parSpreads = new double[m];
    final CDSAnalytic[] curveCDSs = new CDSAnalytic[m];
    for (int i = 0; i < m; i++) {
      parSpreads[i] = PAR_SPREADS_AT_BUCKET_DATES[i] * ONE_BP;
      curveCDSs[i] = new CDSAnalytic(TODAY, EFFECTIVE_DATE, CASH_SETTLE_DATE, TODAY, BUCKET_DATES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
    }

    final int n = MATURITIES.length;
    for (int i = 0; i < n; i++) {
      final CDSAnalytic cds = new CDSAnalytic(TODAY, EFFECTIVE_DATE, CASH_SETTLE_DATE, STARTDATE, MATURITIES[i], PAY_ACC_ON_DEFAULT, PAYMENT_INTERVAL, STUB, PROCTECTION_START, RECOVERY_RATE);
      final double[] cs01 = CS01_CAL.bucketedCS01FromParSpreads(cds, coupon, YIELD_CURVE, curveCDSs, parSpreads, ONE_BP, BumpType.ADDITIVE);
      for (int j = 0; j < m; j++) {
        cs01[j] *= scale;
        assertEquals(MATURITIES[i].toString() + "\t" + BUCKET_DATES[j], BUCKETED_CS01[i][j], cs01[j], 1e-13 * NOTIONAL);
      }
    }
  }

}
