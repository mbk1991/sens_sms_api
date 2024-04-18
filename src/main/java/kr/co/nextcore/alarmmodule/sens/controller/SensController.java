package kr.co.nextcore.alarmmodule.sens.controller;

import kr.co.nextcore.alarmmodule.sens.service.SensService;
import kr.co.nextcore.alarmmodule.sens.vo.SmsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class SensController {

    @Autowired
    @Qualifier("sms")
    SensService smsService;

    @PostMapping(value = "/sms")
    public String sendSms(@RequestBody SmsVo sms){
            smsService.callSmsAPI(sms.getPhoneNumbers(), sms.getMessage());
            return "success";
    }
}
