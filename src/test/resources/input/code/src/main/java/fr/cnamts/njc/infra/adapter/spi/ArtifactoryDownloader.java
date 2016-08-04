package fr.cnamts.njc.infra.adapter.spi;

import hudson.FilePath;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jfrog.build.api.dependency.PropertySearchResult;
import org.jfrog.build.api.dependency.PropertySearchResult.SearchEntry;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryDependenciesClient;
import org.jfrog.hudson.ArtifactoryServer;

import fr.cnamts.njc.domain.bo.build.VarExecutionContext;
import fr.cnamts.njc.domain.bo.module.Dependance;
import fr.cnamts.njc.domain.bo.project.PropArtiBean;
import fr.cnamts.njc.domain.exception.DependancesException;
import fr.cnamts.njc.domain.inter.spi.Downloadable;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.LivrableFilePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceFilePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspacePath;
import fr.cnamts.njc.infra.adapter.wrapper.jenkins.filepath.WorkspaceWorkingDirFactory;
import fr.cnamts.njc.infra.adapter.wrapper.rc.file.ServerFilePath;
import fr.cnamts.njc.infra.adapter.wrapper.rc.file.ServerTargetPath;
import fr.cnamts.njc.infra.artifactory.client.HttpClient;
import fr.cnamts.njc.infra.artifactory.finder.PropertyFinder;

public class ArtifactoryDownloader implements Downloadable {

    private final WorkspaceFilePath libPath;   
    private final WorkspacePath workspace;   
    private ListenerBuild listener;
    private ArtifactoryServer artiServer;
    private ArtifactoryDeployer artiHelper;
    private BuildExecutionContext context;
    
    public ArtifactoryDownloader(BuildExecutionContext context) {

    	final ArtifactoryDeployer artiHelper = new ArtifactoryDeployer(context);
      
    	this.context = (BuildExecutionContext) artiHelper.getContext();
    	this.artiHelper = artiHelper;
    	listener = (ListenerBuild)context.getListener(); 
        
        this.workspace = context.getWorkspace();
        this.libPath = new WorkspaceFilePath(workspace, context.getParameter(VarExecutionContext.COMMUN_DIR));
        try {
        	ArtifactoryDependenciesClient artiClient = artiHelper.initClient(this.context.getParameter(VarExecutionContext.ARTI_COMMUN_DIR));
        	artiServer = artiHelper.getArtifactoryServer();
        } catch (final DependancesException pException) {
        	listener.fatalError(pException.getMessage());
            pException.printStackTrace(listener.getLogger());
        }
    }
    
    public List<Dependance> findAndDownloadDependancesFromArtiCommun(List<Dependance> pLstDep){
    	
    	PropertyFinder propertiesFinder = new PropertyFinder(artiHelper, this.context.getParameter(VarExecutionContext.ARTI_COMMUN_DIR));
         
         List<Dependance> lstDep=new ArrayList<Dependance>();
         
         for (final Dependance dep : pLstDep) {
             
             try {
				lstDep.add(extractDependance(
				 		dep, 
				 		this.context.getParameter(VarExecutionContext.SOCLE_VERSION),
				             propertiesFinder.findDependance(dep),
				             this.context.getListener().getLogger()));
			} catch (DependancesException e) {				
				this.context.getListener().getLogger().println(e);
			} catch (IOException e) {
				this.context.getListener().getLogger().println(e);
			} catch (InterruptedException e) {
				this.context.getListener().getLogger().println(e);
			}

         }
         
         closeClient();
         
         return lstDep;
    	
    }

    private void untarAndDeleteLivrable(String nomComplet, final PrintStream printStream){
    	
    	LivrableFilePath livrable = new LivrableFilePath(workspace, nomComplet);
    	try {
			livrable.untar();
			livrable.delete();
		} catch (IOException e) {
			printStream.println(e);
		} catch (InterruptedException e) {
			printStream.println(e);
		}
    	
    	
    }
    
    private Dependance extractDependance(
    		final Dependance pDependance,
            final String pSocle,   
            final PropertySearchResult files,
            final PrintStream printStream) throws DependancesException, IOException, InterruptedException {
    	
    	final WorkspaceFilePath communDir = WorkspaceWorkingDirFactory.INSTANCE.resolve(VarExecutionContext.COMMUN_DIR, workspace);
         
        final WorkspaceFilePath tmpCommunPath = communDir.getChild(pDependance.getNomcomplet() + ".tmp");       
        
        pDependance.setLocalPath(tmpCommunPath.getLocation());  
                      
        Dependance tempDepend=this.getPubliedOnRepository(files, pDependance, pSocle);
        
        final WorkspaceFilePath tmpFilePath = new WorkspaceFilePath(this.context.getWorkspace(), tempDepend.getLocalPath());
       
        if (tmpFilePath.exists()) {        	
            // Dépendance trouvée dans le referentiel binaire, extraction dans le commun appli        	
        	untarAndDeleteLivrable(pDependance.getNomcomplet(), printStream);

 
        } else {
            
            
        	 createSymlinkToDependance(pSocle, pDependance.getNomcomplet(), printStream);
        }

        return tempDepend;

    }
    
    private void createSymlinkToDependance(String pSocle, String nomComplet, PrintStream printStream) throws IOException, InterruptedException, DependancesException{
    	
    	// Recherche dans le répertoire des publications type /refab_centrale/espace_communs
        final File tmpFile = this.getPubliedOnDir(pSocle, nomComplet);
        printStream.println(tmpFile.getAbsolutePath());
        if (tmpFile.exists()) {
        	printStream.println(
                    "Dépendance " + nomComplet + " trouvée dans le repertoire commun sous " + tmpFile.getAbsolutePath());

            final FilePath tmpDir = this.libPath.toFilePath();

            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
                
            }
           tmpDir.chmod(0777);
           FilePath dependance= tmpDir.child(nomComplet);
           if(!dependance.getParent().exists()){
        	   dependance.getParent().mkdirs();
           }                
            // Création d'un lien symbolique vers le commun trouvé
     
           dependance.symlinkTo(tmpFile.getAbsolutePath(), listener.getHudsonBuildListener());
          

        } else {

            throw new DependancesException("Impossible de trouver la dépendance " + nomComplet);
        }
        
    }

    /**
     * Recherche ordonnée de la dépendance dans le sas des communs commence par dn/multipub/ , puis dn/socle/, puis
     * cnamts/multipub/ et cnamts/socle/
     * 
     * @param pSocle
     *            le socle de fabrication (SOCLE_VERSION de la machine de refab)
     * @param commun
     *            LABEL-A du commun
     * @return le répertoire de dépendance
     */
    private File getPubliedOnDir(final String pSocle, final String commun) {
    	
    	for(ServerTargetPath targetPath : ServerTargetPath.values()){
    		
    		ServerFilePath tmpFile = new ServerFilePath(this.libPath.getLocation(), pSocle, commun);
    		
    		this.listener.getLogger().println(
                  "Recherche sur "+tmpFile.getAbsolutePath(targetPath));
    		
    		if(tmpFile.exists(targetPath)){
    			return tmpFile.getFile(targetPath);
    		}
    		
    	}
    	
    	return null;
    	

    }
    
    private boolean isLivrable(SearchEntry strFile){
    	return strFile.getFilePath().endsWith("tar.gz");
    }
    
    private List<SearchEntry> findSearchEntryByType(List<SearchEntry> entries, boolean isLivrable){
    	
    	List<SearchEntry> sortedEntries = new ArrayList<PropertySearchResult.SearchEntry>();
    	
    	for(SearchEntry entry : entries){
    		
    		if(isLivrable(entry) == isLivrable){
    			
    			sortedEntries.add(entry);
    			
    		}
    	}
    	
    	return sortedEntries;
    }
    
    private PropArtiBean queryForPropArtiBean(SearchEntry searchEntry) throws IOException{
    	
    	final String uriProp = searchEntry.getRepoUri() + "/" + searchEntry.getFilePath() + "?properties";
        listener.getLogger().println("Recherche de " + uriProp);
        
            final PropArtiBean result = artiHelper.traiteResponseAsObject(
                    // récupération sans authentification
            		HttpClient.executeGetRequest(uriProp, "", ""),
                    new PropArtiBean()
                    {
                    }
                    , "Erreur de recuperation des propriétés pour " + uriProp);
            
        return result;
    } 
    
    private boolean isPropArtiBeanEqualsToDepedance(PropArtiBean artiBean, Dependance depLocal){   
    	
    	final Map<String, String> elementsToCheck = new HashMap<String, String>();
        elementsToCheck.put("nompub", depLocal.getNompub());
        elementsToCheck.put("vermin", depLocal.getVermin());
    	
    	List<Boolean> result = new ArrayList<Boolean>(2);
    	
    	for(Entry<String,String> elementToCheck : elementsToCheck.entrySet()){
    		
    		String version = artiBean.getFirstPropertyForKey(elementToCheck.getKey());
    		
    		result.add(version.length() > 0 && version.equalsIgnoreCase(elementToCheck.getValue()));
    	}    	
    	 
    	 return result.get(0) && result.get(1);    	
    
    }
    
    private PropArtiBean newPropArtiBean(SearchEntry strFile, Dependance depLocal) throws IOException{    	
    	        
    	if (depLocal.isCommercial()){ 
    		
    		final PropArtiBean result = queryForPropArtiBean(strFile);
    		
	        if(isPropArtiBeanEqualsToDepedance(result, depLocal)) {
	            return result;                           
	        }
	        
    	}
        else{
        	final PropArtiBean objRet = new PropArtiBean();
            objRet.setUri(strFile.getRepoPath());
            
            return objRet;
        }    	
    	
    	return null;    	
    }
    
    private List<PropArtiBean> createPropArtiBeans(List<SearchEntry> pSetDep, Dependance depLocal, final String pSocle) throws IOException{
    	
    	final List<PropArtiBean> lstUri = new ArrayList<PropArtiBean>();
    	
    	final List<SearchEntry> livrables = findSearchEntryByType(pSetDep, true);           

        for (SearchEntry livrable : livrables) {
                
        	PropArtiBean result = newPropArtiBean(livrable, depLocal);
            
            if (result.isCompatibleWith(pSocle)) {
                lstUri.add(result);
            }            

        }
        
        return lstUri;    	
    	
    }
    
    private void downloadDependance(final Dependance depLocal, String uriDepend){
    	
    	listener.getLogger().println("Récupération de la dépendance " + depLocal.getNomcomplet() + "\n" + depLocal.getUrl() );
        try{
            HttpClient.downloadArchive(uriDepend,  new WorkspaceFilePath(this.context.getWorkspace(), depLocal.getLocalPath()).toFilePath());
        }catch(IOException pIoe){
            listener.fatalError("Récupération de la dépendance " + depLocal.getNomcomplet() + " impossible !", pIoe );
        } catch (InterruptedException pException) {
            listener.fatalError("Récupération de la dépendance " + depLocal.getNomcomplet() + " impossible !", pException );
            
       }
    }
    

    private Dependance getPubliedOnRepository(final PropertySearchResult files, final Dependance depLocal,
            final String pSocle)
            throws IOException {
    	
    	final List<SearchEntry> pSetDep = files.getResults(); 
       
        listener.getLogger().println("Vérification de l'existence de la dépendance " + depLocal.getNomcomplet() + " dans Artifactory" );

        if (pSetDep.size() > 0) {
        	
        	final List<PropArtiBean> lstUri = createPropArtiBeans(pSetDep, depLocal, pSocle);

            // le tri avec l'objet Propobj renvoie la liste ordonnée par prioritée, le premier est donc le prioritaire
            // ordre de priorité dn > cnamts puis multipub > st puis vertech TODO controler par rapport à l'UL
             if(!lstUri.isEmpty()){
                    
	            Collections.sort(lstUri);
	        
	            final PropArtiBean prioriaire = lstUri.get(0);
	            final String uriDepend = artiServer.getUrl().concat("/").concat(prioriaire.getUri());	            
	            depLocal.setPathToRepository(uriDepend);
	          
	            downloadDependance(depLocal, uriDepend);
	         
	            
            }else{
                listener.getLogger().println("Aucun resultat dans Artifactory correspondant à la recherche");
            }
            
        }else{
            listener.getLogger().println("Aucun resultat dans Artifactory ");
        }
        return depLocal;

    }
    
    private void closeClient(){
    	
    	artiHelper.closeClient();
    }

}