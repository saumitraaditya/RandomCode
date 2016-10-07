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

public class dangling_nodes extends Configured implements Tool 
{
	
	public static class mapper extends Mapper<LongWritable, Text, Text, DoubleWritable>
	{
		public Text key_dangling = new Text("dangling");
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString()) ;
			//first 3 entries for Node ID, current rank,previous rank.
			tokenizer.nextToken();
			//get pagerank for the node
			double pagerank = Double.parseDouble(tokenizer.nextToken()) ;
			//skip next field 
			tokenizer.nextToken();
			//check if it has any more tokens 
			if (tokenizer.hasMoreTokens() == false)
			{
				context.write(key_dangling, new DoubleWritable(pagerank));
			}
		}
	}
	
	public static class reducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> 
	{
		public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException
		{
			//initialize total to 0.
			double total = 0;
			//iterate over the list summing values.
			for (DoubleWritable value: values)
			{
				total = total + value.get();
			}
			context.write(key, new DoubleWritable(total));
		}
	}
	
	
	public int run(String[] args) throws Exception
	{
		//takes input path and output path as arguments.
		Path input_path = new Path(args[0]);
		Path output_path = new Path (args[1]);
		Configuration conf = getConf();
		//delete output path as it has to be over written
		String uriStr = "s3://aditya-bucket/output_pagerank/"; 
		URI uri = URI.create(uriStr);
		FileSystem fs = FileSystem.get(uri, conf);
		fs.delete (output_path, true);
		//FileSystem.get(getConf()).delete (output_path, true);
		//set other configurations required for the job.
		Job job = new Job(getConf(), "dangling nodes");
		job.setNumReduceTasks(1);
		job.setJarByClass(dangling_nodes.class);
		job.setMapperClass(mapper.class);
		job.setCombinerClass(reducer.class);
		job.setReducerClass(reducer.class);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		return job.waitForCompletion(true) ? 0 : 1;
		
	}
}