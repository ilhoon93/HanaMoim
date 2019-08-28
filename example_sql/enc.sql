
GRANT EXECUTE on DBMS_CRYPTO to public;

SET SERVEROUTPUT ON
    

CREATE OR REPLACE PACKAGE BODY system.CRYPTO_AES256

IS

/******************************************************************************

  ��ȣȭ

 ******************************************************************************/

 FUNCTION ENC_AES ( input_string IN VARCHAR2
 ) RETURN VARCHAR2
 IS

   encrypted_raw      RAW (2000);             -- ��ȣȭ�� RAWŸ�� ������
   key_bytes_raw      RAW (32);               -- ��ȣȭ KEY (32RAW => 32Byte => 256bit)
   encryption_type    PLS_INTEGER :=          -- ��ȣȭ �˰��� ����

                            DBMS_CRYPTO.ENCRYPT_AES256

                          + DBMS_CRYPTO.CHAIN_CBC

                          + DBMS_CRYPTO.PAD_PKCS5;

    BEGIN

      

        key_bytes_raw :=UTL_I18N.STRING_TO_RAW('12345678901234567890123456789012', 'AL32UTF8');
        encrypted_raw := DBMS_CRYPTO.ENCRYPT
            (
                src => UTL_I18N.STRING_TO_RAW (input_string,  'AL32UTF8'),
                typ => encryption_type,
                key => key_bytes_raw

            );

      

        -- ���� ������ ���� base64_encode�� ���ڵ� ó��..
        -- ORA-06502: PL/SQL: numeric or value error: hex to raw conversion error
        RETURN UTL_RAW.CAST_TO_VARCHAR2(UTL_ENCODE.BASE64_ENCODE(encrypted_raw));

       

    END ENC_AES;

 

 

/******************************************************************************

  ��ȣȭ

 ******************************************************************************/

 FUNCTION DEC_AES (  encrypted_raw IN VARCHAR2

 ) RETURN VARCHAR2

IS

 

   output_string      VARCHAR2 (200);         -- ��ȣȭ�� ���ڿ�

   decrypted_raw      RAW (2000);             -- ��ȣȭ�� rawŸ�� ������

   key_bytes_raw      RAW (32);               -- 256bit ��ȣȭ key

   encryption_type    PLS_INTEGER :=          -- ��ȣȭ �˰��� ����

                            DBMS_CRYPTO.ENCRYPT_AES256

                          + DBMS_CRYPTO.CHAIN_CBC

                          + DBMS_CRYPTO.PAD_PKCS5;

    BEGIN

   

        key_bytes_raw :=UTL_I18N.STRING_TO_RAW('12345678901234567890123456789012', 'AL32UTF8');

        decrypted_raw := DBMS_CRYPTO.DECRYPT

            (

                -- ���� ������ ���� base64_decode�� ���ڵ� ó��..

                -- ORA-06502: PL/SQL: numeric or value error: hex to raw conversion error

                src =>UTL_ENCODE.BASE64_DECODE(UTL_RAW.CAST_TO_RAW(encrypted_raw)),

                typ => encryption_type,

                key => key_bytes_raw

            );

       output_string := UTL_I18N.RAW_TO_CHAR (decrypted_raw, 'AL32UTF8');

    

       RETURN output_string;

      

    END DEC_AES;                       

 

END CRYPTO_AES256;

/



/***************************************************************************
  ��ȣȭ
 ******************************************************************************/
 FUNCTION ENC_AES ( input_string IN VARCHAR2
 ) RETURN VARCHAR2;

/******************************************************************************
  ��ȣȭ
 ******************************************************************************/
 FUNCTION DEC_AES (  encrypted_raw IN VARCHAR2
 ) RETURN VARCHAR2;                       

END CRYPTO_AES256;
------------------------------------------------------------------------------------------

    
--    DECLARE
--      input_string  VARCHAR2 (200) := 'The Oracle';  -- �Է� VARCHAR2 ������
--      input_raw     RAW(128);                        -- �Է� RAW ������
--
--      encrypted_raw RAW (2000); -- ��ȣȭ ������
--
--      key_string VARCHAR2(8) := 'secret';  -- MAC �Լ����� ����� ��� Ű
--      raw_key RAW(128) := UTL_RAW.CAST_TO_RAW(CONVERT(key_string,'AL32UTF8','US7ASCII')); -- ���Ű�� RAW Ÿ������ ��ȯ
--
--    BEGIN
--      -- VARCHAR2�� RAW Ÿ������ ��ȯ
--      input_raw := UTL_I18N.STRING_TO_RAW (input_string, 'AL32UTF8');
--
--      DBMS_OUTPUT.PUT_LINE('----------- HASH �Լ� -------------');
--    encrypted_raw := DBMS_CRYPTO.HASH( src => input_raw,
--                                         typ => DBMS_CRYPTO.HASH_SH1);
--
--      DBMS_OUTPUT.PUT_LINE('�Է� ���ڿ��� �ؽð� : ' || RAWTOHEX(encrypted_raw));
--
--      DBMS_OUTPUT.PUT_LINE('----------- MAC �Լ� -------------');
--      encrypted_raw := DBMS_CRYPTO.MAC( src => input_raw,
--                                        typ => DBMS_CRYPTO.HMAC_MD5,
--                                        key => raw_key);
--
--      DBMS_OUTPUT.PUT_LINE('MAC �� : ' || RAWTOHEX(encrypted_raw));
--    END;