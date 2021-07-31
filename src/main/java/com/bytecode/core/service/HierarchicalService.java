package com.bytecode.core.service;

import java.util.HashMap;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.HierarchicalCluster;
import com.bytecode.core.repository.InstancesRepository;
import com.bytecode.core.utils.HierarchicalModify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weka.core.Instances;

@Service
public class HierarchicalService {
    @Autowired
    InstancesRepository instancesRepository;

    HierarchicalModify hm;

    public void initService(HashMap<String, Object> params) {
        if (!params.containsKey("link"))
            params.put("link", "single");
        if (!params.containsKey("clusters"))
            params.put("clusters", 2);
        try {
            hm = new HierarchicalModify();
            // Se obtienen los datos
            Instances data = instancesRepository.obtenerDatos();
            hm.setLink((String) params.get("link"));
            hm.setNumClusters((int) params.get("clusters"));
            // Se procesan las instancias
            hm.buildClusterer(data);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    public HierarchicalCluster toHierarchicalCluster() {
        try {
            return hm.toHierarchicalCluster();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return null;
    }

    public ClusteredInstances toClusteredInstances() {
        try {
            if (hm == null) {
                initService(new HashMap<String, Object>());
            }
            return hm.toClusteredInstances();
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return null;
    }
}
