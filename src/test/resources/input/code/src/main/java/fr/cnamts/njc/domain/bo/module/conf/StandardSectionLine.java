package fr.cnamts.njc.domain.bo.module.conf;

public class StandardSectionLine implements SectionLine{
	
	private String key;
	private String value;	
	
	public StandardSectionLine(String line) {		
		this.key = extractKey(line);
		this.value = extractValue(line);
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String extractValue(String line){
		return line.substring(line.indexOf("=")+1).trim();
	}
	
	public String extractKey(String line){
		return line.substring(0, line.indexOf("=")).trim();
	}
	
	@Override
	public String toString() {
		return "StandardSectionLine [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		StandardSectionLine other = (StandardSectionLine) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
		
	
	
}