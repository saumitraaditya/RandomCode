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

public class initial_pagerank extends Configured implements Tool 
{
	
	
	public static  class mapper extends   Mapper<LongWritable, Text, Text, Text>
	{
		double pagerank;
		long total_nodes;
		double seed_value = 1.0;
		//get the number of nodes in graph.
		//starting rank of a node = 1/#nodes in graph
		public void setup(Context context) throws IOException, InterruptedException 
		{
			total_nodes = Long.parseLong(context.getConfiguration().get("count"));
			pagerank = seed_value / total_nodes;
		}
		public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException 
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			StringBuffer buffer = new StringBuffer();
			//flag signifies fisrt field in record,ie nodeID
			boolean flag = true;
			Text node = new Text("");
			String next;
			while (tokenizer.hasMoreTokens()) 
			{
				next = tokenizer.nextToken();
				if (flag) 
				{
					flag = false;
					String nodeID = next;
					node = new Text(nodeID);
					//in the beginning both current and previous pageranks are the same.
					buffer.append(pagerank+"\t"); //append new pagerank after nodeID
					buffer.append(pagerank+ "\t"); // append previous pagerank after new pagerank.
				} 
				else 
				{
					//append rest of adjacency list.
					buffer.append(next).append("\t");
				}
			}
			//emit in format
			//nodeID newPR oldPR <List of adajacent nodes>
			context.write(node, new Text(buffer.toString()));
		}
	}
	
	
	public int  run(String[] args) throws Exception
	{
			Path input_path = new Path(args[0]);
			Path output_path = new Path(args[1]);
			String count = args[2];
			Configuration conf = getConf();
			conf.set("count", count);
			Job job = new Job(conf, "starting_ranks");
			job.setJarByClass(getClass());
			FileInputFormat.addInputPath(job, input_path);
			FileOutputFormat.setOutputPath(job, output_path);
			job.setMapperClass(mapper.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(Text.class);
			return job.waitForCompletion(true) ? 0 : 1;
			
	}
	
	
}
	
	
