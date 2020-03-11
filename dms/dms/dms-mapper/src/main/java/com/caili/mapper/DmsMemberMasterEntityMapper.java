package com.caili.mapper;


import com.caili.pojo.DmsMemberMasterEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: huey
 * @Desc:
 */

@Mapper
@Repository
public interface DmsMemberMasterEntityMapper {

    @Select("select CAST(max(dist_no+0) AS SIGNED) from dms_member_master where 1=1 ")
    String getMaxDistNo();


    @Select("select dist_name,level_no from dms_member_master where dist_no=#{distNo}")
    DmsMemberMasterEntity getSponsorInfo(@Param("distNo") String distNo);

    @Insert("insert into dms_member_master (dist_no,dist_name,sponsor_no,sponsor_name,pin,level_no,telephone,dist_status)" +
            "value(#(distInfo.distNo),#(distInfo.distName),#(distInfo.sponsorNo),#(distInfo.sponsorName),#(distInfo.pin)," +
            "#(distInfo.levelNo),#{distInfo.telephone},#{distInfo.distStatus})")
    void insertDistInfo(@Param("distInfo") DmsMemberMasterEntity distInfo);


    @Insert("insert into dms_member_master (dist_no, dist_name, sponsor_no, sponsor_name, level_no, add_time, pin) " +
            "VALUES (#{distNo} ,#{distName} ,#{sponsorNo} ,#{sponsorName} ,#{levelNo}, now(), #{pin} )")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Integer registerDist(DmsMemberMasterEntity dmsMemberMasterEntity);


    @Select("select * from dms_member_master m where m.id = #{id} ")
    DmsMemberMasterEntity findById(Integer id);

    @Select("select * from dms_member_master m where m.dist_no = #{distNo} ")
    DmsMemberMasterEntity findByDistNo(String distNo);

    @Select("select m.dist_no from dms_member_master m where m.sponsor_no = #{sponsorNo} ")
    List<String> findBySponsorNo(String sponsorNo);
}
