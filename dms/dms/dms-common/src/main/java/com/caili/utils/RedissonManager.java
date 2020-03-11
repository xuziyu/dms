package com.caili.utils;


import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * @Author: huey
 * @Desc: Redisson配置   哨兵配置  已经是配置非敏感区的其他
 */
@Slf4j
@Component
public class RedissonManager {

    private static  Redisson redisson;

    private static String masterName;

    private static String nodesAddress ;

    private static String password;

    private static final String localAddress = "redis://127.0.0.1:6379";

    /** 哨兵实例 */
    private static class SingletonHolder {
        private static final RedissonManager INSTANCE = new RedissonManager(masterName,nodesAddress,password);
        //private static final RedissonManager INSTANCE =new RedissonManager();
    }

    /** 本地实例 */
    private static class SingletonHolderLocal {
        private static final RedissonManager INSTANCE = new RedissonManager();
    }

    @Autowired
    private RedissonManager(@Value("${spring.redis.sentinel.master:}") String masterName,
                            @Value("${spring.redis.sentinel.nodes:}")String nodesAddress,
                            @Value("${spring.redis.password:password}")String password){
        RedissonManager.masterName = masterName;
        RedissonManager.nodesAddress = nodesAddress;
        RedissonManager.password=password;
    }

    private RedissonManager(){

    }

    /**
     * @param type 1-本地 2-yml哨兵
     * @return
     */
    public static final RedissonManager getInstance(int type) {
        if(type==1){
            return  SingletonHolderLocal.INSTANCE;
        }else if(type==2){
            return SingletonHolder.INSTANCE;
        }
        return null;
    }

    /**
     *  获取redisson
     * @param type 1-本地 2-yml哨兵
     * @return
     */
    public Redisson getRedisson(int type){
        if(redisson==null){
           init(type);
        }
        return redisson;
    }

    /**
     *  初始化Redisson对象
     * @param type 1-本地 2-yml哨兵
     */
    public void init(int type) {

        //配置
        Config config = new Config();
        if (type == 1) {
            //配置redis地址
            config.useSingleServer().setAddress(localAddress);
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson初始化成功,type={},nodes={}", type, localAddress);
        } else if (type == 2) {
            //哨兵配置
            String[] address = nodesAddress.split(",");
            for (int i=0;i<address.length;i++) {
                address[i]= "redis://"+address[i];
            }
            SentinelServersConfig sentinelServersConfig = config
                    .useSentinelServers()
                    .setMasterName(masterName)
                    .addSentinelAddress(address)
                    .setPassword(password);
            //设置sentinel.conf配置里的sentinel别名
            // sentinelServersConfig.setMasterName(masterName);
            //这里设置sentinel节点的服务IP和端口，sentinel是采用Paxos拜占庭协议
            //记住这里不是配置redis节点的服务端口和IP，sentinel会自己把请求转发给后面monitor的redis节点
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson初始化成功,type={},nodes={},masterName={}", type, nodesAddress, masterName);
        }
    }


}
