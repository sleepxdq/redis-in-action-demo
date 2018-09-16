import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        set(conn);
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
    }
}
