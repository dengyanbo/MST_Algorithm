package mst;
/*
 * (C) Copyright 2003-2017, by Barak Naveh and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.*;

import org.jgraph.*;
import org.jgraph.graph.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;
// resolve ambiguity
import org.jgrapht.graph.DefaultEdge;

/**
 * A demo applet that shows how to use JGraph to visualize JGraphT graphs.
 *
 * @author Barak Naveh
 * @since Aug 3, 2003
 */
public class Demo
    extends JApplet
{
	private static final int width = 1080;
	private static final int height = 720;
    private static final long serialVersionUID = 3256444702936019250L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(width, height);
    
    Graph graph;
    
    public Demo(Graph graph){
    	this.graph = graph;
    }

    //
    @SuppressWarnings("rawtypes")
	private JGraphModelAdapter<String, RelationshipEdge> jgAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        // create a JGraphT graph
    	@SuppressWarnings("rawtypes")
		ListenableGraph<String, RelationshipEdge> g =
                new ListenableDirectedMultigraph<String, RelationshipEdge>(RelationshipEdge.class);

        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<>(g);

        JGraph jgraph = new JGraph(jgAdapter);

        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);
        
        ArrayList<String> vertex = new ArrayList<>();
        for(Node n: graph.getNodes()){
        	vertex.add(n.getID());
        }

        // add some sample data (graph manipulated via JGraphT)
        System.out.println("number of vertices = " + vertex.size());
        for(String v: vertex){
        	System.out.println("vertex " + v);
        	g.addVertex(v);
        }

        System.out.println("number of edges = " + graph.getEdges().size());
        for(Edge edge: graph.getEdges()){
        	String node1ID = edge.getNode1().getID();
        	String node2ID = edge.getNode2().getID();
        	System.out.println("edge " + node1ID + "-" + node2ID + " with weight = " + edge.getWeight());
        	g.addEdge(node1ID, node2ID, new RelationshipEdge<String>(node1ID, node2ID, edge.getWeight()+""));
        }
        
        int row = 1;
        for(;row * row <= vertex.size(); ++row);
        int step = (width - 2*40) / (row);
        for(int i = 0, r = 0, c = 0; i < vertex.size(); ++i, ++r){
//        	System.out.println("r = "+r+" c= "+c);
        	positionVertexAt(vertex.get(i), (int) (40 + step * c + 10*Math.random()), (int) (40 + step * r + 10*Math.random()));
        	if( r >= row - 2 ){
        		r = -1;
        		c++;
        	}
        }
        

        // that's all there is to it!...
    }

    private void adjustDisplaySettings(JGraph jg)
    {
        jg.setPreferredSize(DEFAULT_SIZE);

        Color c = DEFAULT_BG_COLOR;
        String colorStr = null;

        try {
            colorStr = getParameter("bgcolor");
        } catch (Exception e) {
        }

        if (colorStr != null) {
            c = Color.decode(colorStr);
        }

        jg.setBackground(c);
    }

    @SuppressWarnings("unchecked")
    private void positionVertexAt(Object vertex, int x, int y)
    {
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds = new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }

    /**
     * A listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableDirectedMultigraph<V, E>
        extends DefaultListenableGraph<V, E>
    {
        private static final long serialVersionUID = 1L;

        ListenableDirectedMultigraph(Class<E> edgeClass)
        {
            super(new DirectedMultigraph<>(edgeClass));
        }
    }
}

class RelationshipEdge<V> extends DefaultEdge {
	private static final long serialVersionUID = 1L;
	
	private V v1;
    private V v2;
    private String label;

    public RelationshipEdge(V v1, V v2, String label) {
        this.v1 = v1;
        this.v2 = v2;
        this.label = label;
    }

    public V getV1() {
        return v1;
    }

    public V getV2() {
        return v2;
    }

    public String toString() {
        return label;
    }
}
// End JGraphAdapterDemo.java
