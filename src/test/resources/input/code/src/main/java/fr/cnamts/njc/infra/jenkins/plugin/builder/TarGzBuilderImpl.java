package fr.cnamts.njc.infra.jenkins.plugin.builder;

import fr.cnamts.njc.domain.bs.build.action.LivrableFromPubDirDeployerBS;
import fr.cnamts.njc.domain.inter.spi.Deployable;
import fr.cnamts.njc.domain.inter.spi.Initializable;
import fr.cnamts.njc.infra.adapter.api.BSFactory;
import fr.cnamts.njc.infra.adapter.spi.ArtifactoryDeployer;
import fr.cnamts.njc.infra.adapter.spi.BuildExecutionContext;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.IOException;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class TarGzBuilderImpl
        extends Builder
        implements SimpleBuildStep {

    /*
     * Suppression du repertoire de livraison savliv private static final class Freshen implements FileCallable<Void> {
     * private static final long serialVersionUID = 1;
     * 
     * @Override public Void invoke(File f, VirtualChannel channel) throws IOException {
     * 
     * if (f.exists()) { File[] files = f.listFiles(); if (files != null) { for (File sf : files) { if
     * (sf.isDirectory()) { invoke(sf, channel); } else { sf.delete(); } } }
     * 
     * f.delete(); } f.mkdir(); return null; }
     * 
     * @Override public void checkRoles(RoleChecker pArg0) throws SecurityException {
     * 
     * } }
     */

    /**
     * Descriptor for {@link TarGzBuilder}. Used as a singleton. The class is marked as public so that it can be
     * accessed from views.
     * 
     * <p>
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information, simply store it in a field and call save().
         * 
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private String livDir = "liv";
        private String artiRelease = "cnamts_releases";
        public String getArtiRelease() {
			return artiRelease;
		}
		public String getArtiSnapshots() {
			return artiSnapshots;
		}


		private String artiSnapshots = "cnamts_snapshots";
        
         
 

		 
		 public FormValidation doArtiRelease(@QueryParameter final String value)
	                throws IOException, ServletException {

	            if (value.length() == 0) {
	                return FormValidation.error("Saississez le repository de depot des releases !");
	            }

	            return FormValidation.ok();
	        }
		 public FormValidation doArtiSnapshots(@QueryParameter final String value)
	                throws IOException, ServletException {

	            if (value.length() == 0) {
	                return FormValidation.error("Saississez le repository de depot des snapshots !");
	            }

	            return FormValidation.ok();
	        }
		/**
         * In order to load the persisted global configuration, you have to call load() in the constructor.
         */
        public DescriptorImpl() {
            this.load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {

            this.livDir = formData.getString("livDir");
            this.artiRelease=formData.getString("artiRelease");
            this.artiSnapshots=formData.getString("artiSnapshots");

            this.save();

            return super.configure(req, formData);
        }

        public FormValidation doCheckLivDir(@QueryParameter final String value)
                throws IOException, ServletException {

            if (value.length() == 0) {
                return FormValidation.error("Saisissez le repertoire de livraison (relatif au workspace)");
            }

            return FormValidation.ok();
        }
 
        @Override
        public String getDisplayName() {
            return "CNAMTS Création tar.gz final et livraison dans Artifactory";
        }

        public String getLivDir() {
            return this.livDir;
        }

 
        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }
    }

    private String liv;
    private String name;

   

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public TarGzBuilderImpl() {
     
        this.liv = this.getDescriptor().getLivDir();

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
    	
    	BuildExecutionContext context = new BuildExecutionContext(new InProgressBuild(build), new ListenerBuild(listener));    	
    	
    	if(context.buildTarGzLivDir()){
    	
	    	Deployable deployable = new ArtifactoryDeployer(context);
	
	    	return BSFactory.INSTANCE.newLivDirDeployer(context, deployable).deploy(context.getLivDirTarGzName());
	    }
    	
    	
    	return false;
    }

    @Override
    public void perform(final Run<?, ?> build, final FilePath workspace, final Launcher launcher,
            final TaskListener listener) {
        listener.getLogger().println("Perform sur Run ");

    }
}
