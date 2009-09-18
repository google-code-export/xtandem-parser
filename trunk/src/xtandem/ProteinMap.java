package xtandem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * This class holds the protein information in a map.
 * @author Thilo Muth
 *
 */
public class ProteinMap {

	/**
	 * This map has as key the proteinID and as value the protein.
	 */
	private HashMap<String, Protein> iProteinMap = null;

	/**
	 * The constructor gets a raw protein map from the xtandem parser.
	 */
	public ProteinMap(ArrayList<String> aProteinIDList, HashMap aRawProteinMap){
		if (aProteinIDList != null && aRawProteinMap != null) {
			initMap(aProteinIDList, aRawProteinMap);
		}
	}

	  /**
     * This method initializes the protein map from a given raw map.
     * @param aProteinIDList
     * @param aProteinMap
     */
    private void initMap(ArrayList<String> aProteinIDList, HashMap<String, String> aProteinMap) {
        iProteinMap = new HashMap<String, Protein>();

        // Iterate over the protein id list
        if(aProteinIDList != null){
        	Iterator iter = aProteinIDList.iterator();
        	while (iter.hasNext()) {
            	// Get the proteinID
    	        String proteinID = iter.next().toString();

    	        // Get the rest of the protein parameters for the protein map.
    	        String proteinUID = aProteinMap.get("uid" + proteinID).toString();
    	        String proteinLabel = aProteinMap.get("label" + proteinID).toString();
    	        double expectValue = Double.parseDouble(aProteinMap.get("expect" + proteinID).toString());
    	        double summedScore = Double.parseDouble(aProteinMap.get("sumI" + proteinID).toString());

    	        // Create an instance of a protein.
    	        Protein protein = new Protein(proteinID, proteinUID, proteinLabel, expectValue, summedScore);

    	        // Put the protein into the map, value is the id.
    	        iProteinMap.put(proteinID, protein);
            }
        }

     }

    /**
     * Returns a protein object for a given protein id.
     * @param aProteinID String
     * @return iProtein Protein
     */
    public Protein getProtein(String aProteinID){
        return iProteinMap.get(aProteinID);
    }
    /**
	 * This method returns an iterator of the keys in the protein map.
	 * @return iProteinMap.keySet().iterator()
	 */
	public Iterator getProteinIDIterator(){
		return iProteinMap.keySet().iterator();
	}
}