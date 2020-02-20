package com.roncoo.eshop.cache.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;
import redis.clients.jedis.util.Hashing;
import redis.clients.jedis.util.Sharded;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class Config {

    @Bean
    public JedisCluster JedisClusterFactory() {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("192.168.198.130", 7001));
        nodes.add(new HostAndPort("192.168.198.131", 7003));
        nodes.add(new HostAndPort("192.168.198.132", 7005));
        JedisCluster jedisCluster = new JedisCluster(nodes);
        return jedisCluster;
    }

    @Bean
    public ShardedJedisPool shardedJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(2);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        config.setBlockWhenExhausted(true);
        config.setMaxWaitMillis(10 * 1000);
        JedisShardInfo info1 = new JedisShardInfo("192.168.198.130", 7001, 10 * 1000);
        JedisShardInfo info2 = new JedisShardInfo("192.168.198.131", 7003, 10 * 1000);
        JedisShardInfo info3 = new JedisShardInfo("192.168.198.132", 7005, 10 * 1000);
        List<JedisShardInfo> shards = new ArrayList<>(4);
        shards.add(info1);
        shards.add(info2);
        shards.add(info3);
        return new ShardedJedisPool(config, shards, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }
}
