package moim.user.dao;

import java.util.List;

import moim.user.vo.UserVO;

/**
 * ���� ���� CRUD�� ���õ� ���Ŭ����
 * @author Ilhoon
 *
 */

public interface UserDAO {
	/**
	 * ���� �� ���� ��ȸ ����
	 * @param userNO ��ȸ�� ���� ��ȣ
	 * @return ��ȸ�� �����
	 */
	public UserVO selectByNo(int userNo);
	
	/**
	 * ���� �α��� ����
	 * @param dto �α����� ���� ����� ����
	 * @return �α��� �� ���� ���� - ���ٸ� null
	 */
	public UserVO login(UserVO user) throws Exception;
	
	/**
	 * ���� ���� ģ�����
	 * @param userNo
	 * @return ģ�� ���� ��ȣ ���
	 */
	public List<Integer>selectFriendsByNo(int userNo);
	
	/**
	 * ģ�� ��ȣ����� ���� �������� �̸� ���
	 * @param friNoList
	 * @return ģ�� �̸� ���
	 */
	public List<UserVO> selectFriendsName(List<Integer> friNoList);
	
	/**
	 * ���� ��û ���� ����Ʈ�� ���� ������ ���� ���� ���¸� ����
	 * @param requestedList
	 */
	public void updateUserPayStatus(List<Integer> requestedList);
}