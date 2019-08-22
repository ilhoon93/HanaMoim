package moim.util.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.mybatis.logging.Logger;
//import org.mybatis.logging.LoggerFactory;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import moim.user.vo.UserVO;

public class EchoHandler extends TextWebSocketHandler{
	private static Logger logger = LoggerFactory.getLogger(EchoHandler.class);
	private List<WebSocketSession> sessionList = new ArrayList<WebSocketSession>();
	
	Map<String, WebSocketSession> userSessions = new HashMap<String, WebSocketSession>();
	
	
	
	// Ŭ���̾�Ʈ�� ���� ���Ŀ� ����Ǵ� �޼���
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionList.add(session);
		logger.info("{} �����", session.getId());

		String senderId = getId(session);
		String senderName = getName(session);
		userSessions.put(senderId, session);
		for (WebSocketSession sess : sessionList) {
			sess.sendMessage(new TextMessage(senderName + " ���� �����߽��ϴ�."));
		}
	}

	// Ŭ���̾�Ʈ�� ������ �޽����� �������� �� ����Ǵ� �޼���
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		
		logger.info("{}�� ���� {} ����", session.getId(), message.getPayload());
		
		
//		String senderId = getId(session);
		String senderName = getName(session);
		
		
		for (WebSocketSession sess : sessionList) {
//			String senderName = getName(sess);
			sess.sendMessage(new TextMessage(senderName + " : " + message.getPayload()));
		}
	}

	// Ŭ���̾�Ʈ�� ������ ������ �� ����Ǵ� �޼ҵ�
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessionList.remove(session);
		logger.info("{} ���� ����", session.getId());
		String senderName = getName(session);
		for (WebSocketSession sess : sessionList) {

			sess.sendMessage(new TextMessage(senderName + " ���� �������ϴ�."));
		}
	}
	
	private String getId(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		UserVO loginUser = (UserVO)httpSession.get("userVO");
		
		if(loginUser == null) {
			return session.getId();
		}else {
			return String.valueOf(loginUser.getUserNo());
		}
	}
	
	private String getName(WebSocketSession session) {
		// http���ǿ� �ִ� ���� �Ǿ ������ ���ǿ� �����ش�. map���� ����
		Map<String, Object> httpSession = session.getAttributes();
		UserVO loginUser = (UserVO)httpSession.get("userVO");
		
		//�α��� �Ǿ����� �α����� ���� ��ȣ, �ƴϸ� ���� ���̵� ����
		if(loginUser == null) {
			return session.getId();
		}else {
			return String.valueOf(loginUser.getUserName());
		}
	}
	
	
}
