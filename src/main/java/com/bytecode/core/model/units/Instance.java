package com.bytecode.core.model.units;

public class Instance {
    int indice;
    String attr1;
    String attr2;
    int cluster;

    public Instance(int indice, String attr1, String attr2, int cluster) {
        this.indice = indice;
        this.attr1 = attr1;
        this.attr2 = attr2;
        this.cluster= cluster;
    }

    public int getIndice() {
        return this.indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getAttr1() {
        return this.attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return this.attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }

    public int getCluster() {
        return this.cluster;
    }

    public void setCluster(int cluster) {
        this.cluster = cluster;
    }
}
