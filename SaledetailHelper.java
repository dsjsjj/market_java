package service;

//销售记录业务逻辑实现
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import dao.ProductDAO;
import dao.SaledetailDAO;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import vo.Saledetail;
import vo.User;


public class SaledetailHelper {

	// 收银程序
	public static void cashier(User user) throws ParseException {
		System.out.println("请输入商品条形码（6位数字字符）：");
		// 判断格式是否正确
		Scanner scan = new Scanner(System.in);
		String regex = "\\d{6}";
		String barCode;
		while (true) {
			barCode = scan.nextLine();
			if (!barCode.matches(regex)) {// 格式不正确
				System.out.println("条形码输入格式不正确，请重新输入");
			} else// 格式正确 后核对数据库中是否存在该条形码
			{
				if (ProductDAO.get(barCode) == null) {// 不存在
					System.out.println("您输入的商品条形码不存在，请确认后重新输入");
				} else {// 存在
					System.out.println("输入商品数量:");
					int count = scan.nextInt();
					String productName = ProductDAO.get(barCode).getProductName();// 得到对应的名称
					double price = ProductDAO.get(barCode).getPrice();// 得到对应的单价
					String operator = user.getUserName();// 得到对应登录用户名
					Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 收银时间
					SimpleDateFormat lshdf = new SimpleDateFormat("yyyyMMdd"); // 流水号前8位
					String saleTime = df.format(time); // 收银时间
					Date date = df.parse(saleTime);
					Saledetail s = SaledetailDAO.getMaxLsh();
					if (s == null) {// 流水号记录为空
						String lsh_fomr8 = lshdf.format(time);// 今天流水号前8位
						String lsh = lsh_fomr8 + "0000";
						Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
						SaledetailDAO.insert(sd);
						System.out.println("成功增加一笔销售数据");
						return;
					} else {// 不为空
						String MaxLsh = s.getLsh();// 最大流水号
						String ymd = MaxLsh.substring(0, 8);// 最大流水号前8位
						String last_4 = MaxLsh.substring(9, 12);// 最大流水号后4位
						int intlast_4 = Integer.parseInt(last_4);
						String newlast_4 = String.format("%04d", intlast_4 + 1);
						String lsh_fomr8 = lshdf.format(time);// 今天流水号前8位
						String lsh;
						if (lsh_fomr8.equals(ymd)) {// 日期相同
							lsh = lsh_fomr8 + newlast_4;
						} else// 日期不相同
						{
							lsh = lsh_fomr8 + "0000";
						}
						Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
						SaledetailDAO.insert(sd);
						System.out.println("成功增加一笔销售数据");
						System.out.println();
						return;
					}
				}
			}
		}
	}

	
	//根据日期统计
	public static void statistics() {
		System.out.println("请输入销售日期（yyyy-mm-dd）：");
		Scanner scan = new Scanner(System.in);
		String date;
		String reg_yyyy_MM_dd = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
				+ "-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|"
				+ "(02-(0[1-9]|[1][0-9]|2[0-8]))))"
				+ "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
		while (true) {
			date = scan.nextLine();
			if (!date.matches(reg_yyyy_MM_dd)) {
				System.out.println("你输入的日期格式不正确，请重新输入：");
			} else {
//				System.out.println("你的格式输入正确");
				String saleTime = date + "%";
//				System.out.println(saleTime);
				ArrayList<Saledetail> list = SaledetailDAO.query(saleTime);
//				System.out.println(list.toString());

				String year = date.substring(0, 4);
				String month = date.substring(5, 7);
				String day = date.substring(8, 10);
				System.out.println(year + "年" + month + "月" + day + "日销售如下:");
				System.out.println("流水号\t\t商品名称\t单价\t数量\t金额\t时间\t\t\t收银员");
				System.out.println("=====\t\t====\t====\t====\t===\t====\t\t\t=====");
				int saleNum = 0;
				int totalNum = 0;
				double totalMoney = 0;
				for (int i = 0; i < list.size(); i++) {
					Saledetail sd = list.get(i);
					System.out.println(sd.getLsh()+"\t"+sd.getProductName()+"\t"+sd.getPrice()+"\t"
							+ sd.getCount()+"\t"+sd.getCount()*sd.getPrice() + "\t" + 
							sd.getSaleTime() + "\t"+ sd.getOperator());
					saleNum++;// 销售总数
					totalNum = totalNum + sd.getCount();// 商品总数
					totalMoney = totalMoney + sd.getCount() * sd.getPrice();// 销售总金额
				}
				System.out.println("销售总数：" + saleNum + "商品总件：" + totalNum + " 销售总金额：" + totalMoney);
				System.out.println("日期:" + year + "年" + month + "月" + day + "日");
				System.out.println();
				System.out.println("请按任意键返回主界面");
				String anyKey = null;
				anyKey = scan.nextLine();
				if (anyKey != null) {
					return;
				}
			}
		}
	}

	// 商品维护
	public static void preserve() {

		int choice = Menu2();
		while (true) {
			switch (choice) {
			case 1:
				writeToXls();
				break;
			case 2:
				writeToTxt();
				break;
			case 3:
				writeToXml();
				break;
			case 4:
				return;
			default:
				System.out.println("输入无效，只能输入1-4");
			}
			choice = Menu2();
		}
	}
	
	//将销售记录导入txml文件
	private static void writeToXml() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
		Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // 取得日期格式
		String writeTime = df.format(time); //导出日期
		String fileName ="data/saleDetail"+writeTime+".xml";
		// TODO Auto-generated method stub
		//将list集合中的内写入xml文件中
		Document doc = DocumentHelper.createDocument();
		//增加根节点
		Element saledetail = doc.addElement("Saledetail");
		//增加子元素
		for(int i=0;i<listSaledetail.size();i++) {
			Saledetail sd=listSaledetail.get(i);
			Element lsh= saledetail.addElement("流水号");
			Element barCode=saledetail.addElement("barCode");
			Element productName=saledetail.addElement("productName");
			Element price=saledetail.addElement("price");
			Element count=saledetail.addElement("count");
			Element operator=saledetail.addElement("operator");
			Element saleTime=saledetail.addElement("saleTime");
			//为子节点添加属性
			lsh.addAttribute("lsh",sd.getLsh());
			//为元素添加属性
			barCode.setText(sd.getBarCode());
			productName.setText(sd.getProductName());
			price.setText(String.valueOf(sd.getPrice()));
			count.setText(String.valueOf(sd.getCount()));
			operator.setText(sd.getOperator());
			saleTime.setText(String.valueOf(sd.getSaleTime()));
			//实例化输出格式对象
			OutputFormat format = OutputFormat.createPrettyPrint();
			//设置输出编码
			format.setEncoding("UTF-8");
			//创建要写入的File对象
			File file = new File(fileName);
			//生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
			XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
			//开始写入，writer方法中包含上面创建的Docu对象
			writer.write(doc);
		}
		System.out.println("成功导出"+listSaledetail.size()+"条销售数据到xml文件");
		System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	//将销售记录导入txt文件
	public static void writeToTxt() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
			Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // 取得日期格式
			String writeTime = df.format(time); //导出日期
			String fileName ="data/saleDetail"+writeTime+".txt";
			// 下水管
			File file = new File(fileName);
			// 下水道
			FileWriter fw = new FileWriter(file);
			// 水龙头
			PrintWriter pw = new PrintWriter(fw);
			// 标题栏
			pw.println("流水号\t条形码\t商品名称\t价格\t数量\t收银员\t销售时间");

			for (Saledetail sd : listSaledetail) {
				String info = sd.toString();
				pw.println(info);
			}
			fw.close();
			System.out.println("成功导出" + listSaledetail.size() + "条销售数据到文本文件中");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	//将销售记录导入excell表格
	public static void writeToXls() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
			Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // 取得日期格式
			String writeTime = df.format(time); //导出日期
			String fileName ="data/saleDetail"+writeTime+".xls";
			//写入xls表
			//创建工作薄对象
			WritableWorkbook book = Workbook.createWorkbook(new File(fileName));
			//创建工作表对象
			WritableSheet sheet=book.createSheet("流水信息", 0);
			String title[]={"流水号","条形码","商品名称","价格","数量","收银员","销售时间"};
			for(int i=0;i<title.length;i++){
	            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
				CellView cellView = new CellView();
	            cellView.setAutosize(true); //设置自动大小  
	            //sheet.setColumnView(i, cellView); //根据内容自动设置列宽  
	            WritableCellFormat format = new WritableCellFormat(font);
	            format.setAlignment(Alignment.CENTRE); //居中对齐
	            format.setBackground(Colour.YELLOW); //背景色
	            format.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);//边框
				Label label = new Label(i,0,title[i]);
				sheet.addCell(label);//将流水号，条形码，商品名称，价格，数量，收银员，销售时间标题依次加入单元格
			}
			//写数据，按行添加
			int count1 =1;
			for(Saledetail sd:listSaledetail){
				Label label = new Label(0, count1, sd.getLsh());
				sheet.addCell(label);//将listSaledetail中的count行的lsh加入（0，1）位置的单元格
				label = new Label(1, count1, sd.getBarCode());
				sheet.addCell(label);//将listSaledetail中的count行的条形码加入（1，1）位置的单元格
				label =new Label(2, count1, sd.getProductName());
				sheet.addCell(label);//将listSaledetail中的count行的商品名称加入（2，1）位置的单元格
				label = new Label(3, count1,String.valueOf(sd.getPrice()));
				sheet.addCell(label);//将listSaledetail中的count行的单价加入（3，1）位置的单元格
				label = new Label(4, count1,String.valueOf(sd.getCount()));
				sheet.addCell(label);//将listSaledetail中的count行的数量加入（4，1）位置的单元格
				label = new Label(5, count1,String.valueOf(sd.getOperator()));
				sheet.addCell(label);//将listSaledetail中的count行的收银员加入（4，1）位置的单元格
				label = new Label(6, count1,String.valueOf(sd.getSaleTime()));
				sheet.addCell(label);//将listSaledetail中的count行的收银时间加入（4，1）位置的单元格
				count1++;//进入下一行
			}
			book.write();//写入表格
			book.close();
			System.out.println("成功导出"+listSaledetail.size()+"条销售数据到excel文件中");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//子菜单
	private static int Menu2() {
		// TODO Auto-generated method stub
		System.out.println("===纺大超市销售信息导出====");
		System.out.println("1.导出到 excel 文件");
		System.out.println("2.导出到文本文件");
		System.out.println("3.导出到xml文件");
		System.out.println("4.返回主菜单");
		System.out.println("请选择（1-4）：");
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}

}
