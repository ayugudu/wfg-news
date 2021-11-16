package com.wfg.user.mapper;


import com.wfg.pojo.AppUser;
import com.wfg.my.mapper.MyMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserMapper extends MyMapper<AppUser> {
}