package kr.co.nextcore.alarmmodule.sens.service;

import org.apache.tomcat.util.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public abstract class SensService {

    public abstract void callSmsAPI(String phoneNubers, String message);
    protected abstract String makeJsonBodyStr(String phoneNumbers, String message);
    protected abstract void requestApi(String url, String signature, String body, String timeStamp);

    public String makeSignature(String requestUrl, String timestamp, String accessKey, String secretKey) throws Exception {
        String space = " ";                    // one space
        String newLine = "\n";                    // new line
        String method = "POST";                    // method

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(requestUrl)
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
