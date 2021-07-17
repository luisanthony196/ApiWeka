package com.bytecode.core.controller;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Vector;

import com.bytecode.core.model.Result;
import com.bytecode.core.service.DataService;
import com.bytecode.core.service.ResultService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import weka.filters.Filter;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

@RestController
@RequestMapping("/api")
public class DataController {
  @Autowired
  DataService dataService;
  @Autowired
  ResultService resultService;
  Instances m_instances;
  DistanceFunction m_DistanceFunction = new EuclideanDistance();
  Boolean m_Debug = false;
  protected boolean m_bDistanceIsBranchLength = false;
  final static int SINGLE = 0;
  final static int COMPLETE = 1;
  final static int AVERAGE = 2;
  final static int MEAN = 3;
  final static int CENTROID = 4;
  final static int WARD = 5;
  final static int ADJCOMPLETE = 6;
  final static int NEIGHBOR_JOINING = 7;
  int m_nNumClusters = 2;
  int m_nLinkType = SINGLE;
  boolean m_bPrintNewick = true;
  protected Node[] m_clusters;
  int[] m_nClusterNr;

  @GetMapping("/hierarchical")
  public String hierarchical() {
    try {
      // Se obtienen los datos
      Instances data = dataService.obtenerDatos();
      m_instances = data;
      int nInstances = m_instances.numInstances();
      // Hallamos la distancia Euclidiana para las instancias
      m_DistanceFunction.setInstances(m_instances);
      // Usamos un arreglo de enteros para guardar los indices de los clusters
      // E iniciamos con un cluster por cada instancia
      @SuppressWarnings("unchecked")
      Vector<Integer>[] nClusterID = new Vector[data.numInstances()];
      for (int i = 0; i < data.numInstances(); i++) {
        nClusterID[i] = new Vector<Integer>();
        nClusterID[i].add(i);
      }
      // Preparamos el numero de instancias
      int nClusters = data.numInstances();

      // Creamos un arreglo de nodos que contienen instancias
      Node[] clusterNodes = new Node[nInstances];
      // if (m_nLinkType == NEIGHBOR_JOINING) {
      // neighborJoining(nClusters, nClusterID, clusterNodes);
      // } else {
      doLinkClustering(nClusters, nClusterID, clusterNodes);
      // }

      // move all clusters in m_nClusterID array
      // & collect hierarchy
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
    } catch (Exception e) {
      // TODO: handle exception
    }
    return toString();
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
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
      if (m_bPrintNewick && (numberOfClusters() > 0)) {
        for (int i = 0; i < m_clusters.length; i++) {
          if (m_clusters[i] != null) {
            buf.append("Cluster " + i + "\n");
            if (m_instances.attribute(attIndex).isString()) {
              buf.append(m_clusters[i].toString(attIndex));
            } else {
              buf.append(m_clusters[i].toString2(attIndex));
            }
            buf.append("\n\n");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return buf.toString();
  }

  public int numberOfClusters() throws Exception {
    return Math.min(m_nNumClusters, m_instances.numInstances());
  }

  void doLinkClustering(int nClusters, Vector<Integer>[] nClusterID, Node[] clusterNodes) {
    int nInstances = m_instances.numInstances();
    PriorityQueue<Tuple> queue = new PriorityQueue<Tuple>(nClusters * nClusters / 2, new TupleComparator());
    double[][] fDistance0 = new double[nClusters][nClusters];
    double[][] fClusterDistance = null;
    if (m_Debug) {
      fClusterDistance = new double[nClusters][nClusters];
    }
    for (int i = 0; i < nClusters; i++) {
      fDistance0[i][i] = 0;
      for (int j = i + 1; j < nClusters; j++) {
        fDistance0[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
        fDistance0[j][i] = fDistance0[i][j];
        queue.add(new Tuple(fDistance0[i][j], i, j, 1, 1));
        if (m_Debug) {
          fClusterDistance[i][j] = fDistance0[i][j];
          fClusterDistance[j][i] = fDistance0[i][j];
        }
      }
    }
    while (nClusters > m_nNumClusters) {
      int iMin1 = -1;
      int iMin2 = -1;
      // find closest two clusters
      if (m_Debug) {
        /* simple but inefficient implementation */
        double fMinDistance = Double.MAX_VALUE;
        for (int i = 0; i < nInstances; i++) {
          if (nClusterID[i].size() > 0) {
            for (int j = i + 1; j < nInstances; j++) {
              if (nClusterID[j].size() > 0) {
                double fDist = fClusterDistance[i][j];
                if (fDist < fMinDistance) {
                  fMinDistance = fDist;
                  iMin1 = i;
                  iMin2 = j;
                }
              }
            }
          }
        }
        merge(iMin1, iMin2, fMinDistance, fMinDistance, nClusterID, clusterNodes);
      } else {
        // use priority queue to find next best pair to cluster
        Tuple t;
        do {
          t = queue.poll();
        } while (t != null && (nClusterID[t.m_iCluster1].size() != t.m_nClusterSize1
            || nClusterID[t.m_iCluster2].size() != t.m_nClusterSize2));
        iMin1 = t.m_iCluster1;
        iMin2 = t.m_iCluster2;
        merge(iMin1, iMin2, t.m_fDist, t.m_fDist, nClusterID, clusterNodes);
      }
      // merge clusters

      // update distances & queue
      for (int i = 0; i < nInstances; i++) {
        if (i != iMin1 && nClusterID[i].size() != 0) {
          int i1 = Math.min(iMin1, i);
          int i2 = Math.max(iMin1, i);
          double fDistance = getDistance(fDistance0, nClusterID[i1], nClusterID[i2]);
          if (m_Debug) {
            fClusterDistance[i1][i2] = fDistance;
            fClusterDistance[i2][i1] = fDistance;
          }
          queue.add(new Tuple(fDistance, i1, i2, nClusterID[i1].size(), nClusterID[i2].size()));
        }
      }

      nClusters--;
    }
  } // doLinkClustering

  double getDistance0(Vector<Integer> cluster1, Vector<Integer> cluster2) {
    double fBestDist = Double.MAX_VALUE;
    switch (m_nLinkType) {
      case SINGLE:
      case NEIGHBOR_JOINING:
      case CENTROID:
      case COMPLETE:
      case ADJCOMPLETE:
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

  double getDistance(double[][] fDistance, Vector<Integer> cluster1, Vector<Integer> cluster2) {
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
      case ADJCOMPLETE:
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
  double calcESS(Vector<Integer> cluster) {
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

  void merge(int iMin1, int iMin2, double fDist1, double fDist2, Vector<Integer>[] nClusterID, Node[] clusterNodes) {
    if (m_Debug) {
      System.err.println("Merging " + iMin1 + " " + iMin2 + " " + fDist1 + " " + fDist2);
    }
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

    // track hierarchy
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
    if (m_bDistanceIsBranchLength) {
      node.setLength(fDist1, fDist2);
    } else {
      node.setHeight(fDist1, fDist2);
    }
    clusterNodes[iMin1] = node;
  } // merge

  class Tuple {
    public Tuple(double d, int i, int j, int nSize1, int nSize2) {
      m_fDist = d;
      m_iCluster1 = i;
      m_iCluster2 = j;
      m_nClusterSize1 = nSize1;
      m_nClusterSize2 = nSize2;
    }

    double m_fDist;
    int m_iCluster1;
    int m_iCluster2;
    int m_nClusterSize1;
    int m_nClusterSize2;
  }

  /** comparator used by priority queue **/
  class TupleComparator implements Comparator<Tuple> {
    @Override
    public int compare(Tuple o1, Tuple o2) {
      if (o1.m_fDist < o2.m_fDist) {
        return -1;
      } else if (o1.m_fDist == o2.m_fDist) {
        return 0;
      }
      return 1;
    }
  }

  class Node implements Serializable {

    /** ID added to avoid warning */
    private static final long serialVersionUID = 7639483515789717908L;

    Node m_left;
    Node m_right;
    Node m_parent;
    int m_iLeftInstance;
    int m_iRightInstance;
    double m_fLeftLength = 0;
    double m_fRightLength = 0;
    double m_fHeight = 0;

    public String toString(int attIndex) {
      NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
      DecimalFormat myFormatter = (DecimalFormat) nf;
      myFormatter.applyPattern("#.#####");

      if (m_left == null) {
        if (m_right == null) {
          return "(" + m_instances.instance(m_iLeftInstance).stringValue(attIndex) + ":"
              + myFormatter.format(m_fLeftLength) + "," + m_instances.instance(m_iRightInstance).stringValue(attIndex)
              + ":" + myFormatter.format(m_fRightLength) + ")";
        } else {
          return "(" + m_instances.instance(m_iLeftInstance).stringValue(attIndex) + ":"
              + myFormatter.format(m_fLeftLength) + "," + m_right.toString(attIndex) + ":"
              + myFormatter.format(m_fRightLength) + ")";
        }
      } else {
        if (m_right == null) {
          return "(" + m_left.toString(attIndex) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_instances.instance(m_iRightInstance).stringValue(attIndex) + ":" + myFormatter.format(m_fRightLength)
              + ")";
        } else {
          return "(" + m_left.toString(attIndex) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_right.toString(attIndex) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      }
    }

    public String toString2(int attIndex) {
      NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
      DecimalFormat myFormatter = (DecimalFormat) nf;
      myFormatter.applyPattern("#.#####");

      if (m_left == null) {
        if (m_right == null) {
          return "(" + m_instances.instance(m_iLeftInstance).value(attIndex) + ":" + myFormatter.format(m_fLeftLength)
              + "," + m_instances.instance(m_iRightInstance).value(attIndex) + ":" + myFormatter.format(m_fRightLength)
              + ")";
        } else {
          return "(" + m_instances.instance(m_iLeftInstance).value(attIndex) + ":" + myFormatter.format(m_fLeftLength)
              + "," + m_right.toString2(attIndex) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      } else {
        if (m_right == null) {
          return "(" + m_left.toString2(attIndex) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_instances.instance(m_iRightInstance).value(attIndex) + ":" + myFormatter.format(m_fRightLength) + ")";
        } else {
          return "(" + m_left.toString2(attIndex) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_right.toString2(attIndex) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      }
    }

    void setHeight(double fHeight1, double fHeight2) {
      m_fHeight = fHeight1;
      if (m_left == null) {
        m_fLeftLength = fHeight1;
      } else {
        m_fLeftLength = fHeight1 - m_left.m_fHeight;
      }
      if (m_right == null) {
        m_fRightLength = fHeight2;
      } else {
        m_fRightLength = fHeight2 - m_right.m_fHeight;
      }
    }

    void setLength(double fLength1, double fLength2) {
      m_fLeftLength = fLength1;
      m_fRightLength = fLength2;
      m_fHeight = fLength1;
      if (m_left != null) {
        m_fHeight += m_left.m_fHeight;
      }
    }
  }

  @GetMapping("/simplekmean/{num}")
  public Result simplekmean(@PathVariable(name = "num") int num) {
    Result r = null;
    try {
      // Tratamiento de los datos
      Instances datos = dataService.obtenerDatos();
      dataService.listarDatos(datos);
      // Ingnorar atributos
      String[] opciones = new String[] { "-R", "2" };
      Remove remover = new Remove();
      remover.setOptions(opciones);
      remover.setInputFormat(datos);
      Instances nDatos = Filter.useFilter(datos, remover);
      // Generacion del modelo
      SimpleKMeans modelo = new SimpleKMeans();
      modelo.setNumClusters(num);
      modelo.buildClusterer(nDatos);
      // Enviamos los resultados
      System.out.println(modelo);
      r = resultService.obtenerResultado(modelo);
      return r;
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
    return r;
  }
}
