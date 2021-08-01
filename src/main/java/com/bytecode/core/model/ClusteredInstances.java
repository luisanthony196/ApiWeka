package com.bytecode.core.model;

import java.util.ArrayList;
import java.util.List;

import com.bytecode.core.model.units.Instance;

public class ClusteredInstances {
    int n_clusters;
    String[] atributos;
    List<Instance> instances;

    public ClusteredInstances() {
        instances= new ArrayList<Instance>();
    }

    public void addInstance(int indice, String[] attr, int cluster){
        this.instances.add(new Instance(indice, attr[0], attr[1], cluster));
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

    public List<Instance> getInstances() {
        return this.instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }
}
