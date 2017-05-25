/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datafilereader.tabulardata.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author Leonid Andreev
 *      
 * Largely based on the VariableRangeItem entity from the DVN v2-3;
 * original author: Ellen Kraffmiller (2006).
 * 
 */
@Getter
@Setter
public class VariableRangeItem implements Serializable {
    /*
     * Simple constructor: 
     */
    public VariableRangeItem() {
    }
    
    /*
     * Definitions of class properties: 
     */
   
    private static final long serialVersionUID = 1L;
    private Long id;
    
    
    /*
     * value: a numeric (BigDecimal) value of tis Range Item.
     */
    private BigDecimal value;

    /**
     * DataVariable for which this range item is defined.
     */
    private DataVariable dataVariable;
    
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
    public boolean equals(Object object) {
        if (!(object instanceof VariableRangeItem)) {
            return false;
        }
        VariableRangeItem other = (VariableRangeItem)object;
        // TODO: 
        // Should we instead check if the values of the objects equals()
        // each other? -- L.A., Jan. 2014
        if (this.id != other.id) {
            if (this.id == null || !this.id.equals(other.id)) {
                return false;
            }                    
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "edu.harvard.iq.dataverse.VariableRangeItem[ " + this.getValue() + " ]";
    }
}
