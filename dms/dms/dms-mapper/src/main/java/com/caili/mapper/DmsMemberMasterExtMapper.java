package com.caili.mapper;



import com.caili.pojo.DmsMemberMaster;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface DmsMemberMasterExtMapper {

    DmsMemberMaster selectByDistNo(String distNo);

    List<String> findByDistNo(String distNo);

    List<Integer> getGroupId(Integer groupId);

    List<DmsMemberMaster> findAll();

}