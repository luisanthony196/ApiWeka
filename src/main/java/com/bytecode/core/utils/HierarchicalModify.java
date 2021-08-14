package com.bytecode.core.utils;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.HierarchicalCluster;

import smile.clustering.HierarchicalClustering;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.UPGMALinkage;
import smile.clustering.linkage.UPGMCLinkage;
import smile.clustering.linkage.WPGMALinkage;
import smile.clustering.linkage.WPGMCLinkage;
import smile.clustering.linkage.WardLinkage;
import smile.plot.swing.Dendrogram;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class HierarchicalModify extends HierarchicalClusterer{
    public Instances n_instances;
    public int n_clusters;
    int[] instanceStats;
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

    public HierarchicalCluster toHierarchicalCluster() throws Exception {
        HierarchicalCluster hc = new HierarchicalCluster();
        hc.setN_clusters(n_clusters);
        hc.setN_instancias(n_instances.numInstances());
        hc.setImage(base64Image());
        // Eliminamos los saltos de linea del texto
        String text = toString().replaceAll("\n+", "\n");
        // Separamos las lineas en un arreglo
        String[] arrayText = text.split("\n");
        int j = 0;
        // Agregamos clusters al HierarchicalCluster
        for (int i = 0; i < n_clusters; i++) {
            // En caso de que el texto muestre datos del cluster
            if (arrayText.length > j && arrayText[j].equals("Cluster " + i)) {
                hc.addCluster(i, arrayText[j+1], instanceStats[i]);
                j += 2;
            } else {
                hc.addCluster(i, "", instanceStats[i]);
            }
        }
        return hc;
    }

    public ClusteredInstances toClusteredInstances() throws Exception {
        ClusteredInstances ci = new ClusteredInstances();
        ci.setN_clusters(numberOfClusters());
        ci.setAtributos(new String[]{n_instances.attribute(0).name(), n_instances.attribute(1).name()});
        // Iteracion que aglomear en cada unidad de listClusters
        // las instancias que pertencen a 'i' cluster
        for (int i = 0; i < n_instances.numInstances(); i++) {
            Instance inst = n_instances.get(i);
            ci.addInstance(i, new String[]{inst.stringValue(0), inst.stringValue(1)}, clusterInstance(inst));
        }
        return ci;
    }

    public void eval() throws Exception {
        instanceStats = new int[n_clusters];
        for (int i = 0; i < n_instances.numInstances(); i++) {
            instanceStats[clusterInstance(n_instances.get(i))] += 1;
        }
    }

    public String base64Image() throws Exception {
        int n = n_instances.numInstances();
        double[][] proximity = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i+1; j++) {
                if (i == j)
                    proximity[i][j] = 0;
                else {
                    Instance inst1 = n_instances.get(i);
                    Instance inst2 = n_instances.get(j);
                    proximity[i][j] = (inst1.valueSparse(0) + inst1.valueSparse(1)) / 2;
                    proximity[j][i] = (inst2.valueSparse(0) + inst2.valueSparse(1)) / 2;
                }
            }
        }
        var clusters = HierarchicalClustering.fit(getLinkage(proximity));
        var canvas = new Dendrogram(clusters.getTree(), clusters.getHeight()).canvas();
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(canvas.toBufferedImage(300, 300), "png", bos);
            byte[] imageBytes = bos.toByteArray();
            imageString = Base64.getEncoder().encodeToString(imageBytes);
            bos.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return imageString;
    }

    public Linkage getLinkage(double[][] data) {
        Linkage link;
        switch (Integer.parseInt(getLinkType().toString())) {
            case 0:
                link = SingleLinkage.of(data);
                break;
            case 1:
                link = CompleteLinkage.of(data);
                break;
            case 2:
                link = UPGMALinkage.of(data);
                break;
            case 3:
                link = WPGMCLinkage.of(data);
                break;
            case 4:
                link = UPGMCLinkage.of(data);
                break;
            case 5:
                link = WardLinkage.of(data);
                break;
            case 6:
                link = WPGMALinkage.of(data);
                break;
            default: // Si no se especifica es SINGLE
                link = SingleLinkage.of(data);
                break;
        }
        return link;
    }

    public void setNumClusters(int clusters){
        this.n_clusters = clusters;
        super.setNumClusters(clusters);
    }

    public void setLink(String link) {
        int type;
        switch (link.toUpperCase()) {
            case "SINGLE":
                type = 0;
                break;
            case "COMPLETE":
                type = 1;
                break;
            case "AVERAGE":
                type = 2;
                break;
            case "MEAN":
                type = 3;
                break;
            case "CENTROID":
                type = 4;
                break;
            case "WARD":
                type = 5;
                break;
            case "ADJCOMPLETE":
                type = 6;
                break;
            default: // Si no se especifica es SINGLE
                type = 0;
                break;
        }
        setLinkType(new SelectedTag(type, TAGS_LINK_TYPE));
    }

    public boolean isReady() {
        return ready;
    }
}
