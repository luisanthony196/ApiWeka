package com.bytecode.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytecode.core.repository.InstancesRepository;
import com.bytecode.core.utils.SimpleKMeansModify;

import java.util.HashMap;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.SimpleKMeanCluster;

import weka.core.Instances;

@Service
public class SimpleKMeanService {
    @Autowired
    InstancesRepository instancesRepository;

    SimpleKMeansModify skm;

    public void initService(HashMap<String, Object> params) {
        if (!params.containsKey("link"))
            params.put("link", "single");
        if (!params.containsKey("clusters"))
            params.put("clusters", 2);
        try {
            skm = new SimpleKMeansModify();
            // Se obtienen los datos
            Instances datos = instancesRepository.obtenerDatos();
            // Generacion del modelo
            skm.setNumClusters((int) params.get("clusters"));
            // Se procesan las instancias
            skm.buildClusterer(datos);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    public SimpleKMeanCluster toSimpleKMeansCluster() {
        try {
            return skm.toSimpleKMeansCluster();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return null;
    }

    public ClusteredInstances toClusteredInstances() {
        try {
            if (skm == null) {
                initService(new HashMap<String, Object>());
            }
            return skm.toClusteredInstances();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return null;
    }
}
