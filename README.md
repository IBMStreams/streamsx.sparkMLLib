# streamsx.sparkMLlib
Toolkit for real-time scoring using Spark MLLib library.

This toolkit implements the NLS feature. Use the guidelines for the message bundle that are described in [Messages and National Language Support for toolkits](https://github.com/IBMStreams/administration/wiki/Messages-and-National-Language-Support-for-toolkits)

To learn more about Streams:
* [IBM Streams on Github](http://ibmstreams.github.io)
* [Introduction to Streams Quick Start Edition](http://ibmstreams.github.io/streamsx.documentation/docs/4.3/qse-intro/)
* [Streams Getting Started Guide](http://ibmstreams.github.io/streamsx.documentation/docs/4.3/qse-getting-started/)
* [StreamsDev](https://developer.ibm.com/streamsdev/)

# Developing and running applications that use the SparkMLLib Toolkit

To create applications that use the SparkMLLib Toolkit, you must configure either Streams Studio
or the SPL compiler to be aware of the location of the toolkit. 

## Before you begin

* Install IBM InfoSphere Streams.  Configure the product environment variables by entering the following command: 
      source <Streams Installation Directory>/bin/streamsprofile.sh
* Generate a Spark model as described in the next section and save it to the local filesystem or HDFS.
* The worker nodes which execute the toolkit code do not require an separate Apache Spart installation.

## Spark Models
* This toolkit provides a number of operators that can load a stored Spark MLlib model and use it to perform real time scoring on incoming tuple data. 
* To generate the model files point the java classpath either to an installed Apache Spark version (e.g.: $SPARK_HOME/jars) or to the download directory of the streamsx.sparkmllib toolkit (e.g.: $STREAMS_SPLPATH/com.ibm.streamsx.sparkmllib/opt/downloaded)

For example, the SparkCollaborativeFilteringALS operator
can load a Spark collaborative filtering model (of type MatrixFactorizationModel in the Spark API). In order for the operator to be able to use this model within Streams, the Spark program that created the original
model must store the model. The following scala code demonstrates how the model can be saved to HDFS:
```
		//Generate a MatrixFactorizationModel by training against test data
		val model = ALS.train(training, rank, numIter, lambda)
		
		//Save the generated model to the filesystem
		model.save(sparkContext, "hdfs://some/path/my_model")
```

Once the model has been persisted, the path to the persisted model would be passed in as a parameter to the SparkCollaborativeFilteringALS operator. The following code 
demonstrates how this would be done in the SPL program:


```
	(stream&lt;int32 user, int32 counter, list&lt;int32&gt; analysisResult&gt; SparkCollaborativeFilteringALSOut) as
			SparkCollaborativeFilteringALSOp1 =
			SparkCollaborativeFilteringALS(InputPort1)
		{
			param
				analysisType : RecommendProducts ;
				attr1 : Beacon_1_out0.user ;
				attr2 : Beacon_1_out0.counter ;
				modelPath : "hdfs://some/path/my_model" ;
		}
```		

On initialization, the operator will load the model. Each incoming tuple will be used to generate a score using the model and the score would be passed as an attribute called 'analysisResult' on the output schema.

## To Use this Toolkit in you Application

After the location of the toolkit is communicated to the compiler, the SPL artifacts that are specified in the toolkit
can be used by an application. The application can include a use directive to bring the necessary namespaces into scope.
Alternatively, you can fully qualify the operators that are provided by toolkit with their namespaces as prefixes.

1. Make sure that a trained Spark model has been saved to the local file system or on HDFS. Alternatively you can bundle the model files into the sab-file (see sample).
2. Configure the SPL compiler to find the toolkit root directory. Use one of the following methods:
  * Set the **STREAMS_SPLPATH** environment variable to the root directory of a toolkit or multiple toolkits (with : as a separator).
    For example:
        export STREAMS_SPLPATH=$STREAMS_INSTALL/toolkits/com.ibm.streamsx.sparkmllib
  * Specify the **-t** or **--spl-path** command parameter when you run the **sc** command. For example:
        sc -t $STREAMS_INSTALL/toolkits/com.ibm.streamsx.sparkmllib -M MyMain
    where MyMain is the name of the SPL main composite.
    **Note**: These command parameters override the **STREAMS_SPLPATH** environment variable.
  * Add the toolkit location in InfoSphere Streams Studio.
3. Develop your application. 
4. Build your application.  You can use the **sc** command or Streams Studio.  
5. Start the InfoSphere Streams instance. 
6. Run the application. You can submit the application as a job by using the **streamtool submitjob** command or by using Streams Studio. 

# What's changed
## Version 1.3.1
* Update internationalization messages

## Versiom 1.3.0
* The toolkit not longer depend on an installation of Apache Spark and does not need a SPARK_HOME environment variable,
  The toolkit bundles the Sparkmllib libraries along with the toolkit code
* Correct streams studio classpath settings in toolkit project and sample
* Use studio settings in sample makefile if build from studio
* Update description
* Remove compiler warnings
* Describe spark master parameter
* Add framework tests
* Add test and release targets to main build.xml
* New parameter paraneter getProbabilities in operator SparkNaiveBayes

## Version 1.2.0
* Use of actual stark version 2.4.0

## Version 1.1.1
* Some path changes

## Version 1.1.0
* Internationalization for languages de_DE, es_ES, fr_FR, it_IT, ja_JP, ko_KR, pt_BR, ru_RU, zh_CN, zh_TW

