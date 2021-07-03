package com.bytecode.core.model;

public class Cluster {
    private int indice;
    private String valor;
    private int tamanio;
    private Double porcentaje;
    
    public Cluster(int indice, String valor, int tamanio, int total) {
        this.indice = indice;
        this.valor = valor;
        this.tamanio = tamanio;
        this.porcentaje = tamanio * 100.0 / total;
    }
    
    public int getIndice() {
        return this.indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getValor() {
        return this.valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public int getTamanio() {
        return this.tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

    public Double getPorcentaje() {
        return this.porcentaje;
    }

    public void setPorcentaje(Double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
