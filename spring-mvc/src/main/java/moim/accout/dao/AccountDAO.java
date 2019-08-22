package moim.accout.dao;

import java.util.List;
import java.util.Map;

import moim.account.vo.AccountVO;
import moim.account.vo.MoimUserVO;
import moim.user.vo.UserVO;

public interface AccountDAO {
	
	
	/**
	 * Ư�� ������ ���� ���� ������ ��� �������� ����
	 * @param userNo ��ȸ�� ���� ��ȣ
	 * @return ���� ��ü ����Ʈ
	 */
	public List<AccountVO> selectByUser(int userNo);
	
	
	/**
	 * Ư�� ������ �����ϰ� �ִ� ��� ���� ���� ����Ʈ
	 * @param userNo
	 * @return	���� ��ü ����Ʈ
	 */
	public List<AccountVO> selectMoimByUser(int userNo);
	
	
	/**
	 * Ư�� ���¿� ���� ������ �������� ����
	 * @param accountNo ��ȸ�� ���� ��ȣ
	 * @return ���� ��ü
	 */
	public AccountVO selectByNo(int accountNo);
	
	
	/**
	 * Ư�� ���Ӱ��¿� ���� �������� ������ ��� �������� ����
	 * @param accountNo
	 * @return ���� ��ü ����Ʈ
	 */
	public List<UserVO> selectMoimUserByNo(int accountNo);
	
	/**
	 * ���Ӱ��¿� Ư�� �������� �ʴ��ϴ� ����
	 * @param newMoimUser
	 */
	public void insertMoimUser(MoimUserVO newMoimUser);
	
	/**
	 * ���� ���ΰ��¸� �������� ����(���� ����)
	 * @param userNo
	 * @return accountNo
	 */
	public int selectMyAccountOne(int userNo);
	
	
	/**
	 * �׷���¿� ����� ���� �������� ����
	 * �ڱ� ��ȣ�� ���¹�ȣ�� ���� ��ȸ
	 * @param linkedAccount
	 * @return
	 */
	public int selectLinkedAccount(Map<String, Object> linkedAccount);
	


}
