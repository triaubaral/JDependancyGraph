package fr.cnamts.njc.domain.inter.api;

import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import fr.cnamts.njc.domain.bo.module.conf.SectionLine;
import fr.cnamts.njc.domain.bo.module.conf.SectionTitle;

public interface ContentMapper {
	
	List<? extends SectionLine> getContentOfSection(SectionTitle sectionTitle);
	void addLineToSection(SectionLine line, SectionTitle sectionTitle);
	void addLinesToSection(List<? extends SectionLine> sectionContent, SectionTitle sectionTitle);
	Set<Entry<SectionTitle, List<SectionLine>>> entrySet();

}
