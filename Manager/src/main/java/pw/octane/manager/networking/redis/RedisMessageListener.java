package pw.octane.manager.networking.redis;

public interface RedisMessageListener {
    void onReceive(RedisMessage redisMessage);
}
