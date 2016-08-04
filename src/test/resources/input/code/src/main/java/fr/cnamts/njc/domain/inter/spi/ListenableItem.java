package fr.cnamts.njc.domain.inter.spi;

import java.io.PrintStream;

public interface ListenableItem {
	
	PrintStream getLogger();
	void fatalError(String msg);
	void fatalError(String msg, Exception exception);
	void error(String msg);

}
