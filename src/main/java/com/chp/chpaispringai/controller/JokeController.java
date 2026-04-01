//package com.chp.chpaispringai.controller;
//
//import com.chp.chpaispringai.service.JokeService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//public class JokeController {
//
//    private final JokeService jokeService;
//
//    public JokeController(JokeService jokeService) {
//        this.jokeService = jokeService;
//    }
//
//    @GetMapping("/joke")
//    public Mono<String> getJoke(@RequestParam String q) {
//        // 调用服务，传入用户问题
//        return jokeService.generateJoke(q);
//    }
//}