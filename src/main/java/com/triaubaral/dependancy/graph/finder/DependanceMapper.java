package com.triaubaral.dependancy.graph.finder;

public interface DependanceMapper <T, V>{

	T getSource();
	V getDestination();
	
}
