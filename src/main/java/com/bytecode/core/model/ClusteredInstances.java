package com.bytecode.core.model;

import java.util.HashMap;

public class ClusteredInstances {
    int n_clusters;
    String[] atributos;
    HashMap<Integer, String[]> clusters;

    public ClusteredInstances(int n_clusters, String[] atributos, HashMap<Integer, String[]> clusters) {
        this.n_clusters = n_clusters;
        this.atributos = atributos;
        this.clusters = clusters;
    }

    public String[] getAtributos() {
        return this.atributos;
    }

    public void setAtributos(String[] atributos) {
        this.atributos = atributos;
    }

    public int getN_clusters() {
        return this.n_clusters;
    }

    public void setN_clusters(int n_clusters) {
        this.n_clusters = n_clusters;
    }

    public HashMap<Integer, String[]> getClusters() {
		return this.clusters;
	}

    public void setClusters(HashMap<Integer, String[]> clusters) {
		this.clusters = clusters;
	}

}
