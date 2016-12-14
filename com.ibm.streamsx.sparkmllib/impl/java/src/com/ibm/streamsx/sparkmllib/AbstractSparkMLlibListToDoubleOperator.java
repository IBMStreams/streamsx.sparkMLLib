/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/

package com.ibm.streamsx.sparkmllib;
import java.util.List;
import java.util.logging.Logger;

import org.apache.spark.mllib.linalg.Vector;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.StreamingInput;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.Type.MetaType;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.logging.TraceLevel;
import com.ibm.streams.operator.logging.LoggerNames;

/**
 * A specialized abstract operator class that assumes that the concrete operator class handles 
 * Spark analytics that take an input tuple attribute 'testDataAttr' of type list<float64> and outputs
 * an attribute called 'analysisResult' of type double.
 */
public abstract class AbstractSparkMLlibListToDoubleOperator<T> extends AbstractSparkMLlibOperator<T> {
	
	protected Attribute testDataAttr;
	

	private static final String CLASS_NAME = AbstractSparkMLlibListToDoubleOperator.class.getName();
	
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * trace facility
	 */
	private static Logger tracer = Logger.getLogger(CLASS_NAME, "com.ibm.streamsx.sparkmllib.messages");

	
	/**
	 * Check to ensure that an analysisResult attribute of type float64 is present on the output schema
	 */
	@ContextCheck
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
	public synchronized void initialize(OperatorContext context)
			throws Exception {
		super.initialize(context);
		
		//load the testDataAttr parameter as specified in the operator model
		testDataAttr = getAttributeParameter(context, "testDataAttr");
	}

	@Override
	public void processTuple(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		//For each incoming tuple, extract the testDataAttr attribute value as a list of doubles

		try {
			List<Double> testDataList = (List<Double>)tuple.getList(testDataAttr.getIndex());

			//transform the list into a Spark Vector
			Vector features = getVector(testDataList);

			//perform the specific operation using the specific model
			double result = performOperation(features);

			//Generate an output tuple
			OutputTuple out= getOutput(0).newTuple();

			//Pass all incoming attributes as is to the output tuple
			out.assign(tuple);


			//Add the result value
			out.setDouble(ANALYSISRESULT_ATTRIBUTE, result);

			//Submit to the output port
			getOutput(0).submit(out);
		} catch (Exception e){
			tracer.log(TraceLevel.ERROR, "TRACE_M_PROCESS_TUPLE", new String[]{e.getClass().getName(), e.getMessage()});
		}
	}
	/**
	 * Subclasses will override to perform the specific operation depending on the operator type.
	 */
	protected abstract double performOperation(Vector features);
}
