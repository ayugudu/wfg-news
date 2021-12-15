package com.wfg.files.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @program: imooc-news-dev
 * @description:
 * @author: wfg
 * @create: 2021-11-22 08:57
 */
@Component
@PropertySource("classpath:file-${spring.profiles.active}.properties")
@ConfigurationProperties(prefix = "file")
@Data
public class FileResource {
    private String host;
    private String endpoint;
    private String bucketName;
    private String objectName;
    private String ossHost;

}
