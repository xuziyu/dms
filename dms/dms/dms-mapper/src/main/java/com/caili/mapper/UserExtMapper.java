package com.caili.mapper;


import com.caili.pojo.User;
import com.caili.pojo.vo.UserListDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserExtMapper extends UserMapper {

    int updateDistNoById(@Param("id") Integer id, @Param("distNo") String distNo, @Param("sponsor_no") String sponsorNo);

    int updateDistNoAndSponsorNoById(@Param("id") Integer id, @Param("distNo") String distNo, @Param("sponsorNo") String sponsorNo);

    int authIdCar(@Param("userId") Integer userId, @Param("IdCar") String IdCar, @Param("name") String name, @Param("key") String key);

    int isAuthIdCar(@Param("userId") Integer userId);

    List<UserListDto> getUserList(@Param("startTime") String startTime, @Param("pin") Integer pin,
                                  @Param("endTime") String endTime, @Param("id") Integer id,
                                  @Param("type") Integer type, @Param("provinceCode") Integer provinceCode,
                                  @Param("username") String username, @Param("status") Byte status,
                                  @Param("sponsorNo") String sponsorNo);

    User findIdByDistNo(String distNo);

    Integer checkMobile(String mobile);

    void updateVipUserLevel(String distNo);

    void updateSVipUserLevel(String distNo);
}
