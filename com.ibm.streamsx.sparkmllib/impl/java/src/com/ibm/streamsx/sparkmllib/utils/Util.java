/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.sparkmllib.utils;

import java.util.HashMap;
import java.util.TreeMap;

import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.mllib.linalg.SparseVector;
import org.apache.spark.mllib.linalg.Vector;

import com.ibm.streams.function.model.Function;
import com.ibm.streams.toolkit.model.ToolkitLibraries;

@ToolkitLibraries({"opt/downloaded/*","lib/*"})
public class Util {
    private static HashingTF hashingTF = new HashingTF();
    private static org.apache.spark.mllib.feature.HashingTF featureHash = new org.apache.spark.mllib.feature.HashingTF(hashingTF.getNumFeatures());
   
    /**
     * Create a feature vector to from a line of text.
     * @param textData
     * @return
     */
    @Function(name="getFeatureVector", description="Transforms text into a list of float64 values")
    public static double[] getFeatureVector(String textData) {
        int size = hashingTF.getNumFeatures();

        textData = removeWhiteSpaces(textData);
        textData = textData.replaceAll("\\.", " ");
        String[] words = textData.split("\\s+");

        //count the number of words
        HashMap<String, Integer> uniqueWordCount = new HashMap<>();
        for (String word : words) {
            if (!uniqueWordCount.containsKey(word)) {
                uniqueWordCount.put(word, 1);
            } else {
                int prevVal = uniqueWordCount.get(word);
                prevVal++;
                uniqueWordCount.put(word, prevVal);
            }
        }

        //sorted order
        TreeMap<Integer, Integer> featureValueMap = new TreeMap<>();
        for (String word : uniqueWordCount.keySet()) {
            int index = featureHash.indexOf(word);
            int value = uniqueWordCount.get(word);
            featureValueMap.put(index, value);
        }

        int featuresSize = featureValueMap.size();
        int[] indices = new int[featuresSize];
        double[] values = new double[featuresSize];
        int j = 0;
        for (int index : featureValueMap.keySet()) {
            indices[j] = index;
            values[j] = featureValueMap.get(index);
            j++;
        }

        Vector sv = new SparseVector(size, indices, values);   
        
        return sv.toDense().toArray() ;
    }

    /**
     * Removes white spaces that are extraneous and returns a single white space separated string
     *
     * @param whiteSpaceStr
     * @return
     */
    public static String removeWhiteSpaces(String whiteSpaceStr) {
        String[] splitString = whiteSpaceStr.split("\\s+");
        if (splitString.length == 0) {
            return "";
        }
        String retval = splitString[0];
        for (int i = 1; i < splitString.length; i++) {
            retval += " " + splitString[i];
        }

        return retval;
    }


}
