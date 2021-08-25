package com.bytecode.core.model.units;

import java.util.HashMap;

public class Instance {
    int indice;
    HashMap<String, String> attr;
    int cluster;

    public Instance(int indice, HashMap<String, String> attr, int cluster) {
        this.indice = indice;
        this.attr = attr;
        this.cluster= cluster;
    }

    public int getIndice() {
        return this.indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public HashMap<String, String> getAttr() {
		return this.attr;
	}

    public void setAttr(HashMap<String, String> attr) {
		this.attr = attr;
	}

    public int getCluster() {
        return this.cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
