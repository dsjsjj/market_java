package dao;

//商品DAO
import java.util.ArrayList;
import util.JDBCUtil;
import vo.Product;

public class ProductDAO {
		//增加
		public static boolean insert(Product product){
			return JDBCUtil.exeUpdate("insert into tproduct values(?,?,?,?)", 
				product.getBarCode(),
				product.getProductName(),
				product.getPrice(),
				product.getSupply());
		}
		
		//查询一条记录
		public static Product get(String barCode) {
			return JDBCUtil.get("select * from tproduct where barCode =?",Product.class,
					barCode);
		}
		
		//查询多条记录
		public static ArrayList<Product> query(String productName){
			return JDBCUtil.query("select * from tproduct where productName like ?", Product.class, productName);
		}
		
		//查询整个数据库的完整tproduct表
		public static ArrayList<Product> queryAll(){
			return JDBCUtil.query("select * from tproduct", Product.class);
		}
}
