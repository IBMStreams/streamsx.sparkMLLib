/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.sparkmllib.classification;

import org.apache.spark.SparkContext;
import org.apache.spark.mllib.classification.SVMModel;
import org.apache.spark.mllib.linalg.Vector;

import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.sparkmllib.AbstractSparkMLlibListToDoubleOperator;

//@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's SVM machine learning library.")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type list<float64> that will be used as input to the SVM algorithm.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type float64.")
public class SparkLinearSVM extends AbstractSparkMLlibListToDoubleOperator<SVMModel> {


	@Override
	protected SVMModel loadModel(SparkContext sc, String modelPath) {
		return SVMModel.load(sc, modelPath);
	}

	@Override
	protected double performOperation(Vector features) {
		return getModel().predict(features);
	}	
	
	
}
