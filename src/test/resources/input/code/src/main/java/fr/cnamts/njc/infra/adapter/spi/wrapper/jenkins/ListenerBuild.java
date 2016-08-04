package fr.cnamts.njc.infra.adapter.spi.wrapper.jenkins;

import java.io.PrintStream;

import fr.cnamts.njc.domain.inter.spi.ListenableItem;
import hudson.model.BuildListener;

public class ListenerBuild implements ListenableItem{
	
	private BuildListener listener;

	public ListenerBuild(BuildListener listener) {		
		this.listener = listener;
	}
	
	public PrintStream getLogger(){
		return listener.getLogger();
	}
	
	public BuildListener getHudsonBuildListener(){
		return listener;
	}
	
	public void fatalError(String msg){
		
		listener.fatalError(msg);
	}
	
	public void fatalError(String msg, Exception exception){
		
		listener.fatalError(msg, exception);
	}

	@Override
	public void error(String msg) {
		listener.error(msg);
	}

	
}
