package mst;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GraphGenerator {
	ArrayList<Node> nodes = new ArrayList<>();
	ArrayList<Edge> edges = new ArrayList<>();
	
	public GraphGenerator(int n, int m){
		assert(n >=2 && m >= n-1);
		Edge firstEdge = new Edge("0","1", (int) (Math.random() * 20), nodes);
		edges.add(firstEdge);
			
		for(int i = 2; i < n; ++i){//build a spanning tree
			Node oneNode = nodes.get((int)(nodes.size() * Math.random() - 1));
			Edge newEdge = new Edge(oneNode, ""+i, (int) (Math.random() * 20), nodes);
			edges.add(newEdge);
		}
		for(int i = n; i <= m; ++i){//inserting edges to the spanning tree
			int rand1 = (int) (nodes.size() * Math.random() - 1);
			Node node1 = nodes.get(rand1);
			int rand2 = (int) (nodes.size() * Math.random() - 1);
			while(rand2 == rand1){rand2 = (int) (nodes.size() * Math.random() - 1);}
			Node node2 = nodes.get(rand2);
			boolean hasLink = false;
			for(Edge edge: node1.getEdges()){
				if(node2.getEdges().contains(edge)){
					hasLink = true;
					--i;
					break;
				}
			}
			if(!hasLink){
				edges.add(new Edge(node1, node2, (int) (Math.random() * 20), nodes));
			}
		}//end of inserting edges
	}
	
	public Graph getGraph(){return new Graph(this.nodes, this.edges);}
	
	public void saveFile(String filePath, String fileName){
		try {
			PrintWriter out = new PrintWriter(filePath+fileName);
			for(Node node: nodes){
				boolean first = true;
				out.print(node.getID()+"(");
				for(Edge edge: node.getEdges()){
					if(!first){
						out.print(",");
					}
					else{
						first = false;
					}
					Node nextNode = edge.getNext(node);
					out.print("("+nextNode.getID()+","+edge.getWeight()+")");
				}
				out.println("),");
			}
			out.flush();
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
