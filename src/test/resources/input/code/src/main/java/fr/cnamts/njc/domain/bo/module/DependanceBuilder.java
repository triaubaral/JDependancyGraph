package fr.cnamts.njc.domain.bo.module;


import org.jpatterns.gof.BuilderPattern;

import fr.cnamts.njc.domain.exception.DependancesException;

@BuilderPattern(participants={Dependance.class})
public class DependanceBuilder {
	
	private static LabelValidator validator = new LabelValidator();

    private Label label;

    private String nompub;

    private String pathToRepository;

    private String verpub;
    
    private boolean commercial;
    
    private boolean validLabel;
    
    public DependanceBuilder() {
	}
    
    public DependanceBuilder(String pLabelStr) throws DependancesException  {    	
        
        label = new LabelBuilder(pLabelStr).build();
        
        if (pLabelStr.contains("/")) {

            final String[] strDir = pLabelStr.split("/");

            if (validator.isValidNomPub(strDir[0])) {
                commercial = true;
                nompub=strDir[0];
                getLabel().getBuilder().nomCourt(strDir[0]);
                getLabel().getBuilder().module(strDir[0]);
                String[] modtype=strDir[0].split("_");
                if(modtype.length>0){
                    getLabel().getBuilder().codeApp(modtype[0]);
                    getLabel().getBuilder().type(modtype[1]);
                }
                verpub=strDir[1];
                final String[] mintech = strDir[1].split("\\.");
                if(mintech.length >0){
                    getLabel().getBuilder().vermin(mintech[0]);
                       if(mintech.length==2){
                          getLabel().getBuilder().vertech(mintech[1]);
                       }
                }
            } else {
                throw new DependancesException("La dépendance " + pLabelStr
                        + " est incorrecte (NOMPUB mal formé) !");
            }

        }else{
      
            if (!getLabel().isValid()) {
            	validLabel=false;
                throw new DependancesException("Le label de dépendance " + pLabelStr + " est incorrect !");  
            } else{
            	validLabel=true;
            }
         
        } 
	}
    
    public Dependance build() {
    	
		return new Dependance(this);
	}	
	
	public DependanceBuilder label(Label label){
		this.label = label;
		return this;
	}
	
	public DependanceBuilder commercial(boolean commercial){
		this.commercial = commercial;
		return this;
	}
	
	public DependanceBuilder nomPub(String nomPub){
		this.nompub = nomPub;
		return this;
	}
	
	public DependanceBuilder pathToRepository(String pathToRepository){
		this.pathToRepository = pathToRepository;
		return this;
	}
	
	public DependanceBuilder verpub(String verpub){
		this.verpub = verpub;
		return this;
	}
	
	public Label getLabel() {
		return label;
	}

	public String getNompub() {
		return nompub;
	}

	public String getPathToRepository() {
		return pathToRepository;
	}

	public String getVerpub() {
		return verpub;
	}
	
	public boolean isCommercial() {
		return commercial;
	}
	
	public boolean isValidLabel() {
		return validLabel;
	}

	@Override
	public String toString() {
		return "DependanceBuilder [label="
				+ label + ", nompub=" + nompub + ", url=" + pathToRepository + ", verpub="
				+ verpub + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (commercial ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((nompub == null) ? 0 : nompub.hashCode());
		result = prime * result + ((pathToRepository == null) ? 0 : pathToRepository.hashCode());
		result = prime * result + (validLabel ? 1231 : 1237);
		result = prime * result + ((verpub == null) ? 0 : verpub.hashCode());
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
		if (!(obj instanceof DependanceBuilder)) {
			return false;
		}
		DependanceBuilder other = (DependanceBuilder) obj;
		if (commercial != other.commercial) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (nompub == null) {
			if (other.nompub != null) {
				return false;
			}
		} else if (!nompub.equals(other.nompub)) {
			return false;
		}
		if (pathToRepository == null) {
			if (other.pathToRepository != null) {
				return false;
			}
		} else if (!pathToRepository.equals(other.pathToRepository)) {
			return false;
		}
		if (validLabel != other.validLabel) {
			return false;
		}
		if (verpub == null) {
			if (other.verpub != null) {
				return false;
			}
		} else if (!verpub.equals(other.verpub)) {
			return false;
		}
		return true;
	}

	
	
	

}
