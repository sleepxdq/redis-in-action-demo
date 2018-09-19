import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

import java.util.*;

/**
 * @author: 徐东强
 * @date: 2018/9/16 下午1:36
 * @description: 五种数据结构的命令用法
 **/
public class Chapter03 {

    public static void main(String[] args) {
        new Chapter03().run();
    }

    private void run(){
        Jedis conn = new Jedis("localhost");
        //string(conn);
//        list(conn);
//        set(conn);
//        hash(conn);
        zset(conn);
    }

    private void string(Jedis conn){
        conn.del("key");
        System.out.println("====字符串常用命令=====");
        String key = conn.get("key");
        System.out.println("key = "+ key);
        Long incr = conn.incr("key");
        System.out.println(key + " + 1 = " + incr);
        Long incr15 = conn.incrBy("key", 15);
        System.out.println(incr + " + 15 = " + incr15);
        Long decr1 = conn.decr("key");
        System.out.println(incr15 + " - 1 = " + decr1);
        Long decr4 = conn.decrBy("key", 4);
        System.out.println(decr1 + " - 4 = " + decr4);
        Double incrByFloat = conn.incrByFloat("key", 22.03D);
        System.out.println(decr4 + " + 22.03 = " + incrByFloat);

        conn.del("new-string-key");
        conn.append("new-string-key", "hello");
        conn.append("new-string-key", " world!");
        String hellworld = conn.get("new-string-key");
        System.out.println("new-string-key:" + hellworld);
        String getrange05 = conn.getrange("new-string-key", 0, 5);
        System.out.println("helloworld(0,5):" + getrange05);
        conn.setrange("new-string-key", 11, " xdq!");
        assert conn.get("new-string-key").equals("hello world xdq!");
        System.out.println(conn.get("new-string-key"));

        conn.del("bit-string-key");
        conn.set("bit-string-key", "1001");
        conn.setbit("bit-string-key", 1, "1");
        System.out.println("1001 : " + conn.get("bit-string-key"));

    }

    private void list(Jedis conn){
        System.out.println("=====列表常用命令=====");
        conn.del("list-key");
        conn.lpush("list-key", "1");
        System.out.println(conn.lrange("list-key", 0, -1));
        conn.rpush("list-key", "2");
        System.out.println(conn.lrange("list-key", 0, -1));
        assert Objects.equals("1", conn.lindex("list-key", 0));
        conn.lpush("list-key", "0");
        conn.rpush("list-key", "3");
        assert Objects.equals(Arrays.asList("0", "1", "2", "3"), conn.lrange("list-key", 0, -1));
        System.out.println(conn.lrange("list-key", 0, -1));
        String rpop = conn.rpop("list-key");
        System.out.println("rpop:" + rpop);
        conn.lpop("list-key");
        assert Objects.equals(Arrays.asList("1", "2"), conn.lrange("list-key", 0, -1));
        System.out.println(conn.lrange("list-key", 0, -1));
        String ltrim = conn.ltrim("list-key", 0, 0);
        System.out.println("ltrim:" + ltrim);
        assert Objects.equals(Collections.singletonList("1"), conn.lrange("list-key", 0, -1));
        System.out.println(conn.lrange("list-key", 0, -1));

        conn.del("new-list-key1");
        conn.del("new-list-key2");
        conn.rpush("new-list-key1", "a", "b");
        conn.rpush("new-list-key2", "1", "2");
        List<String> blpop = conn.blpop(1, "new-list-key1", "new-list-key2");
        System.out.println("blpop:" + blpop);
        List<String> brpop = conn.brpop(1, "new-list-key1", "new-list-key2");
        System.out.println("brpop:" + brpop);

        conn.del("new-list-key3");
        conn.del("new-list-key4");
        conn.rpush("new-list-key3", "a", "b", "c");
        conn.rpush("new-list-key4", "d", "e", "f");
        for (int i = 0; i < 3; i++){
            conn.rpoplpush("new-list-key3", "new-list-key4");
        }
        System.out.println(conn.lrange("new-list-key4", 0,-1));
    }

    private void set(Jedis conn){
        System.out.println("======集合常用命令=====");
        conn.del("set-key");
        //sadd 向集合里添加元素（一个或者多个），返回插入元素非重复的个数
        Long sadd = conn.sadd("set-key", "a", "b", "c");
        System.out.println(sadd);
        Long d = conn.sadd("set-key", "c");
        System.out.println(d);
        Long srem = conn.srem("set-key", "a");
        assert srem==1;
        assert !conn.sismember("set-key", "a");
        Long size = conn.scard("set-key");
        System.out.println("集合中的数量：" + size);
        Set<String> memberList = conn.smembers("set-key");
        System.out.println("集合中的成员：" + memberList);
        for (int i = 0; i < 10; i++){
            System.out.println("count是默认，随机从集合中获取成员：" + conn.srandmember("set-key"));
            System.out.println("count是正数，随机从集合中获取成员：" + conn.srandmember("set-key", 4));
            System.out.println("count是负数，随机从集合中获取成员：" + conn.srandmember("set-key", -4));
            System.out.println("count是0，随机从集合中获取成员：" + conn.srandmember("set-key", 0));
        }
        String spop = conn.spop("set-key");
        System.out.println("spop是随机移除一个元素：" + spop);
        conn.sadd("new-set-key", "a");
        Long a = conn.smove("new-set-key", "set-set-key", "a");
        assert a==1;
        System.out.println("=====用于组合和处理多集合的命令=====");
        conn.del("new-set-key-1");
        conn.del("new-set-key-2");
        conn.del("new-set-key-3");
        conn.del("new-set-key-4");
        conn.del("new-set-key-5");
        conn.sadd("new-set-key-1","first", "second", "one", "two", "three");
        conn.sadd("new-set-key-2", "one", "two", "second");
        Set<String> sdiff12 = conn.sdiff("new-set-key-1", "new-set-key-2");
        System.out.println(conn.smembers("new-set-key-1") + " - " + conn.smembers("new-set-key-2") + " = " + sdiff12);
        Set<String> sdiff21 = conn.sdiff("new-set-key-2", "new-set-key-1");
        System.out.println(conn.smembers("new-set-key-2") + " - " + conn.smembers("new-set-key-1") + " = " + sdiff21);
        conn.sdiffstore("new-set-key-3", "new-set-key-1", "new-set-key-2");
        assert Objects.equals(new HashSet<>(Arrays.asList("first", "three")), conn.smembers("new-set-key-3"));
        System.out.println("new-set-key-3:" + conn.smembers("new-set-key-3"));
        Set<String> sinter12 = conn.sinter("new-set-key-1", "new-set-key-2");
        System.out.println(conn.smembers("new-set-key-1") + "交集" + conn.smembers("new-set-key-2") + " = " + sinter12);
        conn.sinterstore("new-set-key-4", "new-set-key-1", "new-set-key-2");
        System.out.println("new-set-key-4:" + conn.smembers("new-set-key-4"));
        Set<String> sunion = conn.sunion("new-set-key-1", "new-set-key-2");
        System.out.println(conn.smembers("new-set-key-1") + "并集" + conn.smembers("new-set-key-2") + " = " + sunion);
        conn.sunionstore("new-set-key-5", "new-set-key-1", "new-set-key-2");
        System.out.println("new-set-key-5:" + conn.smembers("new-set-key-5"));
    }

    private void hash(Jedis conn){
        conn.del("new-hash-key1");
        conn.del("new-hash-key2");
        System.out.println("=====散列的API命令=====");
        conn.hset("new-hash-key1", "name", "xdq");
        conn.hset("new-hash-key1", "age", "11");
        Map<String, String> map = new HashMap<>(4);
        map.put("name", "xdq");
        map.put("age", "11");
        conn.hmset("new-hash-key2", map);
        List<String> hmget = conn.hmget("new-hash-key1", "name", "age");
        List<String> hget = Arrays.asList(conn.hget("new-hash-key2", "name"),
                conn.hget("new-hash-key2", "age"));
        assert Objects.equals(hget, hmget);
        System.out.println(hmget);
        Map<String, String> hgetAll = conn.hgetAll("new-hash-key1");
        System.out.println(hgetAll);
        conn.hdel("new-hash-key1", "name");
        assert conn.hlen("new-hash-key1") == 1;
        assert conn.hexists("new-hash-key1", "age");
        List<String> hkeys = new ArrayList<>(conn.hkeys("new-hash-key2"));
        List<String> hvals = conn.hvals("new-hash-key2");
        for (int i = 0; i < hvals.size(); i++){
            System.out.println("key: " + hkeys.get(i) + ",value: " + hvals.get(i));
        }
        Map<String, String> keyValues = conn.hgetAll("new-hash-key2");
        keyValues.forEach((k,v) -> System.out.println(k + ": " + v));
        conn.hincrBy("new-hash-key2", "age", 1);
        assert Objects.equals("2", conn.hget("new-hash-key2", "age"));
        conn.hincrByFloat("new-hash-key2", "age", 0.0D);
        assert Objects.equals("2.0", conn.hget("new-hash-key2", "age"));
    }

    private void zset(Jedis conn){
        conn.del("zset-key");
        System.out.println("=====有序集合的常用命令=====");
        conn.zadd("zset-key", 89, "member01");
        conn.zadd("zset-key", 99.5, "member02");
        Long zcard = conn.zcard("zset-key");
        assert 2 == zcard;
        conn.zrem("zset-key", "member01");
        conn.zadd("zset-key", 79, "member01");
        conn.zadd("zset-key", 85.5, "member03");
        conn.zincrby("zset-key", 2, "member03");
        Long zcount = conn.zcount("zset-key", 80, 100);
        assert 2 == zcount;
        Long member01 = conn.zrank("zset-key", "member01");
        System.out.println("member01 的排名是： " + member01);
        Double member01Score = conn.zscore("zset-key", "member01");
        System.out.println("member01 的成绩是： " + member01Score);
        Set<String> zrange = conn.zrange("zset-key", 0, -1);
        System.out.println(zrange);
        Set<Tuple> tuples = conn.zrangeWithScores("zset-key", 0, -1);
        tuples.forEach(tuple -> System.out.println(tuple.getElement() + " : " + tuple.getScore()));
        System.out.println("=====有序集合的范围型和倒序（由大到小）API=====");
        Long member02 = conn.zrevrank("zset-key", "member02");
        assert 0 == member02;
        Set<String> zrevrange = conn.zrevrange("zset-key", 0, -1);
        System.out.println(zrevrange);
        conn.del("zset-key-1");
        conn.del("z-dest-key1");
        conn.del("z-dest-key2");
        conn.zadd("zset-key-1",33, "student01");
        conn.zadd("zset-key-1",77, "member01");
        ZParams zParams = new ZParams();
        zParams.aggregate(ZParams.Aggregate.MAX);
        //zparams 默认是求和
        conn.zinterstore("z-dest-key1", zParams, "zset-key", "zset-key-1");
        Set<Tuple> destKey1 = conn.zrangeWithScores("z-dest-key1", 0, -1);
        destKey1.forEach(tuple -> System.out.println(tuple.getElement() + " : " + tuple.getScore()));
        zParams.aggregate(ZParams.Aggregate.MIN);
        //zparams 默认是求和
        conn.zunionstore("z-dest-key2", zParams,"zset-key", "zset-key-1");
        Set<Tuple> destKey2 = conn.zrangeWithScores("z-dest-key2", 0, -1);
        destKey2.forEach(tuple -> System.out.println(tuple.getElement() + " : " + tuple.getScore()));
    }
}
