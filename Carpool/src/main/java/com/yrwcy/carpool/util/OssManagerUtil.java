package com.yrwcy.carpool.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

/**
 * 阿里Oss对象存储工具类
 */
public class OssManagerUtil {

    private static final String endpoint = "oss-cn-hangzhou.aliyuncs.com";

    private static final String accessKeyId = "LTAI5tFARVETUF1yuBgSjenX";

    private static final String accessKeySecret = "vnmQUpDTgUzyG07iRRSDxALo7FFZGu";

    private static final String bucket = "bucketofpicture";

    private static final OSS client;

    static {
        client = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    /**
     * 上传图片
     * @param fileName 图片名称
     * @param length   图片大小
     * @param content  输入流
     */
    public static String uploadImage(String fileName, long length, InputStream content) {

        uploadBucketImage(bucket, fileName, length, content);

        return "https://" + bucket + "." + endpoint + "/" + fileName;
    }


    /**
     * 上传文件
     * @param bucket   存储空间名
     * @param fileName 文件名
     * @param length   流的长度
     * @param content  输入流
     */
    public static void uploadBucketImage(String bucket, String fileName, long length, InputStream content) {

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(length);

        // 上传Object.
        client.putObject(bucket, fileName, content, meta);
    }


    /**
     * 删除文件
     * @param url 图片的url
     */
    public static void deleteImage(String url) {

        String filename=url.replace("https://" + bucket + "." + endpoint + "/","");

        //执行删除
        client.deleteObject(bucket, filename);

    }


    /**
     * 删除文件
     * @param file 要更改的头像
     */
    public static String getUrl(MultipartFile file){
        String fileName = file.getOriginalFilename();

        assert fileName != null;

        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        fileName = java.util.UUID.randomUUID().toString().replace("-", "") + "." + suffix;

        String url = null;

        try {
            url = OssManagerUtil.uploadImage(fileName,file.getInputStream().available(),file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }
}

