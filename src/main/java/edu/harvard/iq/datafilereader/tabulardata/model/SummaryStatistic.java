/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datafilereader.tabulardata.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 *
 * @author Leonid Andreev
 *    
 * Largely based on the SummaryStatistic entity from the DVN v2-3;
 * original author: Ellen Kraffmiller (2006).
 * 
 */

@Getter
@Setter
@ToString
public class SummaryStatistic implements Serializable {
    /*
     * Simple constructor: 
     */
    public SummaryStatistic() {
    }
    
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
     * type of this Summary Statistic value (for ex., "median", "mean", etc.)
     */
    
    public enum SummaryStatisticType {MEAN, MEDN, MODE, MIN, MAX, STDEV, VALD, INVD}; 
    
    private SummaryStatisticType type;

    
    /*
     * value: string representation of this Summary Statistic value. 
     */
    private String value;
    
    // This method returns the summary statistic type as a character
    // label used in the DDI.
    public String getTypeLabel() {
        if (isTypeMean()) {
            return "mean";
        }
        if (isTypeMedian()) {
            return "medn";
        }
        if (isTypeMode()) {
            return "mode";
        }
        if (isTypeMin()) {
            return "min";
        }
        if (isTypeMax()) {
            return "max";
        }
        if (isTypeStdDev()) {
            return "stdev";
        }
        if (isTypeValid()) {
            return "vald";
        }
        if (isTypeInvalid()) {
            return "invd";
        }
        
        return null; 
    }
    
    public void setTypeByLabel(final String label) {
        if ("mean".equals(label)) {
            setTypeMean();
        }
        else if ("medn".equals(label)) {
            setTypeMedian();
        }
        else if ("mode".equals(label)) {
            setTypeMode();
        }
        else if ("min".equals(label)) {
            setTypeMin();
        }
        else if ("max".equals(label)) {
            setTypeMax();
        }
        else if ("stdev".equals(label)) {
            setTypeStdDev();
        }
        else if ("vald".equals(label)) {
            setTypeValid();
        }
        else if ("invd".equals(label)) {
            setTypeInvalid();
        }
    }
    
    public void setTypeMean() {
        this.type = SummaryStatisticType.MEAN;
    }
    
    public void setTypeMedian() {
        this.type = SummaryStatisticType.MEDN;
    }
    
    public void setTypeMode() {
        this.type = SummaryStatisticType.MODE;
    }
    
    public void setTypeMin() {
        this.type = SummaryStatisticType.MIN;
    }
    
    public void setTypeMax() {
        this.type = SummaryStatisticType.MAX;
    }
    
    public void setTypeStdDev() {
        this.type = SummaryStatisticType.STDEV;
    }
    
    public void setTypeValid() {
        this.type = SummaryStatisticType.VALD;
    }
    
    public void setTypeInvalid() {
        this.type = SummaryStatisticType.INVD;
    }
    
    
    public boolean isTypeMean() {
        return this.type == SummaryStatisticType.MEAN;
    }
    
    public boolean isTypeMedian() {
        return this.type == SummaryStatisticType.MEDN;
    }
    
    public boolean isTypeMode() {
        return this.type == SummaryStatisticType.MODE;
    }
    
    public boolean isTypeMin() {
        return this.type == SummaryStatisticType.MIN;
    }
    
    public boolean isTypeMax() {
        return this.type == SummaryStatisticType.MAX;
    }
    
    public boolean isTypeStdDev() {
        return this.type == SummaryStatisticType.STDEV;
    }
    
    public boolean isTypeValid() {
        return this.type == SummaryStatisticType.VALD;
    }
    
    public boolean isTypeInvalid() {
        return this.type == SummaryStatisticType.INVD;
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
        if (!(object instanceof SummaryStatistic)) {
            return false;
        }
        
        SummaryStatistic other = (SummaryStatistic)object;
        if (this.id != other.id) {
            if (this.id == null || !this.id.equals(other.id)) {
                return false;
            }                    
        }
        return true;
    }
    
}
