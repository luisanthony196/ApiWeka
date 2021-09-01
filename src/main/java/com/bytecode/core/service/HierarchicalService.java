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
    InstancesRepository ir;

    HierarchicalModify hm;

    public void initService(HashMap<String, Object> params) {
        String[] options;
        String split = "";
        if (!params.containsKey("aglo-linkage")) // Metodo de enlace de Hierarchical
            params.put("aglo-linkage", "single");
        if (!params.containsKey("jerarq-method")) // Metodo de enlace para el dendrograma
            params.put("jerarq-method", params.get("aglo-linkage").toString());
        if (!params.containsKey("n_clusters")) // Numero de clusters para dividir
            params.put("n_clusters", 2);
        if (!params.containsKey("aglo-affinity")) // Metrica para medir el enlazamiento
            params.put("aglo-affinity", "euclidean");
        try {
            hm = new HierarchicalModify();
            // Se obtienen los datos
            ir.setQuery(params.get("columns"), params.get("query"));
            Instances data = ir.obtenerDatos();
            // hm.setLink((String) params.get("link"));
            // hm.setNumClusters((int) params.get("clusters"));
            split += "-N " + params.get("n_clusters");
            split += " -L " + ((String) params.get("aglo-linkage")).toUpperCase();
            split += " -P";
            // split += " -A \"" + DistanceFunction.hp.get(params.get("aglo-affinity").toString()) + "\"";
            options = weka.core.Utils.splitOptions(split);
            hm.setOptions(options);
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
