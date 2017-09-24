package com.wmc.mywebsocket.config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.wmc.mywebsocket.pojo.WSBean;
import com.wmc.mywebsocket.util.SpringUtil;
import com.wmc.mywebsocket.util.WebSocketUtil;

@ServerEndpoint(value = "/websocket/{userid}")
@Component
public class MyWebSocket {
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;
	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<MyWebSocket> webSocketSet = new CopyOnWriteArraySet<MyWebSocket>();
	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(@PathParam("userid") String userid, Session session) {
		this.session = session;
		webSocketSet.add(this); // 加入set中
		addOnlineCount(); // 在线数加1
		WebSocketUtil.put(userid, session);

		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
		try {
			sendMessage("有新连接加入！当前在线人数为" + getOnlineCount());
		} catch (IOException e) {
			System.out.println("IO异常");
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("userid") String userid) {
		webSocketSet.remove(this); // 从set中删除
		subOnlineCount(); // 在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
		WebSocketUtil.remove(userid);
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("userid") String userid) {
		System.out.println("来自客户端的消息:" + message);
		System.out.println("session值：" + session.getId() + "****" + session.getRequestURI());

		WSBean bean = JSON.parseObject(message, WSBean.class);
		System.out.println(bean.toString());
		broadcast(userid + ":" + bean.getMessage(), bean.getFrom(), bean.getTo());

		// 群发消息
		for (MyWebSocket item : webSocketSet) {
			try {
				item.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError(Session session, @PathParam("userid") String userid, Throwable error) {
		WebSocketUtil.remove(userid);
		System.out.println("发生错误");
		error.printStackTrace();
	}

	public void sendMessage(String message) throws IOException {
		//this.session.getBasicRemote().sendText(message);
		// this.session.getAsyncRemote().sendText(message);
	}

	/**
	 * 群发自定义消息
	 */
	public static void sendInfo(String message) throws IOException {
		for (MyWebSocket item : webSocketSet) {
			try {
				item.sendMessage(message);
			} catch (IOException e) {
				continue;
			}
		}
	}

	private void broadcast(String message, String from, String to) {
		if ("-1".equals(to)) {
			List<Session> sessions = WebSocketUtil.getOtherSession(from);
			System.out.println(sessions);
			if (sessions.size() > 0) {
				System.out.println("发送消息："+message);
				for (Session s : sessions) {
					s.getAsyncRemote().sendText(message);
				}
			}

		} else {
			Session session = WebSocketUtil.get(to);
			System.out.println("发送消息："+message);
			if (null != session && session.isOpen()) {
				session.getAsyncRemote().sendText(message);
			} else {
				// 获取自己的session
				Session self = WebSocketUtil.get(from);
				self.getAsyncRemote().sendText("对方已下线");
			}
		}

	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		MyWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		MyWebSocket.onlineCount--;
	}

	public static void main(String[] args) {
		System.out.println(SpringUtil.getBeanById("redisService"));
	}

}
