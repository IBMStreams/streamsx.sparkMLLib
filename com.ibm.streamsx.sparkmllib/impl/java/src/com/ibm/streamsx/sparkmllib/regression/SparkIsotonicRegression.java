/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.sparkmllib.regression;

import java.util.logging.Logger;

import org.apache.spark.SparkContext;
import org.apache.spark.mllib.regression.IsotonicRegressionModel;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.TupleAttribute;
import com.ibm.streams.operator.Type.MetaType;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.logging.TraceLevel;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.InputPorts;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streamsx.sparkmllib.AbstractSparkMLlibOperator;

//@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's isotonic regression machine learning library.")
@InputPorts({@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type float64 that will be used as input to the isotonic regression algorithm."),
	@InputPortSet(cardinality=1,optional=true,controlPort=true,description="This input control port is optional. The port expects a single attribute of type rstring and the value must be a string in JSON format. For example, to reload the spark model, the attribute value must be set to '{\\\"reloadModel\\\":true}'.")})
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type float64")
public class SparkIsotonicRegression extends AbstractSparkMLlibOperator<IsotonicRegressionModel> {

	private static final String CLASS_NAME = SparkIsotonicRegression.class.getName();
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * trace facility
	 */
	private static Logger tracer = Logger.getLogger(CLASS_NAME, "com.ibm.streamsx.sparkmllib.messages");
	private TupleAttribute<Tuple, Double> testDataAttr;
	
	@Parameter(optional=false, description="The attribute of type float64 on the input schema to be used as the input to the spark model.")
	public void setTestDataAttr(TupleAttribute<Tuple, Double> attr) {
		testDataAttr = attr;
	}
	
	@ContextCheck (compile  = true)
	public static void checkOutputAttributeType(OperatorContextChecker checker) {
		OperatorContext context = checker.getOperatorContext();

		StreamSchema schema = context.getStreamingOutputs().get(0).getStreamSchema();
		Attribute resultAttribute = schema.getAttribute(ANALYSISRESULT_ATTRIBUTE);
		
		if(resultAttribute != null && resultAttribute.getType().getMetaType() != MetaType.FLOAT64) {
			tracer.log(TraceLevel.ERROR, "COMPILE_M_WRONG_TYPE_FULL", new Object[]{ANALYSISRESULT_ATTRIBUTE, "float64", resultAttribute.getType()});
			checker.setInvalidContext();
		}
	}

	
	@Override
	protected IsotonicRegressionModel loadModel(SparkContext sc, String modelPath) {
		return IsotonicRegressionModel.load(sc, modelPath);
	}

	@Override
	public void processTuple(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		//For each incoming tuple, extract the testDataAttr attribute value as a double
		try {	
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
		} catch (Exception e){
			tracer.log(TraceLevel.ERROR, "TRACE_M_PROCESS_TUPLE", new String[]{e.getClass().getName(),e.getMessage()});
		}

	}
}
