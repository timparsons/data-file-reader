/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datafilereader.tabulardata.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Leonid Andreev
 * 
 * Largely based on the the DataTable entity from the DVN v2-3;
 * original author: Ellen Kraffmiller (2006).
 * 
 */
@Getter
@Setter
@ToString
public class DataTable implements Serializable {
    
    /** Creates a new instance of DataTable */
    public DataTable() {
    }
    
    private static final long serialVersionUID = 1L;
    private Long id;
    
    /**
     * unf: the Universal Numeric Signature of the 
     * data table.
     */
    private String unf;
    
    /*
     * caseQuantity: Number of observations
     */    
    private Long caseQuantity; 
    
    
    /*
     * varQuantity: Number of variables
     */
    private Long varQuantity;

    /*
     * recordsPerCase: this property is specific to fixed-field data files
     * in which rows of observations may represented by *multiple* lines.
     * The only known use case (so far): the fixed-width data files from 
     * ICPSR. 
     */
     private Long recordsPerCase;
     
     /*
      * DataFile that stores the data for this DataTable
      */
//     private DataFile dataFile;

     /*
      * DataVariables in this DataTable:
     */
    private List<DataVariable> dataVariables;
    
    /* 
     * originalFileType: the format of the file from which this data table was
     * extracted (STATA, SPSS, R, etc.)
     * Note: this was previously stored in the StudyFile. 
     */
    private String originalFileFormat;
    
    /*
     * originalFormatVersion: the version/release number of the original file
     * format; for example, STATA 9, SPSS 12, etc. 
     */
    private String originalFormatVersion;
    
    /* 
     * Custom overrides for hashCode(), equals() and toString() methods:
     */
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof DataTable)) {
            return false;
        }
        DataTable other = (DataTable)object;
        return !(!Objects.equals(this.id, other.id) && (this.id == null || !this.id.equals(other.id)));
    }
}
