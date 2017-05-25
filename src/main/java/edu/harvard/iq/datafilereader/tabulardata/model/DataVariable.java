/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datafilereader.tabulardata.model;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Leonid Andreev
 * 
 * Largely based on the the DataVariable entity from the DVN v2-3;
 * original author: Ellen Kraffmiller.
 *
 */
@Getter
@Setter
@ToString
public class DataVariable implements Serializable {
    
    private static final long serialVersionUID = 1L;
    public enum VariableInterval { DISCRETE, CONTINUOUS, NOMINAL, DICHOTOMOUS }; // former VariableIntervalType
    public enum VariableType { NUMERIC, CHARACTER }; // former VariableFormatType
    
    private Long id;
    
    /*
     * dataTable: DataTable to which this variable belongs.
     */
    private DataTable dataTable;

    /*
     * name: Name of the Variable
     */
    private String name;

    /*
     * label: Variable Label
     */
    private String label;
    
    /*
     * weighted: indicates if this variable is weighted.
     */
    private boolean weighted;
    
    /*
     * fileStartPosition: this property is specific to fixed-width data; 
     * this is a byte offset where the data column begins.
     */
    private Long fileStartPosition;

    /*
     * fileEndPosition: similarly, byte offset where the variable column 
     * ends in the fixed-width data file.
     */
    private Long fileEndPosition;

    

    /*
     * Interval: <FINALIZED>
     * former VariableIntervalType
     */
    //@ManyToOne
    private VariableInterval interval;

    

    /*
     * Type: <FINALIZED>
     * former VariableFormatType
     */
    //@ManyToOne
    //@JoinColumn(nullable=false)
    private VariableType type;

    /*
     * formatSchema: <FINALIZED, DROPPED>
     * Used for the original format - i.e. RData, SPSS, etc. (??)
     */
    //experimentprivate String formatSchema;

    /*
     * format: <FINALIZED>
     * used for format strings - such as "%D-%Y-%M" for date values, etc. 
     * former formatSchemaName
     */
    private String format;

    /*
     * formatCategory: 
     * <FINALIZED>
     * left as is. 
     * TODO: (?) consider replacing with an enum (?)
     * Used for "time", "date", etc.
     */
    private String formatCategory;
    
    /*
     * recordSegmentNumber: this property is specific to fixed-width data 
     * files.
     */
    private Long recordSegmentNumber;

    /*
     * invalidRanges: value ranges that are defined as "invalid" for this
     * variable. 
     * Note that VariableRange is itself an entity.
     */
    private Collection<VariableRange> invalidRanges;
    
    /*
     * invalidRangeItems: a collection of individual value range items defined 
     * as "invalid" for this variable.
     * Note that VariableRangeItem is itself an entity. 
     */
    private Collection<VariableRangeItem> invalidRangeItems;
      
    /*
     * Summary Statistics for this variable.
     * Note that SummaryStatistic is itself an entity.
     */
    private Collection<SummaryStatistic> summaryStatistics;
    
    /*
     * unf: printable representation of the UNF, Universal Numeric Fingerprint
     * of this variable.
     */
    private String unf;
    
    /*
     * Variable Categories, for categorical variables.
     * VariableCategory is itself an entity. 
     */
    private Collection<VariableCategory> categories;
    
    /*
     * The boolean "ordered": identifies ordered categorical variables ("ordinals"). 
     */
    private boolean orderedFactor = false; 
    
    /*
     * the "Universe" of the variable. (see the DDI documentation for the 
     * explanation)
     */
    private String universe;

  
    
    /* 
     * fileOrder: the numeric order in which this variable occurs in the 
     * physical file. 
     */
    private int fileOrder;
    
    /*
     * number of decimal points, where applicable.
     */
    private Long numberOfDecimalPoints;
    
    public String getIntervalLabel() {
        if (isIntervalDiscrete()) {
            return "discrete";
        }
        if (isIntervalContinuous()) {
            return "contin";
        }
        if (isIntervalNominal()) {
            return "nominal";
        }
        if (isIntervalDichotomous()) {
            return "dichotomous";
        }
        return null; 
    }
    
    public void setIntervalDiscrete() {
        this.interval = VariableInterval.DISCRETE;
    }
    
    public void setIntervalContinuous() {
        this.interval = VariableInterval.CONTINUOUS;
    }
    
    public void setIntervalNominal() {
        this.interval = VariableInterval.NOMINAL;
    }
    
    public void setIntervalDichotomous() {
        this.interval = VariableInterval.DICHOTOMOUS;
    }
    
    public boolean isIntervalDiscrete() {
        return this.interval == VariableInterval.DISCRETE;
    }
    
    public boolean isIntervalContinuous() {
        return this.interval == VariableInterval.CONTINUOUS;
    }
    
    public boolean isIntervalNominal() {
        return this.interval == VariableInterval.NOMINAL;
    }
    
    public boolean isIntervalDichotomous() {
        return this.interval == VariableInterval.DICHOTOMOUS;
    }
    
    public void setTypeNumeric() {
        this.type = VariableType.NUMERIC;
    }
    
    public void setTypeCharacter() {
        this.type = VariableType.CHARACTER;
    }
    
    public boolean isTypeNumeric() {
        return this.type == VariableType.NUMERIC;
    }
    
    public boolean isTypeCharacter() {
        return this.type == VariableType.CHARACTER;
    }
    
    public boolean isCategorical () {
        return (categories != null && categories.size() > 0);
    }
    
    public boolean isOrderedCategorical () {
        return isCategorical() && orderedFactor; 
    }
    
    
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
        if (!(object instanceof DataVariable)) {
            return false;
        }
        DataVariable other = (DataVariable)object;
        if (this.id != other.id ) {
            if (this.id == null || !this.id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }
    
    
}
