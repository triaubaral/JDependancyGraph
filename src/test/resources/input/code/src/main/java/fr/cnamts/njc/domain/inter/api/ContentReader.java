package fr.cnamts.njc.domain.inter.api;

public interface ContentReader<T,V> {
	T read(V data);
}
