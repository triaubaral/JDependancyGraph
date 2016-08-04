package fr.cnamts.njc.infra.jenkins.plugin.searcher;


import hudson.EnvVars;
import hudson.Util;
import hudson.util.VariableResolver;

public class VarExecutionContextSearcher {
	
	private EnvVars envVars;
	private VariableResolver<String> buildProjectVars;
	
	public VarExecutionContextSearcher(final EnvVars envVars,
			final VariableResolver<String> buildProjectVars) {
		
		this.envVars = envVars;
		this.buildProjectVars = buildProjectVars;
	}
	
	/**
	 * Détermine si la clef passée en paramètre est une macro.
	 * @param pKey
	 * @return true si la clef est une macro.
	 */
	private  boolean isMacro(String pKey){
		return pKey.startsWith("$") || pKey.startsWith("{");
	}
	
	public  String findValueInEnvScope(String pKey){
		return Util.replaceMacro(pKey, envVars);
	}
	
	/**
	 * Evalue la nullité de pValue et renvoie pKey si et 
	 * seulement si pValue est null. Renvoie pValue dans
	 * le cas contraire
	 * @param pValue
	 * @param pKey
	 * @return pValue or pKey if pValue is null.
	 */
	private  String getKeyIfValueIsNull(String pValue, String pKey){
		
		if (pValue == null)	{		
			return pKey;
		}

		return pValue;
		
	}
	
	public  String findValueInBuildScope(final String pKey){
		
		String valueOfkey = envVars.get(pKey, null);
		
		if (valueOfkey == null) {
			
			valueOfkey = getKeyIfValueIsNull(buildProjectVars.resolve(pKey), pKey);
			
		}
		
		return valueOfkey;
	
	}

	/**
	 * Cette méthode permet de retrouver la valeur associée à une clef
	 * stockée dans le scope projet ou système sous forme 'NOM_VAR'.
	 * Ainsi, pour retrouver la valeur associée à 'NOM_VAR on pourra utiliser 
	 * la clef avec le formalisme suivant : NOM_VAR, $NOM_VAR ou ${NOM_VAR}.
	 * 
	 * Si aucune valeur n'a été retrouvée avec les formalismes précédents
	 * alors la valeur renvoyée sera identique à la clef.
	 * 
	 * @param projectVars
	 * @param pVariableResolver
	 * @param pKey
	 * @return La valeur associée à la clef ou la clef si la valeur est inexistante.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public  String getValueForKeyInEnvAndBuildScope(final String pKey){		

		if (isMacro(pKey)) {			
			return findValueInEnvScope(pKey);
		} 

		return findValueInBuildScope(pKey);		

	}

}
