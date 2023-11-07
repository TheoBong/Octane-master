package pw.octane.manager.networking.redis;

import lombok.Getter;
import pw.octane.manager.OctaneManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashSet;
import java.util.Set;

public class RedisSubscriber {

    private Jedis jedis;
    private OctaneManager octaneManager;

    private JedisPubSub jedisPubSub;
    private @Getter Set<RedisMessageListener> listeners;
    private String rChannel;
    public RedisSubscriber(Jedis jedis, OctaneManager octaneManager) {
        this.jedis = jedis;
        this.octaneManager = octaneManager;
        this.listeners = new HashSet<>();

        this.rChannel = octaneManager.getConfig().getString("networking.redis.channel");

        this.jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if(rChannel.equals(channel)) {
                    for (RedisMessageListener listener : listeners) {
                        listener.onReceive(new RedisMessage(message));
                    }
                }
            }
        };

        octaneManager.getServer().getScheduler().runTaskAsynchronously(octaneManager, ()-> {
           jedis.subscribe(jedisPubSub, rChannel);
        });
    }
}
