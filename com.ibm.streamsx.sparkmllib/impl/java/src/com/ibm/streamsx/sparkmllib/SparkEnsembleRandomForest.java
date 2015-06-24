package com.ibm.streamsx.sparkmllib;

import org.apache.spark.SparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;

//@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's decision tree ensembles machine learning library.")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type list<float64> that will be used as input to the random forest algorithm.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type float64.")
public class SparkEnsembleRandomForest extends AbstractSparkMLlibListToDoubleOperator<RandomForestModel> {

	@Override
	protected RandomForestModel loadModel(SparkContext sc, String modelPath) {
		return RandomForestModel.load(sc, modelPath);
	}
	
	@Override
	protected double performOperation(Vector features) {
		return getModel().predict(features);
	}	
	
	
}
