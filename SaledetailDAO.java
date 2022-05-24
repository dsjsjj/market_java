package dao;

//���ۼ�¼DAO
import java.util.ArrayList;
import util.JDBCUtil;
import vo.Saledetail;

public class SaledetailDAO {
	//����
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
			
		//��ѯһ����¼
		public static Saledetail getMaxLsh() {
			return JDBCUtil.get("select * from tsaledetail where lsh="
					+ "(select max(lsh+0) from tsaledetail)",Saledetail.class);
		}
		
		//��ѯ�������ۼ�¼
		public static ArrayList<Saledetail> query(String saleTime){
			return JDBCUtil.query("select * from tsaledetail where saleTime like ?",
					Saledetail.class, saleTime);
		}
		
		//��������ѯ�������ۼ�¼
		public static ArrayList<Saledetail> queryAll(){
			return JDBCUtil.query("select * from tsaledetail", Saledetail.class);
		}
}
