package com.uni.stuttgart.rest;

import java.util.Stack;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uni.stuttgart.database.*;

@RestController
public class Controller {
	

	private Graph graph;

	private Dijkstra dijkstra;
	
	private int[] distances;


	public void init(String path){
		this.graph = new Graph(path);
		this.dijkstra = new Dijkstra(graph);
		System.out.println(this.graph.getNodes());
	}

	
	@RequestMapping("/route")
	public double[][] sendRoute(@RequestParam int srcID, @RequestParam int destID){
		Stack<Integer> temp = dijkstra.getPath(srcID, destID);
		double[][] route = new double[(int) temp.pop()][1];
		int i = 0;
		int current;
		while(!temp.isEmpty()){
			current = (int) temp.pop();
			route[i] = graph.getLongLat(current);
			i++;
		}
		
		return route;
	}

	@RequestMapping("/shortestPath")
	public int shortestPath(@RequestParam int srcID, @RequestParam int destID){

		System.out.println("source: " + srcID);
		System.out.println("dest: " + destID);
		//gleicher src Knoten und Teil des alten Pfades --> Kein Neuberechnen nötig
		if(distances != null && distances[srcID] == 0 && destID != -1 && distances[destID] != Integer.MAX_VALUE) return distances[destID];
		dijkstra = new Dijkstra(graph);
		distances = dijkstra.dijkstra(srcID, destID);
		if(destID == -1) return 0;
		System.out.println("distance: " + distances[destID]);
		if(distances[destID] != Integer.MAX_VALUE) return distances[destID];
		return -1;
		
	}
	
	@RequestMapping("/neighbourID")
	public double[][] closestNeighbourAndID(@RequestParam double lat, @RequestParam double lng){
		double[][] result = new double [2][1];
		int neighbour = graph.getNeighbour(lat, lng);
		if(neighbour > -1){ 
			result[1][0] = neighbour;
			result[0] = graph.getLatLong(neighbour);
			return result;
		}
		return null;
	}
	
	
	@RequestMapping("/neighbour")
	public double[] closestNeighbour(@RequestParam double lat, @RequestParam double lng){
		int neighbour = graph.getNeighbour(lat, lng);
		if(neighbour > -1) return graph.getLatLong(neighbour);
		return null;
	}

	
	@RequestMapping("/nodeID")
	public int getNodeId(@RequestParam double lat, @RequestParam double lng){
		return graph.getNeighbour(lat, lng);
	}
	
	@RequestMapping("/nodeCoord")
	public double[] getNodeCoord(@RequestParam int nodeID){
		return graph.getLatLong(nodeID);
	}

}
