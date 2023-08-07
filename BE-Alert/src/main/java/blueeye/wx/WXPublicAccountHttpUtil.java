package blueeye.wx;

import java.io.BufferedReader;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



/**
 * @Description 微信公众号http请求工具类
 * @author isymi
 * @version
 * @date 2023年5月29日下午4:07:39
 *
 */
public class WXPublicAccountHttpUtil {


    /**
     * @Description 根据请求获取返回结果字符串（根据请求获取accessToken)
     * @date 2023年5月29日下午4:04:21
     * @param url
     * @return
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } else {
                // Handle error response
                System.out.println("HTTP GET request failed with response code: " + responseCode);
                return null;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * @Description 根据URl获取JSONObject：根据请求获取关注用户列表数据
     * @date 2023年5月29日下午4:02:16
     * @param url
     * @return
     * @throws IOException
     */
    public static JSONObject getJsonObject(String url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            /*
             * 	正确返回的格式
             *      {
    					"total":2,
    					"count":2,
    					"data":{
    						"openid":["OPENID1","OPENID2"]},
    					"next_openid":"NEXT_OPENID"
					}
             */
            return JSON.parseObject(response.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * @Description 获取关注用户的 openid 集合
     * @date 2023年5月29日下午4:04:02
     * @param url
     * @return
     * @throws IOException
     */
    public static  List<String> getOpenidList(String url) throws IOException {
        // 获取关注用户列表数据
        JSONObject jsonObject = getJsonObject(url);
        System.out.println(jsonObject);

        // 错误情况
        if (jsonObject.containsKey("errcode")) {
            int errcode = jsonObject.getIntValue("errcode");
            String errmsg = jsonObject.getString("errmsg");
            throw new RuntimeException("Failed to get openid list. errcode: " + errcode + ", errmsg: " + errmsg);
        }

        int total = jsonObject.getIntValue("total");
        // 无用户关注 {"total":0,"count":0,"next_openid":""}
        if (total == 0) {
            throw new RuntimeException("No openid found. Total is 0.");
        }
        // 有用户关注：
        /**
         * {"total":1,
         * 	"data":{
         * 		"openid":["o-tgG5-VaQfsgdjerHA-z2PeZFls"]},
         * 		"count":1,
         * 		"next_openid":"o-tgG5-VaQfsgdjerHA-z2PeZFls"}
         */
        JSONObject dataObject = jsonObject.getJSONObject("data");

        int count = dataObject.getIntValue("count");
        System.out.println("关注总人数："+count);
        JSONArray openidArray = dataObject.getJSONArray("openid");

        // 将 openid 数组封装为 List 集合
        List<String> openidList = new ArrayList<>();
        for (int i = 0; i < openidArray.size(); i++) {
            String openid = openidArray.getString(i);
            openidList.add(openid);
        }

        return openidList;
    }

    /**
     * @Description 发送消息
     * @date 2023年5月29日下午4:58:02
     * @param accessToken
     * @param templateMessage
     * @return
     * @throws IOException
     */
    public static String sendMessage(String accessToken, TemplateMessage templateMessage) throws IOException {
        String requestUrl ="https://api.weixin.qq.com/cgi-bin/message/template/send" + "?access_token=" + accessToken;

        URL urlObject = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = JSON.toJSONString(templateMessage);
        byte[] requestBodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
        connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBodyBytes);
        outputStream.close();

        int responseCode = connection.getResponseCode();
        BufferedReader reader;
        if (responseCode >= 200 && responseCode <= 299) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        connection.disconnect();
        return requestBody.toString();
    }

}
