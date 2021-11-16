package com.wfg.pojo.bo;



import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-13 20:21
 */
@Data
public class RegisterLoginBO {

    @Pattern(regexp = "^1[3-9]\\d{9}$",message = "手机号不能为空，且只能为11位！")

    private String  mobile;

    @Pattern(regexp = "^\\d{6}$",message = "短信验证码只能为6位且不能为空")

    private String  smsCode;


}
