package com.wfg.pojo.vo;



import lombok.Data;

import java.util.Date;

@Data
public class AppUserVO {

    private String id;



    /**
     * 昵称，媒体号
     */
    private String nickname;

    /**
     * 头像
     */
    private String face;

    private Integer activeStatus;

    private Integer myFollowCounts;

    private Integer myFansCounts;

}