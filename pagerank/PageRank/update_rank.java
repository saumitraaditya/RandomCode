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

public class update_rank extends Configured implements Tool
{
	public static class mapper extends Mapper<LongWritable, Text, Text, Text>
	{
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			//first value is nodeID
			String node = tokenizer.nextToken();
			//2nd value is current pagerank
			double pagerank = Double.parseDouble(tokenizer.nextToken());
			//skip previous pagerank,as it will be the current page rank
			//for next iteration.
			tokenizer.nextToken();
			//Flags to distinguish graph portion and page rank portion.
			String node_flag = "nodes";
			String rank_flag = "pagerank";
			//if there are no more fields it is a dangling node.
			if (tokenizer.hasMoreTokens()== false)
			{
				context.write(new Text(node), new Text(node_flag +"\t" + pagerank));
			}
			else
			{
				//to pass the current page rank to be used as previous in next iteration.
				StringBuffer buffer = new  StringBuffer().append(node_flag + "\t").append(pagerank).append("\t");
				LinkedList<String> Links = new LinkedList<String>();
				//iterate over and add all links to the adj list.
				while (tokenizer.hasMoreTokens())
				{
					Links.add(tokenizer.nextToken());
				}
				//iterate over the adj_list and emit page rank  contribution to each adj node.
				// the contribution is ((pagerank of node)/(outorder)) to each connected node.
				for (int i=0;i<Links.size();i++)
				{
					context.write(new Text(Links.get(i)), new Text(rank_flag +"\t" + (pagerank / Links.size())));
					buffer.append(Links.get(i)+"\t");
				}
				//emit the node ID with its current PR and list of adjacent nodes.
				//we need to do this as we wasnt to preserve the graph and update
				//it with latest PR(to be calculated) in reduce.
				//nodes,pagerank is used as flags to distinguish these two values.
				context.write(new Text(node), new Text(buffer.toString()));
			}
		}
	}
		
		public static class reducer extends Reducer<Text, Text, Text, Text>
		{
			//to be read from count file and passed to configuration.
			long total_nodes;
			//contribution from dangling nodes to be distributed evenly across all nodes.
			double dangling;
			//dampening factor is .85 from paper
			double damp_f = .85;
			
			//read the passed values from config file.
			public void setup(Context context) throws IOException, InterruptedException
			{
				dangling = Double.parseDouble(context.getConfiguration().get("dangling"));
				total_nodes = Long.parseLong(context.getConfiguration().get("total_nodes"));	
			}
			public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
			{
				double pagerank = 0;
				double prev_pagerank = 0;
				StringBuffer buffer = new StringBuffer();
				
				for (Text value:values)
				{
					StringTokenizer tokenizer = new StringTokenizer(value.toString());
					//two typed of values associated with keys.
					// 1. pagerank factors updated.
					// 2. adajacency list and previous pagerank.
					// have to differentiate based on flags set in map phase.
					if (tokenizer.nextToken().equals("nodes"))
					{
						//identifies that it is of type2.
						//builds the Adj. list
						//stores prev PR.
						prev_pagerank = Double.parseDouble(tokenizer.nextToken());
						while (tokenizer.hasMoreTokens())
						{
							buffer.append(tokenizer.nextToken()).append("\t");
						}
					}
					else
					{
						// if of type2, just get the updated PR factor and sum them all
						// to get updated pagerank for node.
						pagerank = pagerank + Double.parseDouble(tokenizer.nextToken());
					}
				}
				//final pagerank given by xpression.
				pagerank = ((pagerank + (dangling/total_nodes) -(1/total_nodes))*damp_f)+(1/total_nodes);
				//write in output file with updated values to be used as input for next iteration.
				context.write(key, new Text(pagerank + "\t" + prev_pagerank + "\t" + buffer.toString()));
			}
		}
	
	
	public int run(String[] args) throws Exception
	{
		Path input_path = new Path(args[0]);
		Path output_path = new Path(args[1]);
		String total_Nodes = args[2];
		String dangling = args[3];
		Configuration conf = getConf();
		conf.set("total_nodes", total_Nodes);
		conf.set("dangling",dangling);
		Job job = new Job(conf, "update ranks");
		job.setJarByClass(update_rank.class);
		job.setMapperClass(mapper.class);
		job.setReducerClass(reducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		return job.waitForCompletion(true) ? 0 : 1;
		
	}
}