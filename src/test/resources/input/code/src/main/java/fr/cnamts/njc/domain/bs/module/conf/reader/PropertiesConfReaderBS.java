package fr.cnamts.njc.domain.bs.module.conf.reader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import fr.cnamts.njc.domain.bo.module.conf.SectionLine;
import fr.cnamts.njc.domain.bo.module.conf.SectionLineFactory;
import fr.cnamts.njc.domain.bo.module.conf.SectionTitle;
import fr.cnamts.njc.domain.bs.module.conf.mapper.PropertiesConfMapperBS;
import fr.cnamts.njc.domain.inter.api.ContentMapper;
import fr.cnamts.njc.domain.inter.api.ContentReader;

public class PropertiesConfReaderBS implements ContentReader<ContentMapper, String> {
			
	private static final Logger LOGGER = Logger.getLogger(PropertiesConfReaderBS.class);		
	
	
	/**
	 * Transforme les données contenues dans le fichier properties.conf
	 * selon la définition des beans implémentant l'interface SectionLineContent.
	 * Puis retourne un objet permettant d'accéder facilement au résultat obtenu.
	 * @param ulConfContent Contenu du fichier properties.conf à traiter.
	 */	
	public ContentMapper read (String ulConfContent) {
		
		ContentMapper propertiesContent = new PropertiesConfMapperBS();
		
		for(Entry<SectionTitle, List<String>> entry : readToMap(ulConfContent).entrySet()){
			
			SectionTitle sectionName = entry.getKey();
			
			List<String> lines = entry.getValue();
			
			for(String line : lines){
				
				SectionLine lineOfSection = SectionLineFactory.INSTANCE.newLine(sectionName, line);
			
				propertiesContent.addLineToSection(lineOfSection, sectionName);
			}
			
		}		
		
		return propertiesContent;
	}	
	
	/**
	 * Lit les données contenu dans le fichier properties.conf
	 * sans les modifier. Il n'effectue qu'un tri pur et simple
	 * du contenu par section.
	 * @param ulConfContent
	 * @return
	 */
	public Map<SectionTitle, List<String>> readToMap (String ulConfContent){
		
		Map<SectionTitle, List<String>> parsedData = new EnumMap<SectionTitle, List<String>>(SectionTitle.class);
		
		List<String> lines = readAllLines(ulConfContent);
		
		List<String> sanitizeLines = deleteComments(fixEndsOfLine(lines));
		
		String content = toText(sanitizeLines);
		
		for(SectionTitle sectionTitle : SectionTitle.values()){
			
			String sectionContent = extractContentOfSection(content, sectionTitle);
					
			parsedData.put(sectionTitle, readAllLines(sectionContent));
			
		}
		
		return parsedData;
	}	
	
	private List<String> readAllLines(String ulConfContent){
		
		try {
			return IOUtils.readLines(new StringReader(ulConfContent));
		} catch (IOException e) {			
			LOGGER.error(e);
		}
		
		return Collections.emptyList();
		
	}
	
	
	
	private List<String> deleteComments(List<String> lines){
		
		List<String> tmpLines = new ArrayList<String>();
		
		for(String line : lines){
			
			if(!isComment(line)){
				tmpLines.add(line);
			}
			
		}
		
		return tmpLines;
		
	}
	
	private boolean isComment(String line){
		return line.trim().startsWith("#");
	}
	
	private List<String> fixEndsOfLine(List<String> lines){
		
		List<String> tmpLines = new ArrayList<String>();
		
		for(String line : lines){			
			tmpLines.add(fixCrLf(line));		
			
		}
		
		return tmpLines;
		
	}	
	
	private String fixCrLf(String s) {
        if (s == null) {
            return null;
        }

        // eliminate CR
        int idx;
        while ((idx = s.indexOf("\r\n")) != -1) {
            s = s.substring(0, idx) + s.substring(idx + 1);
        }
        return s;
    }
	
	
	
	private String toText(List<String> lines){
		
		StringBuffer buffer = new StringBuffer();
		
		for(String line : lines){
			
			buffer.append(line);
			buffer.append("\n");
			
		}
		
		return buffer.toString();
	}
	
	private String extractContentOfSection(String lines, SectionTitle section){
		
		int sectionNamePosition = lines.indexOf(section.getNom());
		
		if(isSectionNotFound(sectionNamePosition)){
			return "";
		}
		
		int startContentSectionPosition = sectionNamePosition+section.getNom().length();
		int nextSectionPosition = lines.indexOf("[", startContentSectionPosition);
		
		if(isLastSection(nextSectionPosition)){
			nextSectionPosition = lines.length();
		}
		
		return lines.substring(startContentSectionPosition, nextSectionPosition).trim();
				
	}
	
	private boolean isSectionNotFound(int sectionNamePosition){
		return sectionNamePosition<0;
	}
	
	private boolean isLastSection(int nextSectionPosition){
		return nextSectionPosition<0;
	}

}
