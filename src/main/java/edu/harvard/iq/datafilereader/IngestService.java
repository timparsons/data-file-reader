package edu.harvard.iq.datafilereader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.harvard.iq.datafilereader.tabulardata.TabularDataFileReader;
import edu.harvard.iq.datafilereader.tabulardata.TabularDataIngest;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.csv.CSVFileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.dta.DTA117FileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.dta.DTAFileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.por.PORFileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.rdata.RDATAFileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.sav.SAVFileReader;
import edu.harvard.iq.datafilereader.tabulardata.impl.plugins.xlsx.XLSXFileReader;
import edu.harvard.iq.datafilereader.tabulardata.model.DataVariable;
import edu.harvard.iq.datafilereader.tabulardata.model.SummaryStatistic;
import edu.harvard.iq.datafilereader.tabulardata.model.SummaryStatistic.SummaryStatisticType;
import edu.harvard.iq.datafilereader.tabulardata.util.IngestableDataChecker;
import edu.harvard.iq.datafilereader.tabulardata.util.SumStatCalculator;
import edu.harvard.iq.datafilereader.tabulardata.util.TabularSubsetGenerator;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IngestService {
    private static final String[] TABULAR_DATA_FORMAT_SET = { "POR", "SAV", "DTA", "RDA" };

    private static Map<String, String> STATISTICAL_FILE_EXTENSION = new HashMap<>();

    /*
     * The following are Stata, SAS and SPSS syntax/control cards: These are
     * recognized as text files (because they are!) so we check all the uploaded
     * "text/plain" files for these extensions, and assign the following types
     * when they are matched; Note that these types are only used in the
     * metadata displayed on the dataset page. We don't support ingest on
     * control cards. -- L.A. 4.0 Oct. 2014
     */

    static {
        STATISTICAL_FILE_EXTENSION.put("do", "application/x-stata-syntax");
        STATISTICAL_FILE_EXTENSION.put("sas", "application/x-sas-syntax");
        STATISTICAL_FILE_EXTENSION.put("sps", "application/x-spss-syntax");
        STATISTICAL_FILE_EXTENSION.put("csv", "text/csv");
    }

    public final static String SHAPEFILE_FILE_TYPE = "application/zipped-shapefile";

    public static final String MIME_TYPE_STATA = "application/x-stata";
    public static final String MIME_TYPE_STATA13 = "application/x-stata-13";
    public static final String MIME_TYPE_RDATA = "application/x-rlang-transport";

    public static final String MIME_TYPE_CSV = "text/csv";
    public static final String MIME_TYPE_CSV_ALT = "text/comma-separated-values";

    public static final String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_TYPE_SPSS_SAV = "application/x-spss-sav";
    public static final String MIME_TYPE_SPSS_POR = "application/x-spss-por";

    public static final String MIME_TYPE_TAB = "text/tab-separated-values";

    public static final String MIME_TYPE_FITS = "application/fits";

    public static final String MIME_TYPE_ZIP = "application/zip";

    public static final String MIME_TYPE_UNDETERMINED_DEFAULT = "application/octet-stream";
    public static final String MIME_TYPE_UNDETERMINED_BINARY = "application/binary";

    public TabularDataIngest ingestData(final File file, final boolean produceSummaryStats) throws IOException {
        Optional<String> fileType = determineType(file, null);

        if (fileType.isPresent()) {
            TabularDataFileReader reader = getTabDataReaderByMimeType(fileType.get());
            try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)) {
                TabularDataIngest ingest = reader.read(bis, Optional.empty());

                if (produceSummaryStats) {
                    produceSummaryStatistics(ingest);
                }

                return ingest;
            }
        } else {
            throw new IllegalArgumentException("Non-tabular data file supplied");
        }
    }

    private TabularDataFileReader getTabDataReaderByMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }

        TabularDataFileReader ingestPlugin = null;

        if (mimeType.equals(MIME_TYPE_STATA)) {
            ingestPlugin = new DTAFileReader();
        } else if (mimeType.equals(MIME_TYPE_STATA13)) {
            ingestPlugin = new DTA117FileReader();
        } else if (mimeType.equals(MIME_TYPE_RDATA)) {
            ingestPlugin = new RDATAFileReader();
        } else if (mimeType.equals(MIME_TYPE_CSV) || mimeType.equals(MIME_TYPE_CSV_ALT)) {
            ingestPlugin = new CSVFileReader();
        } else if (mimeType.equals(MIME_TYPE_XLSX)) {
            ingestPlugin = new XLSXFileReader();
        } else if (mimeType.equals(MIME_TYPE_SPSS_SAV)) {
            ingestPlugin = new SAVFileReader();
        } else if (mimeType.equals(MIME_TYPE_SPSS_POR)) {
            ingestPlugin = new PORFileReader();
        }

        return ingestPlugin;
    }

    private static boolean ingestableAsTabular(final String mimeType) {
        /*
         * In the final 4.0 we'll be doing real-time checks, going through the
         * available plugins and verifying the lists of mime types that they can
         * handle. In 4.0 beta, the ingest plugins are still built into the main
         * code base, so we can just go through a hard-coded list of mime types.
         * -- L.A.
         */

        if (mimeType == null) {
            return false;
        }

        if (mimeType.equals(MIME_TYPE_STATA)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_STATA13)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_RDATA)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_CSV) || mimeType.equals(MIME_TYPE_CSV_ALT)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_XLSX)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_SPSS_SAV)) {
            return true;
        } else if (mimeType.equals(MIME_TYPE_SPSS_POR)) {
            return true;
        }

        return false;
    }

    private void produceSummaryStatistics(final TabularDataIngest dataFile) throws IOException {
        produceDiscreteNumericSummaryStatistics(dataFile);
        produceContinuousSummaryStatistics(dataFile);
        produceCharacterSummaryStatistics(dataFile);
    }

    private void produceDiscreteNumericSummaryStatistics(final TabularDataIngest dataFile) throws IOException {

        TabularSubsetGenerator subsetGenerator = new TabularSubsetGenerator();

        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isIntervalDiscrete()
                    && dataFile.getDataTable().getDataVariables().get(i).isTypeNumeric()) {
                log.trace("subsetting discrete-numeric vector");
                //Double[] variableVector = subsetGenerator.subsetDoubleVector(dataFile, i);
                Long[] variableVector = subsetGenerator.subsetLongVector(dataFile, i);
                // We are discussing calculating the same summary stats for 
                // all numerics (the same kind of sumstats that we've been calculating
                // for numeric continuous type)  -- L.A. Jul. 2014
                calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                // calculate the UNF while we are at it:
                log.trace("Done! (discrete numeric)");
                variableVector = null;
            }
        }
    }

    private void produceContinuousSummaryStatistics(final TabularDataIngest dataFile) throws IOException {

        // quick, but memory-inefficient way:
        // - this method just loads the entire file-worth of continuous vectors 
        // into a Double[][] matrix. 
        //Double[][] variableVectors = subsetContinuousVectors(dataFile);
        //calculateContinuousSummaryStatistics(dataFile, variableVectors);

        // A more sophisticated way: this subsets one column at a time, using 
        // the new optimized subsetting that does not have to read any extra 
        // bytes from the file to extract the column:

        TabularSubsetGenerator subsetGenerator = new TabularSubsetGenerator();

        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isIntervalContinuous()) {
                log.trace("subsetting continuous vector");
                if ("float".equals(dataFile.getDataTable().getDataVariables().get(i).getFormat())) {
                    Float[] variableVector = subsetGenerator.subsetFloatVector(dataFile, i);
                    log.trace("Calculating summary statistics on a Float vector;");
                    calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                    // calculate the UNF while we are at it:
                    variableVector = null;
                } else {
                    Double[] variableVector = subsetGenerator.subsetDoubleVector(dataFile, i);
                    log.trace("Calculating summary statistics on a Double vector;");
                    calculateContinuousSummaryStatistics(dataFile, i, variableVector);
                    // calculate the UNF while we are at it:
                    variableVector = null;
                }
                log.trace("Done! (continuous);");
            }
        }
    }

    private void produceCharacterSummaryStatistics(final TabularDataIngest dataFile) throws IOException {

        /*
         * At this point it's still not clear what kinds of summary stats we
         * want for character types. Though we are pretty confident we don't
         * want to keep doing what we used to do in the past, i.e. simply store
         * the total counts for all the unique values; even if it's a very long
         * vector, and *every* value in it is unique. (As a result of this, our
         * Categorical Variable Value table is the single largest in the
         * production database. With no evidence whatsoever, that this
         * information is at all useful. -- L.A. Jul. 2014
         */

        TabularSubsetGenerator subsetGenerator = new TabularSubsetGenerator();

        for (int i = 0; i < dataFile.getDataTable().getVarQuantity(); i++) {
            if (dataFile.getDataTable().getDataVariables().get(i).isTypeCharacter()) {
                log.trace("subsetting character vector");
                String[] variableVector = subsetGenerator.subsetStringVector(dataFile, i);
                //calculateCharacterSummaryStatistics(dataFile, i, variableVector);
                // calculate the UNF while we are at it:
                variableVector = null;
            }
        }
    }

    private void calculateContinuousSummaryStatistics(final TabularDataIngest dataFile, final int varnum,
            final Number[] dataVector) throws IOException {
        Map<SummaryStatisticType, BigDecimal> sumStats = SumStatCalculator.calculateSummaryStatistics(dataVector);
        assignContinuousSummaryStatistics(dataFile.getDataTable().getDataVariables().get(varnum), sumStats);
    }

    private void assignContinuousSummaryStatistics(final DataVariable variable,
            final Map<SummaryStatisticType, BigDecimal> sumStats) throws IOException {
        if (sumStats == null || sumStats.size() != SummaryStatisticType.values().length) {
            throw new IOException("Wrong number of summary statistics types calculated! (" + sumStats.size() + ")");
        }

        for (Entry<SummaryStatisticType, BigDecimal> sumStat : sumStats.entrySet()) {
            SummaryStatistic ss = new SummaryStatistic();
            ss.setType(sumStat.getKey());
            if (!ss.isTypeMode()) {
                ss.setValue(sumStat.getValue().toPlainString());
            } else {
                ss.setValue(".");
            }
            ss.setDataVariable(variable);
            variable.getSummaryStatistics().add(ss);
        }
    }

    private Optional<String> determineType(final File file, final String suppliedContentType) throws IOException {
        String recognizedType = determineFileType(file, file.getName());
        log.trace("File utility recognized the file as " + recognizedType);
        if (recognizedType != null && !recognizedType.equals("")) {
            // is it any better than the type that was supplied to us,
            // if any?
            // This is not as trivial a task as one might expect... 
            // We may need a list of "good" mime types, that should always
            // be chosen over other choices available. Maybe it should 
            // even be a weighed list... as in, "application/foo" should 
            // be chosen over "application/foo-with-bells-and-whistles".

            // For now the logic will be as follows: 
            //
            // 1. If the contentType supplied (by the browser, most likely) 
            // is some form of "unknown", we always discard it in favor of 
            // whatever our own utilities have determined; 
            // 2. We should NEVER trust the browser when it comes to the 
            // following "ingestable" types: Stata, SPSS, R;
            // 2a. We are willing to TRUST the browser when it comes to
            //  the CSV and XSLX ingestable types.
            // 3. We should ALWAYS trust our utilities when it comes to 
            // ingestable types. 

            if (suppliedContentType == null || suppliedContentType.equals("")
                    || suppliedContentType.equalsIgnoreCase(MIME_TYPE_UNDETERMINED_DEFAULT)
                    || suppliedContentType.equalsIgnoreCase(MIME_TYPE_UNDETERMINED_BINARY)
                    || (ingestableAsTabular(suppliedContentType)
                            && !suppliedContentType.equalsIgnoreCase(MIME_TYPE_CSV)
                            && !suppliedContentType.equalsIgnoreCase(MIME_TYPE_CSV_ALT)
                            && !suppliedContentType.equalsIgnoreCase(MIME_TYPE_XLSX))
                    || ingestableAsTabular(recognizedType)) {
                return Optional.of(recognizedType);
            }
        }

        return Optional.empty();
    }

    private String determineFileType(final File f, final String fileName) throws IOException {
        String fileType = null;
        String fileExtension = getFileExtension(fileName);

        // step 1: 
        // Apply our custom methods to try and recognize data files that can be 
        // converted to tabular data, or can be parsed for extra metadata 
        // (such as FITS).
        log.trace("Attempting to identify potential tabular data files;");
        IngestableDataChecker tabChk = new IngestableDataChecker(TABULAR_DATA_FORMAT_SET);

        fileType = tabChk.detectTabularDataFormat(f);

        log.trace("determineFileType: tabular data checker found " + fileType);

        // step 2: If not found, check if graphml or FITS

        // step 4: 
        // Additional processing; if we haven't gotten much useful information 
        // back from Jhove, we'll try and make an educated guess based on 
        // the file extension:

        if (fileExtension != null) {
            log.trace("fileExtension=" + fileExtension);

            if (fileType == null || fileType.startsWith("text/plain") || "application/octet-stream".equals(fileType)) {
                if (fileType != null && fileType.startsWith("text/plain")
                        && STATISTICAL_FILE_EXTENSION.containsKey(fileExtension)) {
                    fileType = STATISTICAL_FILE_EXTENSION.get(fileExtension);
                }

                log.trace("mime type recognized by extension: " + fileType);
            }
        } else {
            log.trace("fileExtension is null");
        }

        if (fileType == null) {
            throw new IllegalStateException("Unrecognized file type, not a tabular format");
        }

        log.trace("returning fileType " + fileType);
        return fileType;
    }

    private String getFileExtension(final String fileName) {
        String ext = null;
        if (fileName.lastIndexOf(".") != -1) {
            ext = (fileName.substring(fileName.lastIndexOf(".") + 1)).toLowerCase();
        }
        return ext;
    }
}
