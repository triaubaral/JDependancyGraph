package com.triaubaral.dependancy.graph.reader;

public class ImportDeclaration extends AbstractDeclaration{

	public ImportDeclaration(String value) {
		super(value);
	}

	@Override
	public String getJavaKeyWord() {
		return "import";
	}
	
	

}
