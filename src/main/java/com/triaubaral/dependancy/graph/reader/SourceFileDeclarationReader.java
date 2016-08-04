package com.triaubaral.dependancy.graph.reader;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;


public class SourceFileDeclarationReader implements Reader<SourceFileDeclaration, String> {

	@Override
	public SourceFileDeclaration read(String content) {
		
		SourceFileDeclaration data = new SourceFileDeclaration();
		
		List<String> lines = readLineQuietly(new StringReader(content));
		
		Set<ImportDeclaration> imports = new HashSet<ImportDeclaration>();
		
		boolean nameFound = false;
		
		for(String line : lines){
			
			if(		!nameFound && ( 
									line.contains("class") || 
									line.contains("enum") ||
									line.contains("interface"))
					){
				if(line.contains("implements")){
					
					line = line.substring(0,line.indexOf("implements"));
					
				}
				
				if(line.contains("extends")){
					
					line = line.substring(0,line.indexOf("extends"));
					
				}
				
				if(line.contains("abstract")){
					
					line = line.replace("abstract", "");
					
				}
				
				if(line.contains("public")){
					
					line = line.replace("public", "");
					
				}
				
				if(line.contains("class")){
					
					line = line.replace("class", "");
					
				}
				
				if(line.contains("enum")){
					
					line = line.replace("enum", "");
					
				}
				
				if(line.contains("interface")){
					
					line = line.replace("interface", "");
					
				}
				
				if(line.contains("{")){
					
					line = line.replace("{", "");
					
				}
				
				data.setName(line.trim());
				
				nameFound = true;
			}			
			
			if(line.startsWith("package")){
				data.setSourcePackage(new PackageDeclaration(line.replace("package", "").replace(";", "").trim()));
			}
			
			if(line.startsWith("import")){
				imports.add(new ImportDeclaration(line.replace("import", "").replace(";", "").trim()));
			}
			
		}
		
		data.setSourceImports(imports);
		
		return data;
	}
	
	private List<String> readLineQuietly(StringReader content){
		
		try {
			return IOUtils.readLines(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Collections.emptyList();
		
	}

}
