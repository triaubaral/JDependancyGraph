package com.triaubaral.dependancy.graph.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SourceReaderTest {
	
	private SourceFileDeclarationMocker mocker = new SourceFileDeclarationMocker();
	

	@Test
	public void shouldMapSourceFileWithItsPackageAndImportDeclaration()
			throws IOException {

		InputStream stream = this.getClass().getResourceAsStream(
				"/input/code/class/Dependance.java-exemple");

		String fileContent = IOUtils.toString(stream);

		List<String> imports = mocker.newImport("java.util.ArrayList");

		imports.add("hudson.EnvVars");
		imports.add("hudson.model.ParameterValue");
		imports.add("java.io.FileNotFoundException");
		imports.add("java.util.ArrayList");

		SourceFileDeclaration expectedResult = mocker.newSourceFileDeclaration(
				"BuildExecutionContext",
				"fr.cnamts.njc.infra.adapter.spi", imports);

		SourceFileDeclarationReader sourceReader = new SourceFileDeclarationReader();

		SourceFileDeclaration result = sourceReader.read(fileContent);

		Assertions.assertThat(result).isEqualTo(expectedResult);

	}

	@Test
	public void shouldMapManySourceFilesWithItsPackageAndImportDeclaration() {

		List<SourceFileDeclaration> expectedResults = new ArrayList<SourceFileDeclaration>();
		List<String> imports = mocker.newImport("java.util.BuildExecutionContext");
		List<String> imports1 = mocker.newImport("java.util.BoExecutionContext");
		List<String> imports2 = mocker.newImport("java.util.AdapterExecutionContext");

		SourceFileDeclaration expectedResult1 = mocker.newSourceFileDeclaration(
				"BuildExecutionContext",
				"fr.cnamts.njc.infra.adapter.spi", imports);
		SourceFileDeclaration expectedResult2 = mocker.newSourceFileDeclaration(
				"BoExecutionContext",
				"fr.cnamts.njc.infra.adapter.spi", imports1);
		SourceFileDeclaration expectedResult3 = mocker.newSourceFileDeclaration(
				"AdapterExecutionContext",
				"fr.cnamts.njc.infra.adapter.spi", imports2);

		expectedResults.add(expectedResult1);
		expectedResults.add(expectedResult2);
		expectedResults.add(expectedResult3);

		SourceDirReader sourceDirReader = new SourceDirReader();

		List<SourceFileDeclaration> result = sourceDirReader
				.read("src/test/resources/input/code/mini-src");

		Assertions.assertThat(result).containsAll(expectedResults);		

	}

	

}
