package blueeye.note;

/**
 * @Author SDJin
 * @CreationDate 2023/6/26 16:40
 * @Description ：阿里云异步短信通知服务
 */
// This file is auto-generated, don't edit it. Thanks.

import blueeye.pojo.task.impl.alert.AlertTask;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.models.*;
import com.aliyun.sdk.service.dysmsapi20170525.*;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class NoteSender {
    /**
     * 以下属性需用户在配置文件中配置
     */
    public static String  accessKey="";
    public static String  accessKeySecret="";
    public static String  signName="BlueEye报警通知";
    public static String  templateCode="SMS_462200080";
    public static String regionId="cn-hangzhou";
    public static String doSendMessage(AlertTask task) throws ExecutionException, InterruptedException {

        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKey)
                .accessKeySecret(accessKeySecret)
                .build());
        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region(regionId)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();

        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName(signName)
                .templateCode(templateCode)
                .phoneNumbers(task.getNotifier())
                .templateParam(task.getContent())
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
        // Synchronously get the return value of the API request
        SendSmsResponse resp = response.get();
        client.close();
        return new Gson().toJson(resp);
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/
        // Finally, close the client
    }


}
