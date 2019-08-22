package moim.util.websocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import moim.account.service.AccountService;
import moim.account.vo.AccountVO;
import moim.moneyrequest.service.MoneyRequestService;
import moim.moneyrequest.vo.MoneyRequestVONoList;
import moim.transaction.service.TransactionService;
import moim.transaction.vo.TransactionVO;
import moim.user.vo.UserVO;

public class MoneyRequestHandler extends TextWebSocketHandler{
	
	
	@Autowired
	private MoneyRequestService mrService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	// ���� ���� �� ���ǿ� �ִ� userVO �޾ƿͼ� user_pay_status�� R�̸� �˸� â ���� �����ϱ�
	
	List<WebSocketSession > sessions = new ArrayList<WebSocketSession>();
	Map<String, WebSocketSession> userSessions = new HashMap<String, WebSocketSession>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception{
		System.out.println("�׷� ������ ����:" + session);
		sessions.add(session);
		UserVO loginUser = getUser(session);
//		int userNo = loginUser.getUserNo();
		
		
		String senderId = getId(session);
		userSessions.put(senderId, session); // ������� ���̵�� �׿� �ش��ϴ� ���Ǹ���

		System.out.println(loginUser); // ������ ���� 
		
		// ������ ����ڰ� ��û���� ��� ���
		List<MoneyRequestVONoList> result = mrService.selectMrListByUser(loginUser.getUserNo());
		
		
		System.out.println(result);
		// ������ ����ڿ��� ��� ��û �޽��� ����
		for(MoneyRequestVONoList mr : result) {
			TextMessage msg = new TextMessage(mr.getTrcAccountNo()+ "���Ӱ��¿��� \n'" + 
					mr.getTrcTitle() + "'��������"+ mr.getTrcDate()+ "����" +	
					mr.getTrcAmount() + "���� ��ݿ�û�Ͽ����ϴ�.\n\n Ȯ���� ������ ������¿��� �ٷ� ��� �˴ϴ�,"+
					mr.getTrcAccountNo()+","+mr.getTrcAmount()+","+mr.getTrcTitle());
			System.out.println(msg.getPayload());
			session.sendMessage(msg);
		}
		
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
		UserVO loginUser = getUser(session);
//		String senderName = getName(session);
		
		
		String msg = message.getPayload();
		
		System.out.println("������� id : " + senderId + " ���� �޽��� : " + msg);
		
		
		String [] strs = msg.split(",");
		String cmd = strs[0].trim();
		String receiveAccount = strs[1].trim();
		String sendUser = strs[2].trim();
		String money = strs[3].trim();
		String sendAccount = strs[4].trim();
		String title = strs[5].trim();
		if(cmd.equals("agree")) {
			
			
			// ���� ���� ���� ���� ��ȸ
			// ���� ��ü�� ���� �غ�
			Map<String, Object> addMoneyParam = new HashMap<String, Object>();
			Map<String, Object> subtractMoneyParam = new HashMap<String, Object>();
			
			
			addMoneyParam.put("receiveAccount",receiveAccount);
			subtractMoneyParam.put("sendAccount",sendAccount);
			int sendAccountN = Integer.parseInt(sendAccount);
			int receiveAccountN = Integer.parseInt(receiveAccount);
			AccountVO subVO = accountService.selectOneAccount(sendAccountN);
			AccountVO addVO = accountService.selectOneAccount(receiveAccountN);
			
			int subVOAmount = subVO.getAccountTotalAmount();
			int addVOAmount = addVO.getAccountTotalAmount();
			
			int subVOAmountN = subVOAmount - Integer.parseInt(money);
			int addVOAmountN = addVOAmount + Integer.parseInt(money);
			
			
			String newReceiveAccountMoney = String.valueOf(addVOAmountN);
			String newSendAccountMoney = String.valueOf(subVOAmountN);
			
			addMoneyParam.put("money", newReceiveAccountMoney);
			subtractMoneyParam.put("money", newSendAccountMoney);

			
			// ���� ��ü ����
			System.out.println(subtractMoneyParam);
			System.out.println(addMoneyParam);
			mrService.transferMoney(subtractMoneyParam, addMoneyParam);
			
			// ��� ��û ���� ������Ʈ
			List<MoneyRequestVONoList> mr = mrService.selectMrListByUser(loginUser.getUserNo());
			for(MoneyRequestVONoList data : mr) {
				if(Integer.parseInt(receiveAccount)==data.getTrcAccountNo() 
						&& data.getTrcAmount() == Integer.parseInt(money)) {
					Map<String, Object> updateMrList = new HashMap<String, Object>();
					updateMrList.put("trcNo",data.getTrcNo());
					updateMrList.put("userNo",loginUser.getUserNo());
					mrService.updateMrList(updateMrList);
				}
			}
			// Ʈ����� ���
			TransactionVO transaction  = new TransactionVO();
			
			transaction.setTrcAmount(Integer.parseInt(money));
//			System.out.println(money);
			transaction.setTrcSendAccount(sendAccountN);
			transaction.setTrcReceiveAccount(receiveAccountN);
			transaction.setTrcStatus("1");
			transaction.setTrcDate("dummy");
			transaction.setTrcNo(1);
			
			System.out.println(loginUser.getUserName());
			transaction.setTrcSenderName(loginUser.getUserName());
			transaction.setTrcTitle(title);
			
			
			System.out.println(transaction);
			transactionService.insertTransaction(transaction);
			
			
			System.out.println("Ʈ����� ���� ��");
//			transactionService.
			
			

		}
	}
	// �α����� ������ ��ü�� ����
	private UserVO getUser(WebSocketSession session) {
		Map<String, Object> httpSession = session.getAttributes();
		UserVO loginUser = (UserVO)httpSession.get("userVO");
		return loginUser;
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


	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
		System.out.println("afterConnectionClosed" + session + " : " + status);
	}

}
