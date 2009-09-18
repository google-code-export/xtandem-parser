package xtandem;

import interfaces.Modification;

/**
 * This class implements modification and represents a fixed modification object.
 *
 * @author Thilo Muth
 */
public class FixedModification implements Modification {

	 /**
     * This String holds the name of the modification.
     */
    private String iName;

    /**
     * This variable contains the mass of the modification.     *
     */
    private double iMass;

    /**
     * This variable contains the location of the modification.     *
     */
    private String iLocation;

    /**
     * This variable holds the domainID as identification to which peptide belongs the modification.
     */
    private String iDomainID;

    /**
     * This variables holds the modified residue.
     */
    private String iModifiedResidue;
    /**
     * Constructor for building a fixed modification.
     *
     * @param aName The modification name
     * @param aMass The modification mass
     * @param aLocation The modification location
     */
	public FixedModification(String aName, double aMass, String aLocation, String aDomainID){
		iName = aName;
		iMass = aMass;
		iLocation = aLocation;
		iDomainID = aDomainID;
	}

	/**
	 * Returns the modification name.
	 */
	public String getName() {
		return iName;
	}

	/**
	 * Sets the modification name.
	 * @param aName The modification name
	 */
	public void setName(String aName) {
		iName = aName;
	}

	/**
	 * Returns the modification mass.
	 */
	public double getMass() {
		return iMass;
	}

	/**
	 * Sets the modification mass.
	 * @param aMass The modification mass
	 */
	public void setMass(double aMass) {
		iMass = aMass;
	}

	/**
	 * Returns the modification location.
	 */
	public String getLocation() {
		return iLocation;
	}

	/**
	 * Sets the modification location.
	 * @param aLocation The modification location
	 */
	public void setLocation(String aLocation) {
		iLocation = aLocation;
	}

	/**
	 * Always returns true because it's a fixed modification.
	 */
	public boolean isFixed() {
		return true;
	}

	/**
	 * Returns the domain id.
	 * @return iDomainID
	 */
	public String getDomainID() {
		return iDomainID;
	}

	/**
	 * Sets the domain id.
	 * @param aDomainID The domain id
	 */
	public void setDomainID(String aDomainID) {
		iDomainID = aDomainID;
	}

	/**
	 * Returns the modified residue (a peptide letter).
	 * @return iModifiedResidue
	 */
	public String getModifiedResidue() {
		String[] values = iName.split("@");
		iModifiedResidue = values[1];
		return iModifiedResidue;
	}
}
