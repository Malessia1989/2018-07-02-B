package it.polito.tdp.extflightdelays.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	Map<Integer, Airport> idMap;
	ExtFlightDelaysDAO dao;
	Graph<Airport, DefaultWeightedEdge> grafo;
	
	public Model() {
		dao= new ExtFlightDelaysDAO();
		idMap =new HashMap<Integer, Airport>();
		grafo= new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		dao.loadAllAirports(idMap);
	}
	
	

	public boolean isValid(String numInput) {
		

		return numInput.matches("\\d+");
		
	}

	public void creaGrafo(String numInput) {
		
		List<Rotta> rotte= dao.getRotte(idMap,numInput );
		for(Rotta r: rotte) {
			Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2());
		
			DefaultWeightedEdge edge= grafo.getEdge(r.getA1(), r.getA2());
			if(edge == null) {
				Graphs.addEdge(grafo, r.getA1(), r.getA2(), r.getPeso());
			}else {
				grafo.setEdgeWeight(edge, r.getPeso());
			}
		}
		
		System.out.println("vertici: " +grafo.vertexSet().size());
		System.out.println("vertici: " +grafo.edgeSet().size());
		for(DefaultWeightedEdge edge: grafo.edgeSet()) {
			System.out.println(edge +" peso: " +grafo.getEdgeWeight(edge));
		}
	
	}

	public Graph<Airport, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}

	public String getConnessi(Airport aInput) {
		
		List<Airport> vicini= Graphs.neighborListOf(grafo, aInput);
		Collections.sort(vicini, new Comparator<Airport>() {

			@Override
			public int compare(Airport o1, Airport o2) {
			
				DefaultWeightedEdge arco1= grafo.getEdge(aInput, o1);
				double peso1= grafo.getEdgeWeight(arco1);
				
				DefaultWeightedEdge arco2= grafo.getEdge(aInput,o2);
				double peso2= grafo.getEdgeWeight(arco2);
				
						
				return (int) (peso1-peso2);
			}
		});
		String ris="";
		for(Airport a: vicini) {
			DefaultWeightedEdge edge=grafo.getEdge(aInput, a);
			double peso = grafo.getEdgeWeight(edge);
			
			ris+=a.getAirportName() + " " + peso+ "\n";
		}
		return ris;
	}

}
