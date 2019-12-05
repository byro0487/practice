import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class JedisDemo {

    static final InternalLogger logger = Slf4JLoggerFactory.getInstance(JedisDemo.class);

    public static void main(String[] args) {

        Jedis jedis = new Jedis();

        // Strings
        jedis.set("user","byro");
        String s = jedis.get("user");
        logger.info(s);

        //lists
        jedis.lpush("queue#tasks", "firstTask");
        jedis.lpush("queue#tasks", "secondTask");
        String task = jedis.rpop("queue#tasks");
        logger.info(task);

        //sets
        jedis.sadd("nicknames", "nickname#1");
        jedis.sadd("nicknames", "nickname#2");
        jedis.sadd("nicknames", "nickname#1");
        Set<String> nicknames = jedis.smembers("nicknames");
        boolean exists = jedis.sismember("nicknames", "nickname#1");
        logger.info(nicknames.toString());
        logger.info("nickname#1 exists: "+String.valueOf(exists));

        //hashes
        jedis.hset("user#1", "name", "Peter");
        jedis.hset("user#1", "job", "politician");
        String name = jedis.hget("user#1", "name");
        Map<String, String> fields = jedis.hgetAll("user#1");
        String job = fields.get("job");
        logger.info(fields.toString() + job);

        //zsets
        Map<String, Double> scores = new HashMap<String, Double>();
        scores.put("PlayerOne", 3000.0);
        scores.put("PlayerTwo", 1500.0);
        scores.put("PlayerThree", 8200.0);
        scores.entrySet().forEach(playerScore -> {
            jedis.zadd("ranking", playerScore.getValue(), playerScore.getKey());
        });
        String player = jedis.zrevrange("ranking", 0, 1).iterator().next();
        long rank = jedis.zrevrank("ranking", "PlayerOne");
        logger.info(player +" " + rank);

        //transactions
        String friendsPrefix = "friends#";
        String userOneId1 = "4352523";
        String userTwoId1 = "5552321";

        Transaction t = jedis.multi();
        t.sadd(friendsPrefix + userOneId1, userTwoId1);
        t.sadd(friendsPrefix + userTwoId1, userOneId1);
        //t.exec();

        logger.info(jedis.watch("friends#deleted#" + userOneId1));
        logger.info(t.exec().toString());

        //pipeline
        String userOneId = "4352523";
        String userTwoId = "4849888";
        Pipeline p = jedis.pipelined();
        p.sadd("searched#" + userOneId, "paris");
        p.zadd("ranking", 126, userOneId);
        p.zadd("ranking", 325, userTwoId);
        Response<Boolean> pipeExists = p.sismember("searched#" + userOneId, "paris");
        Response<Set<String>> pipeRanking = p.zrange("ranking", 0, -1);
        p.sync();
        String exist = pipeExists.get().toString();
        Set<String> ranking = pipeRanking.get();
        logger.info(exist);
        logger.info(ranking.toString());

    }
}
