package com.triaubaral.dependancy.graph.finder;

import com.triaubaral.dependancy.graph.reader.AbstractDeclaration;
import com.triaubaral.dependancy.graph.reader.PackageDeclaration;

public enum DependanceMapperFactory {
	INSTANCE;

	public DependanceMapper newDependanceMapperFrom(
			AbstractDeclaration declarationSource, AbstractDeclaration declarationCible) {

		if(declarationSource instanceof PackageDeclaration && declarationCible instanceof PackageDeclaration){
			
			return new PackageDependanceMapper((PackageDeclaration)declarationSource, (PackageDeclaration)declarationCible);
		}

		return null;
	}
}
