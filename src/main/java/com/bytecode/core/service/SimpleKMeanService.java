package com.bytecode.core.service;

import org.springframework.stereotype.Service;

import com.bytecode.core.model.units.Cluster;
import com.bytecode.core.model.SimpleKMean;

import weka.clusterers.SimpleKMeans;
import weka.core.Utils;
import weka.core.Instances;

@Service
public class SimpleKMeanService {
    public SimpleKMean obtenerResultado(SimpleKMeans m) throws Exception {
        int atributo = 0, max = -1, k = -1; // Por esta vez solo usamos 1 atributo
        Instances resultado = m.getClusterCentroids();
        Cluster[] grupo = new Cluster[m.numberOfClusters()];
        int total = (int) Utils.sum(m.getClusterSizes());
        for (int i = 0; i < m.numberOfClusters(); i++) {
            int index = (int) resultado.instance(i).value(atributo);
            grupo[i] = new Cluster(
                i,
                resultado.attribute(atributo).value(index),
                (int) m.getClusterSizes()[i],
                total
            );
            if ((int) m.getClusterSizes()[i] > max) {
                k = index;
                max = (int) m.getClusterSizes()[i];
            }
        }
        String fullData = resultado.attribute(atributo).value(k);
        return new SimpleKMean(fullData, total, grupo);
    }
}
