package vo;

//用户模型
public class User {
	private String userName;
	private String password;
	private String name;
	private String role;
	
	public User() {	
	}

	public User(String userName, String password, String name, String role) {
		super();
		this.userName = userName;
		this.password = password;
		this.name = name;
		this.role = role;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return userName +"\t"+ password + "\t" + name + "\t" + role;
	}	
}
