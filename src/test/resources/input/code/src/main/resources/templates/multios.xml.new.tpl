<?xml version='1.0' encoding='UTF-8'?>
<matrix-project plugin="matrix-project">
  <actions/>
  <description></description>
  <logRotator class="hudson.tasks.LogRotator">
    <daysToKeep>3</daysToKeep>
    <numToKeep>2</numToKeep>
    <artifactDaysToKeep>-1</artifactDaysToKeep>
    <artifactNumToKeep>-1</artifactNumToKeep>
  </logRotator>
  <keepDependencies>false</keepDependencies>
  <!-- properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.StringParameterDefinition>
          <name>USER_UL</name>
          <description></description>
          <defaultValue>${USER_UL}</defaultValue>
        </hudson.model.StringParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
  </properties -->
  <scm class="hudson.plugins.filesystem_scm.FSSCM" plugin="filesystem_scm@1.20">
    <path>/data/rce_wks/${CNQD_CVS_MODULE}_BUILD</path>
    <clearWorkspace>false</clearWorkspace>
    <copyHidden>false</copyHidden>
    <filterEnabled>false</filterEnabled>
    <includeFilter>false</includeFilter>
    <filters/>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <axes>
    <hudson.matrix.LabelAxis>
      <name>OSREFAB</name>
      <values>
      	${LST_OS_REFAB}
      </values>
    </hudson.matrix.LabelAxis>
  </axes>
  <builders>
    <fr.cnamts.jenkins.builders.PropertiesInjecterImpl plugin="packaging@1.2.7-SNAPSHOT"/>
    <org.jenkinsci.plugins.managedscripts.ScriptBuildStep plugin="managed-scripts@1.2.1">
      <buildStepId>org.jenkinsci.plugins.managedscripts.ScriptConfig1464276610431</buildStepId>
      <tokenized>false</tokenized>
    </org.jenkinsci.plugins.managedscripts.ScriptBuildStep>
    <fr.cnamts.jenkins.builders.PubCommunBuilderImpl plugin="packaging@1.2.7-SNAPSHOT"/>
    <fr.cnamts.jenkins.builders.TarGzBuilderImpl plugin="packaging@1.2.7-SNAPSHOT">
      <liv>liv</liv>
    </fr.cnamts.jenkins.builders.TarGzBuilderImpl>
  </builders>
  <publishers/>
  <buildWrappers>
 	<hudson.plugins.ws__cleanup.PreBuildCleanup plugin="ws-cleanup@0.29">
      <deleteDirs>false</deleteDirs>
      <cleanupParameter></cleanupParameter>
      <externalDelete></externalDelete>
    </hudson.plugins.ws__cleanup.PreBuildCleanup>
      <EnvInjectBuildWrapper plugin="envinject@1.92.1">
      <info>
        <propertiesFilePath>CNAMTSSCM.properties</propertiesFilePath>
        <loadFilesFromMaster>false</loadFilesFromMaster>
      </info>
    </EnvInjectBuildWrapper>
  </buildWrappers>
  <executionStrategy class="hudson.matrix.DefaultMatrixExecutionStrategyImpl">
    <runSequentially>false</runSequentially>
  </executionStrategy>
</matrix-project>