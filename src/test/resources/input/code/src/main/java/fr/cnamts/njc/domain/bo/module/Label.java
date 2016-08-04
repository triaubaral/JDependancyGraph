package fr.cnamts.njc.domain.bo.module;

import com.google.common.collect.ArrayListMultimap;

public class Label implements Comparable<Label> {

	private static LabelValidator validator = new LabelValidator();
	
	private LabelBuilder builder;
	    
    public Label(LabelBuilder builder){
    	this.builder = builder;
    }
    
    public LabelBuilder getBuilder() {
		return builder;
	}
   

    @Override
    public int compareTo(final Label pO) {
             return this.builder.getNomcomplet().compareTo(pO.getBuilder().getNomcomplet());
    }

    public String getCodeapp() {
        return this.builder.getCodeapp();
    }

    public String getExtension() {
    	return this.builder.getExtension();
    }

    public String getModule() {      
        return this.builder.getModule();
    }

    public boolean isNomlong() {
        return this.builder.isNomLong();
    }

    public boolean isValid() {
        return validator.isValid(this.builder.getNomcourt());
    }


    public void setExtension(final String pExtension) {
    	this.builder.extension(pExtension);
    } 


    public ArrayListMultimap<String, String> getAsPropertiesList(){
    	  final ArrayListMultimap<String, String> hashVerProps = ArrayListMultimap.create();
    	  hashVerProps.put("nomlong",  this.builder.getNomcomplet()) ; // "Nom complet : " + this.getNomcomplet() + "\n" +
    	  hashVerProps.put("nomcourt", this.builder.getNomcourt());
    	  hashVerProps.put("codapp",this.getCodeapp());
    	  hashVerProps.put("module", this.getModule());// + "\n" +
    	  hashVerProps.put("vermaj", this.builder.getVermaj());// + "\n" +
    	  hashVerProps.put("vermin", this.builder.getVermin());
    	  hashVerProps.put("vertech",this.builder.getVertech()); // + "\n" +
    	  hashVerProps.put("type",this.builder.getType());// + "\n" +
    	  hashVerProps.put("extension",this.builder.getExtension());
    	  return hashVerProps;
    }


	public static Label createEmpty() {		 
		return new LabelBuilder().build();
	}

	@Override
	public String toString() {
		return "Label [builder=" + builder + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((builder == null) ? 0 : builder.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Label)) {
			return false;
		}
		Label other = (Label) obj;
		if (builder == null) {
			if (other.builder != null) {
				return false;
			}
		} else if (!builder.equals(other.builder)) {
			return false;
		}
		return true;
	}
	
	
	
}
