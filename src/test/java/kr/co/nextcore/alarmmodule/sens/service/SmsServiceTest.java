package kr.co.nextcore.alarmmodule.sens.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class SmsServiceTest {

    @Test
    void api테스트() {

        String apiUrl = "https://sens.apigw.ntruss.com";
        String requestUrl = "/sms/v2/services/";
        String serviceId = "ncp:sms:kr:331225077443:dev-nextcore-sms-01";
        String request= "/messages";
        long timeStamp = System.currentTimeMillis();
        String accessKey = "HiyJUFsrvWuczgoloSlI";
        String secretKey = "L7ouEiYAg34IHusqczxqHxIimOh5oYqX4MRImJQz";
        String contentType = "application/json; charset=utf-8";

        System.out.println("timeStamp = " + timeStamp);

        String url = new StringBuffer()
                .append(apiUrl).append(requestUrl).append(serviceId).append(request).toString();

        System.out.println("url = " + url);

        try {
            String signature = makeSignature(requestUrl+serviceId+request, timeStamp+"", accessKey, secretKey);
            System.out.println("signature = " + signature);
            
            String type = "SMS";  // SMS | LMS | MMS
            String msgType = "COMM"; // COMM | AD
            String from = "07050152313"; //sender tel
            String content = "test메시지입니다."; //msg content
            JSONArray messages = new JSONArray();  // msg info
            JSONObject msgInfo = new JSONObject();
            msgInfo.put("to","01080806977");
            msgInfo.put("content","테스트");
            messages.add(msgInfo);


            JSONObject body = new JSONObject();
            body.put("type",type);
            body.put("contentType",msgType);
            body.put("from",from);
            body.put("content",content);
            body.put("messages",messages);

            String bodyStr = body.toJSONString();
            System.out.println("bodyStr = " + bodyStr);

            URL endpoint = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("x-ncp-apigw-timestamp", timeStamp+"");
            conn.setRequestProperty("x-ncp-iam-access-key", accessKey);
            conn.setRequestProperty("x-ncp-apigw-signature-v2", signature);

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write(bodyStr.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = conn.getResponseCode();
            BufferedReader bufferedReader;
            if(200<= responseCode && responseCode <300){
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String str;
            StringBuffer readMsg = new StringBuffer();
            while((str = bufferedReader.readLine()) != null){
                readMsg.append(str);
            }
            bufferedReader.close();

            System.out.println("readMsg = " + readMsg);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String makeSignature(String url, String timestamp, String accessKey, String secretKey) throws Exception {
        System.out.println("url = " + url);
        
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }


}