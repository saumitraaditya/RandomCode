package PageRank;
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
import java.net.URI;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.util.*;

public class convergence extends Configured implements Tool
{
	public static class mapper extends Mapper<LongWritable, Text, Text, DoubleWritable>
	{
		Text delta = new Text("rank_diff");
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString()) ;
			//first field is nodeID
			Text nodeID = new Text(tokenizer.nextToken());
			//2nd field is latest pagerank
			double pagerank = Double.parseDouble(tokenizer.nextToken());
			//3rd field is prev pagerank
			double prev_pagerank = Double.parseDouble(tokenizer.nextToken());
			DoubleWritable diff = new DoubleWritable(Math.abs(pagerank - prev_pagerank));
			//emit the diff which has to be collected and summed in the reducer.
			context.write(delta, diff);
		}
		
	}
	
	public static class reducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> 
	{
		public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException
		{
			//sum all the deltas to calculate total delta.
			double sum = 0;
			for (DoubleWritable value:values)
			{
				sum = sum + value.get();
			}
			context.write(key,new DoubleWritable(sum));
		}
	}
	public int run(String[] args) throws Exception
	{
		Path input_path = new Path(args[0]);
		Path output_path = new Path(args[1]);
		Configuration conf = getConf();
		String uriStr = "s3://aditya-bucket/output_pagerank/"; 
		URI uri = URI.create(uriStr);
		FileSystem fs = FileSystem.get(uri, conf);
		fs.delete (output_path, true);
		//FileSystem.get(getConf()).delete (output_path, true);
		Job job = new Job(conf, "convergence");
		job.setJarByClass(convergence.class);
		job.setMapperClass(mapper.class);
		job.setCombinerClass(reducer.class);
		job.setReducerClass(reducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(DoubleWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}