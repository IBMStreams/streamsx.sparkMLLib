package com.ibm.streamsx.sparkmllib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import com.ibm.streams.operator.AbstractOperator;
import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.OperatorContext;
import com.ibm.streams.operator.OperatorContext.ContextCheck;
import com.ibm.streams.operator.ProcessingElement;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.Type;
import com.ibm.streams.operator.Type.MetaType;
import com.ibm.streams.operator.compile.OperatorContextChecker;
import com.ibm.streams.operator.meta.CollectionType;
import com.ibm.streams.operator.model.Libraries;
import com.ibm.streams.operator.model.Parameter;
import com.ibm.streams.operator.model.SharedLoader;

/**
 * Provides the base class for all Spark analytics operators. This base class is responsible
 * for the common parameters, common compiler checks, common utility methods as well
 * as setting up the spark context from the passed parameters so that the analytics models 
 * can be loaded.
 * 
 *  @param T This represents the specific Model class that is to be loaded by a specific 
 *  instance of this class.
 */
@SharedLoader
@Libraries({"impl/lib/streams-sparkmllib.jar","@SPARK_HOME@/lib/*"})
public abstract class AbstractSparkMLlibOperator<T> extends AbstractOperator {

	private String modelPath;
	private String masterString;
	private JavaSparkContext javaContext;
	
	private T model;
	private Map<String, String> params;
	
	protected static final String ANALYSISRESULT_ATTRIBUTE = "analysisResult";

	public AbstractSparkMLlibOperator() {
	}
	
	/**
	 * Compile time to check to ensure that the output schema contains an attribute called
	 * 'analysisResult'. The type of the attribute depends on the specific type of the
	 * operator and is handled by the appropriate derived class.
	 */
	@ContextCheck
	public static void checkOutputAttribute(OperatorContextChecker checker) {
		OperatorContext context = checker.getOperatorContext();
		
		if(context.getNumberOfStreamingOutputs() == 1) {
			StreamSchema schema = context.getStreamingOutputs().get(0).getStreamSchema();
			Attribute resultAttribute = schema.getAttribute(ANALYSISRESULT_ATTRIBUTE);
			
			//check if the output attribute is present where the result will be stored
			if(resultAttribute == null) {
				checker.setInvalidContext("The operator requires an attribute called 'analysisResult' on the output port.", null);
			}
		}
	}

	@Parameter(name="modelPath",optional=false,description="The path containing the persisted analytic model")
	public void setModelPath(String path) {
		this.modelPath = path;
	}
	
	@Parameter(name="sparkMaster",optional=true,description="The spark master to use during the analysis. If not specified, the default value is 'local'")
	public void setSparkMaster(String masterString) {
		this.masterString = masterString;
	}
	
	@Parameter(name="params",optional=true,description="The parameters to be passed to the spark configuration as a comma-separated list of 'key=value'")
	public void setParams(String params) {
		this.params = new HashMap<String, String>();
		String[] configs = params.split(",");
		for(String config: configs) {
			String[] parts = config.split("=");
			this.params.put(parts[0], parts[1]);
		}
	}

	protected JavaSparkContext getJavaSparkContext() {
		return javaContext;
	}
	
	protected T getModel() {
		return model;
	}
	
	protected abstract T loadModel(SparkContext sc, String modelPath);

	@Override
	public synchronized void initialize(OperatorContext context)
			throws Exception {
		super.initialize(context);
		
		//Create a new Spark Configuration. If a sparkMaster parameter value was not specified
		//then use local as the master. Also, generate a unique app name based on the PE and operator Ids
		//so that we can have multiple spark operators connect to the same master without conflict
		SparkConf conf = new SparkConf().setMaster(masterString == null?"local":masterString)
				.setAppName(getUniqueAppName(context));
		
		//set any params that are passed in
		if(params != null) {
			Set<String> keys = params.keySet();
			for(String key: keys) {
				conf.set(key, params.get(key));
			}
		}
		javaContext = new JavaSparkContext(conf);
		
		//Load the model. Each derived class will perform the load that includes
		//reading the model data from the path specified. The path could be
		//any value supported by Spark's load API including filesystem, HDFS.
		model = loadModel(javaContext.sc(), modelPath);
	}
	
	private String getUniqueAppName(OperatorContext context) {
		ProcessingElement pe = context.getPE();
		return pe.getDomainId()+"_"+pe.getInstanceId()+"_"+pe.getPEId()+"_"+context.getName();
	}
	
	//Create a Spark vector from a list of doubles
	protected Vector getVector(List<Double> list) {
		double[] values = new double[list.size()];
		for(int i = 0;i < values.length; i++) {
			values[i] = list.get(i);
		}
		Vector features =  Vectors.dense(values);
		return features;
	}
	
	//Reads an attribute parameter "manually" if the @Parameter could not be used
	//to automatically initialize the field.
	protected Attribute getAttributeParameter(OperatorContext context, String paramName) {
		String attrNameString = context.getParameterValues(paramName).get(0);
		//it will be in the iport$0.get_attrName() format
		String[] parts = attrNameString.split("\\.");
		int port = Integer.parseInt(parts[0].substring("iport$".length()));
		
		int endIndex = parts[1].indexOf("()");
		String attrName = parts[1].substring("get_".length(), endIndex);
		
		return getInput(port).getStreamSchema().getAttribute(attrName);
		
	}

	//Close the spark context on shutdown
	@Override
	public void shutdown() throws Exception {
		javaContext.close();
		super.shutdown();
	}
	
	//Utility method to check whether the passed attribute is a specific type of list
	protected static boolean isList(Attribute attr,
			Class<?> clz) {
		Type type = attr.getType();
		if(type.getMetaType() == MetaType.LIST) {
			CollectionType cType = (CollectionType)type;
			if(cType.getElementType().getObjectType() == clz) {
				return true;
			}
		}
		return false;
	}

}
