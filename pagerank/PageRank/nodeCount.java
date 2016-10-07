package PageRank;
import java.net.URI;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.LongWritable;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;





public class nodeCount extends Configured implements Tool
{

	public static class mapper extends  Mapper<LongWritable, Text, Text, LongWritable>
	{
		//just count the number of lines in the input file
		//that will be equal to the number of nodes.
		Text count  = new Text("count");
		LongWritable one = new LongWritable(1);
	
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			context.write(count,one);
		}
	
	}
	
	public static class reducer extends Reducer<Text, LongWritable, Text, LongWritable>
	{
	    public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException 
	    {
	    	// initialize sum as zero.
	      long sum = 0;
	      // sum all values in the list for a given key.
	      for (LongWritable value : values) 
	      {
	        sum = sum +  value.get();
	      }
	      context.write(key, new LongWritable(sum));
	    }
	  }
	public int run(String[] args) throws Exception
	{
		Path input_path = new Path(args[0]);
		Path output_path = new Path(args[1]);
		Configuration conf = getConf();
		Job job = new Job(conf, "node_count");
		job.setJarByClass(nodeCount.class);
		job.setMapperClass(mapper.class);
		job.setCombinerClass(reducer.class);
		job.setReducerClass(reducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		return job.waitForCompletion(true) ? 0 : 1;
	  }
}