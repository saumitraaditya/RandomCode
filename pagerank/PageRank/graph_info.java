package PageRank;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper.Context;
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


public class graph_info extends Configured implements Tool
{
	public static class mapper extends  Mapper<LongWritable, Text, Text, LongWritable>
	{
	
		Text links = new Text("links");
		long link_count;
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
		{
			StringTokenizer tokenizer = new StringTokenizer(value.toString());
			//skip the field containing nodeID.
			tokenizer.nextToken();
			link_count = tokenizer.countTokens();
			context.write(links, new LongWritable(link_count));
		}
	
	}
	
	public static class reducer extends Reducer<Text, LongWritable, Text, Text>
	{
		long total_nodes;
		public void setup(Context context) throws IOException, InterruptedException
		{
			total_nodes = Long.parseLong(context.getConfiguration().get("total_nodes"));	
		}
		
	    public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException 
	    {
		      long min_links = 9999999;
		      long max_links = -9999999;
		      long total_links = 0;
		      double avg_links;
		      long temp_val;
		      // sum all values in the list for a given key.
		      
		      for (LongWritable value : values) 
		      {
		    		temp_val = 	value.get();
		    		if (min_links > temp_val)
		    		{
		    			min_links = temp_val;
		    		}
		    		if (max_links < temp_val)
		    		{
		    			max_links = temp_val;
		    		}
		    		total_links = total_links + temp_val;
		    	
		      }
		      avg_links = total_links/total_nodes;
		      Text output = new Text("total_nodes\t"+total_nodes+"\ttotal_links\t"+total_links+"\tmin_links\t"+min_links 
		    		  		+"\tmax_links\t"+max_links+"\tavg_links\t"+avg_links);
		      context.write(new Text("graph_summary"), output);
	    	
	    	
	    }
	  }
	public int run(String[] args) throws Exception
	{
		Path input_path = new Path(args[0]);
		Path output_path = new Path(args[1]);
		String total_nodes = args[2];
		Configuration conf = getConf();
		conf.set("total_nodes", total_nodes);
		Job job = new Job(conf, "graph_info");
		job.setJarByClass(graph_info.class);
		job.setMapperClass(mapper.class);
		job.setReducerClass(reducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(1);
		FileInputFormat.addInputPath(job, input_path);
		FileOutputFormat.setOutputPath(job, output_path);
		return job.waitForCompletion(true) ? 0 : 1;
	}
	public static void main(String[] args) throws Exception 
	{
		int exitCode = ToolRunner.run(new Configuration(), new graph_info(), args);
		System.exit(exitCode);
		}
}