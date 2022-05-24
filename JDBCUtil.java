package util;

//封装jdbc
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Properties;

/*
封装jdbc的基本操作
1.加载驱动
2.建立连接
3.创建语句
4.sql语句中的变量赋值（可选）
5.执行语句
6.结果处理
7.关闭连接
*/

//问题1：有些操作在整个项目中只需要执行一次，无需重复执行
//解决方法：采取static代码块
//问题2：程序中某些可能有变化的信息是硬编码，不便于维护
//解决方法：把这些信息（驱动累得名字，URL，用户名，密码）存放在一个properties文件（HashMap）形式key-value中


public class JDBCUtil {
	private static String className;
	private static String url;
	private static String user;
	private static String password;
	//静态代码，配置文件，加载驱动
	static{
		try {
			//从properties文件读取相关信息
			Properties pro = new Properties();
			pro.load(JDBCUtil.class.getResourceAsStream("/resource/config.properties"));
			className = pro.getProperty("className");
			url = pro.getProperty("url");
			user = pro.getProperty("user");
			password = pro.getProperty("password");
			//1.加载驱动
			Class.forName(className);//类的全名
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//2.获取连接
	public static Connection getConnection(){
		Connection con=null;
		try {
			con= DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			// TODO: handle exception
		e.printStackTrace();
		}
		return con;
	}
	/*------封装更新类(insert,delete,update)的sql类-------*/
	//不同之处通过参数进行传递（sql语句，sql语句的变量）
	//需要的sql语句的变量个数不统一
	//解决办法：可变参数
	public static boolean exeUpdate(String sql,Object... params) {
		Connection con=null;
		try {
			con=JDBCUtil.getConnection();
			PreparedStatement pst =con.prepareStatement(sql);
			
			if(params!=null) {//有变量则需要对变量进行赋值
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			return pst.executeUpdate()>0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {//无论有异常都需要执行的代码
		//6.关闭连接
			release(con);
		}
		return false;
	}
	
	//3.关闭连接
	public static void release(Connection con) {
		// TODO Auto-generated method stub
		try {
			if(con!=null) {
				con.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*------5.封装查询类，按关键字（主键）进行查询-----*/
	//返回的是对象，查询不同的表，返回的对象类型不一样
	//解决办法：泛型（任意类型）
	public static <T> T get(String sql,Class<T> cls, Object...params) {
		T t=null;
		Connection con =null;
		try {
			con=JDBCUtil.getConnection();//连接
			PreparedStatement pst= con.prepareStatement(sql);
			//4.执行语句
			if(params!=null) {//有变量则需要对变量进行赋值
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			ResultSet rs=pst.executeQuery();//查询
			
			if(rs.next()) {//有结果
				//将记录集映射为泛型对象
				//创建该对象:使用Class类的对象中的newInstance方法创建该类的对象
				t=cls.getDeclaredConstructor().newInstance();//该方法取代了newInstance();
				//查询结果的字段列表如何获取
				
				//使用ResultSetMetaData类
				ResultSetMetaData md=rs.getMetaData();
				int columnNumber = md.getColumnCount();//获取记录的列数
				//遍历记录集的每一列
				for(int i=1;i<=columnNumber;i++) {
					String columnName = md.getColumnName(i);
					String type = md.getColumnTypeName(i);
					//System.out.println(type);
					Object columnValue;
					switch(type) {
					case "DATE":columnValue =rs.getString(columnName);break;
					case "DECIMAL":columnValue=rs.getDouble(columnName);break;
					default:columnValue =rs.getObject(columnName);
					}
					Field field = cls.getDeclaredField(columnName);
					field.setAccessible(true);
					field.set(t,columnValue);
					field.setAccessible(false);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {
			//6.关闭连接
			release(con);
		}
		return t;
	}
	
	
	//6.封装按照多个条件进行查询，返回的是多条记录
	public static <T> ArrayList<T> query(String sql,Class<T> cls,Object...params){
		ArrayList<T> list =new ArrayList<T>();
		Connection con = null;
		T t= null;
		try {
			con=JDBCUtil.getConnection();
			PreparedStatement pst = con.prepareStatement(sql);
			//4.执行语句
			if(params!=null) {//有变量则需要对变量赋值
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			ResultSet rs = pst.executeQuery();//返回查询结果
			while(rs.next()) {//有结果	
				//将记录集映射为泛型对象
				//创建该对象:使用Class类的对象中的newInstance方法创建该类的对象
				t=cls.getDeclaredConstructor().newInstance();//该方法取代了newInstance();
				//查询结果的字段列表如何获取
				
				//使用ResultSetMetaData类
				ResultSetMetaData md=rs.getMetaData();//得到字段名
				int columnNumber = md.getColumnCount();//获取记录的列数
				//遍历记录集的每一列
				for(int i=1;i<=columnNumber;i++) {
					String columnName = md.getColumnName(i);//返回列名
					String type = md.getColumnTypeName(i);//返回列类型
					Object columnValue;
					switch(type) {
					case "DATE":columnValue =rs.getString(columnName);break;
					case "DECIMAL":columnValue =rs.getDouble(columnName);break;
					default:columnValue =rs.getObject(columnName);
					}
					Field field = cls.getDeclaredField(columnName);
					field.setAccessible(true);
					field.set(t,columnValue);
					field.setAccessible(false);	
				}
				list.add(t);	
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		finally {
			//6.关闭连接
			release(con);
		}
		return list;
	}
}
