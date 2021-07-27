package com.bytecode.core.service;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import com.bytecode.core.model.ClusteredInstances;
import com.bytecode.core.model.HierarchicalCluster;
import com.bytecode.core.utils.Tuple;
import com.bytecode.core.utils.Node;
import com.bytecode.core.utils.TupleComparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

@Service
public class HierarchicalService {
    public Instances m_instances;
    public DistanceFunction m_DistanceFunction = new EuclideanDistance();
    final static int SINGLE = 0;
    final static int COMPLETE = 1;
    final static int AVERAGE = 2;
    final static int MEAN = 3;
    final static int CENTROID = 4;
    final static int WARD = 5;
    public int m_nNumClusters;
    public int m_nLinkType;
    public Node[] m_clusters;
    public int[] m_nClusterNr;
    public double[] instanceStats;
    @Autowired
    InstancesService dataService;

    public void initService(HashMap<String, Object> params) {
        if (!params.containsKey("link"))
            params.put("link", "single");
        if (!params.containsKey("clusters"))
            params.put("clusters", 2);
        try {
            // Se obtienen los datos
            Instances data = dataService.obtenerDatos();
            setLinkType((String) params.get("link"));
            setNumClusters((int) params.get("clusters"));
            // Se procesan las instancias
            init(data);
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
    }

    public void init(Instances data) throws Exception {
        m_instances = data;
        int nInstances = m_instances.numInstances();
        int nClusters = data.numInstances();
        // Preparamos el algoritmo de distancia euclidiana con las
        // instancias, va a usar los atributos y las distancias entre
        // cada dato
        m_DistanceFunction.setInstances(m_instances);
        // Inicializamos un arreglo de Vectores, un Vector por cada instancia
        // (guarda el indice), cada Vector guarda una serie de enteros
        @SuppressWarnings("unchecked")
        Vector<Integer>[] nClusterID = new Vector[data.numInstances()];
        for (int i = 0; i < data.numInstances(); i++) {
            nClusterID[i] = new Vector<Integer>();
            nClusterID[i].add(i);
        }
        // Creamos un arreglo de nodos, un nodo por cada isntancia
        Node[] clusterNodes = new Node[nInstances];
        // Realizamos el proceso de agrupamiento
        doLinkClustering(nClusters, nClusterID, clusterNodes);
        // Movemos todos los clusters en los clusters pedidos
        // por el usuario y se agrupa por jerarquia
        int iCurrent = 0;
        m_clusters = new Node[m_nNumClusters];
        m_nClusterNr = new int[nInstances];
        for (int i = 0; i < nInstances; i++) {
            if (nClusterID[i].size() > 0) {
                for (int j = 0; j < nClusterID[i].size(); j++) {
                    m_nClusterNr[nClusterID[i].elementAt(j)] = iCurrent;
                }
                m_clusters[iCurrent] = clusterNodes[i];
                iCurrent++;
            }
        }
        eval(data);
    }

    public ClusteredInstances obtenerLista() {
        HashMap<Integer, String[]> hm = new HashMap<Integer, String[]>();
        String[] atributos = new String[] { m_instances.attribute(0).name(), m_instances.attribute(1).name() };
        for (int i = 0; i < m_clusters.length; i++) {
            if (m_clusters[i] != null) {
                String arbol = m_clusters[i].inorder(m_instances);
                hm.put(i, arbol.split(";"));
            }
        }
        return new ClusteredInstances(m_clusters.length, atributos, hm);
    }

    public HierarchicalCluster obtenerModelo() {
        HierarchicalCluster hc = new HierarchicalCluster();
        int attIndex = m_instances.classIndex();
        if (attIndex < 0) {
            // try find a string, or last attribute otherwise
            attIndex = 0;
            while (attIndex < m_instances.numAttributes() - 1) {
                if (m_instances.attribute(attIndex).isString()) {
                    break;
                }
                attIndex++;
            }
        }
        try {
            // Elegimos el menor numero, los clusteres pedidos o el numero de instancias
            int numberOfClusters = Math.min(m_nNumClusters, m_instances.numInstances());
            hc.setN_clusters(numberOfClusters);
            hc.setN_instancias(m_instances.numInstances());
            ;
            if (numberOfClusters > 0) {
                for (int i = 0; i < m_clusters.length; i++) {
                    if (m_clusters[i] != null) {
                        if (m_instances.attribute(attIndex).isString()) {
                            hc.addCluster(i, m_clusters[i].toString(attIndex, m_instances), (int) instanceStats[i]);
                        } else {
                            hc.addCluster(i, m_clusters[i].toString2(attIndex, m_instances), (int) instanceStats[i]);
                        }
                    } else {
                        hc.addCluster(i, "", (int) instanceStats[i]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hc;
    }

    public void doLinkClustering(int nClusters, Vector<Integer>[] nClusterID, Node[] clusterNodes) {
        int nInstances = m_instances.numInstances();
        // Tuplas de 2 instancias que almacena su distancia (similitud) y
        // prioriza a las que poseen mayor distancia, aqui se va a llenar
        PriorityQueue<Tuple> queue = new PriorityQueue<Tuple>(nClusters * nClusters / 2, new TupleComparator());
        // Matriz de distancias entre las instancias, en general se hayan como
        // distancia euclidiana, a excepcion del metodo WARD
        // Saca las distancias de la clase EuclideanDistance, que ya ha sido preparado
        double[][] fDistance0 = new double[nClusters][nClusters];
        for (int i = 0; i < nClusters; i++) {
            fDistance0[i][i] = 0;
            for (int j = i + 1; j < nClusters; j++) {
                fDistance0[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
                fDistance0[j][i] = fDistance0[i][j];
                queue.add(new Tuple(fDistance0[i][j], i, j, 1, 1));
            }
        }
        // Combinamos las tuplas (cluster) hasta que el numero de clusters
        // disminuyan para llegar al numero de clusters solicitados
        while (nClusters > m_nNumClusters) {
            int iMin1 = -1;
            int iMin2 = -1;
            Tuple t;
            // Busca la tupla con la distancia mas pequena, y que
            // cumpla que el tamano del Vector sea igual al peso
            // poll obtiene el menos prioritario = menor distancia
            do {
                t = queue.poll();
            } while (t != null && (nClusterID[t.m_iCluster1].size() != t.m_nClusterSize1
                    || nClusterID[t.m_iCluster2].size() != t.m_nClusterSize2));
            // Guardamos temporalmente los clusters de la tupla, los combinamos
            // y lo guardamos en el arreglo de nodos
            iMin1 = t.m_iCluster1;
            iMin2 = t.m_iCluster2;
            merge(iMin1, iMin2, t.m_fDist, t.m_fDist, nClusterID, clusterNodes);
            // merge clusters

            // update distances & queue
            for (int i = 0; i < nInstances; i++) {
                if (i != iMin1 && nClusterID[i].size() != 0) {
                    int i1 = Math.min(iMin1, i);
                    int i2 = Math.max(iMin1, i);
                    double fDistance = getDistance(fDistance0, nClusterID[i1], nClusterID[i2]);
                    queue.add(new Tuple(fDistance, i1, i2, nClusterID[i1].size(), nClusterID[i2].size()));
                }
            }

            nClusters--;
        }
    } // doLinkClustering

    public void eval(Instances data) throws Exception {
        DataSource source = new DataSource(data);
        Instances testRaw = source.getStructure(data.classIndex());
        instanceStats = new double[m_nNumClusters];
        Instance insta;
        while (source.hasMoreElements(testRaw)) {
            // next instance
            insta = source.nextElement(testRaw);

            int cnum = -1;
            try {
                cnum = clusterInstance(insta);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }

            if (cnum != -1) {
                instanceStats[cnum]++;
            }
        }
    }

    // =========================
    // Complemented methods
    // =========================

    public void merge(int iMin1, int iMin2, double fDist1, double fDist2, Vector<Integer>[] nClusterID,
            Node[] clusterNodes) {
        // Aseguramos que iMin2 es mayor o igual que iMin1
        if (iMin1 > iMin2) {
            int h = iMin1;
            iMin1 = iMin2;
            iMin2 = h;
            double f = fDist1;
            fDist1 = fDist2;
            fDist2 = f;
        }
        nClusterID[iMin1].addAll(nClusterID[iMin2]);
        nClusterID[iMin2].removeAllElements();

        // track hierarchy, Arbol, cuyos nodos almacenan 2 indices de intancias
        // Llenamos el nodo con los indices de las instancias, o si ya
        // tienen, lo llenamos con el nodo existente
        Node node = new Node();
        if (clusterNodes[iMin1] == null) {
            node.m_iLeftInstance = iMin1;
        } else {
            node.m_left = clusterNodes[iMin1];
            clusterNodes[iMin1].m_parent = node;
        }
        if (clusterNodes[iMin2] == null) {
            node.m_iRightInstance = iMin2;
        } else {
            node.m_right = clusterNodes[iMin2];
            clusterNodes[iMin2].m_parent = node;
        }
        // Asignamos la altura del nodo formado y lo almacenamos en el arreglo de nodos
        node.setHeight(fDist1, fDist2);
        clusterNodes[iMin1] = node;
    } // merge

    public double getDistance0(Vector<Integer> cluster1, Vector<Integer> cluster2) {
        double fBestDist = Double.MAX_VALUE;
        switch (m_nLinkType) {
            case SINGLE:
            case CENTROID:
            case COMPLETE:
            case AVERAGE:
            case MEAN:
                // set up two instances for distance function
                Instance instance1 = (Instance) m_instances.instance(cluster1.elementAt(0)).copy();
                Instance instance2 = (Instance) m_instances.instance(cluster2.elementAt(0)).copy();
                fBestDist = m_DistanceFunction.distance(instance1, instance2);
                break;
            case WARD: {
                // finds the distance of the change in caused by merging the cluster.
                // The information of a cluster is calculated as the error sum of squares
                // of the
                // centroids of the cluster and its members.
                double ESS1 = calcESS(cluster1);
                double ESS2 = calcESS(cluster2);
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                double ESS = calcESS(merged);
                fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2 * cluster2.size();
            }
                break;
        }
        return fBestDist;
    } // getDistance0

    public double getDistance(double[][] fDistance, Vector<Integer> cluster1, Vector<Integer> cluster2) {
        double fBestDist = Double.MAX_VALUE;
        switch (m_nLinkType) {
            case SINGLE:
                // find single link distance aka minimum link, which is the closest
                // distance between
                // any item in cluster1 and any item in cluster2
                fBestDist = Double.MAX_VALUE;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fBestDist > fDist) {
                            fBestDist = fDist;
                        }
                    }
                }
                break;
            case COMPLETE:
                // find complete link distance aka maximum link, which is the largest
                // distance between
                // any item in cluster1 and any item in cluster2
                fBestDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fBestDist < fDist) {
                            fBestDist = fDist;
                        }
                    }
                }
                if (m_nLinkType == COMPLETE) {
                    break;
                }
                // calculate adjustment, which is the largest within cluster distance
                double fMaxDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = i + 1; j < cluster1.size(); j++) {
                        int i2 = cluster1.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fMaxDist < fDist) {
                            fMaxDist = fDist;
                        }
                    }
                }
                for (int i = 0; i < cluster2.size(); i++) {
                    int i1 = cluster2.elementAt(i);
                    for (int j = i + 1; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        double fDist = fDistance[i1][i2];
                        if (fMaxDist < fDist) {
                            fMaxDist = fDist;
                        }
                    }
                }
                fBestDist -= fMaxDist;
                break;
            case AVERAGE:
                // finds average distance between the elements of the two clusters
                fBestDist = 0;
                for (int i = 0; i < cluster1.size(); i++) {
                    int i1 = cluster1.elementAt(i);
                    for (int j = 0; j < cluster2.size(); j++) {
                        int i2 = cluster2.elementAt(j);
                        fBestDist += fDistance[i1][i2];
                    }
                }
                fBestDist /= (cluster1.size() * cluster2.size());
                break;
            case MEAN: {
                // calculates the mean distance of a merged cluster (akak Group-average
                // agglomerative clustering)
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                fBestDist = 0;
                for (int i = 0; i < merged.size(); i++) {
                    int i1 = merged.elementAt(i);
                    for (int j = i + 1; j < merged.size(); j++) {
                        int i2 = merged.elementAt(j);
                        fBestDist += fDistance[i1][i2];
                    }
                }
                int n = merged.size();
                fBestDist /= (n * (n - 1.0) / 2.0);
            }
                break;
            case CENTROID:
                // finds the distance of the centroids of the clusters
                double[] fValues1 = new double[m_instances.numAttributes()];
                for (int i = 0; i < cluster1.size(); i++) {
                    Instance instance = m_instances.instance(cluster1.elementAt(i));
                    for (int j = 0; j < m_instances.numAttributes(); j++) {
                        fValues1[j] += instance.value(j);
                    }
                }
                double[] fValues2 = new double[m_instances.numAttributes()];
                for (int i = 0; i < cluster2.size(); i++) {
                    Instance instance = m_instances.instance(cluster2.elementAt(i));
                    for (int j = 0; j < m_instances.numAttributes(); j++) {
                        fValues2[j] += instance.value(j);
                    }
                }
                for (int j = 0; j < m_instances.numAttributes(); j++) {
                    fValues1[j] /= cluster1.size();
                    fValues2[j] /= cluster2.size();
                }
                fBestDist = m_DistanceFunction.distance(m_instances.instance(0).copy(fValues1),
                        m_instances.instance(0).copy(fValues2));
                break;
            case WARD: {
                // finds the distance of the change in caused by merging the cluster.
                // The information of a cluster is calculated as the error sum of squares
                // of the
                // centroids of the cluster and its members.
                double ESS1 = calcESS(cluster1);
                double ESS2 = calcESS(cluster2);
                Vector<Integer> merged = new Vector<Integer>();
                merged.addAll(cluster1);
                merged.addAll(cluster2);
                double ESS = calcESS(merged);
                fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2 * cluster2.size();
            }
                break;
        }
        return fBestDist;
    } // getDistance

    /** calculated error sum-of-squares for instances wrt centroid **/
    public double calcESS(Vector<Integer> cluster) {
        double[] fValues1 = new double[m_instances.numAttributes()];
        for (int i = 0; i < cluster.size(); i++) {
            Instance instance = m_instances.instance(cluster.elementAt(i));
            for (int j = 0; j < m_instances.numAttributes(); j++) {
                fValues1[j] += instance.value(j);
            }
        }
        for (int j = 0; j < m_instances.numAttributes(); j++) {
            fValues1[j] /= cluster.size();
        }
        // set up instance for distance function
        Instance centroid = m_instances.instance(cluster.elementAt(0)).copy(fValues1);
        double fESS = 0;
        for (int i = 0; i < cluster.size(); i++) {
            Instance instance = m_instances.instance(cluster.elementAt(i));
            fESS += m_DistanceFunction.distance(centroid, instance);
        }
        return fESS / cluster.size();
    } // calcESS

    public int clusterInstance(Instance instance) throws Exception {
        if (m_instances.numInstances() == 0) {
            return 0;
        }
        double fBestDist = Double.MAX_VALUE;
        int iBestInstance = -1;
        for (int i = 0; i < m_instances.numInstances(); i++) {
            double fDist = m_DistanceFunction.distance(instance, m_instances.instance(i));
            if (fDist < fBestDist) {
                fBestDist = fDist;
                iBestInstance = i;
            }
        }
        return m_nClusterNr[iBestInstance];
    }

    // =========================
    // Set and get methods
    // =========================

    public void setNumClusters(int clusters) {
        this.m_nNumClusters = clusters;
    }

    public void setLinkType(String link) {
        switch (link.toUpperCase()) {
            case "COMPLETE":
                m_nLinkType = COMPLETE;
                break;
            case "AVERAGE":
                m_nLinkType = AVERAGE;
                break;
            case "MEAN":
                m_nLinkType = MEAN;
                break;
            case "CENTROID":
                m_nLinkType = CENTROID;
                break;
            case "WARD":
                m_nLinkType = WARD;
                break;
            default: // Si no se especifica es SINGLE
                m_nLinkType = SINGLE;
                break;
        }
    }
}
