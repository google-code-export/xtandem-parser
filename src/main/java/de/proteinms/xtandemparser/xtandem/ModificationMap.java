package de.proteinms.xtandemparser.xtandem;

import de.proteinms.xtandemparser.interfaces.Modification;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * In this class the fixed and variable modifications are sorted out and lists
 * for these modifications are build.
 *
 * @author Thilo Muth
 */
public class ModificationMap implements Serializable {

    /**
     * Contains a hash map for the fixed modifications.
     */
    private HashMap<String, Modification> iFixedModificationMap;
    /**
     * Contains a hash map for the variable modifications.
     */
    private HashMap<String, Modification> iVarModificationMap;
    /**
     * The instance contains a PeptideMap object.
     */
    private PeptideMap iPeptideMap;
    /**
     * InputParams object.
     */
    private InputParams iInputParams;

    /**
     * The constructor builds the fixed and variable modification maps from the
     * given raw mod map and the input parameters.
     *
     * @param aRawModMap the raw modification map from the parser
     * @param aPeptideMap the peptide map
     * @param aInputParams the input parameters from the parser
     * @param aNumberOfSpectra the total number of spectra
     */
    public ModificationMap(HashMap aRawModMap, PeptideMap aPeptideMap, InputParams aInputParams, int aNumberOfSpectra) {
        iInputParams = aInputParams;
        buildModificationMaps(aRawModMap, aPeptideMap, aNumberOfSpectra);
        iPeptideMap = aPeptideMap;
    }

    /**
     * This method checks for fixed or variable modifications and builds the
     * maps.
     *
     * @param rawModMap the raw modification map from the parser
     * @param peptideMap the peptide map
     * @param numberOfSpectra the total number of spectra
     */
    private void buildModificationMaps(HashMap rawModMap, PeptideMap peptideMap, int numberOfSpectra) {

        // Initialize the modification hash maps
        iFixedModificationMap = new HashMap<String, Modification>();
        iVarModificationMap = new HashMap<String, Modification>();

        if (rawModMap != null) {
            for (int i = 1; i <= numberOfSpectra; i++) {

                for (int j = 1; j <= peptideMap.getNumberOfPeptides(i); j++) {

                    // Get the domainID
                    List<Domain> domainList = peptideMap.getPeptideByIndex(i, j).getDomains();
                    for (int d = 1; d <= domainList.size(); d++) {
                        // The counter for the peptides
                        int m_counter = 1;
                        int m_counter_variable = 1;
                        int m_counter_fixed = 1;
                        String domainID = domainList.get(d - 1).getDomainKey();
                        int domainStart = domainList.get(d - 1).getDomainStart();
                        int domainEnd = domainList.get(d - 1).getDomainEnd();
                        String modKey = "_s" + i + "_p" + j + "_d" + d + "_m" + m_counter;

                        while (rawModMap.get("name" + modKey) != null) {

                            // Get the specific parameters for the modification
                            String modName = rawModMap.get("name" + modKey).toString();
                            double modMass = Double.parseDouble(rawModMap.get("modified" + modKey).toString());
                            String modLocation = rawModMap.get("at" + modKey).toString();
                            String aminoAcidSubstituted = null;

                            String[] values = modName.split("@");
                            String modifiedResidue = values[1];

                            if (rawModMap.get("pm" + modKey) != null) {
                                aminoAcidSubstituted = rawModMap.get("pm" + modKey).toString();
                            }

                            // Check for fixed modification
                            if (isFixedModificationInput(modMass, modifiedResidue, new Integer(modLocation) == domainStart, new Integer(modLocation) == domainEnd)) {

                                // Get a specific id for the modification (domainID)_m(modifcation#)
                                String modID = (domainID + "_m" + m_counter_fixed);

                                // Create an instance of a fixed modification.
                                FixedModification fixedMod = new FixedModification(modName, modMass, modLocation, m_counter_fixed,
                                        aminoAcidSubstituted != null, aminoAcidSubstituted);

                                // Put the modification into the map, value is the mod id.
                                iFixedModificationMap.put(modID, fixedMod);
                                m_counter_fixed++;

                            } else if (isVariableModificationInput(modMass, modifiedResidue, new Integer(modLocation) == domainStart, new Integer(modLocation) == domainEnd)) {

                                // Get a specific id for the modification (domainID)_m(modifcation#)
                                String modID = (domainID + "_m" + m_counter_variable);

                                // The rest will be assumed to be variable modifications.
                                VariableModification varMod = new VariableModification(modName, modMass, modLocation, m_counter_variable,
                                        aminoAcidSubstituted != null, aminoAcidSubstituted);

                                // Put the modification into the map, value is the mod id.
                                iVarModificationMap.put(modID, varMod);
                                m_counter_variable++;

                            } else {

                                // not found as fixed or variable, assumed variable. means that it's in the residue, modification mass [1-n]
                                // Get a specific id for the modification (domainID)_m(modifcation#)
                                String modID = (domainID + "_m" + m_counter_variable);

                                // The rest will be assumed to be variable modifications.
                                VariableModification varMod = new VariableModification(modName, modMass, modLocation, m_counter_variable,
                                        aminoAcidSubstituted != null, aminoAcidSubstituted);

                                // Put the modification into the map, value is the mod id.
                                iVarModificationMap.put(modID, varMod);
                                m_counter_variable++;
                            }

                            m_counter++;
                            modKey = "_s" + i + "_p" + j + "_d" + d + "_m" + m_counter;
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if a given modification mass is given in the fixed modification
     * input parameter section: --> label="residue, modification mass">
     *
     * @param aModMass the modification mass
     * @param aaModified the amino acid modified
     * @param nTerm if the modification is at the n term
     * @param cTerm of the modification is at the c term
     * @return true of the given modification mass is a fixed modification
     */
    private boolean isFixedModificationInput(double aModMass, String aaModified, boolean nTerm, boolean cTerm) {

        BigDecimal modMass = new BigDecimal(aModMass);
        modMass = modMass.setScale(2, BigDecimal.ROUND_HALF_UP);
        String modificationMasses = iInputParams.getResidueModMass();

        if (modificationMasses != null) {

            StringTokenizer tokenizer = new StringTokenizer(modificationMasses, ",");

            while (tokenizer.hasMoreTokens()) {
                String[] tokens = tokenizer.nextToken().split("@");
                BigDecimal inputMass = new BigDecimal(new Double(tokens[0]));
                inputMass = inputMass.setScale(2, BigDecimal.ROUND_HALF_UP);
                if (modMass.equals(inputMass)
                        && (tokens[1].equalsIgnoreCase(aaModified)
                        || (tokens[1].equalsIgnoreCase("[") && nTerm)
                        || (tokens[1].equalsIgnoreCase("]") && cTerm))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if a given modification mass is given in the variable modification
     * input parameter section: --> label="residue, potential modification
     * mass", or in the refine section: label="refine, potential modification
     * mass"
     *
     * @param aModMass the modification mass
     * @param aaModified the amino acid modified
     * @param nTerm if the modification is at the n term
     * @param cTerm of the modification is at the c term
     * @return true of the given modification mass is a variable modification
     */
    private boolean isVariableModificationInput(double aModMass, String aaModified, boolean nTerm, boolean cTerm) {

        BigDecimal modMass = new BigDecimal(aModMass);
        modMass = modMass.setScale(3, BigDecimal.ROUND_HALF_UP);
        String modificationMasses = iInputParams.getResiduePotModMass();
        String refineModificationMasses = iInputParams.getRefineModMass();

        if (modificationMasses != null) {

            StringTokenizer tokenizer = new StringTokenizer(modificationMasses, ",");
            while (tokenizer.hasMoreTokens()) {
                String[] tokens = tokenizer.nextToken().split("@");
                BigDecimal inputMass = new BigDecimal(new Double(tokens[0]));
                inputMass = inputMass.setScale(3, BigDecimal.ROUND_HALF_UP);
                if (modMass.equals(inputMass)
                        && (tokens[1].equalsIgnoreCase(aaModified)
                        || (tokens[1].equalsIgnoreCase("[") && nTerm)
                        || (tokens[1].equalsIgnoreCase("]") && cTerm))) {
                }
            }
        }

        if (refineModificationMasses != null) {

            StringTokenizer tokenizer2 = new StringTokenizer(refineModificationMasses, ",");

            while (tokenizer2.hasMoreTokens()) {
                String[] tokens = tokenizer2.nextToken().split("@");
                BigDecimal inputMass = new BigDecimal(new Double(tokens[0]));
                inputMass = inputMass.setScale(2, BigDecimal.ROUND_HALF_UP);
                if (modMass.equals(inputMass)
                        && (tokens[1].equalsIgnoreCase(aaModified)
                        || (tokens[1].equalsIgnoreCase("[") && nTerm)
                        || (tokens[1].equalsIgnoreCase("]") && cTerm))) {
                }
            }
        }

        return false;
    }

    /**
     * Returns the fixed modifications as list.
     *
     * @param aDomainKey the domainKey of the identification
     * @return modificationsList The fixed modification list
     */
    public ArrayList<Modification> getFixedModifications(String aDomainKey) {
        ArrayList<Modification> modificationList = new ArrayList<Modification>();
        if (iFixedModificationMap != null) {
            int modCount = 1;
            while (iFixedModificationMap.get(aDomainKey + "_m" + modCount) != null) {
                modificationList.add(iFixedModificationMap.get(aDomainKey + "_m" + modCount));
                modCount++;
            }
        }
        return modificationList;
    }

    /**
     * Returns an arrayList of all the fixed modifications in the file.
     *
     * @return fixedModList
     */
    public ArrayList<Modification> getAllFixedModifications() {
        ArrayList<Modification> fixedModList = new ArrayList<Modification>();
        for (Map.Entry<String, Modification> e : iFixedModificationMap.entrySet()) {
            fixedModList.add(e.getValue());
        }
        return fixedModList;
    }

    /**
     * Returns an arrayList of all the variable modifications in the file.
     *
     * @return varModList ArrayList with the modifications
     */
    public ArrayList<Modification> getAllVariableModifications() {
        ArrayList<Modification> varModList = new ArrayList<Modification>();
        for (Map.Entry<String, Modification> e : iVarModificationMap.entrySet()) {
            varModList.add(e.getValue());
        }
        return varModList;
    }

    /**
     * Returns the variable modifications as list.
     *
     * @param aDomainKey the domainID of the identification
     * @return modificationsList The variable modification list
     */
    public ArrayList<Modification> getVariableModifications(String aDomainKey) {
        ArrayList<Modification> modificationList = new ArrayList<Modification>();
        if (iVarModificationMap != null) {
            int modCount = 1;
            while (iVarModificationMap.get(aDomainKey + "_m" + modCount) != null) {
                modificationList.add(iVarModificationMap.get(aDomainKey + "_m" + modCount));
                modCount++;
            }
        }
        return modificationList;
    }
}
