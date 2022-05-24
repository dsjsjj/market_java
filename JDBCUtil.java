package util;

//��װjdbc
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Properties;

/*
��װjdbc�Ļ�������
1.��������
2.��������
3.�������
4.sql����еı�����ֵ����ѡ��
5.ִ�����
6.�������
7.�ر�����
*/

//����1����Щ������������Ŀ��ֻ��Ҫִ��һ�Σ������ظ�ִ��
//�����������ȡstatic�����
//����2��������ĳЩ�����б仯����Ϣ��Ӳ���룬������ά��
//�������������Щ��Ϣ�������۵����֣�URL���û��������룩�����һ��properties�ļ���HashMap����ʽkey-value��


public class JDBCUtil {
	private static String className;
	private static String url;
	private static String user;
	private static String password;
	//��̬���룬�����ļ�����������
	static{
		try {
			//��properties�ļ���ȡ�����Ϣ
			Properties pro = new Properties();
			pro.load(JDBCUtil.class.getResourceAsStream("/resource/config.properties"));
			className = pro.getProperty("className");
			url = pro.getProperty("url");
			user = pro.getProperty("user");
			password = pro.getProperty("password");
			//1.��������
			Class.forName(className);//���ȫ��
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//2.��ȡ����
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
	/*------��װ������(insert,delete,update)��sql��-------*/
	//��֮ͬ��ͨ���������д��ݣ�sql��䣬sql���ı�����
	//��Ҫ��sql���ı���������ͳһ
	//����취���ɱ����
	public static boolean exeUpdate(String sql,Object... params) {
		Connection con=null;
		try {
			con=JDBCUtil.getConnection();
			PreparedStatement pst =con.prepareStatement(sql);
			
			if(params!=null) {//�б�������Ҫ�Ա������и�ֵ
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			return pst.executeUpdate()>0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {//�������쳣����Ҫִ�еĴ���
		//6.�ر�����
			release(con);
		}
		return false;
	}
	
	//3.�ر�����
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
	
	/*------5.��װ��ѯ�࣬���ؼ��֣����������в�ѯ-----*/
	//���ص��Ƕ��󣬲�ѯ��ͬ�ı����صĶ������Ͳ�һ��
	//����취�����ͣ��������ͣ�
	public static <T> T get(String sql,Class<T> cls, Object...params) {
		T t=null;
		Connection con =null;
		try {
			con=JDBCUtil.getConnection();//����
			PreparedStatement pst= con.prepareStatement(sql);
			//4.ִ�����
			if(params!=null) {//�б�������Ҫ�Ա������и�ֵ
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			ResultSet rs=pst.executeQuery();//��ѯ
			
			if(rs.next()) {//�н��
				//����¼��ӳ��Ϊ���Ͷ���
				//�����ö���:ʹ��Class��Ķ����е�newInstance������������Ķ���
				t=cls.getDeclaredConstructor().newInstance();//�÷���ȡ����newInstance();
				//��ѯ������ֶ��б���λ�ȡ
				
				//ʹ��ResultSetMetaData��
				ResultSetMetaData md=rs.getMetaData();
				int columnNumber = md.getColumnCount();//��ȡ��¼������
				//������¼����ÿһ��
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
			//6.�ر�����
			release(con);
		}
		return t;
	}
	
	
	//6.��װ���ն���������в�ѯ�����ص��Ƕ�����¼
	public static <T> ArrayList<T> query(String sql,Class<T> cls,Object...params){
		ArrayList<T> list =new ArrayList<T>();
		Connection con = null;
		T t= null;
		try {
			con=JDBCUtil.getConnection();
			PreparedStatement pst = con.prepareStatement(sql);
			//4.ִ�����
			if(params!=null) {//�б�������Ҫ�Ա�����ֵ
				for(int i=0;i<params.length;i++) {
					pst.setObject(i+1, params[i]);
				}
			}
			ResultSet rs = pst.executeQuery();//���ز�ѯ���
			while(rs.next()) {//�н��	
				//����¼��ӳ��Ϊ���Ͷ���
				//�����ö���:ʹ��Class��Ķ����е�newInstance������������Ķ���
				t=cls.getDeclaredConstructor().newInstance();//�÷���ȡ����newInstance();
				//��ѯ������ֶ��б���λ�ȡ
				
				//ʹ��ResultSetMetaData��
				ResultSetMetaData md=rs.getMetaData();//�õ��ֶ���
				int columnNumber = md.getColumnCount();//��ȡ��¼������
				//������¼����ÿһ��
				for(int i=1;i<=columnNumber;i++) {
					String columnName = md.getColumnName(i);//��������
					String type = md.getColumnTypeName(i);//����������
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
			//6.�ر�����
			release(con);
		}
		return list;
	}
}
