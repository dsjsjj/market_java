package dao;

//销售记录DAO
import java.util.ArrayList;
import util.JDBCUtil;
import vo.Saledetail;

public class SaledetailDAO {
	//增加
			public static boolean insert(Saledetail saledetail){
				return JDBCUtil.exeUpdate("insert into tsaledetail values(?,?,?,?,?,?,?)", 
						saledetail.getLsh(),
						saledetail.getBarCode(),
						saledetail.getProductName(),
						saledetail.getPrice(),
						saledetail.getCount(),
						saledetail.getOperator(),
						saledetail.getSaleTime());
			}
			
		//查询一条记录
		public static Saledetail getMaxLsh() {
			return JDBCUtil.get("select * from tsaledetail where lsh="
					+ "(select max(lsh+0) from tsaledetail)",Saledetail.class);
		}
		
		//查询多条销售记录
		public static ArrayList<Saledetail> query(String saleTime){
			return JDBCUtil.query("select * from tsaledetail where saleTime like ?",
					Saledetail.class, saleTime);
		}
		
		//无条件查询所有销售记录
		public static ArrayList<Saledetail> queryAll(){
			return JDBCUtil.query("select * from tsaledetail", Saledetail.class);
		}
}
