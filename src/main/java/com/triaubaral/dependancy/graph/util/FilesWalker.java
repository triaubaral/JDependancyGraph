package com.triaubaral.dependancy.graph.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public enum FilesWalker {
	
	INSTANCE;
	
	public List<Path> walkThroughDirPath(String pathDir){
		
		final List<Path> filesVisited = new ArrayList<Path>();
		
		Path p = Paths.get(pathDir);
	    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	            throws IOException {
	        	filesVisited.add(file);
	          return FileVisitResult.CONTINUE;
	        }
	      };

	      try {
	        Files.walkFileTree(p, fv);
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	      
	      return filesVisited;
	}

}
