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
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;

public class pagerank extends Configured implements Tool
{
	public int run(String[] args) throws Exception 
	{
		//starting time of the job.
		long start = System.currentTimeMillis();
		String input_path = args[0];
		String output_path = args[1];
		int iteration_limit = Integer.parseInt(args[2]);
		//maximum allowed difference between previous and updated pagerank.
		//to calculate if we have reached convergence.
		double delta = Double.parseDouble(args[3]);
		Configuration conf = getConf();
		String uriStr = "s3://aditya-bucket/output_pagerank/"; 
		URI uri = URI.create(uriStr);
		FileSystem fs = FileSystem.get(uri, conf);
		//FileSystem fs = FileSystem.get(getConf());
		//the output folders for the intermediate results
		String output = args[1] + File.separator + "pagerank_v" ;
		//calculate the number of nodes in the graph.
		// store the results in count folder
		ToolRunner.run(getConf(), new nodeCount(), new String[] { input_path,output_path  + File.separator + "count" } ) ;
		//read the count from the file created in the above folder
		String count = readfile (fs, args[1] + File.separator + "count") ;
		//job to get other information as required in assignment for graph, pass count as argument.
		ToolRunner.run(getConf(), new graph_info(), new String[] { input_path, output_path  + File.separator + "graph_info",count } ) ;
		//calculate initial pagerank which is (1/total nodes) and prepare a input file in format
		// NodeID:updated pagerank:last pagerank: <Adjacency lis>
		ToolRunner.run(getConf(), new initial_pagerank(), new String[] { input_path, output+ String.valueOf(0), count } ) ;
		//variable to keep track of iterations required for reaching convergence.
		int convergence_count = 0 ;
		//till the limit is not breached or , we reach a convergence continue
		while ( convergence_count < iteration_limit ) 
		{
			//get contribution from all dangling nodes which is distributed evenly across all nodes.
			ToolRunner.run(getConf(), new dangling_nodes(), new String[] { output + String.valueOf(convergence_count), output_path  + File.separator + "dangling" } ) ;
			String dangling = readfile(fs, args[1] + File.separator + "dangling") ;
			// the value calculated from above is passed as parameter to the next iteration for updating pagerank
			ToolRunner.run(getConf(), new update_rank(), new String[] { output + String.valueOf(convergence_count), output 
					+ String.valueOf(convergence_count+1), count, dangling } ) ;
			//check if convergence is reached after updating the rank
			//basically is sum of delta of updated and last pagerank of all nodes.
			ToolRunner.run(getConf(), new convergence(), new String[] { output + String.valueOf(convergence_count+1), output_path  + File.separator + "convergence" } ) ;
			double slackness = Double.parseDouble(readfile (fs, output_path + File.separator + "convergence")) ;
			//check it against the required accuracy level ie slackness allowed.
			//if reached that level of accuracy stop. 
			if ( slackness <= delta )
			{
				break ;
			}
				
				convergence_count++;
		}
		//sort the final output in descending order of their pageranks.
		ToolRunner.run(getConf(), new sort_by_rank(), new String[] { output + String.valueOf(convergence_count+1), args[1] + File.separator + "sorted_output" } ) ;
		//other details to be displayed on stdout.
		System.out.println("convergence_count=\t"+convergence_count);
		long end = System.currentTimeMillis();
		System.out.printf("%s job completed in %dms%n", "pagerank", end - start);
		return 0;
	}
	private static String readfile (FileSystem fs, String path) throws IOException 
	{
		String file = path + File.separator + "part-r-00000";
		InputStreamReader isr = new InputStreamReader(fs.open(new Path(file)));
		BufferedReader br = new BufferedReader(isr);
		StringTokenizer tokenizer = new StringTokenizer(br.readLine()) ;
		//first field is just the label.
		tokenizer.nextToken() ;
		String result = tokenizer.nextToken() ;
		br.close() ;
		return result ;
		}
	
		
	public static void main(String[] args) throws Exception 
	{
		
		int exitCode = ToolRunner.run(new Configuration(), new pagerank(), args);
		System.exit(exitCode);
	}
}