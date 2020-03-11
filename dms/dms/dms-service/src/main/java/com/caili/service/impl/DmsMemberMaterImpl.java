package com.caili.service.impl;


import com.caili.mapper.DmsMemberMasterEntityMapper;
import com.caili.pojo.DmsMemberMasterEntity;
import com.caili.pojo.User;
import com.caili.service.DmsMemberMaterService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: huey
 * @Desc:
 */
@Service
public class DmsMemberMaterImpl implements DmsMemberMaterService {

    @Autowired
    private DmsMemberMasterEntityMapper dmsMemberMasterEntityMapper;

    private final Log logs = LogFactory.getLog(DmsMemberMaterImpl.class);

    @Override
    public void setDistInfo(User user) {
        DmsMemberMasterEntity sponsorInfo= dmsMemberMasterEntityMapper.getSponsorInfo(user.getSponsorNo());
        if(sponsorInfo==null){
            //理论上 不会出现   以防万一
            logs.error("该会员不存在");

        }else {
            DmsMemberMasterEntity dmsMemberMasterEntity =new DmsMemberMasterEntity();
            dmsMemberMasterEntity.setDistNo(user.getDistNo());
            dmsMemberMasterEntity.setDistName(user.getNickname());
            dmsMemberMasterEntity.setDistStatus(1);
            dmsMemberMasterEntity.setSponsorNo(user.getSponsorNo());
            dmsMemberMasterEntity.setTelephone(user.getMobile());
            dmsMemberMasterEntity.setSponsorName(sponsorInfo.getDistName());
            dmsMemberMasterEntity.setLevelNo(sponsorInfo.getLevelNo()+1);
            dmsMemberMasterEntity.setPin(1);
            dmsMemberMasterEntityMapper.insertDistInfo(dmsMemberMasterEntity);
        }


    }
}
