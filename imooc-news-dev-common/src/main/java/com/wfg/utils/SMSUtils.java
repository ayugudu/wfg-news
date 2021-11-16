package com.wfg.utils;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.wfg.utils.extend.AliyunResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description:  发送短信服务
 * @author: wfg
 * @create: 2021-11-12 19:43
 */
@Component
public class SMSUtils {
    @Autowired
    public AliyunResource aliyunResource;
    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public  void sendSMS(String mobile,String code)  {
        com.aliyun.dysmsapi20170525.Client client = null;
        try {
            client = SMSUtils.createClient(aliyunResource.getAccessKeyID(), aliyunResource.getAccessKeySecret());

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                //设置发送的手机号
                .setPhoneNumbers(mobile)
                //设置短信签名名称
                .setSignName("ayugudu")
                //设置短信模板
                .setTemplateCode("SMS_227748575")
                //设置
                .setTemplateParam("{\"code\":\""+code+"\"}");
        // 复制代码运行请自行打印 API 的返回值
        client.sendSms(sendSmsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
