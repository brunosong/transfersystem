package com.brunosong.transfer.system.dataaccess;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.brunosong.transfer.system.dataaccess")
@SpringBootConfiguration
public class TestDataAccessConfiguration {
//
//    private static final String IP = "localhost";
//    private static final int PORT = 27017;
//
//    private MongodExecutable mongodExecutable;
//
//    @Bean(destroyMethod = "stop")
//    public MongodExecutable embeddedMongoServer() throws IOException {
//        MongodStarter starter = MongodStarter.getDefaultInstance();
//        MongodConfig mongodConfig = MongodConfig.builder()
//                .net(new Net(IP, PORT, false))
//                .build();
//        mongodExecutable = starter.prepare(mongodConfig);
//        mongodExecutable.start();
//        return mongodExecutable;
//    }
//
//    @Bean
//    public MongoTemplate mongoTemplate() {
//        return new MongoTemplate(new SimpleMongoClientDatabaseFactory("mongodb://" + IP + ":" + PORT + "/test"));
//    }

}
