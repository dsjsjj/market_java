package service;

//�û�ҵ���߼�ʵ��
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Pattern;

import dao.UserDAO;
import vo.User;

public class UserHelper {

	// �û���¼����
	public static User login() {
		System.out.println("��ӭʹ�÷Ĵ�������ϵͳ�����½:");
		for (int i = 0; i < 3; i++) {
			String UserName = inputUserName();//�û���
			String password = inputpassword();//����
			User user = UserDAO.get(UserName);//���ظ����û�����ѯ�Ķ���
			if (user == null) {// ���ж�user�Ƿ�Ϊ��
				System.out.println("�û��������벻��ȷ������������:");
			} else {
				String userFind = user.getPassword();//�õ����ݿ��е�����
//					System.out.println(userFind);
				if (userFind != null) {// ���ж�userFind�Ƿ�Ϊ��
					String MD5password=MD5(password);
					if (userFind.equals(MD5password)) {// ����ж��Ƿ����
						
						return user;
					} else {
						System.out.println("�û��������벻��ȷ������������:");
					}
				} else {
					System.out.println("�û��������벻��ȷ������������:");
				}
			}
		}
		System.out.println("���ֻ�ܳ���3��!");
		return null;
	}

	// �����û���
	public static String inputUserName() {
		// ��ʾ�����û���
		Scanner scan = new Scanner(System.in);
		System.out.println("�������û�����");
		String user = scan.nextLine();
		return user;
	}

	// ��������
	public static String inputpassword() {
		// ��ʾ��������
		Scanner scan = new Scanner(System.in);
		System.out.println("���������룺");
		String password = scan.nextLine();
		return password;
	}
	
	//�޸�����
	public static void updatePassword(User user) {
		System.out.println("�����뵱ǰ�û���ԭ���룺");
		Scanner scan = new Scanner(System.in);
		String oldPassword;
		String newPassword;
		while(true) {
			oldPassword = scan.nextLine();//ԭ����
			String MD5oldPassword=MD5(oldPassword);
			if(!MD5oldPassword.equals(user.getPassword())) {//ԭ��������������һ��
				System.out.println("ԭ�������벻��ȷ������������");
			}
			else {//������ȷ
				System.out.println("�������µ����룺");
				while(true) {
					newPassword=scan.nextLine();
					if(!validatePassword(newPassword)) {//��ƥ��
						System.out.println("�������벻���ϸ�����Ҫ�����볤�Ȳ�����6���ַ���������һ��Сд��ĸ��"
								+ "������һ����д��ĸ������һ�����֣������������룺");
					}
					else {
						System.out.println("������ȷ�����룺");
						String newPasswordTwice;
						while(true) {
							newPasswordTwice=scan.nextLine();
							if(!newPassword.equals(newPasswordTwice)) {
								System.out.println("����������������һ�£�����������ȷ�����룺");
							}
							else {
								String MD5Password=MD5(newPassword);
								UserDAO.update(user.getUserName(), MD5Password);
								System.out.println("���ѳɹ��޸����룬�����");
								return ;
							}
							
						}
					
					}
				}
				
			}
			
		}
		
	}
	
	
	//��������Ƿ���ϸ�ʽҪ��
	public static boolean validatePassword(String password) {
        return 	Pattern.compile("[0-9]").matcher(password).find() &&
                Pattern.compile("[a-z]").matcher(password).find() &&
                Pattern.compile("[A-Z]").matcher(password).find() &&
                Pattern.compile("[a-zA-Z0-9]{5,}").matcher(password).find();
    }
	
	
	//MD5�㷨����
	public static String MD5(String strSrc) {
        try {
            char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f' };            // ����������������
            byte[] bytes = strSrc.getBytes();     // �ַ���ת��Ϊ byte���ͣ�һ���ַ�ת��Ϊһ��byte������ 0Ϊ48,1Ϊ49��ascll��
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);        // ��ָ���� byte �������ժҪ
            bytes = md.digest();  // ͨ��ִ���������֮������ղ�����ɹ�ϣ���㡣�ڵ��ô˷���֮��ժҪ�����ã��õ��̶�����Ϊ16�� bytes����
            int len = bytes.length;  // �õ�����
            char[] chars = new char[len * 2];       //��ʼ���ַ���������,chars���ڴ洢���ܵ����ݣ��˶�����Ϊ����Ĵ���һ��
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];                          // byte���ͱ�ʾ-128~127������
                chars[k++] = hexChars[b >>> 4 & 0xf];       
             // >>>��ʾ�޷������ƣ�oxf��ʾ1111��hexCharsΪ����д�����飬hexChars�ĳ���Ϊ16������ & 0xf ����ȡ���4λ�ó��±�λ��ȡ���ַ���
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5���ܳ�����+" + e);
        }
    }

	
	// �û��˳�
	public static boolean finish() {
		System.out.println("ȷ���˳�ϵͳ��");
		Scanner scan = new Scanner(System.in);
		String anwser = scan.nextLine();
		if (anwser.equals("y")) {
			System.out.println("�����˳���");
			return true;
		} else {
			return false;
		}
	}
	
	//�û�ע��
	public static void sign() {
		Scanner scan = new Scanner(System.in);
		System.out.println("�������û���");
		while(true) {
			String userName=scan.nextLine();
			User user = UserDAO.get(userName);//���ظ����û�����ѯ�Ķ���
			if (user != null) {// user��Ϊ�գ�˵���Ѿ����ڸ��û���
				System.out.println("�Ѵ��ڸ��û���������������:");
			} 
			else {
				System.out.println("���������룺");
				while(true) {
					String password=scan.nextLine();
					if(!validatePassword(password)) {//�����ϸ�ʽ
						System.out.println("�������벻���ϸ�����Ҫ�����볤�Ȳ�����6���ַ���������һ��Сд��ĸ��"
								+ "������һ����д��ĸ������һ�����֣������������룺");
					}
					else {
						System.out.println("������ȷ�����룺");
						String passwordTwice;
						while(true) {
							passwordTwice=scan.nextLine();
							if(!password.equals(passwordTwice)) {//�������벻һ��
								System.out.println("����������������һ�£�����������ȷ�����룺");
							}
							else {
								String MD5Password=MD5(password);
								System.out.println("����������");
								String name=scan.nextLine();
								System.out.println("���������Ľ�ɫ������Ա�����Ա����");
								String role=scan.nextLine();
								User user1=new User(userName, MD5Password, name, role);
								UserDAO.insert(user1);
								System.out.println("ע��ɹ���");
								System.out.println();
								return;
							}
						}
					}
				}	
			}
			
		}
		
	}
}
