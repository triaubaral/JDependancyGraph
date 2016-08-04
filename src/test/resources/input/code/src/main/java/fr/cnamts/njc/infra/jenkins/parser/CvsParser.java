package fr.cnamts.njc.infra.jenkins.parser;

import hudson.scm.CVSSCM;
import hudson.scm.CvsRepository;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.Project;

public class CvsParser {

	private InProgressBuild build;
	private ListenerBuild listener;
	private CvsRepository cvsRepository;

	public CvsParser(InProgressBuild build, ListenerBuild listener) {
		this.build = build;
		this.listener = listener;
		this.cvsRepository = getCVSRepository();
	}
	

	private CvsRepository getCVSRepository() {

		Project rootProject = new Project(this.build.getProject());

		if (rootProject.isCVSProject()) {
			final CVSSCM scmP = CVSSCM.class.cast(rootProject.getScm());
			return scmP.getRepositories()[0];
		}

		this.listener.getLogger().println(
				"Pas de variable CVS sur type " + rootProject.getName() + "!");
		return null;

	}

	public String getCVSModLocName() {

		return this.cvsRepository.getRepositoryItems()[0].getModules()[0].getLocalName();

	}

	public String getCVSRefName() {

		final String[] arrStrCVS = this.cvsRepository.getCvsRoot().split("/");

		return arrStrCVS[arrStrCVS.length - 1];

	}

	public String getCVSLabel() {

		String label = this.cvsRepository.getRepositoryItems()[0].getLocation()
				.getLocationName();
		if (null == label) {
			return "HEAD";
		}

		return label;

	}

	public String getCVSModuleName() {

		return this.cvsRepository.getRepositoryItems()[0].getModules()[0].getRemoteName();

	}

}
