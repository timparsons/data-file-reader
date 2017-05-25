/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/

package edu.harvard.iq.datafilereader.tabulardata.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math.stat.StatUtils;

import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.jet.stat.Descriptive;
import edu.harvard.iq.datafilereader.tabulardata.model.SummaryStatistic.SummaryStatisticType;

/**
 *
 * @author Leonid Andreev
 */
public class SumStatCalculator {
    
    private static Logger logger = Logger.getLogger(SumStatCalculator.class.getPackage().getName());

    public static Map<SummaryStatisticType, BigDecimal> calculateSummaryStatistics(final Number[] x) {
        logger.fine("entering calculate summary statistics ("+x.length+" Number values);");
        
        Map<SummaryStatisticType, BigDecimal> ret = new HashMap<>();
        //("mean", "medn", "mode", "vald", "invd", "min", "max", "stdev");

        Float testNanValue = new Float(Float.NaN);
        Number testNumberValue = testNanValue;
        if (Double.isNaN(testNumberValue.doubleValue())) {
            logger.fine("Float test NaN value is still recognized as a Double NaN.");
        }
        
        int invalid = countInvalidValues(x);
        ret.put(SummaryStatisticType.INVD, new BigDecimal(invalid));
        logger.fine("counted invalid values: " + invalid);
        int valid = x.length - invalid;
        ret.put(SummaryStatisticType.VALD, new BigDecimal(valid));
        logger.fine("counted valid values: " + valid);
        
        
        //double[] newx = prepareForSummaryStats(x);
        double[] newx = prepareForSummaryStatsAlternative(x, x.length - invalid);
        logger.fine("prepared double vector for summary stats calculation ("+newx.length+" double values);");        
        
        ////nx[0] = StatUtils.mean(newx);
        double mean = calculateMean(newx);
        ret.put(SummaryStatisticType.MEAN, new BigDecimal(mean));
        logger.fine("calculated mean: " + mean);
        ////nx[1] = StatUtils.percentile(newx, 50);
        double median = calculateMedian(newx);
        ret.put(SummaryStatisticType.MEDN, new BigDecimal(median));
        logger.fine("calculated medn: " + median);
        double mode = getMode(newx);
        ret.put(SummaryStatisticType.MODE, new BigDecimal(mode));
        
        double min = StatUtils.min(newx);
        ret.put(SummaryStatisticType.MIN, new BigDecimal(min));
        logger.fine("calculated min: " + min);
        double max = StatUtils.max(newx);
        ret.put(SummaryStatisticType.MAX, new BigDecimal(max));
        logger.fine("calculated max: " + max);
        double variance = Math.sqrt(StatUtils.variance(newx));
        ret.put(SummaryStatisticType.STDEV, new BigDecimal(variance));
        logger.fine("calculated stdev: " + variance);
        return ret;
    }

    private static double[] prepareForSummaryStatsAlternative(final Number[] x, final int length) {
        double[] retvector = new double[length];

        int c = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] != null) {
                double xvalue = x[i].doubleValue();
                if (!Double.isNaN(xvalue)) {
                    retvector[c++] = xvalue; 
                }
            }
        }
        
        // Throw exception if c != length in the end?
        
        return retvector;
    }
    
    /**
     * Returns the number of Double.NaNs (or nulls) in a double-type array
     *
     */
    private static int countInvalidValues(final Number[] x){
        int counter=0;
        for (int i=0; i<x.length;i++){
            ////if ( x[i] == null || x[i].equals(Double.NaN) ) {
            if ( x[i] == null || (Double.isNaN(x[i].doubleValue())) ) {
                counter++;
            }
        }
        return counter;
    }
    
    private static double calculateMedian(final double[] values) {
        double[] sorted = new double[values.length];
        System.arraycopy(values, 0, sorted, 0, values.length);
        logger.fine("made an extra copy of the vector;");
        Arrays.sort(sorted);
        logger.fine("sorted double vector for median calculations;");
        
        if (sorted.length == 0) {
            return Double.NaN;
        }
        if (sorted.length == 1) {
            return sorted[0]; // always return single value for n = 1
        }
        double n = sorted.length;
        double pos = (n + 1) / 2;
        double fpos = Math.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;
        
        double lower = sorted[intPos - 1];
        double upper = sorted[intPos];
        
        return lower + dif * (upper - lower);
    }
    
    private static double calculateMean(final double[] values) {
        return calculateMean(values, 0 , values.length);
    }
    
    private static double calculateMean(final double[] values, final int begin, final int length) {

        if (values == null || length == 0) {
            return Double.NaN;
        }

        double sampleSize = length;

        // Compute initial estimate using definitional formula
        double xbar = calculateSum(values) / sampleSize;

        // Compute correction factor in second pass
        double correction = 0;
        for (int i = begin; i < begin + length; i++) {
            correction += values[i] - xbar;
        }
        return xbar + (correction / sampleSize);
    }

    
    private static double calculateSum(final double[] values) {
        return calculateSum(values, 0, values.length);
    }
    
    private static double calculateSum(final double[] values, final int begin, final int length) {
        if (values == null || length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = begin; i < begin + length; i++) {
            sum += values[i];
        }
        return sum;
    }
    
    /**
     * Returns the number of Double.NaNs in a double-type array
     *
     * TODO: figure out if this is actually necessary - to count NaNs and nulls
     * separately; -- L.A. 4.0 alpha 1
     */
    private static int countNaNs(final double[] x) {
        int NaNcounter = 0;
        for (int i = 0; i < x.length; i++) {
            if (Double.isNaN(x[i])) {
                NaNcounter++;
            }
        }
        return NaNcounter;
    }
    
    /**
     * Returns the mode statistic of a double variable
     *
     */
    
    public static double getMode(final double[] x){
        double mode = Double.NaN;

        if ((countNaNs(x) == x.length) || (x.length < 1)){
            return mode;
        } else {
            DoubleArrayList dx = new DoubleArrayList(x);
            dx.sort();
            DoubleArrayList freqTable = new DoubleArrayList(1);
            IntArrayList countTable = new IntArrayList(1);
            Descriptive.frequencies(dx, freqTable, countTable);
            //out.println("freqTable="+
            //    ReflectionToStringBuilder.toString(freqTable));
            //out.println("freqTable="+
            //    ReflectionToStringBuilder.toString(countTable));
            int max_i = 0;
            for (int i=1; i< countTable.size();i++ ){
                if (countTable.get(i)> countTable.get(max_i)){
                    max_i = i;
                }
            }
            mode = freqTable.get(max_i);
            //out.println("position = "+
            //max_i+"\tits value="+freqTable.get(max_i));
        }
        return mode;
    }
    
    public static Map<Double, Integer> calculateFrequencies(final Number[] x) {
        Float testNanValue = new Float(Float.NaN);
        Number testNumberValue = testNanValue;
        if (Double.isNaN(testNumberValue.doubleValue())) {
            logger.fine("Float test NaN value is still recognized as a Double NaN.");
        }

        int invalid = countInvalidValues(x);
        logger.fine("counted invalid values: " + invalid);

        double[] newx = prepareForSummaryStatsAlternative(x, x.length - invalid);
        logger.fine("prepared double vector for summary stats calculation (" + newx.length + " double values);");

        Map<Double, Integer> ret = new HashMap<>();
        if ((countNaNs(newx) != newx.length) && (newx.length > 0)) {
            DoubleArrayList dx = new DoubleArrayList(newx);
            dx.sort();
            DoubleArrayList freqTable = new DoubleArrayList(1);
            IntArrayList countTable = new IntArrayList(1);
            Descriptive.frequencies(dx, freqTable, countTable);

            for (int i = 0; i < freqTable.size(); i++) {
                ret.put(freqTable.get(i), countTable.get(i));
            }
        }

        return ret;
    }
    
    
}
