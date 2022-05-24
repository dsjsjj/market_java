package service;

//商品业务逻辑实现
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import dao.ProductDAO;
import jxl.Sheet;
import jxl.Workbook;
import vo.Product;
import vo.User;

public class ProductHelper {


	//商品维护
	public static void preserve(User user) {
		if(user.getRole().equals("收银员")) {
			System.out.println("当前用户没有执行该项功能的权限");
			System.out.println();
		}
		else {
			 int choice=Menu1();
			 while(true)
			 {
				 switch(choice)
				 {
				 case 1:readFromXls("data/product.xls");break;
				 case 2:readFromTxt("data/product.txt");break;
				 case 3:readFromXml("data/product.xml");break;
				 case 4:readFromKerboard();break;
				 case 5:goodsSearch();break;
				 case 6:return ;
				 default :System.out.println("输入无效，只能输入1-6");
				 }
				 choice=Menu1();
			 }
		}
	}
	

	


	//按商品名称查询
	private static void goodsSearch() {
		// TODO Auto-generated method stub
		System.out.println("请输入查询的商品名称：");
		Scanner scan=new Scanner(System.in);
		String productName=scan.nextLine();
		ArrayList<Product> product=ProductDAO.query("%"+productName+"%");
		System.out.println("满足条件的记录总共"+product.size()+"条，信息如下：");
		System.out.println("序号\t条形码\t商品名称\t单价\t供应商");
		System.out.println("===\t=====\t=====\t===\t=====");
		for(int i=0;i<product.size();i++) {
			Product pro=product.get(i);
			System.out.println((i+1)+"\t"+pro.getBarCode()+"\t"+
			pro.getProductName()+"\t"+pro.getPrice()+"\t"+pro.getSupply());
		}
		System.out.println();
	}
	
	
	

	//从键盘读取商品信息
	private static void readFromKerboard() {
		// TODO Auto-generated method stub
		System.out.println("请输入商品信息，（eg：商品条形码,商品名称,单价,供应商”）：");
		Scanner scan=new Scanner(System.in);
		String info=scan.nextLine();
		String barCodeRge="[\\d]{6}";
		String produceNameRge=".";
		String priceRge="^[0-9]\\d*(\\.\\d{1,2})?$";
		String supplyRge="\\d+";
		while(true) {
			//分离数据
			String arr[]=info.split(",|，");
			//创建对象
			Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
			if(!(arr[0].matches(barCodeRge))||arr[1].matches(produceNameRge)||
					!(arr[2].matches(priceRge))||arr[3].matches(supplyRge)) {
				System.out.println("你输入的数据格式不正确，请重新输入");
				info=scan.nextLine();//输入下一行
			}
			else {
				if(check(pro)) {//不重复
					ProductDAO.insert(pro);//向数据库中添加一条
					System.out.println("导入成功！");
					return;
				}
				else {//条形码重复
					System.out.println("条形码不能重复，请重新输入");
					info=scan.nextLine();//输入下一行
				}
			}
		}
	}
	
	//从xml文件导入数据
	private static void readFromXml(String fileName) {
		// TODO Auto-generated method stub
		ArrayList<Product> product =new ArrayList<Product>();
		try {
			//1.从xml文件中生成document对象
			SAXReader read =new SAXReader();
			Document doc =read.read(new File(fileName));
			//2.获取根元素
			Element root=doc.getRootElement();
			List list=root.elements("product");
			//3.对集合进行遍历
			//采用迭代器的方法进行遍历
			int count=0;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Element sdElement = (Element) iterator.next();
				//取条形码子节点，获取内容
		    	String barCode = sdElement.element("barCode").getText();
				String productName = sdElement.element("productName").getText();
				String price = sdElement.element("price").getText();
				String supply = sdElement.element("supply").getText();
		    	Product pro = new Product(barCode, productName,Double.parseDouble(price),supply);
				product.add(pro);//将商品信息加入列表
			}	
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//商品表中不存在
					ProductDAO.insert(pro);//向数据库中添加一条
					count++;//记录数+1
				}
				else {
					//System.out.println("重复信息，不能添加！");
				}
			}
			System.out.println("成功从xml文件导入"+count+"条商品信息");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	

	
	//从txt读取商品信息
	private static void readFromTxt(String fileName) {
		// TODO Auto-generated method stub
		ArrayList<Product> product =new ArrayList<Product>();
		try {
			//水箱
			File file = new File(fileName);
			//水管
			FileReader read=new FileReader(file);
			//水龙头
			BufferedReader bf=new BufferedReader(read);
			String info=bf.readLine();//第一行，忽略
			int count=0;
			info=bf.readLine();//第二行开始是数据
			while(info!=null) {
				//分离数据
				String arr[]=info.split("\t");
				Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
				product.add(pro);//将读取的数据循环加入product集合中
				info=bf.readLine();//读取下一行
				}
			read.close();
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//商品表中不存在
					ProductDAO.insert(pro);//向数据库中添加一条
					count++;//记录数+1
				}
				else {
					//System.out.println("重复信息，不能添加！");
				}
			}
			System.out.println("成功从文本文件导入"+count+"条商品数据");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	}

	
		//从xls表格中读取商品数据
		public static void readFromXls(String fileName){
		ArrayList<Product> product =new ArrayList<Product>();
		try{
			Workbook wb=Workbook.getWorkbook(new File(fileName));
			Sheet sheet1 =wb.getSheet(0);
			int count=0;
			int rows=sheet1.getRows();//行数
			int cols=sheet1.getColumns();//列数
			for(int j=1;j<rows;j++){
				Product pro = new Product(sheet1.getCell(0, j).getContents(),
										sheet1.getCell(1, j).getContents(),
										Double.parseDouble(sheet1.getCell(2, j).getContents()),
										sheet1.getCell(3, j).getContents());
				product.add(pro);//将读取的数据循环加入product集合中
			}
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//商品表中不存在
					ProductDAO.insert(pro);//向数据库中添加一条
					count++;//记录数+1
				}
				else {
					//System.out.println("重复信息，不能添加！");
				}
			}
			wb.close();
			System.out.println("成功从excel文件导入"+count+"条商品数据");
			System.out.println();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}

	
	//判断该对象和数据库中的对象是否重复
	public static boolean check(Product pro) {
		Product product=ProductDAO.get(pro.getBarCode());//在数据库中查找是否存在相同条形码
		if(product!=null) {//product列表不为空，说明存在相同条形码
			return false;
		}
		return true;
	}
	
	//子菜单
	private static int Menu1() {
		// TODO Auto-generated method stub
		System.out.println("===纺大超市商品管理维护====");
		System.out.println("1.从excel中导入数据");
		System.out.println("2.从文本文件导入数据");
		System.out.println("3.从xml文件导入数据");
		System.out.println("4.从键盘输入");
		System.out.println("5.商品查询");
		System.out.println("6.返回主菜单");
		System.out.println("请选择（1-6）：");
		Scanner scan=new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}
}
