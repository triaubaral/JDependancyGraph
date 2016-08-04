package com.triaubaral.dependancy.graph.draw;

import static org.livingdocumentation.dotdiagram.DotStyles.ASSOCIATION_EDGE_STYLE;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.livingdocumentation.dotdiagram.DotGraph;
import org.livingdocumentation.dotdiagram.DotWriter;
import org.livingdocumentation.dotdiagram.GraphvizDotWriter;
import org.livingdocumentation.dotdiagram.DotGraph.Digraph;

import com.triaubaral.dependancy.graph.link.Relation;

public class DrawEngine {

	public void drawRelations(List<Relation> newRelations) throws IOException, InterruptedException {
		
		final String testName = "simple";
		final DotGraph graph = new DotGraph(testName + " test");
		final Digraph digraph = graph.getDigraph();
		
		List<Relation> expectedResult = newRelations;
		
		for(Relation relation : expectedResult){
		
		digraph.addNode(relation.getSource()).setLabel(relation.getSource().getJavaInstruction());
		digraph.addNode(relation.getDestination()).setLabel(relation.getDestination().getJavaInstruction());
		digraph.addAssociation(relation.getSource(), relation.getDestination()).setOptions(ASSOCIATION_EDGE_STYLE);
		}
		
		final Properties p = new Properties();
		p.load(this.getClass().getResourceAsStream("/graphviz-dot.properties"));

		DotWriter writer = new GraphvizDotWriter(p);
		
		writer.toImage("toImage", digraph.render().trim());
		
	}

}
