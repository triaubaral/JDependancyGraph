package fr.cnamts.njc.domain.bo.module.conf;

public enum TypeURL {

	CERISE_QUERY("Contenu d'un fichier issu d'une requête cerise."), 
	PROPERTIES_QUERY("Contenu d'un fichier properties.conf d'un projet particulier.");
	
	private String description;
	
	private TypeURL(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
