package com.example.blogssm.CanalClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2023/08/30  15:20
 */
@Component
public class CanalClient {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void start() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
                new InetSocketAddress("81.70.175.60",11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            // 打开连接
            connector.connect();
            // 配置扫描范围
            connector.subscribe(".*\\..*");
            // 回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
            connector.rollback();
            int totalEmptyCount = 1200;
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
//                    System.out.println("empty count : " + emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    emptyCount = 0;
                    // 打印SQL语句
                    System.out.printf("message[batchId=%s,size=%s] \n", batchId, size);
                    printEntry(message.getEntries());
                }
                connector.ack(batchId); // 提交确认
                // connector.rollback(batchId); // 处理失败, 回滚数据
            }
//            System.out.println("empty too many times, exit");
        } finally {
            connector.disconnect();
        }
    }
    private  void printEntry(List<CanalEntry.Entry> entrys) {
        for (CanalEntry.Entry entry : entrys) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型，跳过
                continue;
            }
            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            CanalEntry.RowChange rowChage;
            try {
                rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(), e);
            }
            //获取操作类型：insert/update/delete 等类型
            CanalEntry.EventType eventType = rowChage.getEventType();

            //打印Header信息
//            System.out.println(String.format("================》; binlog[%s:%s] , name[%s,%s] , eventType : %s",
//                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
//                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
//                    eventType));

            //判断是否是DDL语句
            if (rowChage.getIsDdl()) {

//                System.out.println("================》;isDdl: true,sql:" + rowChage.getSql());
            }

            //获取RowChange对象里的每一行数据，打印出来
            for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
                //如果是删除语句
                if (eventType == CanalEntry.EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                    String tableName=entry.getHeader().getTableName();
                    System.out.println("---------------------------------");
                    System.out.println(tableName);
                    Integer uid=0;
                    if(tableName.equals("articleinfo")){
                        for(Column column: rowData.getBeforeColumnsList()){
                            System.out.println("---------------------------------");
                            System.out.println(column);
                            if(column.getName().equals("uid")) {
                                uid= Integer.valueOf(column.getValue());
                            }
                        }
                    }
                    rabbitTemplate.convertAndSend("canal.exchange", "canal.routingKey", "user:"+String.valueOf(uid)+":articles");
                    //如果是新增语句
                } else if (eventType == CanalEntry.EventType.INSERT) {
//                    printColumn(rowData.getAfterColumnsList());
//                    System.out.println(entry.getHeader().getSchemaName());
//                    System.out.println(entry.getHeader().getTableName());
//                    System.out.println(rowData.getAfterColumnsList());
                    //if新增的是文章，那么我需要去删除user:1:articles表
                    String tableName=entry.getHeader().getTableName();
//                    System.out.println("---------------------------------");

//                    System.out.println(tableName);
                    Integer uid=0;
                    if(tableName.equals("articleinfo")){
                        for(Column column: rowData.getAfterColumnsList()){
//                            System.out.println("---------------------------------");
//                            System.out.println(column);
                            if(column.getName().equals("uid")) {
                                uid= Integer.valueOf(column.getValue());
                            }
                        }
                    }
                    rabbitTemplate.convertAndSend("canal.exchange", "canal.routingKey", "user:"+String.valueOf(uid)+":articles");
                    //如果是更新的语句
                } else {
                    Integer uid=0;
                    String tableName=entry.getHeader().getTableName();
                    if(tableName.equals("articleinfo")){
                        for(Column column: rowData.getAfterColumnsList()){
                            System.out.println("---------------------------------");
                            System.out.println(column);
                            if(column.getName().equals("id")) {
                                uid= Integer.valueOf(column.getValue());
                            }
                        }
                        rabbitTemplate.convertAndSend("canal.exchange", "canal.routingKey", "article:"+String.valueOf(uid));
                    }

                    //变更前的数据
//                    System.out.println("------->; before");
//                    printColumn(rowData.getBeforeColumnsList());
                    //变更后的数据
//                    System.out.println("------->; after");
//                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }
    private  void printColumn(List<Column> columns) {
        for (Column column : columns) {
//            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
//    private void sendToRabbitMQ(String tableName, String id) {
//        if (rabbitTemplate == null) {
//            System.err.println("RabbitTemplate is not initialized!");
//            return;
//        }
//        try {
//            String key =tableName + ":" + id;
//            rabbitTemplate.convertAndSend("canal.exchange", "canal.routingKey", key);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}