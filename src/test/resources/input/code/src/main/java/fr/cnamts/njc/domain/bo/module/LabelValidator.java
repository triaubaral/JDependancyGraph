package fr.cnamts.njc.domain.bo.module;

import java.util.regex.Pattern;

public class LabelValidator {
	
	 public static String REGEX_LABEL = "^(([A-Z][A-Z0-9][A-Z0-9]{0,2})([0-9]{2})([0-9]{2})([0-9]{2})([A-Z][A-Z0-9]{0,1}))($||(_)([A-Z].*))$";
	 //Valider avec CF si on peut utiliser la regexp ci-dessous afin de pouvoir traiter le cas EN_A_[CAPI]
	 //public static String REGEX_NOMPUB = "^([A-Z][A-Z0-9][A-Z0-9]{0,2})(_{0,1})([A-Z][A-Z0-9]{0,1})($||(_)([A-Z].*))$";
	 public static String REGEX_NOMPUB = "^([A-Z][A-Z0-9][A-Z0-9]{0,2})(_{0,1})([A-Z][A-Z0-9]{0,1})$";
	
	 public boolean isValid(final String pLabelAvalider) {
	
	        return isValidLabel(pLabelAvalider) || isValidNomPub(pLabelAvalider);
	 }
	
	 public boolean isValidLabel(final String pLabelAvalider) {
	
	    return Pattern.matches(REGEX_LABEL, pLabelAvalider);
	 }
	
	
	 public  boolean isValidNomPub(final String pLabelAvalider) {
	
	    return Pattern.matches(REGEX_NOMPUB, pLabelAvalider);
	 }
	 
}
