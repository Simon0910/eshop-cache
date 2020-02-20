package com.roncoo.eshop.cache;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.service.keys.ProductKey;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class JedisTest {

    public static void main(String[] args) throws Exception {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        nodes.add(new HostAndPort("192.168.198.130", 7001));
        nodes.add(new HostAndPort("192.168.198.131", 7003));
        nodes.add(new HostAndPort("192.168.198.132", 7005));
        JedisCluster jedisCluster = new JedisCluster(nodes);

        String s1 = jedisCluster.get(ProductKey.productInfo.generateKey(String.valueOf(1)));
        System.out.println(s1);
        ProductInfo productInfo = JSONObject.parseObject(s1, ProductInfo.class);

        String s2 = jedisCluster.get(ProductKey.shopInfo.generateKey(String.valueOf(1)));
        System.out.println(s2);
        ShopInfo shopInfo = JSONObject.parseObject(s2, ShopInfo.class);

        System.out.println();
    }

}
