package collectData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
 
public class App3 {
 
    public static void main( String[] args ) throws Exception {
         
        String tableName = "paper_info";
        String rowKey = "info";
        String[] cfs = new String[] {"category","title","publisher_url","publisher_name","Issue_date","linkURL","Issue_name","Issue_Number","authorsNumber","Rank","authorname","authorURL"};         
        // configuration
        Configuration config = HBaseConfiguration.create();
        config.clear();
    //    config.set("hbase.master","hadoop-vm.itnp.kr");
     //   config.set("hbase.zookeeper.quorum", "hadoop-vm.itnp.kr");
     //   config.set("hbase.zookeeper.property.clientPort","2181");
 
        // 테이블 생성 & 컬럼 추가
        HBaseAdmin hbase = null;
        try {
            hbase = new HBaseAdmin(config);
            HTableDescriptor desc = new HTableDescriptor(tableName);
            for(int i=0; i<cfs.length; i++) {
                HColumnDescriptor meta = new HColumnDescriptor(cfs[i].getBytes());
                desc.addFamily(meta);
            }
            hbase.createTable(desc); // create
        } finally {
            if(hbase !=null) hbase.close();
        }
         
        HTable table =new HTable(config, tableName);
        try {
           
             
            //  테이블 데이터 조회
            Scan s =new Scan();
            ResultScanner rs = table.getScanner(s);
            for (Result r : rs) {
                for (KeyValue kv : r.raw()) {
                    System.out.println("row:" + new String(kv.getRow()) +"");
                    System.out.println("family:" + new String(kv.getFamily()) +":");
                    System.out.println("qualifier:" + new String(kv.getQualifier()) +"");
                    System.out.println("value:" + new String(kv.getValue()));
                    System.out.println("timestamp:" + kv.getTimestamp() +"");
                    System.out.println("-------------------------------------------");
                }
            }
 
            // 행    삭제
            List list =new ArrayList();
            Delete d1 =new Delete(rowKey.getBytes());
            list.add(d1);
            table.delete(list);
             
            // 테이블 drop
            HBaseAdmin admin =new HBaseAdmin(config);
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
             
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}   