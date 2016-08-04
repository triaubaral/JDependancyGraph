package fr.cnamts.njc.infra.jenkins.plugin.action;

import fr.cnamts.njc.domain.bo.module.Dependance;
import hudson.model.Action;
import hudson.model.AbstractBuild;

import java.util.List;

/**
 * Created by prit8976 on 8/27/15.
 */
public class AffichageDependanceBuildAction implements Action {

    private final AbstractBuild<?, ?> build;
    private transient List<Dependance> dependances;

    public AffichageDependanceBuildAction(final List<Dependance> pDependances, final AbstractBuild<?, ?> build)
    {
        this.dependances = pDependances;
        this.build = build;
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    public int getBuildNumber() {
        return this.build.number;
    }

    public List<Dependance> getDependances() {
        return this.dependances;
    }

    @Override
    public String getDisplayName() {
        return "Affichage des dépendances";
    }

    @Override
    public String getIconFileName() {
        return "/plugin/testExample/img/build-goals.png";
    }

    @Override
    public String getUrlName() {
        return "cnamtsDepBA";
    }

    public void setDependance(final Dependance pDep) {
        
        if (this.dependances.contains(pDep)) {
            this.dependances.remove(this.dependances.indexOf(pDep));
        }
        this.dependances.add(pDep);
    }

    public void setDependances(List<Dependance> pLstDep) {
        dependances=pLstDep;
        
    }
}