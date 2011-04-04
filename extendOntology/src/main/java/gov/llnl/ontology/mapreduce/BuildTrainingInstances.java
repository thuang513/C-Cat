/*
 * Copyright (c) 2010, Lawrence Livermore National Security, LLC. Produced at
 * the Lawrence Livermore National Laboratory. Written by Keith Stevens,
 * kstevens@cs.ucla.edu OCEC-10-073 All rights reserved. 
 *
 * This file is part of the C-Cat package and is covered under the terms and
 * conditions therein.
 *
 * The S-Space package is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation and distributed hereunder to you.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND NO REPRESENTATIONS OR WARRANTIES,
 * EXPRESS OR IMPLIED ARE MADE.  BY WAY OF EXAMPLE, BUT NOT LIMITATION, WE MAKE
 * NO REPRESENTATIONS OR WARRANTIES OF MERCHANT- ABILITY OR FITNESS FOR ANY
 * PARTICULAR PURPOSE OR THAT THE USE OF THE LICENSED SOFTWARE OR DOCUMENTATION
 * WILL NOT INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER
 * RIGHTS.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package gov.llnl.ontology.mapreduce;

import gov.llnl.ontology.evidence.AttributeMap;
import gov.llnl.ontology.evidence.CousinTrainingInstanceBuilder;
import gov.llnl.ontology.evidence.EvidenceInstanceBuilder;
import gov.llnl.ontology.evidence.HypernymTrainingInstanceBuilder;

import gov.llnl.ontology.table.WordNetEvidenceSchema;

import edu.ucla.sspace.util.SerializableUtil;

import edu.ucla.sspace.vector.DoubleVector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.filecache.DistributedCache;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.hbase.HBaseConfiguration;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.File;
import java.io.IOException;

import java.net.URI;


/**
 * This Map/Reduce executible counts the number of unique words generated by
 * each document source.
 *
 * @author Keith Stevens
 */
public class BuildTrainingInstances extends Configured implements Tool {

  /**
   *The logger for this class.
   */
  private static final Log LOG =
    LogFactory.getLog(BuildTrainingInstances.class);

  private static final String BUILDER = "builder";
  private static final String ATTR_MAP = "attributeMap";
  private static final String SOURCE = "source";

  /**
   * The {@link Configuration} for hadoop and hbase.
   */
  private HBaseConfiguration conf;

  /**
   * Runs the map reducer.
   */
  public static void main(String[] args) {
    try {
      ToolRunner.run(new Configuration(), new BuildTrainingInstances(), args);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public int run(String[] args) throws Exception {
    // Validate the arguments.
    if (args.length != 2) {
      System.out.println("usage: java BuildTrainingInstances " +
                         "source [cousin|hypernym] <attrMap>");
      return 1;
    }

    conf = new HBaseConfiguration();

    conf.set(SOURCE, args[0]);
    conf.set(ATTR_MAP, args[1]);

    try {
      // Run the first map/reduce job.
      LOG.info("Preparing map/reduce system.");
      Job job = new Job(conf, "Building Classifer Training Instances");

      // Setup the mapper.
      job.setJarByClass(BuildTrainingInstances.class);

      conf.set(BUILDER, args[1]);
      // Create the builder.
      FileSystem fs = FileSystem.get(conf);
      AttributeMap attributeMap = SerializableUtil.load(fs.open(
            new Path(args[2])));
      EvidenceInstanceBuilder builder = (args[1].equals("cousin"))
        ? new CousinTrainingInstanceBuilder(attributeMap, false, args[0])
        : new HypernymTrainingInstanceBuilder(attributeMap, false, args[0]);

      // Add a scanner that requests the requested similarity column.
      Scan scan = new Scan();
      builder.addToScan(scan);

      // Setup the mapper.
      TableMapReduceUtil.initTableMapperJob(
          WordNetEvidenceSchema.tableName, scan, InstanceMapper.class, 
          Text.class, Text.class, job);

      // Setup the reducer.
      job.setOutputFormatClass(TextOutputFormat.class);
      TextOutputFormat.setOutputPath(
          job, new Path("/data/classifierTrainingInstances"));
      job.setNumReduceTasks(0);

      // Run the first job.
      LOG.info("Extracting training instasnces.");
      job.waitForCompletion(true);
      LOG.info("Extraction completed.");
    }
    catch (Exception e) {
      e.printStackTrace();
      return 1;
    }

    return 0;
  }

  /**
   * This mapper traverses each row in the {@link WordNetEvidenceSchema} table
   * and emits each word that appears from each document source.
   */
  public static class InstanceMapper extends TableMapper<Text, Text> {

    private EvidenceInstanceBuilder builder;

    @Override
    protected void setup(Context context) 
        throws IOException, InterruptedException {
      Configuration conf = context.getConfiguration();
      String source = conf.get(SOURCE);
      FileSystem fs = FileSystem.get(conf);
      AttributeMap attributeMap = SerializableUtil.load(fs.open(
            new Path(conf.get(ATTR_MAP))));
      builder = (conf.get(BUILDER).equals("cousin"))
        ? new CousinTrainingInstanceBuilder(attributeMap, false, source)
        : new HypernymTrainingInstanceBuilder(attributeMap, false, source);
    }

    /**
     */
    @Override
    public void map(ImmutableBytesWritable key, Result row, Context context)
        throws IOException, InterruptedException {
      context.getCounter("Forming training instances", "seen row").increment(1);
      DoubleVector instance = builder.getInstanceFrom(row);
      if (instance != null) {
          context.write(
              new Text(""), new Text(builder.instanceToString(instance)));
        context.getCounter("Forming training instances", "good row").increment(1);
      }
    }
  }
}
