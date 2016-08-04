package com.triaubaral.dependancy.graph.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SourceFileDeclarationMocker {
	
	private static final String PACKAGE_TPL = "package ${path};";
	private static final String IMPORT_TPL = "import ${path};";
	
	public SourceFileDeclaration newSourceFileDeclaration(String className,
			String packageName, List<String> importsDeclaration) {

		SourceFileDeclaration sourceFileDeclartion = new SourceFileDeclaration();

		Set<ImportDeclaration> imports = new HashSet<ImportDeclaration>();

		for (String importName : importsDeclaration)
			imports.add(new ImportDeclaration(importName));

		sourceFileDeclartion.setName(className);

		sourceFileDeclartion.setSourceImports(imports);

		sourceFileDeclartion.setSourcePackage(new PackageDeclaration(packageName));

		return sourceFileDeclartion;

	}
	
	public String newPackageTpl(String path){
		
		return PACKAGE_TPL.replace("${path}", path);
	}
	
	public String newImportTpl(String path){
		
		return IMPORT_TPL.replace("${path}", path);
	}

	public List<String> newImport(String line) {

		List<String> imports = new ArrayList<String>();

		imports.add("hudson.EnvVars");
		imports.add("hudson.model.ParameterValue");
		imports.add("java.io.FileNotFoundException");
		imports.add(line);

		return imports;
	}

}
