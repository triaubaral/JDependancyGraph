package fr.cnamts.njc.infra.jenkins.plugin.builder;


import fr.cnamts.njc.domain.bs.build.action.DependanceDisplayerBS;
import fr.cnamts.njc.domain.inter.api.Checkable;
import fr.cnamts.njc.infra.adapter.api.BSFactory;
import fr.cnamts.njc.infra.adapter.spi.ArtifactoryDownloader;
import fr.cnamts.njc.infra.adapter.spi.BuildExecutionContext;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceFilePath;
import fr.cnamts.njc.infra.jenkins.plugin.action.AffichageDependanceBuildAction;
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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

public class PropertiesInjecterImpl
        extends Builder
        implements SimpleBuildStep {

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
        private String fileConf; 
        
       
        /**
         * In order to load the persisted global configuration, you have to call load() in the constructor.
         */
        public DescriptorImpl() {
            this.load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {

            this.fileConf = formData.getString("fileConf");

            if (this.fileConf.length() == 0) {
                this.fileConf = PROP_FILE;
                formData.put("fileConf", this.fileConf);

            }
           
            this.save();

            return super.configure(req, formData);
        }

        public FormValidation doFileConf(@QueryParameter final String value)
                throws IOException, ServletException {

            if (value.length() == 0) {
                return FormValidation.error("Saississez le chemin du fichier properties.conf");
            }

            return FormValidation.ok();
        }

        @Override
        public String getDisplayName() {
            return "CNAMTS injection properties.conf";
        }

        @Exported
        public String getFileConf() {
            if (this.fileConf == null || this.fileConf.length() == 0) {
                this.fileConf = PROP_FILE;
            }
            return this.fileConf;
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

    }
 

    private static final String PROP_FILE = "install" + File.separator + "properties.conf";

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public PropertiesInjecterImpl() {
        

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
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
    	
    	boolean result = false;
    	
    	BuildExecutionContext context = new BuildExecutionContext(new InProgressBuild(build), new ListenerBuild(listener));
    	
    	Checkable socleChecker = BSFactory.INSTANCE.newCheckable(context);
    	
    	if(socleChecker.verify()){
    		
    		ArtifactoryDownloader downloader = new ArtifactoryDownloader(context);    	
    		DependanceDisplayerBS dependanceDisplayer = new DependanceDisplayerBS(context, downloader);
    		 // Suppression des repertoire techniques CVS
        	WorkspaceFilePath.rmSubDirs("CVS", build.getWorkspace());
    		result = dependanceDisplayer.display(this.getDescriptor().getFileConf());
    		
    		final AffichageDependanceBuildAction act = new AffichageDependanceBuildAction(context.getDependances(), build);
    		build.addAction(act);               
            act.setDependances(dependanceDisplayer.getDownloadedDependances());
    	
    		
    	}
        
    	return result;

    }

	

    @Override
    public void perform(final Run<?, ?> build, final FilePath workspace, final Launcher launcher,
            final TaskListener listener) {
        listener.getLogger().println("Perform sur Run ");

    }
    
}
