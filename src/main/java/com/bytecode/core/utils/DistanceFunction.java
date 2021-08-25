package com.bytecode.core.utils;

import java.util.HashMap;

public class DistanceFunction {
    public static HashMap<String, String> hp;
    static {
        hp = new HashMap<>();
        hp.put("euclidean", "weka.core.EuclideanDistance -R first-last");
        hp.put("manhattan", "weka.core.ManhattanDistance -R first-last");
        hp.put("l1", "weka.core.ManhattanDistance -R first-last");
        hp.put("l2", "weka.core.MinkowskiDistance");
    }
    
}
