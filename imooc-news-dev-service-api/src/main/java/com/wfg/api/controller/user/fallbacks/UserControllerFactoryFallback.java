package com.wfg.api.controller.user.fallbacks;

import com.wfg.api.controller.user.UserControllerApi;
import com.wfg.pojo.bo.UpdateUserInfoBO;
import com.wfg.pojo.vo.AppUserVO;
import com.wfg.result.GraceJSONResult;
import com.wfg.result.ResponseStatusEnum;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-12-08 10:51
 */
@Component
public class UserControllerFactoryFallback implements FallbackFactory<UserControllerApi> {
    @Override
    public UserControllerApi create(Throwable cause) {
        return new UserControllerApi() {
            @Override
            public GraceJSONResult getUserInfo(String userId) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public GraceJSONResult getAccountInfo(String userId) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public GraceJSONResult updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.SYSTEM_ERROR_FEIGN);
            }

            @Override
            public GraceJSONResult queryByIds(String userIds) {
                System.out.println("进入客户端 降级处理");
                List<AppUserVO> list = new ArrayList<>();
                return GraceJSONResult.ok(list);
            }
        };
    }
}
