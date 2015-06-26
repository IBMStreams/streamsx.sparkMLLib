package com.ibm.streamsx.sparkmllib.regression;

import org.apache.spark.SparkContext;
import org.apache.spark.mllib.regression.IsotonicRegressionModel;

import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.TupleAttribute;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.PrimitiveOperator;
import com.ibm.streamsx.sparkmllib.AbstractSparkMLlibOperator;

@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's isotonic regression machine learning library.")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type float64 that will be used as input to the isotonic regression algorithm.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type float64")
public class SparkIsotonicRegression extends AbstractSparkMLlibOperator<IsotonicRegressionModel> {

	private TupleAttribute<Tuple, Double> testDataAttr;
	
	@Parameter(optional=false, description="The attribute of type float64 on the input schema to be used as the input to the spark model.")
	public void setTestDataAttr(TupleAttribute<Tuple, Double> attr) {
		testDataAttr = attr;
	}
	
	@Override
	protected IsotonicRegressionModel loadModel(SparkContext sc, String modelPath) {
		return IsotonicRegressionModel.load(sc, modelPath);
	}
	
	@Override
	public void process(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		//For each incoming tuple, extract the testDataAttr attribute value as a double
		Double value = testDataAttr.getValue(tuple);
		
		//perform the specific operation using the specific model
		double result = getModel().predict(value);
		
		//Generate an output tuple
		OutputTuple out = getOutput(0).newTuple();
		
		//Pass all incoming attributes as is to the output tuple
		out.assign(tuple);
		
		//Add the result value
		out.setDouble(ANALYSISRESULT_ATTRIBUTE, result);
		
		//Submit to the output port
		getOutput(0).submit(out);
	}
	
}
