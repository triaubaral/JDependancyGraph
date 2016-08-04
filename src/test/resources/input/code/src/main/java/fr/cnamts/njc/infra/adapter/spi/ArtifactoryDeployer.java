package fr.cnamts.njc.infra.adapter.spi;

import edu.emory.mathcs.backport.java.util.Collections;
import fr.cnamts.njc.domain.bo.project.PropArtiBean;
import fr.cnamts.njc.domain.exception.DependancesException;
import fr.cnamts.njc.domain.inter.spi.Deployable;
import fr.cnamts.njc.domain.inter.spi.Initializable;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.InProgressBuild;
import fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins.ListenerBuild;
import fr.cnamts.njc.infra.artifactory.client.CnamtsBuildInfoDeployer;
import hudson.FilePath;
import hudson.model.Cause;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jenkins.model.Jenkins;
import jenkins.util.BuildListenerAdapter;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.jenkinsci.remoting.RoleChecker;
import org.jfrog.build.api.Artifact;
import org.jfrog.build.api.BuildInfoFields;
import org.jfrog.build.api.builder.ArtifactBuilder;
import org.jfrog.build.api.util.FileChecksumCalculator;
import org.jfrog.build.client.DeployDetails;
import org.jfrog.build.client.ProxyConfiguration;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryBuildInfoClient;
import org.jfrog.build.extractor.clientConfiguration.client.ArtifactoryDependenciesClient;
import org.jfrog.build.extractor.clientConfiguration.util.DependenciesDownloader;
import org.jfrog.build.extractor.clientConfiguration.util.PublishedItemsHelper;
import org.jfrog.hudson.ArtifactoryServer;
import org.jfrog.hudson.CredentialsConfig;
import org.jfrog.hudson.ArtifactoryBuilder;
import org.jfrog.hudson.action.ActionableHelper;
import org.jfrog.hudson.util.BuildUniqueIdentifierHelper;
import org.jfrog.hudson.util.ExtractorUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ArtifactoryDeployer implements Deployable {

    private static class FilesDeployerCallable implements FilePath.FileCallable<List<Artifact>> {
    
        private static final long serialVersionUID = 8139176444924465668L;       
        private ArrayListMultimap<String, String> buildProperties;       
        private ListenerBuild listener;
        private Multimap<String, String> patternPairs;
        private final String repositoryKey;
        private ArtifactoryServer server;
        private CredentialsConfig creds;
        private ProxyConfiguration proxyConfiguration;      

        private FilesDeployerCallable(ListenerBuild pListener, ArtifactoryServer pServer,
                Multimap<String, String> pPairs,
                CredentialsConfig pCreds, String pRepository,
                ArrayListMultimap<String, String> pGetbuildPropertiesMap, ProxyConfiguration pProxyConfiguration) {
            this.server = pServer;
            this.listener = pListener;
            this.patternPairs = pPairs;
            this.creds = pCreds;
            this.repositoryKey = pRepository;
            this.proxyConfiguration=pProxyConfiguration;
            this.buildProperties = pGetbuildPropertiesMap;
        }

        private Set<DeployDetails> buildDeployDetailsFromFileEntry(Map.Entry<String, File> fileEntry)
                throws IOException {
            final Set<DeployDetails> result = Sets.newHashSet();
            final String targetPath = fileEntry.getKey();
            final File artifactFile = fileEntry.getValue();
            String path = PublishedItemsHelper.calculateTargetPath(targetPath, artifactFile);
            path = StringUtils.replace(path, "//", "/");
           
            Map<String, String> checksums = Maps.newHashMap();
            try {
                checksums = FileChecksumCalculator.calculateChecksums(artifactFile, "SHA1", "MD5");
            } catch (final NoSuchAlgorithmException e) {
                this.listener.getLogger().println("Impossible de calculer un checksum " + "SHA1" + " ou " + "MD5");
            }
            final DeployDetails.Builder builder = new DeployDetails.Builder()
                    .file(artifactFile)
                    .artifactPath(path)
                    .targetRepository(repositoryKey)
                    .md5(checksums.get("MD5")).sha1(checksums.get("SHA1"))
                    .addProperties(buildProperties);
            result.add(builder.build());

            return result;
        }

        private Multimap<String, File> buildTargetPathToFiles(File workspace) throws IOException {
            Multimap<String, File> result = HashMultimap.create();
            for (Map.Entry<String, String> entry : patternPairs.entries()) {
                String pattern = entry.getKey();
                String targetPath = entry.getValue();
                Multimap<String, File> publishingData = PublishedItemsHelper.buildPublishingData(workspace, pattern,
                        targetPath);
                if (publishingData != null) {
                    listener.getLogger().println(
                            "For pattern: " + pattern + " " + publishingData.size() + " artifacts were found");
                    result.putAll(publishingData);
                } else {
                    listener.getLogger().println("For pattern: " + pattern + " no artifacts were found");
                }
            }

            return result;
        }

        @Override
        public void checkRoles(RoleChecker pArg0) throws SecurityException {
            // TODO Auto-generated method stub
            //
        }

        private List<Artifact> convertDeployDetailsToArtifacts(Set<DeployDetails> details) {
            List<Artifact> result = Lists.newArrayList();
            for (DeployDetails detail : details) {
                String ext = FilenameUtils.getExtension(detail.getFile().getName());
                Artifact artifact = new ArtifactBuilder(detail.getFile().getName()).md5(detail.getMd5())
                        .sha1(detail.getSha1()).type(ext).build();
                result.add(artifact);
            }
            return result;
        }

        public void deploy(ArtifactoryBuildInfoClient client, Set<DeployDetails> artifactsToDeploy)
                throws IOException {
            for (DeployDetails deployDetail : artifactsToDeploy) {
                StringBuilder deploymentPathBuilder = new StringBuilder(server.getUrl());
                deploymentPathBuilder.append("/").append(repositoryKey);
                if (!deployDetail.getArtifactPath().startsWith("/")) {
                    deploymentPathBuilder.append("/");
                }
                deploymentPathBuilder.append(deployDetail.getArtifactPath());
                listener.getLogger().println("Deploying artifact: " + deploymentPathBuilder.toString());
                client.deployArtifact(deployDetail);
            }
        }

        public List<Artifact> invoke(File workspace, VirtualChannel channel) throws IOException, InterruptedException {
            Multimap<String, File> targetPathToFilesMap = buildTargetPathToFiles(workspace);
            Set<DeployDetails> artifactsToDeploy = Sets.newHashSet();
            for (Map.Entry<String, File> entry : targetPathToFilesMap.entries()) {
                artifactsToDeploy.addAll(buildDeployDetailsFromFileEntry(entry));
            }
            ArtifactoryBuildInfoClient client = this.server.createArtifactoryClient(creds.getUsername(),
                    creds.getPassword(),
                   proxyConfiguration);

            try {
                deploy(client, artifactsToDeploy);
                List<Artifact> deployed = convertDeployDetailsToArtifacts(artifactsToDeploy);

                return deployed;
            } finally {
                client.shutdown();
            }
        }
    }

    protected ArtifactoryServer artifactoryServer;
    private InProgressBuild build;
    protected ArtifactoryDependenciesClient clientArtifactory;
    protected ArtifactoryBuilder.DescriptorImpl descriptor;
    protected DependenciesDownloader downloader;
   
    private List<String> hashPairs;
    private ArrayListMultimap<String, String> hashProperties;

    protected ListenerBuild listener;
    protected Initializable context;
  

    public ArtifactoryDeployer(Initializable context) {
        this.build = (InProgressBuild)context.getBuild();
        this.listener = (ListenerBuild)context.getListener();
        this.context = context;

        // Utilisation du plugin jenkins artifactory, verification de sa diponibilité
        if (Jenkins.getInstance().getPlugin("artifactory") == null) {
            this.listener.fatalError("Le plugin Artifactory n'est pas présent sur jenkins !");
        } else {

            this.hashPairs = new ArrayList<String>();
            // descriptor du plugin artifactory
            this.descriptor = (ArtifactoryBuilder.DescriptorImpl)
                    Jenkins.getInstance().getDescriptor(ArtifactoryBuilder.class);

        }

    }
    
    public Initializable getContext() {
		return context;
	}

    /**
     * @param pPath
     *            chemin local du fichier
     * @param pTarPub
     *            chemin distant (hors repo)
     * @return
     */
    public List<String> addFileToDeploy(final String pPath, final String pTarPub) {

        String deployPattern = pTarPub + "=>" + pPath;
        // deployPattern = StringUtils.replace(deployPattern, "\r\n", "\n");
        // deployPattern = StringUtils.replace(deployPattern, ",", "\n");
        this.hashPairs.add(deployPattern);
        return this.hashPairs;

    }
    
    public void closeClient() {
        this.clientArtifactory.shutdown();
    }
    
    private List<Artifact> getDeployedArtifacts(Multimap<String, String> pairs, CredentialsConfig cred, String pRepository){
    	
    	try {
			return build.getWorkspace().act(
			        new FilesDeployerCallable(listener, artifactoryServer, pairs, cred, pRepository,
			                getbuildPropertiesMap(),   artifactoryServer.createProxyConfiguration(Jenkins.getInstance().proxy)));
		} catch (IOException e) {
			this.context.getListener().getLogger().println(e);
		} catch (InterruptedException e) {
			this.context.getListener().getLogger().println(e);
		}
    	
    	return Collections.emptyList();
    }

    /**
     * @param pPath
     *            Chemin à appliquer dans l'outil de dépendances
     * @param pTarPub
     *            nom du tar.gz à publier
     * @param pRepository
     *            repository à utiliser dans artifactory
     * @param pWithBuildInfo
     * @return boolean
     * @throws IOException
     * @throws InterruptedException
     * @throws NoSuchAlgorithmException
     */
    public boolean deployDependance(final String pRepository, final boolean pWithBuildInfo) {

        boolean wasDeployed = false;
        //
        // // Création de la liste des artefacts à déployer
        // final Multimap<String, File> targetPathToFilesMap = this.buildTargetPathToFiles(
        // this.build.getWorkspace());
        //
        // final Set<DeployDetails> artifactsToDeploy = Sets.newHashSet();

        // for (final Map.Entry<String, File> entry : targetPathToFilesMap.entries()) {
        // artifactsToDeploy.addAll(this.buildDeployDetailsFromFileEntry(entry, pRepository));
        // }

        if (this.descriptor != null) {
            // récupération des informations sur les serveurs artifactory
            // Si il y en a plusieurs, on deploiera sur chacuns d'eux si il ont le repository attendu
            final List<ArtifactoryServer> lstArt = this.descriptor.getArtifactoryServers();

            for (final ArtifactoryServer artifactoryServer : lstArt) {

                final CredentialsConfig cred = artifactoryServer.getDeployerCredentialsConfig();

                final List<String> lstRepo = artifactoryServer.getLocalRepositoryKeys(cred.getCredentials(build.getHudsonBuild().getParent()));

                if (lstRepo.contains(pRepository)) {
                    this.listener.getLogger().println(pRepository + " trouvé pour déploiement");

                    final StringBuffer bufferS = new StringBuffer();
                    for (final String hasP : this.hashPairs) {
                        bufferS.append(hasP.trim()).append('\n');
                    }

                    final Multimap<String, String> pairs = PublishedItemsHelper
                            .getPublishedItemsPatternPairs(bufferS.toString());
                    List<Artifact> deployed = getDeployedArtifacts(pairs, cred, pRepository);

                    wasDeployed = !deployed.isEmpty();
                    // wasDeployed = this.deploy(client, artifactsToDeploy, artifactoryServer.getUrl(),
                    // pRepository);

                    if (wasDeployed && pWithBuildInfo) {
                        // Enregistrement des données du build dans artifactory
                        ArtifactoryBuildInfoClient client = artifactoryServer
                                .createArtifactoryClient(cred.getUsername(),
                                        cred.getPassword(),
                                        artifactoryServer.createProxyConfiguration(Jenkins.getInstance().proxy));
                        try {

                            final CnamtsBuildInfoDeployer deployer = new CnamtsBuildInfoDeployer(client,
                                    artifactoryServer,
                                    this.build, this.listener, deployed);
                            // this.convertDeployDetailsToArtifacts(artifactsToDeploy));
                            deployer.deploy();
                        } finally {
                            client.shutdown();
                        }
                    }

                } else {
                    this.listener.getLogger().println(
                            pRepository + " non trouvé pour déploiement sur " + artifactoryServer.getUrl());

                }
            }

        }

        return wasDeployed;
    }

    private ArrayListMultimap<String, String> getbuildPropertiesMap() throws IOException, InterruptedException {
        final ArrayListMultimap<String, String> properties = ArrayListMultimap.create();
        properties.putAll(this.getHashProperties());
        properties.put("build.socle", this.build.getEnvironment(this.listener).get("SOCLE_VERSION").toLowerCase());
        properties.put("build.name", BuildUniqueIdentifierHelper.getBuildName(this.build.getHudsonBuild()));
        properties.put("build.number", BuildUniqueIdentifierHelper.getBuildNumber(this.build.getHudsonBuild()));
        properties.put("build.timestamp", this.build.getTimestamp().getTime().getTime() + "");
        final Cause.UpstreamCause parent = ActionableHelper.getUpstreamCause(this.build.getHudsonBuild());
        if (parent != null) {
            properties.put("build.parentName", ExtractorUtils.sanitizeBuildName(parent.getUpstreamProject()));
            properties.put("build.parentNumber", parent.getUpstreamBuild() + "");
        }
        final String revision = ExtractorUtils.getVcsRevision(this.build.getEnvironment(this.listener));
        if (StringUtils.isNotBlank(revision)) {
            properties.put(BuildInfoFields.VCS_REVISION, revision);
        }

        // addMatrixParams(properties);

        return properties;
    }

    public ArrayListMultimap<String, String> getHashProperties() {
        return this.hashProperties;
    }

    /**
     * Crée un client pour acces à artifactory Balaye la liste des serveur definis dans le plugin artifactory et
     * retourne le premier qui contient le repository demandé
     * 
     * @param pRepository
     * @return
     * @throws DependancesException
     */
    public ArtifactoryDependenciesClient initClient(final String pRepository) throws DependancesException {

        final List<ArtifactoryServer> lstArt = this.descriptor.getArtifactoryServers();

        // for (ArtifactoryServer artifactoryServer : lstArt) {
        this.artifactoryServer = lstArt.get(0); // on prend la premiere configuration, à revoir si on en utilise
                                                // plusieurs
        final CredentialsConfig cred = this.artifactoryServer.getDeployerCredentialsConfig();
        final List<String> lstRepo = this.artifactoryServer.getLocalRepositoryKeys(cred.getCredentials(build.getHudsonBuild().getParent()));

        if (lstRepo.contains(pRepository)) {

            this.listener.getLogger().println(pRepository + " trouvé pour recherche");

            this.clientArtifactory = this.artifactoryServer
                    .createArtifactoryDependenciesClient(cred.getUsername(),
                            cred.getPassword(),
                            this.artifactoryServer.createProxyConfiguration(Jenkins.getInstance().proxy),
                            BuildListenerAdapter.wrap(this.listener.getHudsonBuildListener())
                    );

        }
        // }
        if (null == this.clientArtifactory) {
            throw new DependancesException(
                    "[Error] Impossible de trouver un serveur artifactory contenant le repository " + pRepository);
        }
        return this.clientArtifactory;
    }

    public void setHashProperties(final ArrayListMultimap<String, String> hashProperties) {
        this.hashProperties = hashProperties;
    }

    protected PropArtiBean traiteResponseAsObject(final HttpResponse response, final PropArtiBean valueType,
            final String errorMessage)
            throws IOException
    {
        if (response.getStatusLine().getStatusCode() == 200) {
            final HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }

            InputStream content = null;
            try
            {
                content = entity.getContent();

                final JsonFactory jsonFactory = new JsonFactory();
                final ObjectMapper mapper = new ObjectMapper(jsonFactory);
                mapper.getSerializationConfig().setAnnotationIntrospector(new JacksonAnnotationIntrospector());
                mapper.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
                jsonFactory.setCodec(mapper);
                final JsonParser parser = jsonFactory.createJsonParser(content);

                final PropArtiBean localObject1 = parser.readValueAs(PropArtiBean.class);

                return localObject1;
            } finally
            {
                if (content != null) {
                    IOUtils.closeQuietly(content);
                }
            }
        }
        final HttpEntity httpEntity = response.getEntity();
        if (httpEntity != null) {
            IOUtils.closeQuietly(httpEntity.getContent());
        }
        throw new IOException(errorMessage + ": " + response.getStatusLine());
    }

	/**
	 * @return the artifactoryServer
	 */
	public ArtifactoryServer getArtifactoryServer() {
		return artifactoryServer;
	}
	
}
