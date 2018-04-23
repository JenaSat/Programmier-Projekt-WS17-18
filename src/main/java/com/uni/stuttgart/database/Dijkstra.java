package com.uni.stuttgart.database;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class Dijkstra{
	int nodes;
	int edges;
	
	//Look-Up Table um im Dijkstra Duplikaten aus dem Weg zu gehen
	int[] indexMap;
	
	//Queue fuer den Dijkstra
	//Abgespeichert werden Indizes der Knoten.
	PriorityQueue<Integer> prio = new PriorityQueue<Integer>(new DistanceComparator());

	// 0 - source, 1 - dest, 2 - cost
	int[][] graph = null;

	// speichert die distanzen zu den jeweiligen Knoten - i = id des knoten im
	// graphen
	private int[] distances;
	//Speichert die Vorgänger-Kanten: Eintrag entspricht Index i im Offset-Array, d.h. insbesondere liefert graph[0][i] den Vorgänger-Knoten
	int[] predecessor;
	
	@Autowired
	public Dijkstra(Graph graph) {
		this.graph = graph.getGraph();
		this.nodes = graph.getNodes();
		this.edges = graph.getEdges();
		
		this.distances = new int[nodes];
		this.indexMap = new int[nodes];
		//U.u. nicht so sinnvoll alle Einträge mit 0 zu initialisieren (Stichwort unerreichbare Knoten)
		this.predecessor = new int[nodes];
	}
	
	//Gibt einen Iterator zurück, der den Pfad [momentan] rückwärts abläuft, sofern einer existiert. Sonst null.
	public Stack<Integer> getPath(int source, int dest){
		
		if(this.distances[dest] == Integer.MAX_VALUE){
			return null;
		}
		int current = dest;
		int pathLength = 0;
		
		Stack<Integer> resultPath = new Stack<Integer>();

		while(current != source){
			resultPath.push(current);
			pathLength++;
			current = graph[0][predecessor[current]];
		}
		resultPath.push(source);
		resultPath.push(++pathLength);
		
		return resultPath;
	}
	
	public int getDistance(int node){
		return this.distances[node];
	}

	public int[] dijkstra(int source, int dest) {

		for (int i = 0; i < nodes; i++) {
			if (i != source) {
				this.distances[i] = Integer.MAX_VALUE;
			}
		}
		this.distances[source] = 0;
		prio.add(source);

		boolean found = false;
		
		while (!prio.isEmpty() && !found) {
			
			//int current = extractMin();
			int current =  prio.poll();
			
			//Check ob Duplikat eines bereits betrachteten Knotens
			if(indexMap[current] == -1){
				continue;
			}
					
			
			//Knoten besucht, Duplikate sollen nicht erneut betrachtet werden
			indexMap[current] = -1;
			
					
			if (current == dest) {
				//found = true;
				break;
			}
			
			// Distanz zum aktuellen Knoten
			int distToCurr = this.distances[current];
			
			//Kein Pfad zu diesem Knoten, deswegen ignorieren
			if (distToCurr == Integer.MAX_VALUE) {
				continue;
			}
			
					
			// liefert im offset-Array den edgeIdx für den aktuellen Knoten
			int offset = graph[3][current];
			

			// Pointer zum aktuell betrachteten Knotens
			int currentDest;

			// die zu aktualisierende Distanz der adjazenten Knoten
			int distToTrg;

			// starte beim offset (dort fangen die adjazenten Knoten des
			// aktuellen Knotens an, siehe Roupa-Folien, Folie 4) und betrachte
			// Zielknoten des aktuell betrachteten Knotens
			for (int i = offset; i < edges && graph[0][i] == current; i++) {
				
				// (eine der) Adjazenten Zielknoten des aktuell betrachteten
				// Knoten
				currentDest = graph[1][i];

				// die Kosten vom Startknoten bis zum aktuell betrachteteten
				// Zielknoten
				// graph[2][i] liefert die kosten für die jeweilige Kante
					distToTrg = distToCurr + graph[2][i];

					if (distToTrg < this.distances[currentDest]) {
						//Wenn Distanz aktualisiert, muss auch Position in der Prio-Qeue angepasst werden --> Neu einfügen
						this.distances[currentDest] = distToTrg;
						//Speichert die Vorgänger Kante zum aktuell betrachteten Knoten in Form des Offset-Index'
						this.predecessor[currentDest] = i;
						prio.add(currentDest);
					}

			}

		}
		
		return this.distances;

	}	
	
	//Comparator für die Prio-Queue
	//Vergleicht für zwei Einträge (das sind jeweils Indizes der Knoten) die Distanzen
	 class DistanceComparator implements Comparator<Integer>{

		@Override
		public int compare(Integer o1, Integer o2) {
			return Integer.compare(distances[o1], distances[o2]);

		}
		
	}
	 


}