package com.bytecode.core.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

@Repository
public class DataRepository {
	private Instances datos = null;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    public void llenarDatos() {
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername(this.username);
            query.setPassword(this.password);
            query.setQuery("select distinct htitulo_cat, htitulo from oferta where id_estado is null order by 1,2 limit 500");
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

    public Instances obtenerDatos() {
        return datos;
    }
}
