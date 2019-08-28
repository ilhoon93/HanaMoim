select * from tab;
-- ���� �ֱ� ������ ��������
select * from t_user WHERE user_no = (SELECT max(user_no) from t_user);

-- Ư�� ���Ӱ��¿� �������� ��� ���� ����
SELECT * FROM t_user WHERE user_no IN (SELECT member_no FROM t_moim_user WHERE moim_no = 1000000001);


-- Ư�� ������ �����ϴ� ��� ���Ӱ��� ����
SELECT * FROM t_account WHERE account_no IN (SELECT moim_no FROM t_moim_user WHERE member_no = 2);

-- Ư�� ������ ���� ģ����� ��ȸ
SELECT * FROM t_user WHERE user_no IN 
(SELECT decode(my_no,3,friends_no) || decode(friends_no,3,my_no) as friends_no FROM (SELECT my_no, friends_no FROM t_friends WHERE my_no = 3 OR friends_no = 3));

-- 2���� ����, �����ϴ� ��� ���� ���

SELECT * FROM t_account WHERE account_owner_no = 2;

SELECT * FROM t_moim_user WHERE member_no = 2;



SELECT (my_no) FROM t_friends WHERE friends_no IN(1,2,3);


SELECT user_name FROM t_user WHERE user_no IN (1,2,3);

desc t_friends;


-- ��ü ���� ��ȸ
select * from t_user;

-- ģ�� ���� ��� ��ȸ
select * from t_friends;

-- ���� �׷� ���̺�
select * from t_paygroup;

-- ���� ������ ���̺�
select * from t_moim_user;

-- ��ü ���� ��ȸ
select * from t_account;


COMMIT;
-- ���� ���� ���� ��� ��ȸ
select * from t_moim_account;

-- ��ü �ŷ� ���� ��ȸ
select * from t_trc_history;

-- ȸ�� ��û ��� ��ȸ
select * from t_money_request;

select * from t_user;

select * from t_money_request_list;
desc t_account;

SELECT trc_no, SUBSTR(trc_date,0,10), trc_amount, trc_send_account, trc_receive_account, trc_status FROM t_trc_history
    WHERE trc_receive_account = 500000001;

-- ���� ���� ��� ��û
SELECT r_no as trcNo, r_title as trcTitle, r_account_no as trcAccountNo,
				r_amount as trcAmount, r_date as trcDate
		FROM t_money_request
		WHERE r_no IN (SELECT r_no FROM t_money_request_list WHERE r_user_no = 1);

select t.* 
		from (
				select * 
				  from t_money_request 
				  order by r_no desc
			 ) t
		 where rownum = 1;
         
desc t_money_request;
