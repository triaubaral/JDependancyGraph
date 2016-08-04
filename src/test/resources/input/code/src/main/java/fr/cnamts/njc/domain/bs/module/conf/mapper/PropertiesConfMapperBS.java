package fr.cnamts.njc.domain.bs.module.conf.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.cnamts.njc.domain.bo.module.conf.SectionLine;
import fr.cnamts.njc.domain.bo.module.conf.SectionTitle;
import fr.cnamts.njc.domain.inter.api.ContentMapper;

public class PropertiesConfMapperBS implements ContentMapper {
	
	private Map<SectionTitle, List<SectionLine>> content;
	
	public PropertiesConfMapperBS() {
		content = new EnumMap<SectionTitle, List<SectionLine>>(SectionTitle.class);
	}
	
	public List<? extends SectionLine> getContentOfSection(SectionTitle sectionTitle){
		
		if(content.get(sectionTitle) == null){
			return Collections.emptyList();
		}
		
		return content.get(sectionTitle);
	}
	
	public void addLineToSection(SectionLine line, SectionTitle sectionTitle){
		
		if(content.get(sectionTitle) == null){
			content.put(sectionTitle, new ArrayList<SectionLine>());
		}
		
		content.get(sectionTitle).add(line);
		
	}
	
	public void addLinesToSection(List<? extends SectionLine> sectionContent, SectionTitle sectionTitle) {
		content.put(sectionTitle, (List<SectionLine>) sectionContent);
	}
	
	public Set<Entry<SectionTitle, List<SectionLine>>> entrySet(){
		return content.entrySet();
	}

	@Override
	public String toString() {
		return "PropertiesConfContent [content=" + content + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PropertiesConfMapperBS other = (PropertiesConfMapperBS) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}
	
	

}
