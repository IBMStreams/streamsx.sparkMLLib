import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

public class JavaTrainingApplication {
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("SparkStreamsSampleTrainingApplication");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		
		JavaRDD<String> lines = jsc.textFile("data/random_2d_training.csv");
		@SuppressWarnings("serial")
		JavaRDD<Vector> parsedData = lines.map(
	      new Function<String, Vector>() {
			@Override
			public Vector call(String s) {
			    String[] sarray = s.split(",");
		          double[] values = new double[sarray.length];
		          for (int i = 0; i < sarray.length; i++) {
		            values[i] = Double.parseDouble(sarray[i]);
		          }
		          return Vectors.dense(values);
			}
	      }
	    );
		parsedData.cache();
		
	    int numClusters = 10;
	    int numIterations = 20;
	    KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);
	    clusters.save(jsc.sc(), "etc/kmeans_model");
	    jsc.close();
	}
}