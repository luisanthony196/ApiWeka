package com.bytecode.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusteredInstances {
    int n_clusters;
    String[] atributos;
    List<HashMap<String, Object>> instances;

    public ClusteredInstances() {
        instances= new ArrayList<HashMap<String, Object>>();
    }

    public void addInstance(int indice, String[] attr, int cluster){
        HashMap<String, Object> hp = new HashMap<>();
        hp.put("indice", indice);
        hp.put("atributos", attr);
        // for (int i = 0; i < attr.length; i++) {
        //     hp.put("attr" + i, attr[i]);
        // }
        hp.put("cluster", cluster);
        instances.add(hp);
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

    public List<HashMap<String, Object>> getInstances() {
		return this.instances;
	}

    public void setInstances(List<HashMap<String, Object>> instances) {
		this.instances = instances;
	}
}
