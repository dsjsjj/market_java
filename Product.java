package vo;

//商品模型
public class Product {
	private String barCode;
	private String productName;
	private double price;
	private String supply;
	
	public Product() {
		super();
	}

	public Product(String barCode, String productName, double price, String supply) {
		super();
		this.barCode = barCode;
		this.productName = productName;
		this.price = price;
		this.supply = supply;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSupply() {
		return supply;
	}

	public void setSupply(String supply) {
		this.supply = supply;
	}

	@Override
	public String toString() {
		return barCode+"\t"+productName+"\t"+price+"\t"+supply;
	}
	
}
