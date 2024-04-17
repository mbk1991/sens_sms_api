package kr.co.nextcore.alarmmodule.sens.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service("sms")
public class SmsService extends SensService {

    @Value("${sens.api.url}")
    private String apiUrl;
    @Value("${sens.api.smsRequest}")
    private String smsRequest;
    @Value("${sens.api.serviceId}")
    private String serviceId;
    @Value("${sens.api.request}")
    private String request;
    @Value("${sens.api.accessKey}")
    private String accessKey;
    @Value("${sens.api.secretKey}")
    private String secretKey;
    @Value("${sens.api.senderTel}")
    private String senderTel;
    @Value("${sens.api.msgType}")
    private String msgType;
    @Value("${sens.api.msgGubun}")
    private String msgGubun;

    Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void callSmsAPI(String phoneNubers, String message) {
        try {
            String timeStamp = Long.toString(System.currentTimeMillis());
            String fullUrl = new StringBuffer()
                    .append(apiUrl).append(smsRequest).append(serviceId).append(request)
                    .toString();
            String rqstUrl = new StringBuffer()
                    .append(smsRequest).append(serviceId).append(request)
                    .toString();
            String signature = super.makeSignature(rqstUrl, timeStamp, accessKey, secretKey);
            String bodyStr = makeJsonBodyStr(phoneNubers, message);

            requestApi(fullUrl, signature, bodyStr, timeStamp);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String makeJsonBodyStr(String phoneNumbers, String message) {
        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();

        for (String phoneNo : phoneNumbers.split(";")) {
            JSONObject o = new JSONObject();
            o.put("to", phoneNo);
            o.put("content", message);
            messages.add(o);
        }

        body.put("type", msgType);
        body.put("contentType", msgGubun);
        body.put("from", senderTel);
        body.put("content", message);
        body.put("messages", messages);

        return body.toJSONString();
    }

    @Override
    protected void requestApi(String fullUrl, String signature, String body, String timeStamp) {
        System.out.println("fullUrl = " + fullUrl);
        
        try {
            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setRequestProperty("x-ncp-apigw-timestamp", timeStamp);
            conn.setRequestProperty("x-ncp-iam-access-key", accessKey);
            conn.setRequestProperty("x-ncp-apigw-signature-v2", signature);

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.write(body.getBytes());
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = conn.getResponseCode();
            BufferedReader bufferedReader;
            if (200 <= responseCode && responseCode < 300) {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String str;
            StringBuffer readMsg = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                readMsg.append(str);
            }
            bufferedReader.close();

            logger.info("readMsg = " + readMsg);

        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
