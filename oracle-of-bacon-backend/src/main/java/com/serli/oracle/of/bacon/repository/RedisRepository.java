package com.serli.oracle.of.bacon.repository;

import java.util.List;
import redis.clients.jedis.Jedis;

import static com.google.common.primitives.Longs.min;


public class RedisRepository {
    Jedis jedis = new Jedis("redis://redis-13100.c10.us-east-1-3.ec2.cloud.redislabs.com:13100");
    public List<String> getLastTenSearches() {
        return jedis.lrange("lastSearch",0,9);
    }

    public void addQuery(String searchQuery) {
        jedis.lpush("lastSearch",searchQuery);
        if (jedis.llen("lastSearch")>10)
            jedis.rpop("lastSearch");
    }
}
