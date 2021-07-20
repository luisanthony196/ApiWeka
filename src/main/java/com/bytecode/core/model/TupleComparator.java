package com.bytecode.core.model;

import java.util.Comparator;

public class TupleComparator implements Comparator<Tuple> {
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
