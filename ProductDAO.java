package dao;

//��ƷDAO
import java.util.ArrayList;
import util.JDBCUtil;
import vo.Product;

public class ProductDAO {
		//����
		public static boolean insert(Product product){
			return JDBCUtil.exeUpdate("insert into tproduct values(?,?,?,?)", 
				product.getBarCode(),
				product.getProductName(),
				product.getPrice(),
				product.getSupply());
		}
		
		//��ѯһ����¼
		public static Product get(String barCode) {
			return JDBCUtil.get("select * from tproduct where barCode =?",Product.class,
					barCode);
		}
		
		//��ѯ������¼
		public static ArrayList<Product> query(String productName){
			return JDBCUtil.query("select * from tproduct where productName like ?", Product.class, productName);
		}
		
		//��ѯ�������ݿ������tproduct��
		public static ArrayList<Product> queryAll(){
			return JDBCUtil.query("select * from tproduct", Product.class);
		}
}
