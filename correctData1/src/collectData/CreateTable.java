package collectData;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class CreateTable {
  public static void main(String[] args) throws IOException {
    Configuration config = HBaseConfiguration.create();

    // Example of setting zookeeper values for HDInsight
    //   in code instead of an hbase-site.xml file
    //
    // config.set("hbase.zookeeper.quorum",
    //            "zookeepernode0,zookeepernode1,zookeepernode2");
    //config.set("hbase.zookeeper.property.clientPort", "2181");
    //config.set("hbase.cluster.distributed", "true");

    // create an admin object using the config
    HBaseAdmin admin = new HBaseAdmin(config);

    // create the table...
    HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf("paper_info"));
    // ... with two column families
    tableDescriptor.addFamily(new HColumnDescriptor("info"));
      admin.createTable(tableDescriptor);
      System.out.println();
  	}
  }
