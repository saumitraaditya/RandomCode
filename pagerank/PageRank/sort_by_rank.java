package PageRank;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer.Context;
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

import PageRank.graph_info.mapper;
import PageRank.graph_info.reducer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.hadoop.io.WritableComparator;

public class sort_by_rank extends Configured implements Tool
{
	public static class mapper extends  Mapper<LongWritable, Text, DoubleWritable, Text>
	{
	
		
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString()) ;
			//the first field is node ID
			Text node = new Text(tokenizer.nextToken());
			//2nd field is the pagerank.
			DoubleWritable pagerank = new DoubleWritable(Double.parseDouble(tokenizer.nextToken()));
			//emit in reverse order <PR:NodeID>, sorting is done on keys automatically.
			context.write(pagerank,node);
		}
	
	}
	
	public static class reducer extends Reducer<DoubleWritable, Text, Text, Text>
	{
	    public void reduce(DoubleWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	    {
	    	for (Text value:values)
	    	{
	    		//again reverse the order to get output in recommended format.
	    		//<Node_ID:PR>
	    		context.write(new Text(value), new Text(key.toString()));
	    	}
	    }
	  }
	
	//override default comparator to sort in descending order.
	 public static class DecreasingDoubleComparator extends DoubleWritable.Comparator 
	 {
	        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
	            return -super.compare(b1, s1, l1, b2, s2, l2);
	        }

	        static {
	            WritableComparator.define(DecreasingDoubleComparator.class, new DecreasingDoubleComparator());
	        }
	    }
	
	public int run(String[] args) throws Exception
	{
		Path input_path = new Path(args[0]);
		Path output_path = new Path(args[1]);
		Configuration conf = getConf();
		Job job = new Job(conf, "sorted_output");
		job.setJarByClass(sort_by_rank.class);
		job.setMapperClass(mapper.class);
		job.setReducerClass(reducer.class);
		job.setSortComparatorClass(DecreasingDoubleComparator.class);
		job.setMapOutputKeyClass(DoubleWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		return job.waitForCompletion(true) ? 0 : 1;
	}
}