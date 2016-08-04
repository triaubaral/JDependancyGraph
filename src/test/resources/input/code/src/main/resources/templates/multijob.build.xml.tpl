<?xml version='1.0' encoding='UTF-8'?>
<com.tikal.jenkins.plugins.multijob.MultiJobProject plugin="jenkins-multijob-plugin@1.21">
  <actions/>
  <description></description>
  <keepDependencies>false</keepDependencies>
  <properties>
    <hudson.model.ParametersDefinitionProperty>
      <parameterDefinitions>
        <hudson.model.StringParameterDefinition>
          <name>${CNQD_CVS_MODULE}_LABEL</name>
          <description></description>
          <defaultValue>${CNQD_CVS_LABEL}</defaultValue>
        </hudson.model.StringParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>CNQD_CVS_MODULE</name>
          <description></description>
          <defaultValue>${CNQD_CVS_MODULE}</defaultValue>
        </hudson.model.StringParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>${CNQD_CVS_MODULE}_REF</name>
          <description></description>
          <defaultValue>${CNQD_CVS_REFERENTIEL}</defaultValue>
        </hudson.model.StringParameterDefinition>
        <hudson.model.StringParameterDefinition>
          <name>USER_UL</name>
          <description></description>
          <defaultValue>cnamts</defaultValue>
        </hudson.model.StringParameterDefinition>
      </parameterDefinitions>
    </hudson.model.ParametersDefinitionProperty>
    <org.jenkinsci.plugins.sharedworkspace.SharedWorkspace plugin="shared-workspace">
      <name>NONE</name>
    </org.jenkinsci.plugins.sharedworkspace.SharedWorkspace>
    <jenkins.model.BuildDiscarderProperty>
      <strategy class="hudson.tasks.LogRotator">
        <daysToKeep>-1</daysToKeep>
        <numToKeep>2</numToKeep>
        <artifactDaysToKeep>-1</artifactDaysToKeep>
        <artifactNumToKeep>-1</artifactNumToKeep>
      </strategy>
    </jenkins.model.BuildDiscarderProperty>
    </properties>
  <scm class="hudson.scm.CVSSCM" plugin="cvs@2.12">
    <flatten>true</flatten>
    <repositories>
      <hudson.scm.CvsRepository>
        <cvsRoot>:pserver:anonyme:anonyme@cvsweb.cnqd.cnamts.fr:/cvsdata/${${CNQD_CVS_MODULE}_REF}</cvsRoot>
        <repositoryItems>
          <hudson.scm.CvsRepositoryItem>
            <modules>
              <hudson.scm.CvsModule>
                <localName>${CNQD_CVS_MODULE}</localName>
                <remoteName>${CNQD_CVS_MODULE}</remoteName>
              </hudson.scm.CvsModule>
            </modules>
            <location class="hudson.scm.CvsRepositoryLocation$TagRepositoryLocation">
              <locationType>TAG</locationType>
              <locationName>${${CNQD_CVS_MODULE}_LABEL}</locationName>
              <useHeadIfNotFound>false</useHeadIfNotFound>
            </location>
          </hudson.scm.CvsRepositoryItem>
        </repositoryItems>
        <compressionLevel>-1</compressionLevel>
        <excludedRegions>
          <hudson.scm.ExcludedRegion>
            <pattern></pattern>
          </hudson.scm.ExcludedRegion>
        </excludedRegions>
        <passwordRequired>false</passwordRequired>
      </hudson.scm.CvsRepository>
    </repositories>
    <canUseUpdate>false</canUseUpdate>
    <skipChangeLog>true</skipChangeLog>
    <pruneEmptyDirectories>true</pruneEmptyDirectories>
    <disableCvsQuiet>false</disableCvsQuiet>
    <cleanOnFailedUpdate>false</cleanOnFailedUpdate>
    <forceCleanCopy>false</forceCleanCopy>
  </scm>
  <canRoam>true</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
 <jdk>${UL_JDK}</jdk>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <builders>
    <com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
      <phaseName>Fabrication ${CNQD_CVS_MODULE}</phaseName>
      <phaseJobs>
        <com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
          <jobName>${CNQD_CVS_MODULE}</jobName>
          <currParams>true</currParams>
          <exposedSCM>false</exposedSCM>
          <disableJob>false</disableJob>
          <parsingRulesPath></parsingRulesPath>
          <maxRetries>0</maxRetries>
          <enableRetryStrategy>false</enableRetryStrategy>
          <enableCondition>false</enableCondition>
          <abortAllJob>true</abortAllJob>
          <condition></condition>
          <configs>
            <hudson.plugins.parameterizedtrigger.CurrentBuildParameters plugin="parameterized-trigger@2.30"/>
            <hudson.plugins.parameterizedtrigger.FileBuildParameters plugin="parameterized-trigger@2.30">
              <propertiesFile>CNAMTSSCM.properties</propertiesFile>
              <failTriggerOnMissing>true</failTriggerOnMissing>
              <useMatrixChild>false</useMatrixChild>
              <onlyExactRuns>false</onlyExactRuns>
            </hudson.plugins.parameterizedtrigger.FileBuildParameters>
          </configs>
          <killPhaseOnJobResultCondition>FAILURE</killPhaseOnJobResultCondition>
          <buildOnlyIfSCMChanges>false</buildOnlyIfSCMChanges>
          <applyConditionOnlyIfNoSCMChanges>false</applyConditionOnlyIfNoSCMChanges>
        </com.tikal.jenkins.plugins.multijob.PhaseJobsConfig>
      </phaseJobs>
      <continuationCondition>ALWAYS</continuationCondition>
    </com.tikal.jenkins.plugins.multijob.MultiJobBuilder>
    <org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder plugin="conditional-buildstep@1.3.5">
      <runner class="org.jenkins_ci.plugins.run_condition.BuildStepRunner$DontRun" plugin="run-condition@1.0"/>
      <runCondition class="org.jenkins_ci.plugins.run_condition.logic.Or" plugin="run-condition@1.0">
        <conditions>
          <org.jenkins__ci.plugins.run__condition.logic.ConditionContainer>
            <condition class="org.jenkins_ci.plugins.run_condition.core.ExpressionCondition">
              <expression>.*ST1.*</expression>
              <label>${ENV,var=&quot;OSREFAB&quot;}</label>
            </condition>
          </org.jenkins__ci.plugins.run__condition.logic.ConditionContainer>
          <org.jenkins__ci.plugins.run__condition.logic.ConditionContainer>
            <condition class="org.jenkins_ci.plugins.run_condition.core.ExpressionCondition">
              <expression>.*st1.*</expression>
              <label>${ENV,var=&quot;OSREFAB&quot;}</label>
            </condition>
          </org.jenkins__ci.plugins.run__condition.logic.ConditionContainer>
        </conditions>
      </runCondition>
      <conditionalbuilders>
        <org.ukiuni.callOtherJenkins.CallOtherJenkins.CallOtherJenkinsBuilder plugin="call-remote-job-plugin@1.0.21">
          <hostName>55.10.122.19:8080</hostName>
          <jobName>GENERATOR</jobName>
          <span>10000</span>
          <retry>10</retry>
          <userName></userName>
          <password></password>
          <parameters>USER_UL=$USER_UL&amp;JOB_NAME=$CNQD_CVS_MODULE</parameters>
        </org.ukiuni.callOtherJenkins.CallOtherJenkins.CallOtherJenkinsBuilder>
        <org.ukiuni.callOtherJenkins.CallOtherJenkins.CallOtherJenkinsBuilder plugin="call-remote-job-plugin@1.0.21">
          <hostName>55.10.122.19:8080</hostName>
          <jobName>${CNQD_CVS_MODULE}</jobName>
          <span>10000</span>
          <retry>10</retry>
          <userName></userName>
          <password></password>
          <parameters>USER_UL=$USER_UL&amp;JOB_NAME=$CNQD_CVS_MODULE</parameters>
        </org.ukiuni.callOtherJenkins.CallOtherJenkins.CallOtherJenkinsBuilder>
      </conditionalbuilders>
    </org.jenkinsci.plugins.conditionalbuildstep.ConditionalBuilder>
  </builders>
  <publishers/>
  <buildWrappers>
    <hudson.plugins.ws__cleanup.PreBuildCleanup plugin="ws-cleanup@0.29">
      <deleteDirs>false</deleteDirs>
      <cleanupParameter></cleanupParameter>
      <externalDelete></externalDelete>
    </hudson.plugins.ws__cleanup.PreBuildCleanup>
    <fr.cnamts.jenkins.notifiers.ExtractEnvInfosImpl plugin="packaging@1.2.7-SNAPSHOT"/>
  </buildWrappers>
  <pollSubjobs>true</pollSubjobs>
</com.tikal.jenkins.plugins.multijob.MultiJobProject>