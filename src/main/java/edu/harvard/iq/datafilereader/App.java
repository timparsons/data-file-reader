package edu.harvard.iq.datafilereader;

import java.io.File;
import java.io.IOException;

import edu.harvard.iq.datafilereader.tabulardata.TabularDataIngest;

public class App {

    public static void main(final String[] args) throws IOException {
        IngestService service = new IngestService();
        TabularDataIngest result = service.ingestData(new File("/Users/tim/Downloads/Dec15c_Archive.sav"),
                true);

        result.getDataTable().getDataVariables().forEach(variable -> {
            System.out.println(variable.getName());
            System.out.println("\tLabel: " + variable.getLabel());
            System.out.println("\tType: " + variable.getType().name());
            System.out.println("\tResponse Categories:");
            variable.getCategories().forEach(category -> {
                System.out.println("\t\t" + category.getValue() + " = " + category.getLabel());
            });
            System.out.println("\tStats:");
            variable.getSummaryStatistics().forEach(stat -> {
                System.out.println("\t\t" + stat.getTypeLabel() + " => " + stat.getValue());
            });
        });
    }

}
