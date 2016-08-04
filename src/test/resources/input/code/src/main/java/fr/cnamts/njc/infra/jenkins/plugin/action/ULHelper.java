package fr.cnamts.njc.infra.jenkins.plugin.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import fr.cnamts.njc.domain.bo.module.conf.SectionLine;
import fr.cnamts.njc.domain.bo.module.conf.SectionTitle;
import fr.cnamts.njc.domain.bo.module.conf.TypeURL;
import fr.cnamts.njc.domain.bo.project.ProjectDetail;
import fr.cnamts.njc.domain.bs.module.conf.mapper.PropertiesConfMapperBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.CeriseReaderBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.PropertiesConfReaderBS;
import fr.cnamts.njc.domain.bs.module.conf.reader.CeriseReaderBS.CVSPathElement;
import fr.cnamts.njc.domain.inter.api.ContentMapper;
import fr.cnamts.njc.domain.inter.api.ContentReader;
import fr.cnamts.njc.infra.adapter.api.BSFactory;
import fr.cnamts.njc.infra.cvsweb.module.conf.net.RemoteFile;
import fr.cnamts.njc.infra.cvsweb.module.conf.net.RemoteFileFactory;


public class ULHelper {


    public static ProjectDetail getUnitaryProjectDetail(final String pLabel) throws IOException, InterruptedException {
    	
    	ProjectDetail appBuildInfos = fillProjectDetailFromCerise(new ProjectDetail(), pLabel);
        
    	ContentMapper ulConfContentMap = getPropertiesConfMap(appBuildInfos.getCVSRef(), appBuildInfos.getCVSMod(), appBuildInfos.getCVSLab());
        
        List<String> ciblesDeRefab = findValuesOfSection(ulConfContentMap, SectionTitle.REFAB);        
                
        for(String cible : ciblesDeRefab){
        	appBuildInfos.addOsCible(cible.toLowerCase());
        }        
        
        return appBuildInfos;
    	
    }
    
    private static String getRemoteFileContent(TypeURL url, String...strings){
    	
    	RemoteFile remoteFile = RemoteFileFactory.INSTANCE.newRemoteFile(url, strings);
        
        String content = remoteFile.getContent();
        
        remoteFile.close();
        
        return content;
    }
    
    private static ProjectDetail fillProjectDetailFromCerise(ProjectDetail projectDetail, final String pLabel){
    	
    	CeriseReaderBS ceriseFileReader = new CeriseReaderBS();
    	
        String ceriseConfContent = getRemoteFileContent(TypeURL.CERISE_QUERY, pLabel);
        
        Map<CVSPathElement, String> ceriseInfo = ceriseFileReader.read(ceriseConfContent);
            
        String referentiel = ceriseInfo.get(CVSPathElement.REFERENTIEL);
        String module = ceriseInfo.get(CVSPathElement.MODULE);
        String label = ceriseInfo.get(CVSPathElement.LABEL);     
     
        try {
        	projectDetail.setCVSLab(label);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        projectDetail.setCVSMod(module);
        projectDetail.setCVSRef(referentiel); 
    	
        return projectDetail;
    }
    
    private static List<String> findValuesOfSection(ContentMapper ulConfContentMap,  SectionTitle targetedSection){
    	
    	List<String> values = new ArrayList<String>();   
         
         for(Entry<SectionTitle, List<SectionLine>> sectionContent : ulConfContentMap.entrySet()){
         	
         	if(targetedSection.equals(sectionContent.getKey())){                		
         		for(SectionLine value : sectionContent.getValue()){
         			values.add(value.getValue());
         		}
         	}
         }
         
         return values;
    }
    
    public static ContentMapper getPropertiesConfMap(String referentiel, String module, String label){
    	
    	ContentReader<ContentMapper, String> confReader = BSFactory.INSTANCE.newPropertiesConfReader();
    	
    	String ulConfContent = getRemoteFileContent(TypeURL.PROPERTIES_QUERY, referentiel, module, label);        
        
        return confReader.read(ulConfContent);
    	
    }
       
    
    public static ProjectDetail treeDependances(int pProfond, final String pLabel) {
        
        ProjectDetail appBuildInfos = fillProjectDetailFromCerise(new ProjectDetail(), pLabel);
        
        ContentMapper ulConfContentMap = getPropertiesConfMap(appBuildInfos.getCVSRef(), appBuildInfos.getCVSMod(), appBuildInfos.getCVSLab());
        
        List<String> ciblesDeRefab = findValuesOfSection(ulConfContentMap, SectionTitle.REFAB);        
        
        List<String> dependances = findValuesOfSection(ulConfContentMap, SectionTitle.DEPENDANCES_FAB);        
        
        for(String cible : ciblesDeRefab){
        	appBuildInfos.addOsCible(cible.toLowerCase());
        }
        
        pProfond++;
        
        appBuildInfos.setProfond(pProfond);
        
        for(String dependance : dependances){        	
          
        	ProjectDetail dependProject = treeDependances(pProfond, dependance.trim());
        	
        	appBuildInfos.addDepend(dependProject);
        }
        
        return appBuildInfos;
    }

}
