package com.bytecode.core.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import weka.core.Instances;

public class Node implements Serializable {
    /** ID added to avoid warning */
    private static final long serialVersionUID = 7639483515789717908L;

    public Node m_left;
    public Node m_right;
    public Node m_parent;
    public int m_iLeftInstance;
    public int m_iRightInstance;
    public double m_fLeftLength = 0;
    public double m_fRightLength = 0;
    public double m_fHeight = 0;

    public String toString(int attIndex, Instances m_instances) {
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
              + myFormatter.format(m_fLeftLength) + "," + m_right.toString(attIndex, m_instances) + ":"
              + myFormatter.format(m_fRightLength) + ")";
        }
      } else {
        if (m_right == null) {
          return "(" + m_left.toString(attIndex, m_instances) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_instances.instance(m_iRightInstance).stringValue(attIndex) + ":" + myFormatter.format(m_fRightLength)
              + ")";
        } else {
          return "(" + m_left.toString(attIndex, m_instances) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_right.toString(attIndex, m_instances) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      }
    }

    public String toString2(int attIndex, Instances m_instances) {
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
              + "," + m_right.toString2(attIndex, m_instances) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      } else {
        if (m_right == null) {
          return "(" + m_left.toString2(attIndex, m_instances) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_instances.instance(m_iRightInstance).value(attIndex) + ":" + myFormatter.format(m_fRightLength) + ")";
        } else {
          return "(" + m_left.toString2(attIndex, m_instances) + ":" + myFormatter.format(m_fLeftLength) + ","
              + m_right.toString2(attIndex, m_instances) + ":" + myFormatter.format(m_fRightLength) + ")";
        }
      }
    }

    public void setHeight(double fHeight1, double fHeight2) {
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

    public void setLength(double fLength1, double fLength2) {
      m_fLeftLength = fLength1;
      m_fRightLength = fLength2;
      m_fHeight = fLength1;
      if (m_left != null) {
        m_fHeight += m_left.m_fHeight;
      }
    }
}
