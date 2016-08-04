package fr.cnamts.njc.domain.bo.module.conf;

public class DependSectionLine implements SectionLine {	
	
	private String key;	
	private String value;
	
	public DependSectionLine(String line) {
		
		this.key = line.trim().substring(line.indexOf(";")+1);		
		this.value = line.trim().substring(0,line.indexOf(";"));
	}

	@Override
	public String getKey() {		
		return key;
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
		return "DependSectionLine [value=" + value + "]";
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
		DependSectionLine other = (DependSectionLine) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

}
