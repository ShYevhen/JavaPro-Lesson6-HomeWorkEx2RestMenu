package net.ukr.shyevhen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@NamedQueries({ @NamedQuery(name = "Menu.all", query = "SELECT d FROM Dish d"),
		@NamedQuery(name = "Menu.discount", query = "SELECT d FROM Dish d WHERE d.discount > 0"),
		@NamedQuery(name = "Menu.price", query = "SELECT d FROM Dish d WHERE d.price >= :min and d.price <= :max") })
@Table(name = "Restaurant_menu")
public class Dish {
	@Id
	@GeneratedValue
	private long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private BigDecimal price;
	@Column(nullable = false)
	private int weight;
	private int discount;

	@OneToMany(mappedBy = "dish", fetch = FetchType.LAZY)
	private List<Order> orders = new ArrayList<>();

	public Dish(String name, BigDecimal price, int weight, Integer discount) {
		super();
		this.name = name;
		this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.weight = weight;
		this.discount = discount;
	}

	public Dish() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Integer getDiscount() {
		return discount;
	}

	public void setDiscount(Integer discount) {
		this.discount = discount;
	}

	public long getId() {
		return id;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public void addOrder(Order order) {
		if (!orders.contains(order)) {
			orders.add(order);
		}
	}

	@Override
	public String toString() {
		return "id=" + id + "\t| name=" + name + "\t| price=" + price + "\t| weight=" + weight + " g\t| discount="
				+ discount;
	}

}
