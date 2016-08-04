package fr.cnamts.njc.domain.bo.module;


public class Dependance implements Comparable<Dependance> {    

    private DependanceBuilder builder;

    private transient String localPath;
    
    public String getLocalPath() {
        return localPath;
    }
    
    public boolean isValidLabel() {   	 
		return builder.isValidLabel();
	}

    public void setLocalPath(String pLocalPath) {
        localPath = pLocalPath;
    }
    
    public Dependance(DependanceBuilder builder){
    	this.builder = builder;    	
    }
    
    public Label getLabel() {
		return builder.getLabel();
	}
    
    public String getUrl(){
    	return builder.getPathToRepository();   	
    	
    }
    
    public String getVerpub(){
    	return builder.getVerpub();
    }
    
    
    public void setLabel(Label label) {
		builder.label(label);
	}
    
    public void setNomPub(String nomPub){
    	builder.nomPub(nomPub);
    }
    
    public void setPathToRepository(String pathToRepository){
    	builder.pathToRepository(pathToRepository);	
    	
    }
    
    public void setVerpub(String verpub){
    	builder.verpub(verpub);
    }
    

    @Override
    public int compareTo(final Dependance pParamT) {
        // la comparaison va se faire sur le label
        return this.getLabel().compareTo(pParamT.getLabel());
    }

    public String getNomcomplet() {

        return this.getLabel().getBuilder().getNomcomplet();
    }

    public String getNompub() {
        return this.builder.getNompub();
    }
    
    public String getVermin(){
    	return this.builder.getLabel().getBuilder().getVermin();
    }

    public boolean isCommercial() {
        return this.builder.isCommercial();
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dependance [builder=" + builder + "]";
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
		if (!(obj instanceof Dependance)) {
			return false;
		}
		Dependance other = (Dependance) obj;
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