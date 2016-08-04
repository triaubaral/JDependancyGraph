package fr.cnamts.njc.domain.bo.module.conf;


public class RefabSectionLine implements SectionLine {	
	
	private String value;
	
	public RefabSectionLine(String line) {		
		this.value = line.replace("OSREFAB", "").replace("=", "").trim();		
	}

	@Override
	public String getKey() {
		
		return "OSREFAB";
	}

	@Override
	public String getValue() {
		
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "RefabSectionLine [value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		RefabSectionLine other = (RefabSectionLine) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
	
	

}
