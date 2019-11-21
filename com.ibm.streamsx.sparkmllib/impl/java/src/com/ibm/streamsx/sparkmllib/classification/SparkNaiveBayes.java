/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.sparkmllib.classification;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.SparkContext;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.logging.TraceLevel;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streamsx.sparkmllib.AbstractSparkMLlibListToDoubleOperator;


//@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's naive bayes machine learning library.")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type list<float64> that will be used as input to the naive bayes algorithm.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type float64")
public class SparkNaiveBayes extends AbstractSparkMLlibListToDoubleOperator<NaiveBayesModel> {

	private boolean getProbabilities;
	public static final String ANALYSISPROBABILITIES_ATTRIBUTE = "probabilities";

	private static final String CLASS_NAME = SparkNaiveBayes.class.getName();
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * trace facility
	 */
	private static Logger tracer = Logger.getLogger(CLASS_NAME, "com.ibm.streamsx.sparkmllib.messages");

	@Override
	protected NaiveBayesModel loadModel(SparkContext sc, String modelPath) {
		return NaiveBayesModel.load(sc, modelPath);
	}

	@Override
	protected double performOperation(Vector features) {
		return getModel().predict(features);
	}	
	
	@Parameter(name="getProbabilities",optional=true,description="If predicted probabilities are required in output")
	public void setGetProbabilities(boolean getProbabilities) {
		this.getProbabilities = getProbabilities;
	}
	
	@ContextCheck(compile=false)
	public  void checkOutputAttributeRuntime(OperatorContextChecker checker) {
		OperatorContext context = checker.getOperatorContext();
		StreamSchema schema = context.getStreamingOutputs().get(0).getStreamSchema();

		//check if getProbabilities parameter is present and set to true, then output schema should have probabilities attribute of type list<float64>
		
		if(this.getProbabilities){
			Attribute probabilitiesAttribute = schema.getAttribute(ANALYSISPROBABILITIES_ATTRIBUTE);
			if(probabilitiesAttribute == null) {
				tracer.log(TraceLevel.ERROR, "COMPILE_M_MISSING_ATTRIBUTE", new Object[]{ ANALYSISPROBABILITIES_ATTRIBUTE});
				checker.setInvalidContext();
			}else if(!isList(probabilitiesAttribute, Double.class)) {
				tracer.log(TraceLevel.ERROR, "COMPILE_M_WRONG_TYPE_FULL", new Object[]{ANALYSISPROBABILITIES_ATTRIBUTE, "list<float64>", probabilitiesAttribute.getType()});
				checker.setInvalidContext();
			}
		}
	}
	
	
	@Override
	public void processTuple(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		
		//For each incoming tuple, extract the testDataAttr attribute value as a list of doubles
		try {
			@SuppressWarnings("unchecked")
			List<Double> testDataList = (List<Double>)tuple.getList(testDataAttr.getIndex());
			
			//Generate an output tuple
			OutputTuple out= getOutput(0).newTuple();

			//Pass all incoming attributes as is to the output tuple
			out.assign(tuple);

			//transform the list into a Spark Vector
			Vector features = getVector(testDataList);

			//perform the specific operation using the specific model
			double result = performOperation(features);

			//Add the prediction result 
			out.setDouble(ANALYSISRESULT_ATTRIBUTE, result);

			//Add the prediction probabilities value if required
			if(this.getProbabilities){
				Vector resultProb = getModel().predictProbabilities(features);
				out.setList(ANALYSISPROBABILITIES_ATTRIBUTE, Arrays.asList(ArrayUtils.toObject(resultProb.toArray())));
			}
			//Submit to the output port
			getOutput(0).submit(out);
		} catch (Exception e){
			
			tracer.log(TraceLevel.ERROR, "TRACE_M_PROCESS_TUPLE", new String[]{e.getClass().getName(), e.getMessage()});
		}
	}


}
