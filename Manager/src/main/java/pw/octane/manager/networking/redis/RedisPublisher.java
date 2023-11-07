package pw.octane.manager.networking.redis;

import lombok.Getter;
import pw.octane.manager.OctaneManager;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.Queue;

public class RedisPublisher {

    private Jedis jedis;
    private OctaneManager octaneManager;
    private @Getter Queue<RedisMessage> messageQueue;
    private boolean running;
    public RedisPublisher(Jedis jedis, OctaneManager octaneManager) {
        this.jedis = jedis;
        this.octaneManager = octaneManager;
        this.messageQueue = new LinkedList<>();

        octaneManager.getServer().getScheduler().runTaskTimerAsynchronously(octaneManager, ()-> {
            if(!messageQueue.isEmpty()) {
                RedisMessage redisMessage = messageQueue.poll();
                jedis.publish(octaneManager.getConfig().getString("networking.redis.channel"), redisMessage.getMessage().toString());
            }
        }, 1, 1);
    }
}
