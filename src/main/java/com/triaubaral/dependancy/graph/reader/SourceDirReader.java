package com.triaubaral.dependancy.graph.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.triaubaral.dependancy.graph.util.FilesWalker;

public class SourceDirReader implements Reader<List<SourceFileDeclaration>, String> {

	public List<SourceFileDeclaration> read(String pathToDir) {
		
		List<Path> sourceFiles = FilesWalker.INSTANCE.walkThroughDirPath(pathToDir);
		
		List<SourceFileDeclaration> declarations = new ArrayList<SourceFileDeclaration>();
		
		SourceFileDeclarationReader reader = new SourceFileDeclarationReader();
		
		for(Path sourceFile : sourceFiles){
			
			if(sourceFile.toFile().getName().endsWith(".java")){
				String content = readQuietly(sourceFile);			
				declarations.add(reader.read(content));
			}
			
		}			
	
		return declarations;
	}
	
	public String readQuietly(Path sourceFile){
		try {
			return FileUtils.readFileToString(sourceFile.toFile(), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
}
