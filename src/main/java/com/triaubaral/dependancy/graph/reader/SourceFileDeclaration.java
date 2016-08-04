package com.triaubaral.dependancy.graph.reader;

import java.util.Set;

public class SourceFileDeclaration {
	
	private PackageDeclaration sourcePackage;
	private Set<ImportDeclaration> sourceImports;
	private String name;
	
	public PackageDeclaration getSourcePackage() {
		return sourcePackage;
	}
	public void setSourcePackage(PackageDeclaration sourcePackage) {
		this.sourcePackage = sourcePackage;
	}
	public Set<ImportDeclaration> getSourceImports() {
		return sourceImports;
	}
	public void setSourceImports(Set<ImportDeclaration> sourceImports) {
		this.sourceImports = sourceImports;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "SourceFileDeclaration [sourcePackage=" + sourcePackage
				+ ", sourceImports=" + sourceImports + ", name=" + name + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((sourceImports == null) ? 0 : sourceImports.hashCode());
		result = prime * result
				+ ((sourcePackage == null) ? 0 : sourcePackage.hashCode());
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
		SourceFileDeclaration other = (SourceFileDeclaration) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceImports == null) {
			if (other.sourceImports != null)
				return false;
		} else if (!sourceImports.equals(other.sourceImports))
			return false;
		if (sourcePackage == null) {
			if (other.sourcePackage != null)
				return false;
		} else if (!sourcePackage.equals(other.sourcePackage))
			return false;
		return true;
	}

}
