package fr.cnamts.njc.domain.bo.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jpatterns.gof.BuilderPattern;

@BuilderPattern(participants={Label.class})
public class LabelBuilder {
	
    private String codeapp = "";
    private String extension = "";
    private String module;
    private String nomcomplet = "";
    private String nomcourt;
    private String type = "";
    private String url;
    private String vermaj;
    private String vermin;
    private String vertech;
    private boolean nomLong;
    
    public LabelBuilder() {
		
	}
    
    public LabelBuilder(String nomComplet) {
    	
    	final Matcher match = Pattern.compile(LabelValidator.REGEX_LABEL).matcher(nomComplet);
        if (match.find()) {
            
					nomcourt=match.group(1);
					codeapp=match.group(2);
					vermaj=match.group(3);
					vermin=match.group(4);
					vertech=match.group(5);
					type=match.group(6);
					nomLong=null!=match.group(8);
					module = this.codeapp + "_" + this.type;
            
            if (nomLong) {

                extension=match.group(9);
            }            

        } else { 
				nomcourt=nomComplet;					
        } 
        
        this.nomcomplet=nomComplet;
	}
    
    public Label build(){  
    	return new Label(this);
    }
    
    public LabelBuilder nomlong(boolean nomLong){
    	this.nomLong = nomLong;
    	return this;
    }
    
    public LabelBuilder vertech(String vertech){
    	this.vertech = vertech;
    	return this;
    }
    
    public LabelBuilder vermin(String vermin){
    	this.vermin = vermin;
    	return this;
    }
    
    
    public LabelBuilder vermaj(String vermaj){
    	this.vermaj = vermaj;
    	return this;
    }
    
    public LabelBuilder url(String url){
    	this.url = url;
    	return this;
    }
    
    public LabelBuilder type(String type){
    	this.type = type;
    	return this;
    }
    
    public LabelBuilder nomCourt(String nomCourt){
    	nomcourt = nomCourt;
    	return this;
    }
    
    public LabelBuilder nomComplet(String nomComplet){
    	nomcomplet = nomComplet;
    	return this;
    }
    
    public LabelBuilder module(String module){
    	this.module = module;
    	return this;
    }
    
    public LabelBuilder codeApp(String codeApp){
    	codeapp = codeApp;
    	return this;
    }
    
    public LabelBuilder extension(String extension){    	
    	this.extension = extension;
    	return this;
    }   

	public String getCodeapp() {
		return codeapp;
	}

	public String getExtension() {
		return extension;
	}

	public String getModule() {
		return module;
	}

	public String getNomcomplet() {
		return nomcomplet;
	}

	public String getNomcourt() {
		return nomcourt;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public String getVermaj() {
		return vermaj;
	}

	public String getVermin() {
		return vermin;
	}

	public String getVertech() {
		return vertech;
	}
	
	public boolean isNomLong() {
		return nomLong;
	}

	@Override
	public String toString() {
		return "LabelBuilder [codeapp=" + codeapp + ", extension=" + extension
				+ ", module=" + module + ", nomcomplet=" + nomcomplet
				+ ", nomcourt=" + nomcourt + ", type=" + type + ", url=" + url
				+ ", vermaj=" + vermaj + ", vermin=" + vermin + ", vertech="
				+ vertech + ", nomLong=" + nomLong + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeapp == null) ? 0 : codeapp.hashCode());
		result = prime * result
				+ ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + (nomLong ? 1231 : 1237);
		result = prime * result
				+ ((nomcomplet == null) ? 0 : nomcomplet.hashCode());
		result = prime * result
				+ ((nomcourt == null) ? 0 : nomcourt.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((vermaj == null) ? 0 : vermaj.hashCode());
		result = prime * result + ((vermin == null) ? 0 : vermin.hashCode());
		result = prime * result + ((vertech == null) ? 0 : vertech.hashCode());
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
		LabelBuilder other = (LabelBuilder) obj;
		if (codeapp == null) {
			if (other.codeapp != null)
				return false;
		} else if (!codeapp.equals(other.codeapp))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		if (nomLong != other.nomLong)
			return false;
		if (nomcomplet == null) {
			if (other.nomcomplet != null)
				return false;
		} else if (!nomcomplet.equals(other.nomcomplet))
			return false;
		if (nomcourt == null) {
			if (other.nomcourt != null)
				return false;
		} else if (!nomcourt.equals(other.nomcourt))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (vermaj == null) {
			if (other.vermaj != null)
				return false;
		} else if (!vermaj.equals(other.vermaj))
			return false;
		if (vermin == null) {
			if (other.vermin != null)
				return false;
		} else if (!vermin.equals(other.vermin))
			return false;
		if (vertech == null) {
			if (other.vertech != null)
				return false;
		} else if (!vertech.equals(other.vertech))
			return false;
		return true;
	}
	
	
}
