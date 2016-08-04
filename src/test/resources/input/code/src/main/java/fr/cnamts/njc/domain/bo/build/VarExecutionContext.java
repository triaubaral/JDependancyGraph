package fr.cnamts.njc.domain.bo.build;

public enum VarExecutionContext {
	
	ARTI_COMMUN_DIR,
	
	LIVRAISON_DIR,
	
	COMMUN_DIR,
	
	PUBLICATION_DIR,
	
	SNAPSHOT_DIR,
	
	RELEASE_DIR,
	
	LABEL,
	
	SOCLE_VERSION,
	
	MULTIPUB,
	
	NOMPUB,
	
	VERPUB,
	
	OSREFAB,
	
	VERMIN,
	
	VERTECH,
	
	USER_UL,
	
	/**
	 * OS/PLC de refabrication courant (RCI)
	 */
	CNQD_OSREFAB,
	

	/**
	 * Répertoire temporaire des dépendances annoncées dans le fichier properties.conf
	 */
	CNQD_COMMON_REP,
	
	/**
	 * Répertoire publication (en sortie)
	 */
	CNQD_PUBLICATION,
	
	/**
	 * Répertoire liv (en sortie)
	 */
	CNQD_CVS_LIVREP,
	
	/**
	 * Référentiel CVS
	 */
	CNQD_CVS_REFERENTIEL,
	
	/**
	 * Module CVS
	 */
	CNQD_CVS_MODULE,
	
	/**
	 * Label CVS (avec suffixe Dir.Op éventuel)
	 */
	CNQD_CVS_LABEL_LONG,
	
	/**
	 * Label CVS, réduit au label Officiel si le label d'origine est suffixé par la Dir. Op
	 */
	CNQD_CVS_LABEL,
	
	
	/**
	 * Répertoire du module (répertoire de travail)
	 */
	CNQD_CVS_MODREP,	
	
	
	/**
	 * Variable non référencée dans build.sh
	 * TODO:Demander à CF s'il s'agit du bon nom
	 */
	CNQD_CVS_CODENATURE;	

}
