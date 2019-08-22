package moim.util.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import moim.account.service.AccountService;
import moim.account.vo.MoimUserVO;
import moim.moneyrequest.vo.MoneyRequestVO;
import moim.user.vo.UserVO;
public class InviteEchoHandler extends TextWebSocketHandler{
	
	
	@Autowired
	private AccountService accountService;
	
	/*
	 * ��ü ����ڿ��� �޽��� ������
	 * 1. ������ ������ �����ϴ� ����Ʈ ����
	 * 2. ��� ������ ����� �� add �޽����� ����Ʈ�� ���� 
	 * 3. ��� ������ for������ ���鼭 send�޽��� ������ 
	 */
	
	/*
	 * Ư�� ģ������ ���� �ʴ��� ������
	 * http������ ������ ���ǿ� �Ǿ��ֱ�
	 */
	List<WebSocketSession > sessions = new ArrayList<WebSocketSession>();
	Map<String, WebSocketSession> userSessions = new HashMap<String, WebSocketSession>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		System.out.println("afterConnectionEstablished:" + session);
		sessions.add(session);
		String senderId = getId(session);
		userSessions.put(senderId, session); // ������� ���̵�� �׿� �ش��ϴ� ���Ǹ���


//		MoneyRequestVO mrVO = getMrVO(session);
//		System.out.println(mrVO);
		// ���� ���ϴ� Ư�� ���� ���ӽ�
//		for(Map.Entry<String, WebSocketSession> sess : userSessions.entrySet()) {
//			if(sess.getKey().equals("1")) {
//				TextMessage aa = new TextMessage("��������");
//				sess.getValue().sendMessage(aa);
//			}
//		}
		
		
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
		System.out.println("handleTextMessage:" + session + " : " + message);
		
		//��ü ���� ���� ���� ���̵�, ���ǰ� ���
//		for(Map.Entry<String, WebSocketSession> sess : userSessions.entrySet()) {
//			System.out.println("userID : " + sess.getKey() + ", value : " + sess.getValue());
//		}
		// ������ 2�� ���� ���� �ٸ� ���ǿ��� ���̵� �����.
		String senderId = getId(session);
		String senderName = getName(session);
		
		
		/*
		 ���ǿ� ������ ��� �����ڿ��� �޼��� ������
		 js�� onmessage �κ��� ź��.
		for(WebSocketSession sess : sessions) {
			TextMessage txt = new TextMessage(senderId + " : "+ message.getPayload());
			sess.sendMessage(txt);
		}
		*/
		
		// ���� protocol : cmd, ������, �ʴ� ���� ����, ���� ���� (invite, 1, 2, 5)
		// ex) protocol : cmd, ��� �ۼ���, �Խñ� �ۼ���, bno (reply, user2, user1, 234)
		
		String msg = message.getPayload();
		
		System.out.println("������� id : " + senderId + " ���� �޽��� : " + msg);
		
		if(!StringUtils.isEmpty(msg)) {
			String[] strs = msg.split(",");
			//�ʴ�� ������
			if(strs != null && strs.length == 4) {
				String cmd = strs[0].trim();
				String moimOwner = strs[1].trim();
				String invitedUser = strs[2].trim();
				String accountNo = strs[3].trim();
				TextMessage tmpMsg = new TextMessage(senderId + "," + accountNo + "," +senderName + "����" + accountNo + "���Ӱ��¿� �ʴ��߽��ϴ�.");
				
				//�ʴ���� ������ �¶����� ����
				WebSocketSession invitedUserSession = userSessions.get(invitedUser);
				
				if("invite".equals(cmd) && invitedUserSession != null) {
					invitedUserSession.sendMessage(tmpMsg);
				}
				

			}else if(strs != null && strs.length == 3) { //�ʴ� ����
				String cmd = strs[0].trim();
				String accountNo = strs[1].trim();
				String invitedUser = strs[2].trim();

				// ���� ������ ��
				if("inviteAgree".equals(cmd)) {
					MoimUserVO moimUser = new MoimUserVO();
					
					// ���� ���� �������� ����
					int linkAccount = accountService.selectMyAccountOne(Integer.parseInt(invitedUser));
					moimUser.setAccountNo(Integer.parseInt(accountNo));
					moimUser.setNewUserNo(Integer.parseInt(invitedUser));
					moimUser.setNewUserAccountNo(linkAccount);
					
					System.out.println(moimUser);
					
					accountService.insertMoimUser(moimUser);
					
					System.out.println("�ʴ����");
				}
			}
		}
	}
	

	private String getId(WebSocketSession session) {
		// http���ǿ� �ִ� ���� �Ǿ ������ ���ǿ� �����ش�. map���� ����
		Map<String, Object> httpSession = session.getAttributes();
		UserVO loginUser = (UserVO)httpSession.get("userVO");
		
		//�α��� �Ǿ����� �α����� ���� ��ȣ, �ƴϸ� ���� ���̵� ����
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

	
//	private MoneyRequestVO getMrVO(WebSocketSession session) {
//		Map<String, Object> httpSession = session.getAttributes();
//		MoneyRequestVO mrVO = (MoneyRequestVO)httpSession.get("moneyRequestVO");
//		return mrVO;
//	}

	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		System.out.println("afterConnectionClosed" + session + " : " + status);
	}
}
