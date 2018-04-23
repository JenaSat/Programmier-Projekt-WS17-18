package com.uni.stuttgart.database;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class Graph {
	
	//number of nodes
	int nodes;
	
	//number of edges
	int edges;
	
	int[] source = {};
	int[] dest = {};
	int[] cost = {};
	int[] offset = {};
	int[][] graph = new int[4][1];
	double[] lat = {};
	double[] lng = {};

	
	@Autowired
	public Graph(String path){
		readFile(path);
		this.graph[0] = source;
		this.graph[1] = dest;
		this.graph[2] = cost;
		this.graph[3] = offset;
	}
	
	public int getNodes(){
		return this.nodes;
	}
	
	public int getEdges(){
		return this.edges;
	}
	
	public int[][] getGraph(){
		return this.graph;
	}
	
	
	//Gibt Koordinaten für speziellen Knoten zurück
	public double[] getLatLong(int node){
		if(node < 0  || node >= this.nodes) return null;
		double[] nodeLatLong = {this.lat[node], this.lng[node]};
		return nodeLatLong;
	}
	
	//Zusatzmethode für die Koord. für die geoJSON Route; geoJSON verwendet [lng, lat]
	public double[] getLongLat(int node){
		double[] nodeLongLat = {this.lng[node], this.lat[node]};
		return nodeLongLat;
	}
	
	//Ermittelt den nächsten Nachbar für eine Query mit Input latitude, longitude
	//Gibt Knoten zurück (Zugriff auf lat, long des Knotens über latLong[k], latLong[k+1]
	public int getNeighbour(double lat, double lng){
		double distance = Double.POSITIVE_INFINITY;
		double currDist;
		int position = -1;
		for(int k = 0; k < this.nodes; k++){
			currDist = (double) Math.sqrt(Math.pow((lat - this.lat[k]), 2) + Math.pow((lng - this.lng[k]), 2));
			if(currDist < distance){
				distance = currDist;
				position = k;
			}
		}
		return position;
	}
	

	

	
	//reads the file with the graph and fills the corresponding data structures and values
	public void readFile(String file){
		long startTimeReading = System.nanoTime();
		
		BufferedReader br = null;
		FileInputStream fr = null;

		try {

			String sCurrentLine;
			fr = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fr));

			//skippt die ersten 5 Zeilen
			for(int i = 0; i < 5; i++){
				br.readLine();
			}

			this.nodes = Integer.parseInt(br.readLine());
			this.edges = Integer.parseInt(br.readLine());

			this.source = new int[edges];
			this.dest = new int[edges];
			this.cost = new int[edges];
			this.offset = new int[nodes];
			this.offset[0] = 0;
			//Array mit den lat/long Werten der Knoten 
			this.lat = new double[this.nodes];
			this.lng = new double[this.nodes];



			int i = 0;
			int j = 0;

			int currPos = 0;
			int currVal = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] splitted = sCurrentLine.split(" ");
				

				// Trage lat/long Werte ein
				if(i < this.nodes){
					this.lat[i] = Double.parseDouble(splitted[2]);
					this.lng[i] = Double.parseDouble(splitted[3]);
					i++;
					continue;
				}
				
				if (splitted.length > 1 && i > this.nodes-1) {
					
					//Source-Einträge setzen
					this.source[j] = Integer.parseInt(splitted[0]);
					
					//offset berechnen anhand source Einträge:
					//Falls Pointer auf aktuellen Knoten mit source Eintrag übereinstimmt
					//offset Wert inkrementieren (Anm.: offset Wert des Folgeknotens!)
					if(this.source[j] == currPos){
						currVal++;
						
					//Falls keine Übereinstimmung:
					//Nach nächster passender Übereinstimmung suchen
					//Und solange auf Mismatch getroffen wird, für die
					//jeweiligen Knoten den offset auf den momentanen Wert setzen
					} else {
						currPos++;
						while(currPos != this.source[j]){
							this.offset[currPos] = currVal;
							currPos++;
						}
						this.offset[currPos] = currVal;
						currVal++;
					}
					//Dest-Einträge setzen
					this.dest[j] = Integer.parseInt(splitted[1]);
					//Kantengewicht-Einträge setzen
					this.cost[j] = Integer.parseInt(splitted[2]);
					j++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		
		long finishTimeReading = System.nanoTime();
		
		System.out.println("Read the file in: " + TimeUnit.MILLISECONDS.convert(finishTimeReading-startTimeReading, TimeUnit.NANOSECONDS) + " ms/ " + TimeUnit.SECONDS.convert(finishTimeReading-startTimeReading, TimeUnit.NANOSECONDS) + " second(s)");

	}
	
	
}

