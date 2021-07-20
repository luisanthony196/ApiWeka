package com.bytecode.core.model;

public class Tuple {
    public Tuple(double d, int i, int j, int nSize1, int nSize2) {
      m_fDist = d;
      m_iCluster1 = i;
      m_iCluster2 = j;
      m_nClusterSize1 = nSize1;
      m_nClusterSize2 = nSize2;
    }

    public double m_fDist;
    public int m_iCluster1;
    public int m_iCluster2;
    public int m_nClusterSize1;
    public int m_nClusterSize2;   
}
