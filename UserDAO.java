package dao;

//用户DAO
import java.util.ArrayList;
import util.JDBCUtil;
import vo.User;

public class UserDAO {
	//1.增加
	public static boolean insert(User user){
		return JDBCUtil.exeUpdate("insert into tuser values(?,?,?,?)", 
			user.getUserName(),
			user.getPassword(),
			user.getName(),
			user.getRole());
	}
	
	//删除
	public static boolean delete(String userName){
		return JDBCUtil.exeUpdate("delete from tuser where userName=?", userName);
	}
	
	//查询一条记录
	public static User get(String userName) {
		return JDBCUtil.get("select * from tuser where userName =?",User.class,
				userName);
	}
	
	//查询多条记录
	public static ArrayList<User> query(String name){
		return JDBCUtil.query("select * from tuser where name =?", User.class, name);
	}
	
	//修改密码
	public static boolean update(String userName,String newPassword) {
		return JDBCUtil.exeUpdate("update tuser set password=? where userName = ?",newPassword, userName);
	}
}
