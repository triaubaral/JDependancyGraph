/*
 * Copyright (C) 2010 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.cnamts.njc.infra.artifactory.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jfrog.build.api.Agent;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.Build;
import org.jfrog.build.api.BuildAgent;
import org.jfrog.build.api.BuildType;
import org.jfrog.build.api.builder.BuildInfoBuilder;
import org.jfrog.build.api.builder.ModuleBuilder;
import org.jfrog.build.api.dependency.BuildDependency;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryBuildInfoClient;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.action.ActionableHelper;
import org.jfrog.hudson.util.BuildUniqueIdentifierHelper;
import org.jfrog.hudson.util.ExtractorUtils;

import com.google.common.collect.Lists;

import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.jenkins.plugin.action.AffichageDependanceBuildAction;

/**
 * Builds the build info for cnamts deployment
 */
public class CnamtsBuildInfoDeployer {

	private final ArtifactoryServer artifactoryServer;
	private final InProgressBuild build;
	private Build buildInfo;
	private final ArtifactoryBuildInfoClient client;
	private List<BuildDependency> currentDeps = new ArrayList<BuildDependency>();
	private final Map<String, String> env;
	private ListenerBuild listener;
  
	// TODO probleme sur les modules fabriqués
	public CnamtsBuildInfoDeployer(final ArtifactoryBuildInfoClient pClient,
			final ArtifactoryServer pArtifactoryServer,
			final InProgressBuild build, final ListenerBuild listener,
			final List<Artifact> deployedArtifacts) {

		this.env = build.getEnvironment(listener);
		this.client = pClient;
		this.artifactoryServer = pArtifactoryServer;
		this.build = build;

		// recuperation des dépendanes
		final AffichageDependanceBuildAction action = build
				.getAction(AffichageDependanceBuildAction.class);
		if (action != null) {
			this.setDependencies(action.getDependances());
		}
		try {
			this.buildInfo = this
					.createBuildInfo("CNAMTS", "UL", BuildType.GENERIC);
			
			this.createDeployDetailsAndAddToBuildInfo(deployedArtifacts);
		} catch (IOException e) {
			listener.getLogger().println(e);
		} catch (InterruptedException e) {
			listener.getLogger().println(e);
		} catch (NoSuchAlgorithmException e) {
			listener.getLogger().println(e);
		}

		

	}

	private void addBuildInfoProperties(final BuildInfoBuilder builder) {

		this.addBuildVariables(builder);

		this.addEnvVariables(builder);

		this.addSystemVariables(builder);

	}

	private void addBuildVariables(final BuildInfoBuilder builder) {
		final Map<String, String> buildVariables = this.build
				.getBuildVariables();
		for (final Map.Entry entry : buildVariables.entrySet()) {
			final String varKey = (String) entry.getKey();

			builder.addProperty("buildInfo.env." + varKey, entry.getValue());
		}
	}

	private void addEnvVariables(final BuildInfoBuilder builder) {
		for (final Map.Entry entry : this.env.entrySet()) {
			final String varKey = (String) entry.getKey();

			builder.addProperty("buildInfo.env." + varKey, entry.getValue());
		}
	}

	private void addSystemVariables(final BuildInfoBuilder builder) {
		final Properties systemProperties = System.getProperties();
		final Enumeration enumeration = systemProperties.propertyNames();
		while (enumeration.hasMoreElements()) {
			final String propertyKey = (String) enumeration.nextElement();

			builder.addProperty(propertyKey,
					systemProperties.getProperty(propertyKey));
		}
	}

	protected Build createBuildInfo(final String buildAgentName,
			final String buildAgentVersion, final BuildType buildType)
			throws IOException, InterruptedException {
		final BuildInfoBuilder builder = new BuildInfoBuilder(
				BuildUniqueIdentifierHelper.getBuildName(this.build.getHudsonBuild()))
				.number(BuildUniqueIdentifierHelper.getBuildNumber(this.build.getHudsonBuild()))
				.type(buildType)
				.artifactoryPluginVersion(
						ActionableHelper.getArtifactoryPluginVersion())
				.buildAgent(new BuildAgent(buildAgentName, buildAgentVersion))
				.agent(new Agent("UL Jenkins", this.build.getHudsonBuild().getHudsonVersion()));

		final String buildUrl = ActionableHelper.getBuildUrl(this.build.getHudsonBuild());
		if (StringUtils.isNotBlank(buildUrl)) {
			builder.url(buildUrl);
		}

		final Calendar startedTimestamp = this.build.getTimestamp();
		builder.startedDate(startedTimestamp.getTime());

		final long duration = System.currentTimeMillis()
				- startedTimestamp.getTimeInMillis();
		builder.durationMillis(duration);

		String artifactoryPrincipal = this.artifactoryServer
				.getResolvingCredentialsConfig().getUsername();
		if (StringUtils.isBlank(artifactoryPrincipal)) {
			artifactoryPrincipal = "";
		}
		builder.artifactoryPrincipal(artifactoryPrincipal);

		final String userCause = ActionableHelper
				.getUserCausePrincipal(this.build.getHudsonBuild());
		if (userCause != null) {
			builder.principal(userCause);
		}

		final hudson.model.Cause.UpstreamCause parent = ActionableHelper
				.getUpstreamCause(this.build.getHudsonBuild());
		if (parent != null) {
			final String parentProject = ExtractorUtils
					.sanitizeBuildName(parent.getUpstreamProject());
			final int parentNumber = parent.getUpstreamBuild();
			builder.parentName(parentProject);
			builder.parentNumber(parentNumber + "");
			if (StringUtils.isBlank(userCause)) {
				builder.principal("auto");
			}
		}

		final String revision = this.build.getEnvironment(this.listener).get(
				"CNQD_CVS_LABEL"); // ExtractorUtils.getVcsRevision(this.env);
		if (StringUtils.isNotBlank(revision)) {
			builder.vcsRevision(revision);
		}

		this.addBuildInfoProperties(builder);
		 

		final Build buildInfo = builder.build();

		if (parent != null) {
			buildInfo.setParentBuildId(parent.getUpstreamProject());
		}

		for (final BuildDependency dep : this.currentDeps) {
			buildInfo.addBuildDependency(dep);
		}

		return buildInfo;
	}

	private void createDeployDetailsAndAddToBuildInfo(
			final List<Artifact> deployedArtifacts) throws IOException,
			NoSuchAlgorithmException {
		final ModuleBuilder moduleBuilder = new ModuleBuilder()
				.id(ExtractorUtils.sanitizeBuildName(this.build.getHudsonBuild().getParent()
						.getDisplayName()) + ":" + this.build.getNumber());
		moduleBuilder.artifacts(deployedArtifacts);

		this.buildInfo.setModules(Lists.newArrayList(moduleBuilder.build()));
	}

	public void deploy(){
		// String url = artifactoryServer.getUrl(); //
		// configurator.getArtifactoryServer().getUrl() + "/api/build";
		try {
			this.client.sendBuildInfo(this.buildInfo);
		} catch (IOException e) {
			this.listener.getLogger().println(e);
		}
	}

	private void setDependencies(final List<Dependance> pLstDepend) {

		this.currentDeps = new ArrayList<BuildDependency>();

		for (final Dependance label : pLstDepend) {
			final BuildDependency dep = new BuildDependency();
			dep.setName(label.getNomcomplet());
			dep.setNumber(label.getLabel().getBuilder().getVermaj() + label.getVermin()
					+ label.getLabel().getBuilder().getVertech());
			dep.setUrl(label.getUrl());
			this.currentDeps.add(dep);
		}

	}
}
