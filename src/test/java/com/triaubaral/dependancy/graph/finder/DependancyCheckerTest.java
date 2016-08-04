package com.triaubaral.dependancy.graph.finder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.triaubaral.dependancy.graph.draw.DrawEngine;
import com.triaubaral.dependancy.graph.link.Relation;
import com.triaubaral.dependancy.graph.link.Sens;
import com.triaubaral.dependancy.graph.link.Type;
import com.triaubaral.dependancy.graph.reader.SourceDirReader;
import com.triaubaral.dependancy.graph.reader.SourceFileDeclaration;
import com.triaubaral.dependancy.graph.reader.SourceFileDeclarationMocker;
import com.triaubaral.dependancy.graph.reader.SourceFileDeclarationReader;
import com.triaubaral.dependancy.graph.reader.SourceReaderTest;

public class DependancyCheckerTest {
	
	private SourceFileDeclarationMocker mocker = new SourceFileDeclarationMocker();
	private DependancyChecker checker = new DependancyChecker();

	@Test
	public void shouldFindDependanceBetweenTwoSourceFilesDeclaration() {
		
		boolean expectedResult = true;	
		
		boolean result = checker.relationExists(newDeclarationList().get(0), newDeclarationList().get(1));	
		
		Assertions.assertThat(result).isEqualTo(expectedResult);
		
	}
	
	@Test
	public void shouldCheckForRelations(){
		
		List<Relation> expectedResult = newRelations();		
		List<Relation> result = checker.findRelations(newDeclarationList().get(0), newDeclarationList().get(1));
				
		Assertions.assertThat(result).containsAll(expectedResult);
	}
	
	private List<Relation> newRelations(){
		
		List<Relation> expectedResult = new ArrayList<Relation>();
		
		expectedResult.add(new Relation(Sens.PARENT, Type.SIMPLE, newDeclarationList().get(0).getSourcePackage(), newDeclarationList().get(1).getSourcePackage()));
		
		return expectedResult;
	}
	
	@Test
	public void shouldCreateGraphviz() throws IOException, InterruptedException{
		
		DrawEngine drawer = new DrawEngine();
		
		drawer.drawRelations(newRelations());
		
		Assertions.assertThat(true);
		
	}
	
	
	private List<SourceFileDeclaration> newDeclarationList(){

		 List<SourceFileDeclaration> list = new ArrayList<SourceFileDeclaration>();

		SourceFileDeclaration f1 = mocker.newSourceFileDeclaration(
				"MyClass1", 
				"fr.cnamts.toto",
				Arrays.asList(new String[]{"java.util.*", "java.map.tu"}));
		
		SourceFileDeclaration f2 = mocker.newSourceFileDeclaration(
				"MyClass2", 
				"fr.cnamts.titi", 
				Arrays.asList(new String[]{"java.util", "fr.cnamts.toto.MaClass"}));
		
		list.add(f1);
		list.add(f2);
		
		return list;
	}
	
	@Test
	public void shouldCreateBigGraph() throws IOException, InterruptedException{
		
		DrawEngine drawer = new DrawEngine();
		SourceDirReader sourceDirReader = new SourceDirReader();
		List<Relation> relations = new ArrayList<Relation>();		
		
		List<SourceFileDeclaration> result = sourceDirReader.read("src/test/resources/input/code/src");
		
		for(SourceFileDeclaration sourceFile : result){
			
			if(sourceFile.getSourcePackage().getValue().contains("fr.cnamts.njc.domain.bs")){
			
				for(SourceFileDeclaration destinationFile : result){
					
					if(checker.relationExists(sourceFile, destinationFile)){
						
						relations.add(new Relation(Sens.PARENT, Type.SIMPLE, sourceFile.getSourcePackage(), destinationFile.getSourcePackage()));
						
					}
					
				}
			}
		}
		
		drawer.drawRelations(relations);
		
	}

}
