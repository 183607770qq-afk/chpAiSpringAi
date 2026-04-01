package com.chp.chpaispringai.controller;

import com.chp.chpaispringai.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/ai")
@RestController
public class EmbeddingController {
    @Autowired
    private EmbeddingService embeddingService;
    @RequestMapping("/findsim")
    public Map<String,String> findSimText(@RequestParam(value = "message") String message){
        String str = embeddingService.findSimText(message);
        return Map.of(message,str);
    }
}
