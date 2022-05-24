package service;

//用户业务逻辑实现
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Pattern;

import dao.UserDAO;
import vo.User;

public class UserHelper {

	// 用户登录程序
	public static User login() {
		System.out.println("欢迎使用纺大超市收银系统，请登陆:");
		for (int i = 0; i < 3; i++) {
			String UserName = inputUserName();//用户名
			String password = inputpassword();//密码
			User user = UserDAO.get(UserName);//返回根据用户名查询的对象
			if (user == null) {// 先判断user是否为空
				System.out.println("用户名或密码不正确，请重新输入:");
			} else {
				String userFind = user.getPassword();//得到数据库中的密码
//					System.out.println(userFind);
				if (userFind != null) {// 再判断userFind是否为空
					String MD5password=MD5(password);
					if (userFind.equals(MD5password)) {// 最后判断是否相等
						
						return user;
					} else {
						System.out.println("用户名或密码不正确，请重新输入:");
					}
				} else {
					System.out.println("用户名或密码不正确，请重新输入:");
				}
			}
		}
		System.out.println("最多只能尝试3次!");
		return null;
	}

	// 输入用户名
	public static String inputUserName() {
		// 提示输入用户名
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入用户名：");
		String user = scan.nextLine();
		return user;
	}

	// 输入密码
	public static String inputpassword() {
		// 提示输入密码
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入密码：");
		String password = scan.nextLine();
		return password;
	}
	
	//修改密码
	public static void updatePassword(User user) {
		System.out.println("请输入当前用户的原密码：");
		Scanner scan = new Scanner(System.in);
		String oldPassword;
		String newPassword;
		while(true) {
			oldPassword = scan.nextLine();//原密码
			String MD5oldPassword=MD5(oldPassword);
			if(!MD5oldPassword.equals(user.getPassword())) {//原密码和输入的密码一样
				System.out.println("原密码输入不正确，请重新输入");
			}
			else {//密码正确
				System.out.println("请设置新的密码：");
				while(true) {
					newPassword=scan.nextLine();
					if(!validatePassword(newPassword)) {//不匹配
						System.out.println("您的密码不符合复杂性要求（密码长度不少于6个字符，至少有一个小写字母，"
								+ "至少有一个大写字母，至少一个数字），请重新输入：");
					}
					else {
						System.out.println("请输入确认密码：");
						String newPasswordTwice;
						while(true) {
							newPasswordTwice=scan.nextLine();
							if(!newPassword.equals(newPasswordTwice)) {
								System.out.println("两次输入的密码必须一致，请重新输入确认密码：");
							}
							else {
								String MD5Password=MD5(newPassword);
								UserDAO.update(user.getUserName(), MD5Password);
								System.out.println("您已成功修改密码，请谨记");
								return ;
							}
							
						}
					
					}
				}
				
			}
			
		}
		
	}
	
	
	//检查密码是否符合格式要求
	public static boolean validatePassword(String password) {
        return 	Pattern.compile("[0-9]").matcher(password).find() &&
                Pattern.compile("[a-z]").matcher(password).find() &&
                Pattern.compile("[A-Z]").matcher(password).find() &&
                Pattern.compile("[a-zA-Z0-9]{5,}").matcher(password).find();
    }
	
	
	//MD5算法加密
	public static String MD5(String strSrc) {
        try {
            char hexChars[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f' };            // 用于输出编码的类型
            byte[] bytes = strSrc.getBytes();     // 字符串转化为 byte类型，一个字符转化为一个byte，比如 0为48,1为49，ascll码
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);        // 用指定的 byte 数组更新摘要
            bytes = md.digest();  // 通过执行诸如填充之类的最终操作完成哈希计算。在调用此方法之后，摘要被重置，得到固定长度为16的 bytes数组
            int len = bytes.length;  // 得到长度
            char[] chars = new char[len * 2];       //初始化字符数组容量,chars用于存储加密的内容，乘二是因为下面的处理，一个
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];                          // byte类型表示-128~127的整数
                chars[k++] = hexChars[b >>> 4 & 0xf];       
             // >>>表示无符号右移，oxf表示1111，hexChars为上面写的数组，hexChars的长度为16，经过 & 0xf 运算取最低4位得出下标位置取出字符串
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }

	
	// 用户退出
	public static boolean finish() {
		System.out.println("确认退出系统吗？");
		Scanner scan = new Scanner(System.in);
		String anwser = scan.nextLine();
		if (anwser.equals("y")) {
			System.out.println("程序退出！");
			return true;
		} else {
			return false;
		}
	}
	
	//用户注册
	public static void sign() {
		Scanner scan = new Scanner(System.in);
		System.out.println("请输入用户名");
		while(true) {
			String userName=scan.nextLine();
			User user = UserDAO.get(userName);//返回根据用户名查询的对象
			if (user != null) {// user不为空，说明已经存在该用户名
				System.out.println("已存在该用户名，请重新输入:");
			} 
			else {
				System.out.println("请设置密码：");
				while(true) {
					String password=scan.nextLine();
					if(!validatePassword(password)) {//不符合格式
						System.out.println("您的密码不符合复杂性要求（密码长度不少于6个字符，至少有一个小写字母，"
								+ "至少有一个大写字母，至少一个数字），请重新输入：");
					}
					else {
						System.out.println("请输入确认密码：");
						String passwordTwice;
						while(true) {
							passwordTwice=scan.nextLine();
							if(!password.equals(passwordTwice)) {//两次密码不一致
								System.out.println("两次输入的密码必须一致，请重新输入确认密码：");
							}
							else {
								String MD5Password=MD5(password);
								System.out.println("请输入姓名");
								String name=scan.nextLine();
								System.out.println("请输入您的角色（收银员或管理员）：");
								String role=scan.nextLine();
								User user1=new User(userName, MD5Password, name, role);
								UserDAO.insert(user1);
								System.out.println("注册成功！");
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
