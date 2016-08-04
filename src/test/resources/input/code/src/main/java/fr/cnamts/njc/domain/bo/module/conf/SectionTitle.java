package fr.cnamts.njc.domain.bo.module.conf;


/**
 * Le descriptif détaillé de chaque section est disponible dans le 
 * document : http://cvs.cnqd.cnamts.fr/cgi-bin/viewvc.cgi/SOUTIEN_TECHNIQUE/USINE_LOGICIELLE/UL%20-%20Manuel%20de%20reference%20du%20fichier%20properties.conf.doc?profil=standard&view=log
 * @author DPFT/DAIT/DDST
 *
 */
public enum SectionTitle {

	/**
	 * Cette section décrit les machines qui doivent refabriquer le livrable. Elle est constituée d’une ou plusieurs lignes contenant une déclaration du type :

		OSREFAB=<osplc>
		ou
		OSREFAB=st<version>-<os>
	 */
	REFAB("[REFAB]"), 
	/**
	 * Cette section décrit les cibles possibles sur lesquelles pourra être diffusé le livrable avec GEDIFF (remplacement d’Admisys).

		Elle est constituée d’une ou plusieurs lignes contenant une déclaration du type :
		
		TYPE_DESTINATAIRE=<destinataire>
		
		La liste des destinataires possibles est une liste ouverte et n'est soumise à aucun contrôle à ce jour.
		Les destinataires les plus courants sont :
		cti
		ctidata
		cen
		cpam
		doc
	 */
	DIFFUSION("[DIFFUSION]"),
	
	/**
	 * Cette section décrit la procédure à suivre pour l’installation du livrable.
	 *  	Elle est constituée d’une ligne (obligatoire) contenant une déclaration du type :
	 *		KS=<oui|non>
	 * 
	 */
	INSTALLATION("[INSTALLATION]"),	

	/**
	 * 	La section [DEPENDANCES_FAB] est constituée d'une ligne par dépendance requise.
		Plusieurs dépendances peuvent être déclarées: dans ce cas, la section contiendra plusieurs lignes.
		Les lignes qui composent cette section peuvent être de la forme suivante:
		
		<AA><v6><T>;[<base_variable>][;<path_variable>]
		ou
		<AA>_<T>[_NOMPUB]/<xxx>.<yyy>;[<base_variable>][;<path_variable>]
	 */
	DEPENDANCES_FAB("[DEPENDANCES_FAB]"),
	
	/**
	 * La publication permet de mettre à disposition dans les espaces communs les
	 * fichiers déposés dans le répertoire $CNQD_PUBLICATION par le label refabriqué.
	 */
	PUBLICATION("[PUBLICATION]"),
	
	/**
	 * Attention : Section disponible uniquement sur PLC
	 * Cette section permet de positionner des variables qui seront utilisées par le programme build.sh lancé par le processus de refabrication. Le contenu de cette section est interprété avant le lancement du programme build.sh. D’une manière générale, cette section permet de positionner des variables standard :
		JAVA_HOME
		ANT_HOME
		ACU_BASE_EH
	 */
	ENV_BUILD("[ENV_BUILD]"),
	
	//Les deux enum (DIVERS et DEPENDANCES_INST) sont inconnus du manuel est ce normal cf CF ?	
	DIVERS("[DIVERS]"),	
	DEPENDANCES_INST("[DEPENDANCES_INST]");
	
	private String nom;
	
	private SectionTitle(String nomSection) {
		nom = nomSection;
	}
	
	public String getNom() {
		return nom;
	}
	
	
}
