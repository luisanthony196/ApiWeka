package com.bytecode.core.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

@Repository
public class InstancesRepository {
	private Instances datos = null;
    protected String queryString;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    public Instances obtenerDatos() {
        if (isEmpty())
            llenarDatos();
        return datos;
    }

    public void llenarDatos() {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername(this.username);
            query.setPassword(this.password);
            query.setQuery(this.queryString);
            datos = query.retrieveInstances();
            System.out.println("Se llenaron los datos");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isEmpty() {
        if (datos == null)
            return true;
        if (datos.isEmpty())
            return true;
        return false;
    }

    public void setQuery(Object columns, Object query) {
        if (query == null) {
            if (columns == null) {
                this.queryString = null;
            } else {
                String temp = columns.toString().replaceAll("\\[|\\]", "");
                this.queryString = "select distinct " + temp + " from oferta where id_estado is null order by 1,2 limit 1000";
            }
        } else {
            if (this.queryString == null) {
                this.queryString = query.toString() + " limit 1000";
            } else if (!this.queryString.equals(query.toString())) {
                this.datos = null;
                this.queryString = query.toString() + " limit 1000";
            }
        }
    }

    // public void setColumns(String columns) {
    //     String temp = columns.replaceAll("\\[|\\]", "");
    //     if (this.columns == null) {
    //         this.columns = temp;
    //     } else if (!this.columns.equals(temp)){
    //         this.datos = null;
    //         this.columns = temp;
    //     }
    // }
}
