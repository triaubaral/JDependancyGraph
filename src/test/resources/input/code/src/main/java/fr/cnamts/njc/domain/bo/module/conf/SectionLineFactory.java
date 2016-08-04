package fr.cnamts.njc.domain.bo.module.conf;


public enum SectionLineFactory {
	
	INSTANCE;
	
	private RefabSectionLine refabLinesTmp;
	
	public SectionLine newLine(SectionTitle title, String line){
		
		if(title.equals(SectionTitle.DEPENDANCES_FAB)){
			return new DependSectionLine(line);
		}
		
		if(title.equals(SectionTitle.REFAB)){

			if(refabLinesTmp == null){
				
				refabLinesTmp = new RefabSectionLine(line);
				
			}
			else{
					
				refabLinesTmp.setValue(refabLinesTmp.getValue()+","+new RefabSectionLine(line).getValue());
				
			}
			
			return refabLinesTmp;	
			
		}
		else{
			refabLinesTmp = null;
		}
		
		return new StandardSectionLine(line);
	}

}
