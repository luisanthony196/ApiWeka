package com.bytecode.core.utils;

import java.util.HashMap;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.SimpleKMeanCluster;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class SimpleKMeansModify extends SimpleKMeans{
    public Instances n_instances;
    public int n_clusters;
    String[] instanceStats;
    boolean ready = false;
    
    public void buildClusterer(Instances n_instances) throws Exception {
        this.n_instances = n_instances;
        this.n_clusters = numberOfClusters();
        // Construimos el algoritmo llamando al metodo padre
        super.buildClusterer(n_instances);
        // Creamos las estadisticas del cluster
        eval();
        // Definimos el modelo como cargado
        this.ready = true;
    }

    public SimpleKMeanCluster toSimpleKMeansCluster() throws Exception {
        SimpleKMeanCluster skmc = new SimpleKMeanCluster();
        skmc.setTotal((int) Utils.sum(getClusterSizes()));
        skmc.setFullData(instanceStats[0] + ":" + instanceStats[1]);
        for (int i = 0; i < numberOfClusters(); i++) {
            Instance inst = getClusterCentroids().instance(i);
            skmc.addCluster(i, inst.stringValue(0) + ":" + inst.stringValue(1), (int) getClusterSizes()[i]);
        }
        return skmc;
    }

    public ClusteredInstances toClusteredInstances() throws Exception {
        HashMap<Integer, String[]> hm = new HashMap<Integer, String[]>();
        String[] atributos = new String[] { n_instances.attribute(0).name(), n_instances.attribute(1).name() };
        // Lista donde se guardara la informacion de las
        // instancias, ademas de inicializarlo
        String[] listClusters = new String[n_clusters];
        for (int i = 0; i < n_clusters; i++) {
            listClusters[i] = "";
        }
        // Iteracion que aglomear en cada unidad de listClusters
        // las instancias que pertencen a 'i' cluster
        for (int i = 0; i < n_instances.numInstances(); i++) {
            Instance inst = n_instances.get(i);
            listClusters[clusterInstance(inst)] += inst.stringValue(0) + ":" + inst.stringValue(1) + ";";
        }
        // Agregamos clusters al HashMap
        for (int i = 0; i < n_clusters; i++) {
            hm.put(i, listClusters[i].split(";"));
        }
        return new ClusteredInstances(n_clusters, atributos, hm);
    }

    public void eval() throws Exception {
        instanceStats = new String[n_instances.numAttributes()];
        double[] temp = moveCentroid(0, n_instances, true, false);
        for (int i = 0; i < temp.length; i++) {
            instanceStats[i] = getClusterCentroids().attribute(i).value((int) temp[i]);
        }
    }
}
