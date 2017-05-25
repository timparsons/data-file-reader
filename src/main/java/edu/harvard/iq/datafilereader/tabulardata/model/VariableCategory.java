/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datafilereader.tabulardata.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.harvard.iq.datafilereader.tabulardata.model.util.AlphaNumericComparator;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Ellen Kraffmiller
 * @author Leonid Andreev
 *    
 * Largely based on the VariableCategory entity from the DVN v2-3;
 * original author: Ellen Kraffmiller (2006).
 * 
 */
@Getter
@Setter
public class VariableCategory  implements Comparable, Serializable {
    /*
     * Simple constructor: 
     */
    public VariableCategory() {
    }
    
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();
    
    /*
     * Definitions of class properties: 
     */
   
    private static final long serialVersionUID = 1L;
    private Long id;
    
    /*
     * DataVariable for which this range is defined.
     */
    private DataVariable dataVariable;
    
    /*
     * Category Value: 
     */
    private String value;

    /*
     * Category Label:  
     */
    private String label;
    
    /*
     * Is this a missing category?
     */
    private boolean missing;
    
    /*
     * If this is an "Ordered Categorical Variable", aka an "Ordinal", it 
     * has an explicitly assigned order value:
     */
    private int catOrder;
    
    /*
     * Frequency of this category:
     */
    private Double frequency;

    
    /* 
     * Helper methods: 
     */
    
    
    // helper for html display  
    // [TODO: double-check if we still need this method in 4.0; -- L.A., jan. 2014] 
    private transient List charList;

    public List getValueCharacterList() {
        if (charList == null) {
            charList = new ArrayList();
            for (int i=0; i < this.value.length(); i++) {
                if (this.value.charAt(i) == ' ') {
                    charList.add( "&nbsp;" );
                } else {
                    charList.add( this.value.charAt(i) );
                }
            }
        }
        return charList;
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
    public boolean equals(Object object) {
        if (!(object instanceof VariableCategory)) {
            return false;
        }
        
        // TODO: 
        // We should probably compare the values instead, similarly 
        // to comareTo() below. -- L.A., Jan. 2014
        VariableCategory other = (VariableCategory)object;
        if (this.id != other.id) {
            if (this.id == null || !this.id.equals(other.id)) {
                return false;
            }                    
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "edu.harvard.iq.dataverse.VariableCategory[ value=" + value + " ]";
    }
    
    @Override
    public int compareTo(Object obj) {
        VariableCategory ss = (VariableCategory)obj;     
        return alphaNumericComparator.compare(this.getValue(),ss.getValue());
        
    }
    
}
