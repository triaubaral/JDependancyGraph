package com.triaubaral.dependancy.graph.reader;

public interface Reader <T, V>{
	T read(V content);
}
