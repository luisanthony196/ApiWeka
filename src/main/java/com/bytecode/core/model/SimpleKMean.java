package com.bytecode.core.model;

import com.bytecode.core.model.units.Cluster;

public class SimpleKMean {
    private int total;
    private String fullData;
    private Cluster[] grupo;

    public SimpleKMean(String fullData, int total, Cluster[] grupo) {
        this.fullData = fullData;
        this.total = total;
        this.grupo = grupo;
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

    public Cluster[] getGrupo() {
        return this.grupo;
    }

    public void setGrupo(Cluster[] grupo) {
        this.grupo = grupo;
    }
}
