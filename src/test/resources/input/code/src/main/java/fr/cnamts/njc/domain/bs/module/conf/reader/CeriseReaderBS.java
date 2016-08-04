package fr.cnamts.njc.domain.bs.module.conf.reader;

import java.util.EnumMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;
import fr.cnamts.njc.domain.bs.module.conf.reader.CeriseReaderBS.CVSPathElement;
import fr.cnamts.njc.domain.inter.api.ContentReader;

public class CeriseReaderBS implements ContentReader<Map<CVSPathElement, String>, String> {
	
	public enum CVSPathElement{REFERENTIEL,MODULE,LABEL}
	
	public Map<CVSPathElement, String> read(String ceriseContent){		
	
		Map<CVSPathElement, String> tryptiqueCVSPath = new EnumMap<CVSPathElement, String>(CVSPathElement.class);
		
		final String[] tryptiqueCVSPathElements =  ceriseContent.split(DELIMITER);
		
        if (isCeriseContentValid(tryptiqueCVSPathElements)) {
        	
        	tryptiqueCVSPath.put(CVSPathElement.REFERENTIEL, tryptiqueCVSPathElements[0]);
        	tryptiqueCVSPath.put(CVSPathElement.MODULE, tryptiqueCVSPathElements[1]);
        	tryptiqueCVSPath.put(CVSPathElement.LABEL, tryptiqueCVSPathElements[2]);
        	
        	return tryptiqueCVSPath;
            
        }
		
		return Collections.emptyMap();
	}
	
	private boolean isCeriseContentValid(String[] tryptiqueCVSPathElements){
		return tryptiqueCVSPathElements != null && tryptiqueCVSPathElements.length == NB_MAX_ELEMENT;
	}
	
	private final String DELIMITER = ";";
	private final int NB_MAX_ELEMENT = 3;

}
