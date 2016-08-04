package fr.cnamts.njc.infra.jenkins.plugin.builder;

import fr.cnamts.njc.domain.bs.build.action.LivrableFromLivDirDeployerBS;
import fr.cnamts.njc.domain.inter.spi.Deployable;
import fr.cnamts.njc.domain.inter.spi.Initializable;
import fr.cnamts.njc.infra.adapter.api.BSFactory;
import fr.cnamts.njc.infra.adapter.spi.ArtifactoryDeployer;
import fr.cnamts.njc.infra.adapter.spi.BuildExecutionContext;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import hudson.Extension;
import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class PubCommunBuilderImpl
        extends Builder implements SimpleBuildStep {

    /**
     * Descriptor for {@link TarGzBuilder}. Used as a singleton. The class is marked as public so that it can be
     * accessed from views.
     * 
     * <p>
     */
    @Extension
    // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        private String artiCommuns = "espace_communs";
        private String servCommuns = "/refab_centrale/espaces_commun/";
        private String pubDir = "pub";

        /**
         * In order to load the persisted global configuration, you have to call load() in the constructor.
         */
        public DescriptorImpl() {
            this.load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {

           // this.servCommuns = formData.getString("servCommuns");
        	this.pubDir = formData.getString("pubDir");
            // savDir = formData.getString("savDir");
            this.artiCommuns = formData.getString("artiCommuns");
           
            this.save();

            return super.configure(req, formData);
        }
        public FormValidation doCheckPubDir(@QueryParameter final String value)
                throws IOException, ServletException {

            // if (value.length() == 0)
            // return FormValidation.error("Saississez le repertoire de depot du tar.gz (relatif au workspace)");

            return FormValidation.ok();
        }
        
        public String getPubDir() {
            return this.pubDir;
        }
        public FormValidation doArtiCommuns(@QueryParameter final String value)
                throws IOException, ServletException {

            if (value.length() == 0) {
                return FormValidation.error("Saississez le repository de depot des communs !");
            }

            return FormValidation.ok();
        }

        public FormValidation doServCommuns(@QueryParameter final String value)
                throws IOException, ServletException {

            if (value.length() == 0) {
                return FormValidation.error("Saississez le repertoire de depot des communs !");
            }
            final File servCommunsFile = new File(this.servCommuns);
            if (!servCommunsFile.exists()) {
                return FormValidation.error("Le repertoire de depot des communs n'existe pas !");
            }
            return FormValidation.ok();
        }

        public String getArtiCommuns() {
            return this.artiCommuns;
        }

        @Override
        public String getDisplayName() {
            return "CNAMTS création du tar.gz de dependances communs et publication dans Artifactory";
        }

        public String getServCommuns() {
            return this.servCommuns;
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

    }

    // Suppression du repertoire de livraison savliv
    private static final class Freshen implements FileCallable<Void> {
        private static final long serialVersionUID = 1;

        @Override
        public void checkRoles(final RoleChecker pArg0) throws SecurityException {

        }

        @Override
        public Void invoke(final File f, final VirtualChannel channel) throws IOException {

            if (f.exists()) {
                final File[] files = f.listFiles();
                if (files != null) {
                    for (final File sf : files) {
                        if (sf.isDirectory()) {
                            this.invoke(sf, channel);
                        } else {
                            sf.delete();
                        }
                    }
                }

                f.delete();
            }
            f.mkdir();
            return null;
        }
    }


	private String pubDir;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PubCommunBuilderImpl() {
        // this.servCommuns = getDescriptor().getServCommuns();
    	//this.pubDir = this.getDescriptor().getPubDir();
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
    	
    	BuildExecutionContext context = new BuildExecutionContext(new InProgressBuild(build), new ListenerBuild(listener));
    	
    	if(context.buildTarGzPubDir()){
    		
    		Deployable deployable = new ArtifactoryDeployer(context);    		
	                	
			return BSFactory.INSTANCE.newPubDirDeployer(context, deployable).deploy(context.getPubDirTarGzName());
				
			
    	}
        
        return false;

    }



   
    @Override
    public void perform(final Run<?, ?> build, final FilePath workspace, final Launcher launcher,
            final TaskListener listener) {
        listener.getLogger().println("Perform sur Run ");

    }
}
