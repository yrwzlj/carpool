package com.yrwcy.carpool.util;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;

public class Sample {

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId  AccessKey ID
     * @param accessKeySecret  AccessKey Secret
     * @return Client
     * @throws Exception 1
     */

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {

        Config config = new Config()
                .setAccessKeyId(accessKeyId)// AccessKey ID
                .setAccessKeySecret(accessKeySecret);// AccessKey Secret

        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";

        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static void sendMessage(String phone,String nums) throws Exception {

        com.aliyun.dysmsapi20170525.Client client =
                Sample.createClient("LTAI5t8WM3LAcQoTyChPsaUC", "Zje7oViKrbrrCEfBK3gcvFNC8jRH4s");

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + nums + "\"}");

        RuntimeOptions runtime = new RuntimeOptions();

        try {
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);

            System.out.println("1"+sendSmsResponse.getBody());

        } catch (TeaException error) {
            Common.assertAsString(error.message);

        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);

            Common.assertAsString(error.message);
        }
    }
}
