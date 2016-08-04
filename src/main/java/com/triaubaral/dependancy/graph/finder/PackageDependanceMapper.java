package com.triaubaral.dependancy.graph.finder;

import com.triaubaral.dependancy.graph.reader.PackageDeclaration;

public class PackageDependanceMapper implements DependanceMapper<PackageDeclaration, PackageDeclaration> {

	private PackageDeclaration source;
	private PackageDeclaration destination;	
	
	public PackageDependanceMapper(PackageDeclaration source,
			PackageDeclaration destination) {
		super();
		this.source = source;
		this.destination = destination;
	}

	@Override
	public PackageDeclaration getSource() {
		return source;
	}

	@Override
	public PackageDeclaration getDestination() {
		return destination;
	}

	@Override
	public String toString() {
		return "PackageMapper [source=" + source + ", destination="
				+ destination + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PackageDependanceMapper other = (PackageDependanceMapper) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}

	


}
