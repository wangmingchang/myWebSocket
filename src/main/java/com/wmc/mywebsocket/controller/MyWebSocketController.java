package com.wmc.mywebsocket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wmc.mywebsocket.config.MyWebSocket;
import com.wmc.mywebsocket.servic.RedisService;
import com.wmc.mywebsocket.util.RedisUtil;

/**
 * 返回页面的Controller
 * 
 * @author 王明昌
 * @date 2017年9月23日
 */
@RestController
public class MyWebSocketController {
	@Autowired
	private RedisService redisService;
	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping("/index")
	public void index() throws Exception {
		
		MyWebSocket.sendInfo("这是contorlle发来的消息");
	}
}
