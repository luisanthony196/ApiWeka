package com.bytecode.core.utils;

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

    public void eval() throws Exception {
        instanceStats = new String[n_instances.numAttributes()];
        double[] temp = moveCentroid(0, n_instances, true, false);
        for (int i = 0; i < temp.length; i++) {
            instanceStats[i] = getClusterCentroids().attribute(i).value((int) temp[i]);
        }
    }
}
