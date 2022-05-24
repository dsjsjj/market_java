package service;

//��Ʒҵ���߼�ʵ��
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


	//��Ʒά��
	public static void preserve(User user) {
		if(user.getRole().equals("����Ա")) {
			System.out.println("��ǰ�û�û��ִ�и���ܵ�Ȩ��");
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
				 default :System.out.println("������Ч��ֻ������1-6");
				 }
				 choice=Menu1();
			 }
		}
	}
	

	


	//����Ʒ���Ʋ�ѯ
	private static void goodsSearch() {
		// TODO Auto-generated method stub
		System.out.println("�������ѯ����Ʒ���ƣ�");
		Scanner scan=new Scanner(System.in);
		String productName=scan.nextLine();
		ArrayList<Product> product=ProductDAO.query("%"+productName+"%");
		System.out.println("���������ļ�¼�ܹ�"+product.size()+"������Ϣ���£�");
		System.out.println("���\t������\t��Ʒ����\t����\t��Ӧ��");
		System.out.println("===\t=====\t=====\t===\t=====");
		for(int i=0;i<product.size();i++) {
			Product pro=product.get(i);
			System.out.println((i+1)+"\t"+pro.getBarCode()+"\t"+
			pro.getProductName()+"\t"+pro.getPrice()+"\t"+pro.getSupply());
		}
		System.out.println();
	}
	
	
	

	//�Ӽ��̶�ȡ��Ʒ��Ϣ
	private static void readFromKerboard() {
		// TODO Auto-generated method stub
		System.out.println("��������Ʒ��Ϣ����eg����Ʒ������,��Ʒ����,����,��Ӧ�̡�����");
		Scanner scan=new Scanner(System.in);
		String info=scan.nextLine();
		String barCodeRge="[\\d]{6}";
		String produceNameRge=".";
		String priceRge="^[0-9]\\d*(\\.\\d{1,2})?$";
		String supplyRge="\\d+";
		while(true) {
			//��������
			String arr[]=info.split(",|��");
			//��������
			Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
			if(!(arr[0].matches(barCodeRge))||arr[1].matches(produceNameRge)||
					!(arr[2].matches(priceRge))||arr[3].matches(supplyRge)) {
				System.out.println("����������ݸ�ʽ����ȷ������������");
				info=scan.nextLine();//������һ��
			}
			else {
				if(check(pro)) {//���ظ�
					ProductDAO.insert(pro);//�����ݿ������һ��
					System.out.println("����ɹ���");
					return;
				}
				else {//�������ظ�
					System.out.println("�����벻���ظ�������������");
					info=scan.nextLine();//������һ��
				}
			}
		}
	}
	
	//��xml�ļ���������
	private static void readFromXml(String fileName) {
		// TODO Auto-generated method stub
		ArrayList<Product> product =new ArrayList<Product>();
		try {
			//1.��xml�ļ�������document����
			SAXReader read =new SAXReader();
			Document doc =read.read(new File(fileName));
			//2.��ȡ��Ԫ��
			Element root=doc.getRootElement();
			List list=root.elements("product");
			//3.�Լ��Ͻ��б���
			//���õ������ķ������б���
			int count=0;
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				Element sdElement = (Element) iterator.next();
				//ȡ�������ӽڵ㣬��ȡ����
		    	String barCode = sdElement.element("barCode").getText();
				String productName = sdElement.element("productName").getText();
				String price = sdElement.element("price").getText();
				String supply = sdElement.element("supply").getText();
		    	Product pro = new Product(barCode, productName,Double.parseDouble(price),supply);
				product.add(pro);//����Ʒ��Ϣ�����б�
			}	
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//��Ʒ���в�����
					ProductDAO.insert(pro);//�����ݿ������һ��
					count++;//��¼��+1
				}
				else {
					//System.out.println("�ظ���Ϣ��������ӣ�");
				}
			}
			System.out.println("�ɹ���xml�ļ�����"+count+"����Ʒ��Ϣ");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	

	
	//��txt��ȡ��Ʒ��Ϣ
	private static void readFromTxt(String fileName) {
		// TODO Auto-generated method stub
		ArrayList<Product> product =new ArrayList<Product>();
		try {
			//ˮ��
			File file = new File(fileName);
			//ˮ��
			FileReader read=new FileReader(file);
			//ˮ��ͷ
			BufferedReader bf=new BufferedReader(read);
			String info=bf.readLine();//��һ�У�����
			int count=0;
			info=bf.readLine();//�ڶ��п�ʼ������
			while(info!=null) {
				//��������
				String arr[]=info.split("\t");
				Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
				product.add(pro);//����ȡ������ѭ������product������
				info=bf.readLine();//��ȡ��һ��
				}
			read.close();
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//��Ʒ���в�����
					ProductDAO.insert(pro);//�����ݿ������һ��
					count++;//��¼��+1
				}
				else {
					//System.out.println("�ظ���Ϣ��������ӣ�");
				}
			}
			System.out.println("�ɹ����ı��ļ�����"+count+"����Ʒ����");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
	}

	
		//��xls����ж�ȡ��Ʒ����
		public static void readFromXls(String fileName){
		ArrayList<Product> product =new ArrayList<Product>();
		try{
			Workbook wb=Workbook.getWorkbook(new File(fileName));
			Sheet sheet1 =wb.getSheet(0);
			int count=0;
			int rows=sheet1.getRows();//����
			int cols=sheet1.getColumns();//����
			for(int j=1;j<rows;j++){
				Product pro = new Product(sheet1.getCell(0, j).getContents(),
										sheet1.getCell(1, j).getContents(),
										Double.parseDouble(sheet1.getCell(2, j).getContents()),
										sheet1.getCell(3, j).getContents());
				product.add(pro);//����ȡ������ѭ������product������
			}
			for(int i=0;i<product.size();i++) {
				Product pro=product.get(i);
				if(check(pro)) {//��Ʒ���в�����
					ProductDAO.insert(pro);//�����ݿ������һ��
					count++;//��¼��+1
				}
				else {
					//System.out.println("�ظ���Ϣ��������ӣ�");
				}
			}
			wb.close();
			System.out.println("�ɹ���excel�ļ�����"+count+"����Ʒ����");
			System.out.println();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}

	
	//�жϸö�������ݿ��еĶ����Ƿ��ظ�
	public static boolean check(Product pro) {
		Product product=ProductDAO.get(pro.getBarCode());//�����ݿ��в����Ƿ������ͬ������
		if(product!=null) {//product�б�Ϊ�գ�˵��������ͬ������
			return false;
		}
		return true;
	}
	
	//�Ӳ˵�
	private static int Menu1() {
		// TODO Auto-generated method stub
		System.out.println("===�Ĵ�����Ʒ����ά��====");
		System.out.println("1.��excel�е�������");
		System.out.println("2.���ı��ļ���������");
		System.out.println("3.��xml�ļ���������");
		System.out.println("4.�Ӽ�������");
		System.out.println("5.��Ʒ��ѯ");
		System.out.println("6.�������˵�");
		System.out.println("��ѡ��1-6����");
		Scanner scan=new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}
}
