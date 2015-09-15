/*******************************************************************************
 * Copyright (C) 2015 International Business Machines Corporation
 * All Rights Reserved
 *******************************************************************************/
package com.ibm.streamsx.sparkmllib.clustering;

import java.util.List;
import java.util.logging.Logger;

import org.apache.spark.SparkContext;
import org.apache.spark.mllib.clustering.KMeansModel;
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
import com.ibm.streams.operator.logging.LogLevel;
import com.ibm.streams.operator.logging.LoggerNames;
import com.ibm.streams.operator.model.InputPortSet;
import com.ibm.streams.operator.model.OutputPortSet;
import com.ibm.streamsx.sparkmllib.AbstractSparkMLlibOperator;

//@PrimitiveOperator(description="This operator provides support for analysis of incoming tuple data against Apache Spark's kmeans clustering machine learning library.")
@InputPortSet(cardinality=1,description="This input port is required. The operator expects an attribute of type list<float64> that will be used as input to the kmeans clustering algorithm.")
@OutputPortSet(cardinality=1,description="This output port is required. The operator passes through all attributes on the input port as-is to the output port. In addition, it expects an attribute called 'analysisResult' of type int32.")
public class SparkClusteringKMeans extends AbstractSparkMLlibOperator<KMeansModel> {

	private Attribute testDataAttr;


	private static final String CLASS_NAME =  "com.ibm.streamsx.sparkmllib.clustering.SparkClusteringKMeans";
	/**
	 * Create a {@code Logger} specific to this class that will write to the SPL
	 * log facility as a child of the {@link LoggerNames#LOG_FACILITY}
	 * {@code Logger}. The {@code Logger} uses a
	 */
	private static Logger log = Logger.getLogger(LoggerNames.LOG_FACILITY + "." + CLASS_NAME, "com.ibm.streamsx.sparkmllib.Messages");

	/**
	 * Check to ensure that an analysisResult attribute of type int32 is present on the output schema
	 */
	@ContextCheck
	public static void checkOutputAttributeType(OperatorContextChecker checker) {
		
		OperatorContext context = checker.getOperatorContext();
		StreamSchema schema = context.getStreamingOutputs().get(0).getStreamSchema();
		Attribute resultAttribute = schema.getAttribute(ANALYSISRESULT_ATTRIBUTE);
		
		if(resultAttribute != null && resultAttribute.getType().getMetaType() != MetaType.INT32) {
			log.log(LogLevel.ERROR, "WRONG_TYPE_FULL", new Object[]{ANALYSISRESULT_ATTRIBUTE, "int32", resultAttribute.getType()});
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
	protected KMeansModel loadModel(SparkContext sc, String modelPath) {
		return KMeansModel.load(sc, modelPath);
	}


	@Override
	public void processTuple(StreamingInput<Tuple> stream, Tuple tuple)
			throws Exception {
		try {
			//For each incoming tuple, extract the testDataAttr attribute value as a list of doubles
			List<Double> testDataList = (List<Double>)tuple.getList(testDataAttr.getIndex());

			//transform the list into a Spark Vector
			Vector points = getVector(testDataList);

			//perform the specific operation using the specific model
			int result = getModel().predict(points);

			//Generate an output tuple
			OutputTuple out = getOutput(0).newTuple();

			//Pass all incoming attributes as is to the output tuple
			out.assign(tuple);

			//Add the result value
			out.setInt(ANALYSISRESULT_ATTRIBUTE, result);

			//Submit to the output port
			getOutput(0).submit(out);

		} catch (Exception e){
			log.log(LogLevel.ERROR, "PROCESS_TUPLE", new Object[]{e.getClass().getName(),e.getMessage()});
		}
	}
}
