package com.bytecode.core.model;

import java.util.ArrayList;
import java.util.List;

import com.bytecode.core.model.units.Cluster;

public class HierarchicalCluster {
    private int n_instancias;
    private int n_clusters;
    private List<Cluster> clusters;

    public HierarchicalCluster() {
        clusters = new ArrayList<Cluster>();
    }

    public void addCluster(int indice, String valor, int tamanio){
        double porcentaje = tamanio * 100.0 / n_instancias;
        this.clusters.add(new Cluster(indice, valor, tamanio, porcentaje));
    }

    public int getN_instancias() {
        return this.n_instancias;
    }

    public void setN_instancias(int n_instancias) {
        this.n_instancias = n_instancias;
    }

    public int getN_clusters() {
        return this.n_clusters;
    }

    public void setN_clusters(int n_clusters) {
        this.n_clusters = n_clusters;
    }

    public List<Cluster> getClusters() {
        return this.clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }
}
