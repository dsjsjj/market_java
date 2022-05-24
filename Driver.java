package view;

import java.text.ParseException;
import java.util.Scanner;

import service.ProductHelper;
import service.SaledetailHelper;
import service.UserHelper;
import vo.User;

//������
public class Driver {
	public static void main(String[] args) throws ParseException {
		User user=UserHelper.login();
		String name=user.getName();
		if(name!=null){
		}
		else {
			System.exit(0);
		}
		int choice=showMenu(name);
		while(true)
		{
			switch(choice)
			{
			case 1:SaledetailHelper.cashier(user);break;
			case 2:SaledetailHelper.statistics();break;
			case 3:ProductHelper.preserve(user);break;
			case 4:UserHelper.updatePassword(user);break;
			case 5:SaledetailHelper.preserve();break;
			case 6:UserHelper.sign();break;
			case 7:if (UserHelper.finish()) {
				System.exit(0);
			} else {
				break;
			}
			default :System.out.println("������Ч��ֻ������ 1-7");
			}
			choice=showMenu(name);
		}
	}
	
	//���˵�
	private static int showMenu(String name) {
		// TODO Auto-generated method stub
		System.out.println("===�Ĵ�������ϵͳ===");
		System.out.println("1.����");
		System.out.println("2.��ѯͳ��");
		System.out.println("3.��Ʒά��");
		System.out.println("4.�޸�����");
		System.out.println("5.���ݵ���");
		System.out.println("6.�û�ע��");
		System.out.println("7.�˳�");
		System.out.println("��ǰ����Ա��"+name);
		System.out.println("��ѡ��1-7����");
		Scanner scan=new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}
}
