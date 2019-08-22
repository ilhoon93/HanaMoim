package moim.transaction.dao;

import java.util.List;
import java.util.Map;

import moim.transaction.vo.TransactionVO;

public interface TransactionDAO {

	public void insertTransaction(TransactionVO newTransaction);
	
	public List<TransactionVO> selectTransactionList(int accountNo);
	
	
	/**
	 * ���ϸ��� �����ͺ��̽��� ���ε��ϴ� �ڵ�
	 * @param fileMap
	 * @return
	 */
	public void updateFile(Map<String, Object> fileMap);
	
}
