package fr.cnamts.njc.infra.adapter.spi;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.ParameterValue;
import hudson.model.AbstractBuild;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jenkins.model.Jenkins;
import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.bo.module.DependanceBuilder;
import fr.cnamts.njc.domain.bo.module.conf.SectionLine;
import fr.cnamts.njc.domain.bo.module.conf.SectionTitle;
import fr.cnamts.njc.domain.exception.DependancesException;
import fr.cnamts.njc.domain.inter.api.ContentMapper;
import fr.cnamts.njc.domain.inter.spi.Initializable;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.Project;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceFilePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspacePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceWorkingDirFactory;
import fr.cnamts.njc.infra.cvsweb.module.conf.parser.ConfParser;
import fr.cnamts.njc.infra.jenkins.parser.CvsParser;
import fr.cnamts.njc.infra.jenkins.plugin.builder.PropertiesInjecterImpl;
import fr.cnamts.njc.infra.jenkins.plugin.builder.PubCommunBuilderImpl;
import fr.cnamts.njc.infra.jenkins.plugin.searcher.VarExecutionContextSearcher;

public class BuildExecutionContext implements Initializable {
	
	private InProgressBuild build;	
	private ListenerBuild listener;
	private String servCommuns;	
	private WorkspacePath workspace;	
	private List<ParameterValue> parameters = new ArrayList<ParameterValue>();
	private List<Dependance> dependances = new ArrayList<Dependance>();
	private Map<VarExecutionContext, String> varsExecutionContext = new EnumMap<VarExecutionContext, String>(VarExecutionContext.class);
	private Map<VarExecutionContext, WorkspaceFilePath> workspaceFiles = new EnumMap<VarExecutionContext, WorkspaceFilePath>(VarExecutionContext.class);
	private List<ParameterValue> lstParam = new ArrayList<ParameterValue>();	
	private String livDirTarGzName;
	private String pubDirTarGzName;
	private VarExecutionContextSearcher lookupForVar;
	
	private WorkspaceFilePath buildWorkspaceFilePath(VarExecutionContext contextVar){
		return WorkspaceWorkingDirFactory.INSTANCE.resolve(contextVar, workspace);
	}	
		
	public void buildContext(){
		
		workspace = new WorkspacePath(this.build);	
		lookupForVar = new VarExecutionContextSearcher(build.getEnvironment(listener), build.getBuildVariableResolver());
		
		workspaceFiles.put(VarExecutionContext.LIVRAISON_DIR, buildWorkspaceFilePath(VarExecutionContext.LIVRAISON_DIR));
		workspaceFiles.put(VarExecutionContext.PUBLICATION_DIR,  buildWorkspaceFilePath(VarExecutionContext.PUBLICATION_DIR));
		workspaceFiles.put(VarExecutionContext.COMMUN_DIR,  buildWorkspaceFilePath(VarExecutionContext.COMMUN_DIR));
		workspaceFiles.put(VarExecutionContext.ARTI_COMMUN_DIR,  buildWorkspaceFilePath(VarExecutionContext.ARTI_COMMUN_DIR));
		workspaceFiles.put(VarExecutionContext.SNAPSHOT_DIR,  buildWorkspaceFilePath(VarExecutionContext.SNAPSHOT_DIR));
		workspaceFiles.put(VarExecutionContext.RELEASE_DIR,  buildWorkspaceFilePath(VarExecutionContext.RELEASE_DIR));	
		
		varsExecutionContext.put(VarExecutionContext.LIVRAISON_DIR, workspaceFiles.get(VarExecutionContext.LIVRAISON_DIR).getLocation());
		varsExecutionContext.put(VarExecutionContext.PUBLICATION_DIR, workspaceFiles.get(VarExecutionContext.PUBLICATION_DIR).getLocation());
		varsExecutionContext.put(VarExecutionContext.COMMUN_DIR, workspaceFiles.get(VarExecutionContext.COMMUN_DIR).getLocation());
		varsExecutionContext.put(VarExecutionContext.ARTI_COMMUN_DIR, workspaceFiles.get(VarExecutionContext.ARTI_COMMUN_DIR).getLocation());
		varsExecutionContext.put(VarExecutionContext.SNAPSHOT_DIR, workspaceFiles.get(VarExecutionContext.SNAPSHOT_DIR).getLocation());
		varsExecutionContext.put(VarExecutionContext.RELEASE_DIR, workspaceFiles.get(VarExecutionContext.RELEASE_DIR).getLocation());
		
		varsExecutionContext.put(VarExecutionContext.LABEL, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.LABEL.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.MULTIPUB, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.MULTIPUB.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.NOMPUB, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.NOMPUB.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.SOCLE_VERSION, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.SOCLE_VERSION.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.VERMIN, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.VERMIN.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.VERPUB, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.VERPUB.name().toLowerCase()));
		varsExecutionContext.put(VarExecutionContext.VERTECH, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.VERTECH.name().toLowerCase()));
		
		
		final PubCommunBuilderImpl.DescriptorImpl pubDescriptor = (PubCommunBuilderImpl.DescriptorImpl) Jenkins
				.getInstance().getDescriptor(PubCommunBuilderImpl.class);
		servCommuns = pubDescriptor.getServCommuns();
		
		final PropertiesInjecterImpl.DescriptorImpl descriptor = (PropertiesInjecterImpl.DescriptorImpl) Jenkins
				.getInstance().getDescriptor(PropertiesInjecterImpl.class);
		
		String propertiesConfPath = descriptor.getFileConf();
		
		ConfParser propertiesBS = new ConfParser(this.build, listener);
		
		ContentMapper propertiesFileContent = propertiesBS.extractFromPropertiesConfPath(propertiesConfPath);
		
		for(SectionLine line : propertiesFileContent.getContentOfSection(SectionTitle.DEPENDANCES_FAB)){				
			
			try {
				
				// TODO: Hook pour ne pas traiter le cas du Path variable dans les labels commerciaux à revoir avec CF !!!
				if(!line.getKey().startsWith(";")){
				
					dependances.add(new DependanceBuilder(line.getValue()).build());
				
				}
				
				
			} catch (DependancesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		for(Entry<SectionTitle, List<SectionLine>> sectionContent : propertiesFileContent.entrySet()){									
			
			for(SectionLine line : sectionContent.getValue()){
				
				parameters.add(new StringParameterValue(line.getKey(), line.getValue()));
			}
		
		}	
		
		String livPath = varsExecutionContext.get(VarExecutionContext.LIVRAISON_DIR);
		String pubPath = varsExecutionContext.get(VarExecutionContext.PUBLICATION_DIR);
		String communPath = varsExecutionContext.get(VarExecutionContext.COMMUN_DIR);
		
		parameters.addAll(extractEnvInfos(livPath, pubPath, communPath));
		
	}

	public BuildExecutionContext(final InProgressBuild pBuild, final ListenerBuild pListener) {
		this.build = pBuild;
		this.listener = pListener;				
	}
	
	public String getLivDirTarGzName() {
		return livDirTarGzName;
	}
	
	public String getPubDirTarGzName() {
		return pubDirTarGzName;
	}
	
	public boolean buildTarGzLivDir(){ 
		
        // VarEnvHelper.buildAll(this.getProgressBuild().getHudsonBuild(), this.getListenerBuild().getHudsonBuildListener());
        /**
         * if (!Result.SUCCESS.equals(this.getProgressBuild().getResult())) {
         * this.getListenerBuild().getLogger().println("Pas de création de TAR.GZ suite aux erreurs précédentes");
         * 
         * return false; }
         */

        //TODO: Cette initialisation ne sert à rien ?
        //this.liv = this.getDescriptor().getLivDir();
       
        String label = "HEAD";
        
        String livDir = getParameter(VarExecutionContext.CNQD_CVS_LIVREP);

        String labelO = getParameter(VarExecutionContext.CNQD_CVS_LABEL);

        if (null == labelO || labelO.toString().length() == 0) {
            labelO = getParameter(VarExecutionContext.LABEL);
        }

        label = labelO.toString();

        if (label.length() > 0 && !("HEAD".equalsIgnoreCase(labelO.toString()))) {
            label = labelO.toString();
        } else {
        	String tmpLabel = getParameter(VarExecutionContext.CNQD_CVS_LABEL, "TEMP");
        	String tmpModule = getParameter(VarExecutionContext.CNQD_CVS_MODULE, "TEMP");
        	
            getListener().getLogger().println(
                    "Pas de label défini ou archivage interdit sur HEAD, création d'un tar.gz temporaire pour test ("
                            + tmpModule+ "-" + tmpLabel  + ".tar.gz )");
            label = tmpModule + "-" +  tmpLabel;
        }
        
       	            
        final FilePath savelivPath = this.getBuild().getWorkspace();

        final FilePath livPath = this.getBuild().getHudsonBuild().getWorkspace().child(livDir);        
        try {
        if (livPath.exists()) {
              
         	String codenat=getParameter(VarExecutionContext.CNQD_CVS_CODENATURE);
         	String cvsModule = getParameter(VarExecutionContext.CNQD_CVS_MODULE);
         	livDirTarGzName = label + "_"+codenat+".tar.gz";
                      
            FilePath tarGz = savelivPath.child(livDirTarGzName);
            
           
            
            
            if (tarGz.exists()) {
                tarGz.delete();
            }

        	// Copie du properties.conf TODO verification existance et verification droit finaux des livrables;
            livPath.chmod(0777);
        	FilePath proConf = this.getBuild().getWorkspace().child(cvsModule).child("install").child("properties.conf"); 
        	proConf.copyTo(livPath.child("install").child("properties.conf"));
        	
            this.getListener().getLogger().println("Création de l'archive " + tarGz.getName());
         
            this.getBuild().getWorkspace().archive(hudson.util.io.ArchiverFactory.TARGZ,
                   // new FileOutputStream(targzDest),
                    tarGz.write(),
                    livDir + "/**/**");
            
            if(tarGz.exists() && !("HEAD".equalsIgnoreCase(labelO.toString())) ){
            	
				return true;
            	
            }

        } else {
            this.getListener().getLogger().println("[WARNING] : Le répertoire " + livDir + " n'existe pas, aucune livraison n'a été effectuée.");

        }
        
		 } catch (final FileNotFoundException pException) {
	            this.getListener().getLogger().println("ERROR Archivage tar.gz :  " + pException.getMessage());
	            pException.printStackTrace(this.getListener().getLogger());
	        } catch (final IOException pException) {
	            this.getListener().getLogger().println("ERROR Archivage tar.gz :  " + pException.getMessage());
	            pException.printStackTrace(this.getListener().getLogger());
	        } catch (final InterruptedException pException) {
	            this.getListener().getLogger().println("ERROR Archivage tar.gz :  " + pException.getMessage());
	            pException.printStackTrace(this.getListener().getLogger());
	        } 
		
		return false;
	}
	
	public boolean buildTarGzPubDir(){
		
		final WorkspaceFilePath pubDir = new WorkspaceFilePath(this.getWorkspace(),this.getParameter(VarExecutionContext.ARTI_COMMUN_DIR));
        if (pubDir.exists() && !pubDir.isEmpty()) {  
        	
             final String cvsLabel = getParameter(VarExecutionContext.CNQD_CVS_LABEL);
             
             pubDirTarGzName = cvsLabel + "_pub.tar.gz";             
           
             WorkspaceFilePath targzDestPub = new WorkspaceFilePath(this.getWorkspace(), pubDirTarGzName);                
            
            if (targzDestPub.exists()) {
                try {
					targzDestPub.delete();
				} catch (IOException e) {
					this.getListener().getLogger().println(e);
				} catch (InterruptedException e) {
					this.getListener().getLogger().println(e);
				}
            }
                            
            this.getListener().getLogger().println("Création de l'archive de publication " + targzDestPub.getLocation());

            pubDir.archiveTo(targzDestPub);
            
            return true;
        }
        
        this.getListener().fatalError("[ERROR] Erreur de déploiement dans artifactory");
        
        return false;
		
	}
		
	
		
	public void close(Reader reader){
		
		try {
			reader.close();
		} catch (IOException e) {			
			e.printStackTrace(listener.getLogger());
		}
		
	}	

	public List<ParameterValue> extractEnvInfos(											 
												final String livDir,
												final String pubDir,
												final String commonRepDir) {

							
		final Project upstreamProject = new Project(initUpstreamProject(build));
		
		Project rootProject = new Project(build.getProject().getRootProject());		
		
		if(upstreamProject != null && upstreamProject.isCVSProject() && !(rootProject.isCVSProject() )){		
			
			rootProject = new Project(upstreamProject);
		}
		
		dispLogForCVSSCMProject(
							rootProject, 
							upstreamProject,
							listener);
		

		if (rootProject.isCVSProject()) {
			
			varsExecutionContext.putAll(initCvsVarFromCvsJenkinsPlugin(listener));
			

		} else {
			
			Map<VarExecutionContext,String> map = findVarExecutionContextFromEnvAndBuildScope(lookupForVar);
			
			varsExecutionContext.putAll(initEmptyVarWithDefaultValues(map, build.getProject().getName(), lookupForVar));

			listener.getLogger().println(
					"Pas de configuration CVS sur type " + rootProject.getName()
							+ "!");
			
		}	
		
		final String labelCourt = lookupForVar.getValueForKeyInEnvAndBuildScope(varsExecutionContext.get(VarExecutionContext.CNQD_CVS_LABEL)).split("_")[0];
		final String modEval = lookupForVar.getValueForKeyInEnvAndBuildScope(varsExecutionContext.get(VarExecutionContext.CNQD_CVS_MODREP));
		final WorkspaceFilePath workspace = build.findWorkspace(modEval);
		final String codeNature = "A"; // @TODO arbitraire on ne fabrique que des appli pour l'instant
		
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_REFERENTIEL, lookupForVar.getValueForKeyInEnvAndBuildScope(varsExecutionContext.get(VarExecutionContext.CNQD_CVS_REFERENTIEL)));
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_LABEL, lookupForVar.getValueForKeyInEnvAndBuildScope(varsExecutionContext.get(VarExecutionContext.CNQD_CVS_LABEL)));				
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_LABEL_LONG, lookupForVar.getValueForKeyInEnvAndBuildScope(labelCourt));
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_MODULE, lookupForVar.getValueForKeyInEnvAndBuildScope(varsExecutionContext.get(VarExecutionContext.CNQD_CVS_MODULE)));		
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_MODREP, workspace.getLocation());
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_CODENATURE, codeNature);
		varsExecutionContext.put(VarExecutionContext.CNQD_CVS_LIVREP, livDir);
		varsExecutionContext.put(VarExecutionContext.CNQD_COMMON_REP, commonRepDir);
		varsExecutionContext.put(VarExecutionContext.CNQD_PUBLICATION, pubDir);
		varsExecutionContext.put(VarExecutionContext.CNQD_OSREFAB, "SOCLE_VERSION");
		varsExecutionContext.put(VarExecutionContext.USER_UL, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.USER_UL.toString()));
		
		
		lstParam.addAll(toListParameterValue(varsExecutionContext));

		dispExtractedVarExecutionContexts(lstParam, listener.getLogger());
		
		//this.buildAll();
		
		return lstParam;

	}
	
	private void dispExtractedVarExecutionContexts(List<ParameterValue> VarExecutionContexts, final PrintStream printStream){
		
		for (ParameterValue sVal : VarExecutionContexts) {
			printStream.println(
					"Valeurs en variable du module : " + sVal.getName() + " = "
							+ sVal.getValue().toString());

		}
		
	}
	
	private  List<ParameterValue> toListParameterValue(Map<VarExecutionContext, String> cnqdParams){
		
		List<ParameterValue> parameters = new ArrayList<ParameterValue>();		
		
		for(Entry<VarExecutionContext, String> cnqdParam: cnqdParams.entrySet()){
			
			parameters.add(new StringParameterValue(cnqdParam.getKey().name(), cnqdParam.getValue()));
			
		}
		
		return parameters;
		
	}

	private  void buildAll()throws IOException, InterruptedException {

		final EnvVars env = build.getHudsonBuild().getEnvironment(listener.getHudsonBuildListener());
		
		if (build.getHudsonBuild() instanceof AbstractBuild) {
			env.overrideAll(((AbstractBuild) build.getHudsonBuild()).getBuildVariables()); // Add
																			// in
																			// matrix
																			// axes..
		} else {
			// Abstract#getEnvironment(TaskListener) put build parameters to
			// environments, but Run#getEnvironment(TaskListener) doesn't.
			// That means we can't retrieve build parameters from WorkflowRun
			// as it is a subclass of Run, not of AbstractBuild.
			// We need expand build parameters manually.
			// See JENKINS-26694, JENKINS-30357 for details.
			for (final ParametersAction pa : build.getHudsonBuild()
					.getActions(ParametersAction.class)) {
				// We have to extract parameters manally as
				// ParametersAction#buildEnvVars
				// (overrides EnvironmentContributingAction#buildEnvVars)
				// is applicable only for AbstractBuild.
				for (final ParameterValue pv : pa.getParameters()) {
					pv.buildEnvironment(build.getHudsonBuild(), env);
				}
			}
		}
	}

	

	public  void dispLogForCVSSCMProject(
			Project rootProject, Project upstreamProject,
			final ListenerBuild listener) {

		if (!(rootProject.isCVSProject())) {
			listener.getLogger().println(
					"Pas de configuration CVS sur type " + rootProject.getName()
							+ "! Recherche projet parent...");
			if (null == upstreamProject
					|| !(upstreamProject.isCVSProject())) {
				listener.getLogger()
						.println(
								"Pas de projet parent ou de configuration CVS sur projet parent !");

			} else {
				listener.getLogger().println(
						"Configuration CVS sur type "
								+ upstreamProject.getName() + "...");				
			}
		}

	}

	public  Project initUpstreamProject(InProgressBuild build) {

		Project rootProject = build.getProject().getRootProject();

		Project upstreamProject = build.getProject()
				.getBuildingUpstream();

		if (rootProject.equals(upstreamProject)) {
			upstreamProject = build.getProject().getRootProject()
					.getBuildingUpstream();
		}

		return upstreamProject;

	}

	private  Map<VarExecutionContext,String> findVarExecutionContextFromEnvAndBuildScope(final VarExecutionContextSearcher lookupForVar){			
		
		Map<VarExecutionContext,String> map = new EnumMap<VarExecutionContext, String>(VarExecutionContext.class);
		
		for(VarExecutionContext VarExecutionContext : VarExecutionContext.values()){
			
			map.put(VarExecutionContext, lookupForVar.getValueForKeyInEnvAndBuildScope(VarExecutionContext.name()));
		}
		
		return map;
	}
	
	private  Map<VarExecutionContext,String> initCvsVarFromCvsJenkinsPlugin(final ListenerBuild listener){
		
		Map<VarExecutionContext,String> map = new EnumMap<VarExecutionContext, String>(VarExecutionContext.class);	
		
		CvsParser parser = new CvsParser(build, listener);
		
		map.put(VarExecutionContext.CNQD_CVS_REFERENTIEL, parser.getCVSRefName());

		map.put(VarExecutionContext.CNQD_CVS_MODREP, parser.getCVSModLocName());

		map.put(VarExecutionContext.CNQD_CVS_LABEL, parser.getCVSLabel()); 

		map.put(VarExecutionContext.CNQD_CVS_MODULE,parser.getCVSModuleName()); 

		return map;
	}
	
	
	private  StringParameterValue newStringParameterValue(BuildExecutionContext helper, String name, String value) throws IOException, InterruptedException{
		
		return helper.createParameterVal(name, value);
		
	}

	private  Map<VarExecutionContext, String> initEmptyVarWithDefaultValues(
			Map<VarExecutionContext, String> map, String name, VarExecutionContextSearcher searchVars) {
		
		String module = map.get(VarExecutionContext.CNQD_CVS_MODULE);
		
		if(module == null || module.length() == 0){
			map.put(VarExecutionContext.CNQD_CVS_MODULE, name);
		}
		
		map.put(VarExecutionContext.CNQD_COMMON_REP, searchVars.getValueForKeyInEnvAndBuildScope(map.get(VarExecutionContext.CNQD_CVS_MODULE)));
		
		return map;
	}

	/**
	 * Retrouve la valeur de la variable recherchée dans le scope
	 * système ou projet.
	 * @param pBuild
	 * @param pListener
	 * @param pKey
	 * @return la valeur associée au nom de la variable ou le nom de la variable si elle n'existe pas.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public  String getValueForVariable(final InProgressBuild pBuild,
			final ListenerBuild pListener, final String pKey)
			throws IOException, InterruptedException {
		// Si var type Jenkins on recupere le nom reel
		// Si la valeur de retour contient une variable d'env -> remplacement
		// par sa valeur		
		
		VarExecutionContextSearcher searchVars = new VarExecutionContextSearcher(pBuild.getEnvironment(pListener), pBuild.getBuildVariableResolver());
		
		String modEval = searchVars.getValueForKeyInEnvAndBuildScope(pKey);

		 //pBuild est toujours de type AbstractBuild

		//debut - @useless : @aurel : ce code ci-dessous ne sert à rien 
		 final EnvVars env = pBuild.getEnvironment(pListener);
		 env.overrideAll(pBuild.getBuildVariables());
		 //fin - @useless
		return modEval;

	}
	
	public int addParams(final List<ParameterValue> pParamsValue) {
		this.lstParam.addAll(pParamsValue);
		return this.lstParam.size();
	}

	/**
	 * @param pKey
	 * @param pValue
	 *            Création d'un parametre. Si la valeur est une valeur issue
	 *            d'une autre variable d'env on met sa valeur réelle pour eviter
	 *            une mauvaise gestion sur la suite
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public StringParameterValue createParameterVal(final String pKey,
			final String pValue) throws IOException, InterruptedException {
		
		//TODO: vérifier avec CF l'intérêt de cette ligne ci-dessous...
		//final String modEval = getValueForVariable(new InProgressBuild(this.build), new ListenerBuild(this.listener), pValue);
		return new StringParameterValue(pKey, pValue);
	}

	public List<ParameterValue> getLstParam() {
		return this.lstParam;
	}

	public void injectEnv() {

		this.build.getHudsonBuild().addAction(new ParametersAction(this.lstParam));// (build,envVar
																	// ));
																	// //lstParam));
	}

	@Override
	public InProgressBuild getBuild() {		
		return this.build;
	}


	@Override
	public ListenerBuild getListener() {
		return this.listener;
	}

	public void setParameters(List<ParameterValue> parameters) {
		this.parameters = parameters;
	}

	public List<Dependance> getDependances() {
		return dependances;
	}

	public void setDependances(List<Dependance> dependances) {
		this.dependances = dependances;
	}

	public void setBuild(InProgressBuild build) {
		this.build = build;
	}

	public void setListener(ListenerBuild listener) {
		this.listener = listener;
	}

	public void setLstParam(List<ParameterValue> lstParam) {
		this.lstParam = lstParam;
	}	
	
	public WorkspacePath getWorkspace() {
		return workspace;
	}
	
	public String getParameter(VarExecutionContext varContext){
		return varsExecutionContext.get(varContext);
	}
	
	public String getParameter(VarExecutionContext varContext, String defaultValue){
		String value = varsExecutionContext.get(varContext);
		
		if(value == null || value.isEmpty()){
			return defaultValue;
		}
		
		return value;
	}
	
	public String getServCommuns(){
		return this.servCommuns;
	}	

}