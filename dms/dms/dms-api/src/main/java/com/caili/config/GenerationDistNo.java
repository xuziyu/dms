package com.caili.config;



import com.caili.mapper.DmsMemberMasterEntityMapper;
import com.caili.mapper.DmsMemberMasterExtMapper;
import com.caili.pojo.DmsMemberMasterEntity;
import com.caili.utils.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: huey
 * @Desc: 自动生成会员编号  todo 必要条件redis里面必须有key vue
 */
@Service
@Slf4j
public class GenerationDistNo {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DmsMemberMasterEntityMapper dmsMemberMasterEntityMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private DmsMemberMasterExtMapper dmsMemberMasterExtMapper;

/*    @Autowired
    private DmsMemberMasterMapper dmsMemberMasterMapper;*/

    private final Log logs = LogFactory.getLog(GenerationDistNo.class);

    @Transactional(rollbackFor = Exception.class)
    public void  generationDistNo() throws Exception {
        try {
           // dmsMemberMasterMapper.getMaxDistNo();
            //加锁
           // RedisLock.acquire("add_dist_no");   如果项目是分布式  这里需要稍微改动  加入这个分布式锁
            String newDistNo=null;
            //判断 redis 里是否存在 最大值
            String key=RedisKey.getKey("max_dist_no",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
            String distNo=redisTemplate.opsForValue().get(key);
            // 不存在  查询数据库
            if(StringUtils.isEmpty(distNo)){
                newDistNo= dmsMemberMasterEntityMapper.getMaxDistNo();
                if(!StringUtils.isEmpty(newDistNo)){
                    redisTemplate.opsForValue().set(key,String.valueOf(Integer.parseInt(newDistNo)+1));
                }else {
                    redisTemplate.opsForValue().set(key,"00000000");
                    logs.info("数据库中会员主表不存在会员编号");
                }
            }else {
                newDistNo=String.valueOf(Integer.parseInt(distNo)+1);
                redisTemplate.opsForValue().set(key,newDistNo);
            }
            //解锁
           // RedisLock.release("add_dist_no");
        }catch (Exception e) {
             throw new Exception("生成会员编号错误");
        }

    }

    public Integer registerDist(DmsMemberMasterEntity dmsMemberMasterEntity) {
        RLock rLock = redissonClient.getLock("user_register_dist_" + dmsMemberMasterEntity.getDistNo());
        Integer id = null;
        try {
            boolean lock = rLock.tryLock(4, TimeUnit.SECONDS);
            if (lock) {
                id = transactionTemplate.execute(s-> dmsMemberMasterEntityMapper.registerDist(dmsMemberMasterEntity));
            } else {
                log.warn("can't get redis lock");
            }
        } catch (InterruptedException e) {
            log.error("get redis lock exception", e);
            e.printStackTrace();
        } finally {
            if (rLock.isLocked()) {
                rLock.unlock();
            }
        }
        return id;
    }

    public DmsMemberMasterEntity findById(Integer id) {
        return dmsMemberMasterEntityMapper.findById(id);
    }


    public List<Integer> getGroupId() {
        List<Integer> groupId = dmsMemberMasterExtMapper.getGroupId(2);
        groupId.toString();
        log.info("groupId:{}",groupId);
        return groupId;
    }

}
