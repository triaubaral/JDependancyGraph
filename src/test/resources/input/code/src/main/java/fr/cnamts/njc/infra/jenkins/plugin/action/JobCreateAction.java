/*
 * The MIT License
 * 
 * Copyright (c) 2011, Jesse Farinacci
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.cnamts.njc.infra.jenkins.plugin.action;

import fr.cnamts.njc.domain.bo.module.LabelValidator;
import fr.cnamts.njc.domain.bo.project.ProjectDetail;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Api;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.model.Queue;
import hudson.util.FormValidation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import jenkins.model.Jenkins;
import jenkins.util.TimeDuration;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Extension
public final class JobCreateAction implements RootAction,
		Describable<JobCreateAction> {

	
	/**
	 * API d'appel pour la création et lancement d'un job selon modele multiconfig
	 * @param req
	 * @param rsp
	 * @param label le label du projet
	 * @param user l'utilisateur lanceur - cnamts ou dn
	 * @TODO a securiser avec l'utilisateur jenkins 
	 */
	public void doCreateAndBuild(StaplerRequest req, StaplerResponse rsp, @QueryParameter String label, @QueryParameter String user)  {
	    
		    LOGGER.log(Level.FINE, "build on {0}", label);
		    
		    LabelValidator validator = new LabelValidator();
		    
			try {

			if (label != null && validator.isValidLabel(label)) {
				//createdJobs = new ArrayList<String>();
			 
				    ProjectDetail hdep = ULHelper.getUnitaryProjectDetail(label);
			        hdep.setUserBuild(user);
					if(createAndBuildProject(hdep, "multios.xml.new.tpl")){
					
						rsp.sendRedirect(HttpServletResponse.SC_CREATED, req.getContextPath() + "/job/" + hdep.getCVSMod());
					
					
					
					}else{
						rsp.sendError(500, "Impossible de creer le projet pour " + label + " \nDetails : " + hdep.toString());
					}
				
			 

			}else{
				rsp.sendError(500, "Label " + label +" incorrect !");
			}
			
			} catch (IOException e) {
					  e.printStackTrace();
			} catch (InterruptedException e) {
				try {
					rsp.sendError(500, e.getMessage());
				} catch (IOException e1) {
				 
					e1.printStackTrace();
				}
				 
			}
			
		 
//	        ParameterizedJobMixIn.ParameterizedJob p = project(job, req, rsp);
//	        if (delay == null) {
//	            delay = new TimeDuration(p.getQuietPeriod());
//	        }
//	        ParametersDefinitionProperty pp = ((Job<?,?>) p).getProperty(ParametersDefinitionProperty.class);
//	        if (pp != null) {
//	            LOGGER.fine("wrong kind");
//	            throw HttpResponses.error(HttpServletResponse.SC_BAD_REQUEST, "Use /buildByToken/buildWithParameters for this job since it takes parameters");
//	        }
//	        Queue.Item item = Jenkins.getInstance().getQueue().schedule(p, delay.getTime(), getBuildCause(req));
//	        if (item != null) {
//	            rsp.sendRedirect(SC_CREATED, req.getContextPath() + '/' + item.getUrl());
//	        } else {
//	            rsp.sendRedirect(".");
//	        }
	    }
	
	private static final Logger LOGGER = Logger.getLogger(JobCreateAction.class
			.getName());

	private String label;

	private List<String> createdJobs;

	public List<String> getCreatedJobs() {
		return createdJobs;
	}

	public void setCreatedJobs(List<String> createdJobs) {
		this.createdJobs = createdJobs;
	}

	public String replace(final String pCherche, final InputStream pBase,
			final String pReplace) throws IOException {
		final char[] buffer = new char[0x10000];
		final StringBuilder out = new StringBuilder();
		final Reader inReader = new InputStreamReader(pBase, "UTF-8");
		int read;
		do {
			read = inReader.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.append(buffer, 0, read);
			}
		} while (read >= 0);

		final String pChaine = out.toString();

		return pChaine.replace(pCherche, pReplace);

	}

	private InputStream formalizeXML(ProjectDetail dep, String tmpl) throws IOException {
		// FileInputStream file =
		// Jenkins.getInstance().getRootPath().child(relOrAbsolute)
		String content = "";
		try {
			URL url = getClass().getResource("/templates/" + tmpl); // multios.xml.template");
			InputStream stream = url.openStream();
			// BufferedInputStream buffIn = new
			// BufferedInputStream(url.openStream());
			content = replace("${CNQD_CVS_LABEL}", stream, dep.getCVSLab());
			stream.close();
			content = content.replace("${CNQD_CVS_MODULE}", dep.getCVSMod());
			content = content.replace("${CNQD_CVS_REFERENTIEL}",
					dep.getCVSRef());

			StringBuffer buff = new StringBuffer();

			List<Node> nodes = Jenkins.getInstance().getNodes();
			List<String> availlables = new ArrayList<String>();

			for (Node node : nodes) {
				availlables.add(node.getNodeName().toUpperCase());
			}

			for (String osCible : dep.getOsCibles()) {
				if (availlables.contains(osCible.toUpperCase())) {
					buff.append("<string>");
					buff.append(osCible.toUpperCase());
					buff.append("</string>");
				}
			}
			content = content.replace("${LST_OS_REFAB}", buff.toString());
			
			content = content.replace("${USER_UL}",dep.getUserBuild()); //TODO extract jdk
			
			List jdks = Jenkins.getInstance().getJDKs();

			content = content.replace("${UL_JDK}", "REFAB_JAVA1664"); //TODO extract jdk
			
			
		} catch (Exception e) {
			e.printStackTrace();
		 
			return null;
		}
	 
		return new ByteArrayInputStream(
				content.getBytes());

	}

	private boolean createOrUpdateJob(ProjectDetail depen) throws IOException {

		boolean isOk = true;
		String baseTree = "";
		for (int i = 0; i < depen.getProfond(); i++) {
			baseTree += "&nbsp;&nbsp;&nbsp;";
		}
		;

		// Projet _BUILD
		TopLevelItem projetItem = Jenkins.getInstance().getItem(
				depen.getCVSMod() + "_BUILD");

		if (projetItem != null) {

			InputStream configStream = formalizeXML(depen,
					"multijob.build.xml.tpl");
			if (configStream != null) {
				((AbstractProject) projetItem)
						.updateByXml((Source) new StreamSource(configStream));

				createdJobs.add(baseTree + depen.getCVSMod()
						+ "_BUILD (modifié) - label : " + depen.getCVSLab());
			} else {
				createdJobs.add(baseTree + depen.getCVSMod()
						+ "_BUILD (Creation impossible) - label : "
						+ depen.getCVSLab());
			}
			isOk = true;

		} else {

			InputStream configStream = formalizeXML(depen,
					"multijob.build.xml.tpl");
			if (configStream != null) {
				// create build
				isOk = (Jenkins.getInstance().createProjectFromXML(
						depen.getCVSMod() + "_BUILD", configStream) != null);
				if (isOk) {
					createdJobs.add(baseTree + depen.getCVSMod()
							+ "_BUILD (créé) - label : " + depen.getCVSLab());
				} else {
					createdJobs.add(baseTree + depen.getCVSMod()
							+ "_BUILD (creation impossible) - label : "
							+ depen.getCVSLab());
				}
			} else {
				createdJobs.add(baseTree + depen.getCVSMod()
						+ "_BUILD (creation impossible) - label : "
						+ depen.getCVSLab());
			}

		}

		// Projet unitaire
		projetItem = Jenkins.getInstance().getItem(depen.getCVSMod());

		if (Jenkins.getInstance().getItem(depen.getCVSMod()) != null) {
			InputStream configStream = formalizeXML(depen,
					"multios.xml.new.tpl");
			if (configStream != null) {
				((AbstractProject) projetItem)
						.updateByXml((Source) new StreamSource(configStream));
				createdJobs.add(baseTree + depen.getCVSMod()
						+ " (modifié) - label : " + depen.getCVSLab());

			} else {
				createdJobs.add(baseTree + depen.getCVSMod()
						+ " (Creation impossible) - label : "
						+ depen.getCVSLab());
			}
			isOk = true;

		} else {
			createdJobs.add(baseTree + depen.getCVSMod() + " (créé) - label : "
					+ depen.getCVSLab());

			// create job
			InputStream configStream = formalizeXML(depen,
					"multios.xml.new.tpl");
			if (configStream != null) {
				// create build
				isOk = (Jenkins.getInstance().createProjectFromXML(
						depen.getCVSMod(), configStream) != null);
				if (isOk) {
					createdJobs.add(baseTree + depen.getCVSMod()
							+ " (créé) - label : " + depen.getCVSLab());
				} else {
					createdJobs.add(baseTree + depen.getCVSMod()
							+ " (creation impossible) - label : "
							+ depen.getCVSLab());
				}
			} else {
				createdJobs.add(baseTree + depen.getCVSMod()
						+ " (creation impossible) - label : "
						+ depen.getCVSLab());
			}

		}

		for (ProjectDetail childDep : depen.getLstDepend()) {
			createOrUpdateJob(childDep);

		}

		return isOk;
	}

	
	private boolean createAndBuildProject(ProjectDetail pDetails,  String nomModele) throws IOException{
				// Projet unitaire
				TopLevelItem	projetItem = Jenkins.getInstance().getItem(pDetails.getCVSMod());
		          
				if (projetItem != null) {
					InputStream configStream = formalizeXML(pDetails,
							nomModele);//	"multios.xml.new.tpl");
					if (configStream != null) {
						((AbstractProject) projetItem)
								.updateByXml((Source) new StreamSource(configStream));
						TimeDuration     delay = new TimeDuration(((AbstractProject) projetItem).getQuietPeriod());
						 Queue.Item item = Jenkins.getInstance().getQueue().schedule(((AbstractProject) projetItem), delay.getTime());
						 return true;
					} else{
						return false;
					}
					

				} else {
				  	// create job
					InputStream configStream = formalizeXML(pDetails,
							nomModele); //	"multios.xml.new.tpl");
					if (configStream != null) {
						// create build
					   if( (Jenkins.getInstance().createProjectFromXML(
								pDetails.getCVSMod(), configStream) != null)){
					
						     Queue.Item item = Jenkins.getInstance().getQueue().schedule(((AbstractProject) projetItem), 300);
							 return true;
					   }else{
						   return false;
					   }
						 
					} else {
						return false;
					}

				}
	}
	
	
	public void doCreate(final StaplerRequest request,
			final StaplerResponse response) throws ServletException,
			IOException {

		if (request.hasParameter("label")) {
			createdJobs = new ArrayList<String>();
			String label = request.getParameter("label").trim();

			ProjectDetail hdep = ULHelper.treeDependances(0, label);

			createOrUpdateJob(hdep);

		}

		response.forwardToPreviousPage(request);
	}

	private static String text(Element e, String name) {
		NodeList nl = e.getElementsByTagName(name);
		if (nl.getLength() == 1) {
			Element e2 = (Element) nl.item(0);
			return e2.getTextContent();
		} else {
			return null;
		}
	}

	public FormValidation doTestConnection(
			@QueryParameter("remoteUrl") final String remoteUrl) {
		return FormValidation.ok();
	}

	public String getRootUrl() {
		return Jenkins.getInstance().getRootUrl();
	}

	public Api getApi() {
		return new Api(this);
	}

	public String getDisplayName() {
	//	if (Jenkins.getInstance().hasPermission(Jenkins.ADMINISTER)) {

			return "Creation cascade CNAMTS";
		//} else {
			//return "Creation cascade CNAMTS"; //return null;
		//}
	}

	public String getIconFileName() {
		return null;
	}

	public String getLabel() {
		return label;
	}

	public String getUrlName() {
		return "/createCnamJob";
	}

	public boolean isCreatedJobsAvailable() {
		if (null == createdJobs) {
			createdJobs = new ArrayList<String>();
		}
		return createdJobs.size() > 0;
	}

	@Override
	public Descriptor<JobCreateAction> getDescriptor() {
		// TODO switch to Jenkins.getActiveInstance() once 1.590+ is the
		// baseline
		Jenkins jenkins = Jenkins.getInstance();
		if (jenkins == null) {
			throw new IllegalStateException(
					"Jenkins has not been started, or was already shut down");
		}
		return jenkins.getDescriptorOrDie(getClass());
	}

	@Extension
	public static final class JobCreateActionDescriptor extends
			Descriptor<JobCreateAction> {

		@Override
		public String getDisplayName() {
			return "";
		}

	}
}
