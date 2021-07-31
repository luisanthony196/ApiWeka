package com.bytecode.core.model;

import java.util.ArrayList;
import java.util.List;

import com.bytecode.core.model.units.Cluster;

public class SimpleKMeanCluster {
    private int total;
    private String fullData;
    private List<Cluster> clusters;

    public SimpleKMeanCluster() {
        clusters = new ArrayList<Cluster>();
    }

    public void addCluster(int indice, String valor, int tamanio){
        double porcentaje = tamanio * 100.0 / total;
        this.clusters.add(new Cluster(indice, valor, tamanio, porcentaje));
    }

    public String getFullData() {
        return this.fullData;
    }

    public void setFullData(String fullData) {
        this.fullData = fullData;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Cluster> getClusters() {
        return this.clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }
}
