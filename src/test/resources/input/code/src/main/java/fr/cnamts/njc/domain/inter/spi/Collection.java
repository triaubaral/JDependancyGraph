package fr.cnamts.njc.domain.inter.spi;

import java.util.Calendar;
import java.util.Map;

public interface Collection<BuildableItem> {
	
	int getNumber();
	Calendar getTimestamp();
	Map getBuildVariables();
	BuildableItem getProject();
}
