package com.wfg.utils;

import com.aliyuncs.utils.Base64Helper;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import java.util.*;
import com.aliyuncs.facebody.model.v20191230.*;
import com.wfg.enums.FaceVerifyType;
import com.wfg.exection.GraceException;
import com.wfg.result.ResponseStatusEnum;
import com.wfg.utils.extend.AliyunResource;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;


@Component
public class FaceVerifyUtils {

    final static Logger logger = LoggerFactory.getLogger(FaceVerifyUtils.class);
    @Autowired
    private AliyunResource aliyunResource;

//    private AliyunResource aliyunResource= new AliyunResource("LTAI5tGy7j58Kijij8XMrRMF","rwmVyvvkoS0y7O1TyLrImfv3cx6XSi");

    private static final String gateway = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/verify";

    /*
     * 计算MD5+BASE64
     */
    public static String MD5Base64(String s) {
        if (s == null)
            return null;
        String encodeStr = "";
        byte[] utfBytes = s.getBytes();
        MessageDigest mdTemp;
        try {
            mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(utfBytes);
            byte[] md5Bytes = mdTemp.digest();
            Base64Helper b64Encoder = new Base64Helper();
            encodeStr = b64Encoder.encode(md5Bytes);
        } catch (Exception e) {
            throw new Error("Failed to generate MD5 : " + e.getMessage());
        }
        return encodeStr;
    }

    /*
     * 计算 HMAC-SHA1
     */
    public static String HMACSha1(String data, String key) {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = (new Base64Helper()).encode(rawHmac);
        } catch (Exception e) {
            throw new Error("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    /*
     * 等同于javaScript中的 new Date().toUTCString();
     */
    public static String toGMTString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.UK);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    /**
     * 发送POST请求 进行两张图的人脸对比
     * @param type
     *          0: 通过url识别，参数image_url不为空；1: 通过图片content识别，参数content不为空
     * @param face1
     *          type为0，则传入图片url，为1则传入base64
     * @param face2
     *          type为0，则传入图片url，为1则传入base64
     * @return
     */
    //如果发送的是转换为base64编码后后面加请求参数type为1，如果请求的是图片的url则不用加type参数。
    public String sendPostVerifyFace(int type,String face1, String face2)  {

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", aliyunResource.getAccessKeyID(), aliyunResource.getAccessKeySecret());
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/
        IAcsClient client = new DefaultAcsClient(profile);

        CompareFaceRequest request = new CompareFaceRequest();
        if(type ==0){
            request.setImageURLA(face1);
            request.setImageURLB(face2);
        }else if (type ==1){
            request.setImageDataA(face1);
            request.setImageDataB(face2);
        }else{
            return null;
        }

        try {
            CompareFaceResponse response = client.getAcsResponse(request);
            return new Gson().toJson(response);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());

        }
      return  null;
    }

    /**
     *
     * @param face1
     * @param face2
     * @param targetConfidence
     *          目标可信度，自定义阈值
     * @return
     */
    public boolean faceVerify( String face1, String face2, double targetConfidence) {

        String response = null;

        response = sendPostVerifyFace(FaceVerifyType.BASE64.type, face1, face2);
        if(response ==null){
            GraceException.display(ResponseStatusEnum.FACE_COMPARED_ERROR);
        }

        Map<String, Object> map = JsonUtils.jsonToPojo(response, Map.class);
        Map<String,Object> confidenceStr = (Map<String, Object>) map.get("data");
        Double responseConfidence = (Double)confidenceStr.get("confidence");

        logger.info("人脸对比结果：{}", responseConfidence);

//        System.out.println(response.toString());
//        System.out.println(map.toString());

        if (responseConfidence > targetConfidence) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * 将图片转换为Base64
     * 将base64编码字符串解码成img图片
     * @param imgUrl
     * @return
     */
    public String getImgBase64(String imgUrl){
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(imgUrl);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容放到内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        return Base64.encodeBase64String(data.toByteArray());
    }

    public static void main(String[] args) {
        String face3 = "https://viapi-test.oss-cn-shanghai.aliyuncs.com/test-team/ceshi/CompareFaceA1.jpg";
        String face4 = "https://viapi-test.oss-cn-shanghai.aliyuncs.com/test-team/ceshi/CompareFaceB.jpg";

        boolean result = new FaceVerifyUtils().faceVerify(face3, face4, 60);

        logger.info("人脸对比是否成功：{}", result);
    }

}

