package edu.harvard.iq.datafilereader.tabulardata.model;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Base of the object hierarchy for "anything that can be inside a dataverse".
 *
 * @author michael
 */
@Getter
@Setter
public abstract class DvObject extends DataverseEntity implements java.io.Serializable {
    
    public static final String DATAVERSE_DTYPE_STRING = "Dataverse";
    public static final String DATASET_DTYPE_STRING = "Dataset";
    public static final String DATAFILE_DTYPE_STRING = "DataFile";
    public static final List<String> DTYPE_LIST = Arrays.asList(DATAVERSE_DTYPE_STRING, DATASET_DTYPE_STRING, DATAFILE_DTYPE_STRING);
    
    private Long id;

    private DvObject owner;

    private Timestamp publicationDate;

    private Timestamp createDate;

    private Timestamp modificationTime;

    /**
     * @todo Rename this to contentIndexTime (or something) to differentiate it
     * from permissionIndexTime. Content Solr docs vs. permission Solr docs.
     */
    private Timestamp indexTime;

    /**
     * @todo Make this nullable=true. Currently we can't because the
     * CreateDataverseCommand saves the dataverse before it assigns a role.
     */
    private Timestamp permissionModificationTime;

    private Timestamp permissionIndexTime;

    /**
     * previewImageAvailable could also be thought of as "thumbnail has been
     * generated. However, were all three thumbnails generated? We might need a
     * boolean per thumbnail size.
     */
    private boolean previewImageAvailable;
    


    /**
     * @return Whether {@code this} takes no permissions from roles assigned on its parents.
     */
    public abstract boolean isEffectivelyPermissionRoot();

    public boolean isReleased() {
        return publicationDate != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public String toString() {
        String classNameComps[] = getClass().getName().split("\\.");
        return String.format("[%s id:%d %s]", classNameComps[classNameComps.length - 1],
                getId(), toStringExtras());
    }

    /**
     * Convenience method to add data to the default toString output.
     *
     * @return
     */
    protected String toStringExtras() {
        return "";
    }
    
    public abstract String getDisplayName();
    
    /**
     * 
     * @param other 
     * @return {@code true} iff {@code other} is {@code this} or below {@code this} in the containment hierarchy.
     */
    public abstract boolean isAncestorOf( DvObject other );
}
