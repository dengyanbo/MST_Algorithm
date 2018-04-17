package mst;

import java.io.File;

import javax.swing.JFrame;

public class Main {
	final static String filePath = "C:\\Users\\dengy\\JAVAworkspace\\MST\\src\\graphs\\";
	final static boolean generate = true;
	
	public static void displayGraph(final Graph g, String title){
		Demo applet = new Demo(g);
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
	}
	
	public static void generateGraphs(int n, int nMax){
		for(int i = n; i<nMax; ++i){
			for(int j = i-1; j<i+5; ++j){
				GraphGenerator gg = new GraphGenerator(i,j);
				String fileName = "graph"+i+"_"+j+".txt";
				gg.saveFile(filePath, fileName);
			}
		}
	}

	public static void main(String[] args) {
		if(generate){
			generateGraphs(10, 15);
		}
		
		File dir = new File(filePath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
		  for (File file: directoryListing) {
			  Graph g = new Graph();
				g.createGraphFromFile(file);
				displayGraph(g, "Original Graph "+file.getName());
				System.out.println("\n**********Prim's**********");
				displayGraph(g.prim("0"), "Prim's MST for "+file.getName());
				System.out.println("\n**********Kruskal's**********");
				displayGraph(g.kruskal(), "Kruskal's MST for "+file.getName());
				System.out.println("\n**********Boruvka's**********");
				displayGraph(g.boruvka("0"), "Boruvka's MST for "+file.getName());
		  }
		}
		
	}

}
