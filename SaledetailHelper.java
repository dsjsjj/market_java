package service;

//���ۼ�¼ҵ���߼�ʵ��
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

	// ��������
	public static void cashier(User user) throws ParseException {
		System.out.println("��������Ʒ�����루6λ�����ַ�����");
		// �жϸ�ʽ�Ƿ���ȷ
		Scanner scan = new Scanner(System.in);
		String regex = "\\d{6}";
		String barCode;
		while (true) {
			barCode = scan.nextLine();
			if (!barCode.matches(regex)) {// ��ʽ����ȷ
				System.out.println("�����������ʽ����ȷ������������");
			} else// ��ʽ��ȷ ��˶����ݿ����Ƿ���ڸ�������
			{
				if (ProductDAO.get(barCode) == null) {// ������
					System.out.println("���������Ʒ�����벻���ڣ���ȷ�Ϻ���������");
				} else {// ����
					System.out.println("������Ʒ����:");
					int count = scan.nextInt();
					String productName = ProductDAO.get(barCode).getProductName();// �õ���Ӧ������
					double price = ProductDAO.get(barCode).getPrice();// �õ���Ӧ�ĵ���
					String operator = user.getUserName();// �õ���Ӧ��¼�û���
					Timestamp time = new Timestamp(System.currentTimeMillis());// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// ����ʱ��
					SimpleDateFormat lshdf = new SimpleDateFormat("yyyyMMdd"); // ��ˮ��ǰ8λ
					String saleTime = df.format(time); // ����ʱ��
					Date date = df.parse(saleTime);
					Saledetail s = SaledetailDAO.getMaxLsh();
					if (s == null) {// ��ˮ�ż�¼Ϊ��
						String lsh_fomr8 = lshdf.format(time);// ������ˮ��ǰ8λ
						String lsh = lsh_fomr8 + "0000";
						Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
						SaledetailDAO.insert(sd);
						System.out.println("�ɹ�����һ����������");
						return;
					} else {// ��Ϊ��
						String MaxLsh = s.getLsh();// �����ˮ��
						String ymd = MaxLsh.substring(0, 8);// �����ˮ��ǰ8λ
						String last_4 = MaxLsh.substring(9, 12);// �����ˮ�ź�4λ
						int intlast_4 = Integer.parseInt(last_4);
						String newlast_4 = String.format("%04d", intlast_4 + 1);
						String lsh_fomr8 = lshdf.format(time);// ������ˮ��ǰ8λ
						String lsh;
						if (lsh_fomr8.equals(ymd)) {// ������ͬ
							lsh = lsh_fomr8 + newlast_4;
						} else// ���ڲ���ͬ
						{
							lsh = lsh_fomr8 + "0000";
						}
						Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
						SaledetailDAO.insert(sd);
						System.out.println("�ɹ�����һ����������");
						System.out.println();
						return;
					}
				}
			}
		}
	}

	
	//��������ͳ��
	public static void statistics() {
		System.out.println("�������������ڣ�yyyy-mm-dd����");
		Scanner scan = new Scanner(System.in);
		String date;
		String reg_yyyy_MM_dd = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
				+ "-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|"
				+ "(02-(0[1-9]|[1][0-9]|2[0-8]))))"
				+ "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
		while (true) {
			date = scan.nextLine();
			if (!date.matches(reg_yyyy_MM_dd)) {
				System.out.println("����������ڸ�ʽ����ȷ�����������룺");
			} else {
//				System.out.println("��ĸ�ʽ������ȷ");
				String saleTime = date + "%";
//				System.out.println(saleTime);
				ArrayList<Saledetail> list = SaledetailDAO.query(saleTime);
//				System.out.println(list.toString());

				String year = date.substring(0, 4);
				String month = date.substring(5, 7);
				String day = date.substring(8, 10);
				System.out.println(year + "��" + month + "��" + day + "����������:");
				System.out.println("��ˮ��\t\t��Ʒ����\t����\t����\t���\tʱ��\t\t\t����Ա");
				System.out.println("=====\t\t====\t====\t====\t===\t====\t\t\t=====");
				int saleNum = 0;
				int totalNum = 0;
				double totalMoney = 0;
				for (int i = 0; i < list.size(); i++) {
					Saledetail sd = list.get(i);
					System.out.println(sd.getLsh()+"\t"+sd.getProductName()+"\t"+sd.getPrice()+"\t"
							+ sd.getCount()+"\t"+sd.getCount()*sd.getPrice() + "\t" + 
							sd.getSaleTime() + "\t"+ sd.getOperator());
					saleNum++;// ��������
					totalNum = totalNum + sd.getCount();// ��Ʒ����
					totalMoney = totalMoney + sd.getCount() * sd.getPrice();// �����ܽ��
				}
				System.out.println("����������" + saleNum + "��Ʒ�ܼ���" + totalNum + " �����ܽ�" + totalMoney);
				System.out.println("����:" + year + "��" + month + "��" + day + "��");
				System.out.println();
				System.out.println("�밴���������������");
				String anyKey = null;
				anyKey = scan.nextLine();
				if (anyKey != null) {
					return;
				}
			}
		}
	}

	// ��Ʒά��
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
				System.out.println("������Ч��ֻ������1-4");
			}
			choice = Menu2();
		}
	}
	
	//�����ۼ�¼����txml�ļ�
	private static void writeToXml() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
		Timestamp time = new Timestamp(System.currentTimeMillis());// ��ȡϵͳ��ǰʱ��
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // ȡ�����ڸ�ʽ
		String writeTime = df.format(time); //��������
		String fileName ="data/saleDetail"+writeTime+".xml";
		// TODO Auto-generated method stub
		//��list�����е���д��xml�ļ���
		Document doc = DocumentHelper.createDocument();
		//���Ӹ��ڵ�
		Element saledetail = doc.addElement("Saledetail");
		//������Ԫ��
		for(int i=0;i<listSaledetail.size();i++) {
			Saledetail sd=listSaledetail.get(i);
			Element lsh= saledetail.addElement("��ˮ��");
			Element barCode=saledetail.addElement("barCode");
			Element productName=saledetail.addElement("productName");
			Element price=saledetail.addElement("price");
			Element count=saledetail.addElement("count");
			Element operator=saledetail.addElement("operator");
			Element saleTime=saledetail.addElement("saleTime");
			//Ϊ�ӽڵ��������
			lsh.addAttribute("lsh",sd.getLsh());
			//ΪԪ���������
			barCode.setText(sd.getBarCode());
			productName.setText(sd.getProductName());
			price.setText(String.valueOf(sd.getPrice()));
			count.setText(String.valueOf(sd.getCount()));
			operator.setText(sd.getOperator());
			saleTime.setText(String.valueOf(sd.getSaleTime()));
			//ʵ���������ʽ����
			OutputFormat format = OutputFormat.createPrettyPrint();
			//�����������
			format.setEncoding("UTF-8");
			//����Ҫд���File����
			File file = new File(fileName);
			//����XMLWriter���󣬹��캯���еĲ���Ϊ��Ҫ������ļ����͸�ʽ
			XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
			//��ʼд�룬writer�����а������洴����Docu����
			writer.write(doc);
		}
		System.out.println("�ɹ�����"+listSaledetail.size()+"���������ݵ�xml�ļ�");
		System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	//�����ۼ�¼����txt�ļ�
	public static void writeToTxt() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
			Timestamp time = new Timestamp(System.currentTimeMillis());// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // ȡ�����ڸ�ʽ
			String writeTime = df.format(time); //��������
			String fileName ="data/saleDetail"+writeTime+".txt";
			// ��ˮ��
			File file = new File(fileName);
			// ��ˮ��
			FileWriter fw = new FileWriter(file);
			// ˮ��ͷ
			PrintWriter pw = new PrintWriter(fw);
			// ������
			pw.println("��ˮ��\t������\t��Ʒ����\t�۸�\t����\t����Ա\t����ʱ��");

			for (Saledetail sd : listSaledetail) {
				String info = sd.toString();
				pw.println(info);
			}
			fw.close();
			System.out.println("�ɹ�����" + listSaledetail.size() + "���������ݵ��ı��ļ���");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	//�����ۼ�¼����excell���
	public static void writeToXls() {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
			Timestamp time = new Timestamp(System.currentTimeMillis());// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // ȡ�����ڸ�ʽ
			String writeTime = df.format(time); //��������
			String fileName ="data/saleDetail"+writeTime+".xls";
			//д��xls��
			//��������������
			WritableWorkbook book = Workbook.createWorkbook(new File(fileName));
			//�������������
			WritableSheet sheet=book.createSheet("��ˮ��Ϣ", 0);
			String title[]={"��ˮ��","������","��Ʒ����","�۸�","����","����Ա","����ʱ��"};
			for(int i=0;i<title.length;i++){
	            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
				CellView cellView = new CellView();
	            cellView.setAutosize(true); //�����Զ���С  
	            //sheet.setColumnView(i, cellView); //���������Զ������п�  
	            WritableCellFormat format = new WritableCellFormat(font);
	            format.setAlignment(Alignment.CENTRE); //���ж���
	            format.setBackground(Colour.YELLOW); //����ɫ
	            format.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);//�߿�
				Label label = new Label(i,0,title[i]);
				sheet.addCell(label);//����ˮ�ţ������룬��Ʒ���ƣ��۸�����������Ա������ʱ��������μ��뵥Ԫ��
			}
			//д���ݣ��������
			int count1 =1;
			for(Saledetail sd:listSaledetail){
				Label label = new Label(0, count1, sd.getLsh());
				sheet.addCell(label);//��listSaledetail�е�count�е�lsh���루0��1��λ�õĵ�Ԫ��
				label = new Label(1, count1, sd.getBarCode());
				sheet.addCell(label);//��listSaledetail�е�count�е���������루1��1��λ�õĵ�Ԫ��
				label =new Label(2, count1, sd.getProductName());
				sheet.addCell(label);//��listSaledetail�е�count�е���Ʒ���Ƽ��루2��1��λ�õĵ�Ԫ��
				label = new Label(3, count1,String.valueOf(sd.getPrice()));
				sheet.addCell(label);//��listSaledetail�е�count�еĵ��ۼ��루3��1��λ�õĵ�Ԫ��
				label = new Label(4, count1,String.valueOf(sd.getCount()));
				sheet.addCell(label);//��listSaledetail�е�count�е��������루4��1��λ�õĵ�Ԫ��
				label = new Label(5, count1,String.valueOf(sd.getOperator()));
				sheet.addCell(label);//��listSaledetail�е�count�е�����Ա���루4��1��λ�õĵ�Ԫ��
				label = new Label(6, count1,String.valueOf(sd.getSaleTime()));
				sheet.addCell(label);//��listSaledetail�е�count�е�����ʱ����루4��1��λ�õĵ�Ԫ��
				count1++;//������һ��
			}
			book.write();//д����
			book.close();
			System.out.println("�ɹ�����"+listSaledetail.size()+"���������ݵ�excel�ļ���");
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//�Ӳ˵�
	private static int Menu2() {
		// TODO Auto-generated method stub
		System.out.println("===�Ĵ���������Ϣ����====");
		System.out.println("1.������ excel �ļ�");
		System.out.println("2.�������ı��ļ�");
		System.out.println("3.������xml�ļ�");
		System.out.println("4.�������˵�");
		System.out.println("��ѡ��1-4����");
		Scanner scan = new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}

}
