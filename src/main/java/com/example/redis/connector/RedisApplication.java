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

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            String hostName = System.getenv("REDIS_CLUSTER_ENDPOINT");
            List<InetAddress> clusterNodes = Arrays.asList(InetAddress.getAllByName(hostName));

            Set<HostAndPort> hostAndPorts = clusterNodes.stream()
                    .map(inetAddress -> new HostAndPort(inetAddress.getCanonicalHostName(), 6379))
                    .collect(Collectors.toSet());

            logger.error(hostAndPorts.toString());
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
