package com.triaubaral.dependancy.graph.finder;

import java.util.ArrayList;
import java.util.List;

import com.triaubaral.dependancy.graph.link.Relation;
import com.triaubaral.dependancy.graph.link.Sens;
import com.triaubaral.dependancy.graph.link.Type;
import com.triaubaral.dependancy.graph.reader.ImportDeclaration;
import com.triaubaral.dependancy.graph.reader.SourceFileDeclaration;

public class DependancyChecker {

	public boolean relationExists(SourceFileDeclaration sourceFileDeclarationSource,
			SourceFileDeclaration sourceFileDeclarationDestination) {
		
		String packageSource = sourceFileDeclarationSource.getSourcePackage().getValue();
		
		for(ImportDeclaration depImport : sourceFileDeclarationDestination.getSourceImports()){
						
			if(depImport.getValue().contains(packageSource))
				return true;			
		}
		
		return false;
	}

	public List<Relation> findRelations(
			SourceFileDeclaration sourceFileDeclaration,
			SourceFileDeclaration sourceFileDeclaration2) {
		
		List<Relation> relations = new ArrayList<Relation>();
		
		relations.add(new Relation(Sens.PARENT, Type.SIMPLE, sourceFileDeclaration.getSourcePackage(), sourceFileDeclaration2.getSourcePackage()));
//		relations.add(new Relation(Sens.ENFANT, Type.SIMPLE, sourceFileDeclaration2.getSourcePackage(), sourceFileDeclaration.getSourcePackage()));

	
		return relations;
	}

}
