package com.triaubaral.dependancy.graph.link;

import com.triaubaral.dependancy.graph.reader.AbstractDeclaration;

public class Relation {
	
	private Sens sens;
	private Type type;	
	private AbstractDeclaration source;
	private AbstractDeclaration destination;
	
	public Relation(Sens sens, Type type, AbstractDeclaration source,
			AbstractDeclaration destination) {
		super();
		this.sens = sens;
		this.type = type;
		this.source = source;
		this.destination = destination;
	}

	public Sens getSens() {
		return sens;
	}

	public void setSens(Sens sens) {
		this.sens = sens;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public AbstractDeclaration getSource() {
		return source;
	}

	public void setSource(AbstractDeclaration source) {
		this.source = source;
	}

	public AbstractDeclaration getDestination() {
		return destination;
	}

	public void setDestination(AbstractDeclaration destination) {
		this.destination = destination;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((sens == null) ? 0 : sens.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Relation other = (Relation) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (sens != other.sens)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Relation [sens=" + sens + ", type=" + type + ", source="
				+ source + ", destination=" + destination + "]";
	}
	
	

}
