package fr.cnamts.njc.domain.bo.project;

import java.util.HashMap;
import java.util.Map;

public class PropArtiBean implements Comparable<PropArtiBean> {

    private static final String DN = "dn";

    private static final String MULTIPUB = "multipub";

    /**
     * Map des properties
     */
    private Map<String, String[]> properties = new HashMap<String, String[]>();

    private String uri;     

    @Override
    /** Comparaison pour tri dn prioritaire, multipub, puis vertech
     *  return int
     */
    public int compareTo(final PropArtiBean pComparedObject) {

        final String uri = this.getUri();
        final String compUri = pComparedObject.getUri();

        if (uri.contains(DN)) { // dn prioritaire
            if (compUri.contains(DN)) { // dn vs dn
                if (uri.equals(compUri)) {
                    return this.compareVerTech(pComparedObject); // identique donc on verify le vertech >
                } else {
                    if (uri.contains(MULTIPUB)) { // multipub prioritaire
                        return -1; // multipub vs st
                    } else { // compUri contient multipub
                        return 1;
                    }
                }
            } else { // dn prioritaire
                return -1;
            }
        } else { // cnamts
            if (compUri.contains(DN)) { // cnamts vs dn
                return 1;
            } else { // cnamts vs cnamts
                if (uri.equals(compUri)) {
                    return this.compareVerTech(pComparedObject); // identique donc on verify le vertech >
                } else {
                    if (uri.contains(MULTIPUB)) { // multipub prioritaire
                        return -1; // multipub vs st
                    } else { // compUri contient multipub
                        return 1;
                    }
                }

            }
        }
    }

    private int compareVerTech(final PropArtiBean pCompObj) {

        final String vertech = this.getFirstPropertyForKey("vertech");
        final String vertechComp = pCompObj.getFirstPropertyForKey("vertech");
        if (null == vertech && null == vertechComp) {
            return 0;
        } else {
            if (null == vertech) {
                return -1;
            } else {

                if (vertech.equals(vertechComp)) {
                    return 0;
                } else {

                    if (null == vertechComp) {
                        return 1;
                    }
                    if (Integer.parseInt(vertech) < Integer.parseInt(vertechComp)) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            }
        }
    }

    public void setProperties(Map<String, String[]> properties) {
		this.properties = properties;
	}
    
    public String getFirstPropertyForKey(final String pKey) {
        if (this.properties.containsKey(pKey)) {
            return this.properties.get(pKey)[0];
        } else {
            return "";
        }
    }

    public String getUri() {
        return this.uri;
    }

    public boolean isCompatibleWith(final String pSocle) {
        return this.getUri().contains(MULTIPUB) || this.getUri().contains(pSocle);
    }

    public void setUri(final String pUri) {
        this.uri = pUri;
    }

}
