package view;

import java.text.ParseException;
import java.util.Scanner;

import service.ProductHelper;
import service.SaledetailHelper;
import service.UserHelper;
import vo.User;

//主程序
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
			default :System.out.println("输入无效，只能输入 1-7");
			}
			choice=showMenu(name);
		}
	}
	
	//主菜单
	private static int showMenu(String name) {
		// TODO Auto-generated method stub
		System.out.println("===纺大超市收银系统===");
		System.out.println("1.收银");
		System.out.println("2.查询统计");
		System.out.println("3.商品维护");
		System.out.println("4.修改密码");
		System.out.println("5.数据导出");
		System.out.println("6.用户注册");
		System.out.println("7.退出");
		System.out.println("当前收银员："+name);
		System.out.println("请选择（1-7）：");
		Scanner scan=new Scanner(System.in);
		int choice = scan.nextInt();
		return choice;
	}
}
