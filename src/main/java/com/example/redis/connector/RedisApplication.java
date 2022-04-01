package com.example.redis.connector;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.Set;

@SpringBootApplication
public class RedisApplication implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(RedisApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Application");
        SpringApplication.run(RedisApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            String node1 = System.getenv("REDIS_NODE1");
            String node2 = System.getenv("REDIS_NODE2");
            String node3 = System.getenv("REDIS_NODE3");
            int port = Integer.parseInt(System.getenv("REDIS_PORT"));
            logger.info("Adding nodes {} , {}, {} -> Port = {}", node1, node2, node3, port);
            Set<HostAndPort> hostAndPorts = Set.copyOf(Arrays.asList(new HostAndPort(node1, port),
                    new HostAndPort(node2, port),
                    new HostAndPort(node3, port)));

            // int connectionTimeout, int soTimeout, int maxAttempts, String password, String clientName, GenericObjectPoolConfig<Connection> poolConfig, boolean ssl)
            int timeOuts = 10000;
            GenericObjectPoolConfig<Connection> connectionPoolConfig = new GenericObjectPoolConfig<>();

            JedisCluster cluster = new JedisCluster(hostAndPorts, timeOuts, timeOuts, 2, null,
                 null, connectionPoolConfig, true);

            String str = cluster.set("BESTIES", "ROMEO/JULIET");
            logger.info(str);

            logger.info("Invoking Business Application");
        } catch (Exception ex) {
            logger.info("Failed to get connection", ex);
        }

    }
}
