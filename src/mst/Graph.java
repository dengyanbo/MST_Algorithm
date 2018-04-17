package mst;

import java.io.*;
import java.util.*;

//import org.jgrapht.alg.util.UnionFind;

public class Graph{
	final static int INFINITE = Integer.MAX_VALUE;
	
	ArrayList<Node> nodes = new ArrayList<>();
	ArrayList<Edge> edges = new ArrayList<>();
	ArrayList<Edge> Pmst = new ArrayList<>();
	ArrayList<Edge> Kmst = new ArrayList<>();
	ArrayList<Edge> Bmst = new ArrayList<>();
	
	public Graph(){}
	
	public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges){
		this.nodes = nodes;
		this.edges = edges;
	}
	
	public void createGraphFromFile(File file){
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			//read file line by line
		    for(String line; (line = br.readLine()) != null; ) {
		    	parseLine(line);
		    }
		    br.close();
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Graph boruvka(String startVertex){
		Set<Node> nodeSet = new HashSet<Node>();
		nodeSet.addAll(this.nodes);
		int forestNum = this.nodes.size();
		UnionFind<Node> UF = new UnionFind<>(nodeSet);
		while(forestNum > 1){
			for(Node node: nodes){
				Edge minEdge = null;
				Node nextNode = null;
				int minWeight = INFINITE;
				for(Edge edge: node.getEdges()){
					if(edge.getWeight() < minWeight){
						if(UF.find(node) == UF.find(edge.getNext(node))){
							continue;
						}else{
							minWeight = edge.getWeight();
							minEdge = edge;
							nextNode = edge.getNext(node);
						}
					}
				}
				if(minEdge != null && nextNode != null){
					System.out.println("union node: "+node.getID()+" & "+nextNode.getID()+" with weight = "+minEdge.getWeight());
					UF.union(node, nextNode);
					forestNum--;
					Bmst.add(minEdge);
				}
			}
		}
		return new Graph(this.nodes, this.Bmst);
	}
	
	public Graph kruskal(){
		Set<Node> nodeSet = new HashSet<Node>();
		nodeSet.addAll(this.nodes);
		UnionFind<Node> UF = new UnionFind<>(nodeSet);
//		UF.addElement( new Node("Base", null, nodes));
		PriorityQueue<minEdgeHeap> Edges = new PriorityQueue<minEdgeHeap>(this.edges.size(), 
				new Comparator<minEdgeHeap>() {
	        @Override
	        public int compare(minEdgeHeap h1, minEdgeHeap h2) {
	            return h1.getLabel() - h2.getLabel();
	        }
	    });
		for(Edge edge: edges){
			Edges.add(new minEdgeHeap(edge, edge.getWeight()));
		}
		int spanning = this.nodes.size() - 1;
		while(Edges.size() != 0 && Kmst.size() != spanning){
			minEdgeHeap minEdge = Edges.poll();
			Node node1 = minEdge.getEdge().getNode1();
			Node node2 = minEdge.getEdge().getNode2();
			
			if(UF.find(node1) == UF.find(node2)){
				continue;
			}			
			UF.union(node1, node2);
			Kmst.add(minEdge.getEdge());
		}
		return new Graph(this.nodes, this.Kmst);
	}
	
	public Graph prim(String startID){
		HashMap<Node, Edge> nodeLink = new HashMap<>();
		PriorityQueue<minHeap> Nodes = new PriorityQueue<minHeap>(this.nodes.size(), 
				new Comparator<minHeap>() {
	        @Override
	        public int compare(minHeap h1, minHeap h2) {
	            return h1.getLabel() - h2.getLabel();
	        }
	    });
		
		Node startNode = null;
		for(final Node node: this.nodes){
			if(node.getID().equals(startID)){
				startNode = node;
				Nodes.add(new minHeap(node, 0));
			}else{
				Nodes.add(new minHeap(node, INFINITE));
			}
		}
		if(startNode == null){
			System.err.println("Can not find the start node: " + startID);
			return null;
		}
		
		while(!Nodes.isEmpty()){
//			System.out.println("**\nnumber of Nodes remaining = "+Nodes.size());
			minHeap min = Nodes.poll();
			if(nodeLink.containsKey(min.getNode())){
				Pmst.add(nodeLink.get(min.getNode()));
			}
//			System.out.println("remove node: "+min.getNode().getID());
			
			for(minHeap h: Nodes){//for every node in the minHeap
//				System.out.println("Now come to node: "+h.getNode().getID());
				Node node = h.getNode();//current node
				for(Edge edge: min.getNode().getEdges()){//------------------------------------
					if(edge.getNext(min.getNode()) == node){//if it is connected to the removed node(minNode)
//						System.out.println("current edge: "+ edge.getNode1().getID() + "-" + edge.getNode2().getID());
//						System.out.println("node: "+node.getID()+" is connected to minNode");
						if(edge.getWeight() < h.getLabel()){//if its label need to be updated
							h.setLabel(edge.getWeight());//update the label
							nodeLink.put(node, edge);//store which edge provide the label
//							System.out.println("update node: "+node.getID()+" label: "+h.getLabel()+" with edge: " 
//									+ edge.getNode1().getID() + "-" + edge.getNode2().getID());
						}
					}
				}
			}//end of updating labels
			ArrayList<minHeap> minHeap = new ArrayList<>();
			for(minHeap h:Nodes){
				minHeap.add(h);
			}
			Nodes.clear();
			for(minHeap h:minHeap){
				Nodes.add(h);
			}
					
		}//end of iteration while
		return new Graph(this.nodes, this.Pmst);
	}

	
	public void parseLine(String line){
		State state = State.idxVertex;
		String node1ID = "";
		String node2ID = "";
		String temp = "";
		//read lines char by char
    	char[] Line = line.toCharArray();
    	temp = temp + Line[0];
    	for (int i = 1; i < Line.length; i++) {
    		if(Line[i] == ' ' || Line[i] == '\n' || Line[i] == '\t')
    			continue;
    		
    		switch(state){
    		case idxVertex:
    			if(Line[i] == '('){
    				node1ID = temp;
    				temp = "";
    				state = State.outLeftParen;
    			}else{
    				temp = temp + Line[i];
    			}
    			break;
    		case outLeftParen:
    			if(Line[i] == '('){
    				state = State.leftParen;
    			}else{
    				System.err.println("format error! There should be a ( but found a " + Line[i]);
    				return;
    			}
    			break;
    		case outRightParen:
    			if(Line[i] != ','){
    				System.err.println("Format error! There should be the end of line but found a " + Line[i]);
    				return;
    			}
    			break;
    		case leftParen:
    			temp = temp + Line[i];
    			state = State.vertex;
    			break;
    		case rightParen:
    			if(Line[i] == ')'){
    				state = State.outRightParen;
    			}else if(Line[i] == ','){
    				state = State.interComma;
    			}else{
    				System.err.println("Format error! There should be a ) or a , but found a " + Line[i]);
    				return;
    			}
    			break;
    		case comma:
    			assert(Character.isDigit(Line[i]));
    			temp = temp + Line[i];
    			state = State.weight;
    			break;
    		case interComma:
    			if(Line[i] == '('){
    				state = State.leftParen;
    			}else{
    				System.err.println("Format error! There should be a new ( but found a " + Line[i]);
    				return;
       			}
    			break;
			case vertex:
    			if(Line[i] == ','){
    				node2ID = temp;
    				temp = "";
    				state = State.comma;
    			}else{
    				temp = temp + Line[i];
    			}
    			break;
    		case weight:
    			if(Line[i] == ')'){
    				int weight = Integer.parseInt(temp);
    				temp = "";
    				switch (checkLinkCorrectness(node1ID, node2ID, weight)){
    				case linkCorrect:
	    				Node node1 = findNode(node1ID);
	    				Node node2 = findNode(node2ID);
	    				if(node1 == null && node2 == null){
//	    					System.out.println("node1: "+node1ID+" node2: "+node2ID);
	    					Edge edge = new Edge(node1ID, node2ID, weight, nodes);
	    					edges.add(edge);
	    				}else if(node1 == null && node2 != null){
//	    					System.out.println("node1: "+node1ID+" node2: "+node2ID);
	    					Edge edge = new Edge(node1ID, node2, weight, nodes);
	    					edges.add(edge);
	    				}else if(node1 != null && node2 == null){
//	    					System.out.println("node1: "+node1ID+" node2: "+node2ID);
	    					Edge edge = new Edge(node1, node2ID, weight, nodes);
	    					edges.add(edge);
	    				}else{
//	    					System.out.println("node1: "+node1ID+" node2: "+node2ID);
	    					Edge edge = new Edge(node1, node2, weight, nodes);
	    					edges.add(edge);
	    				}
	    				break;
					case linkError:
						System.err.println("Input data error!");
						return;
					case linkExisted:
						break;
					default:
						break;
    				}
    				state = State.rightParen;
    			}else{
    				assert(Character.isDigit(Line[i]));
    				temp = temp + Line[i];
    			}
    			break;
    		default:
    				System.err.println("Format error! Found a " + Line[i]);
    		}
    	}
	}

	public LinkCorrectness checkLinkCorrectness(String node1ID, String node2ID, int weight){
//		System.out.println("checking node1: "+node1ID+" node2: "+node2ID);
		if(node1ID.equals(node2ID)){
			System.err.println("Error self linked!");
			return LinkCorrectness.linkError;
		}
		for(Edge edge: edges){
			//System.out.println("edge with nodes " + edge.getNode1() +" and "+ edge.getNode2());
			if((edge.getNode1().getID().equals(node1ID) && edge.getNode2().getID().equals(node2ID)) || 
					(edge.getNode1().getID().equals(node2ID) && edge.getNode2().getID().equals(node1ID))){
				if(edge.getWeight() == weight){
					return LinkCorrectness.linkExisted;
				}else{
					System.out.println("Same edge has two different weight!");
					return LinkCorrectness.linkError;
				}
			}
		}
		return LinkCorrectness.linkCorrect;
	}
	
	public Node findNode(String nodeID){
		for(Node node: nodes){
			if(nodeID.equals(node.getID()))
				return node;
		}
		return null;
	}
	
	public ArrayList<Node> getNodes(){return nodes;}
	public ArrayList<Edge> getEdges(){return edges;}
	
	public enum State{
		idxVertex, outLeftParen, outRightParen, leftParen, rightParen, comma, vertex, weight, interComma
	}
	
	public enum LinkCorrectness{
		linkCorrect, linkError, linkExisted
	}
}

class minHeap{
	Node node;
	Integer label;
	public minHeap(Node node, Integer label){
		this.node = node;
		this.label = label;
	}
	public Node	getNode(){return this.node;}
	public Integer getLabel(){return this.label;}
	public void setLabel(Integer label){this.label = label;}
}

class minEdgeHeap{
	Edge edge;
	Integer label;
	public minEdgeHeap(Edge edge, Integer label){
		this.edge = edge;
		this.label = label;
	}
	public Edge	getEdge(){return this.edge;}
	public Integer getLabel(){return this.label;}
}

class Node {
	String nodeID;
	ArrayList<Edge> edges = new ArrayList<>();
	public Node(String nodeID, Edge edge, ArrayList<Node> nodes){
		this.nodeID = nodeID;
		edges.add(edge);
		nodes.add(this);
	}
	public String getID(){return this.nodeID;}
	public void addEdge(Edge edge){this.edges.add(edge);}
	public ArrayList<Edge> getEdges(){return this.edges;}
}

class Edge {
	Node node1, node2;
	int weight;
	public Edge(String node1ID, String node2ID, int weight, ArrayList<Node> nodes){
		this.node1 = new Node(node1ID, this, nodes);
		this.node2 = new Node(node2ID, this, nodes);
		this.weight = weight;
	}
	public Edge(Node node1, String node2ID, int weight, ArrayList<Node> nodes){
		this.node1 = node1;
		this.node1.addEdge(this);
		this.node2 = new Node(node2ID, this, nodes);
		this.weight = weight;
	}
	public Edge(String node1ID, Node node2, int weight, ArrayList<Node> nodes){
		this.node1 = new Node(node1ID, this, nodes);
		this.node2 = node2;
		this.node2.addEdge(this);
		this.weight = weight;
	}
	public Edge(Node node1, Node node2, int weight, ArrayList<Node> nodes){
		this.node1 = node1;
		this.node1.addEdge(this);
		this.node2 = node2;
		this.node2.addEdge(this);
		this.weight = weight;
	}
	public Edge(int weight){
		this.weight = weight;
	}
	public Node getNode1(){return this.node1;}
	public Node getNode2(){return this.node2;}
	public Node getNext(Node node){
		if(node == node1)
			return node2;
		else if(node == node2)
			return node1;
		else
			return null;
	}
	public int getWeight(){return this.weight;}
}
