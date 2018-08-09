package net.ukr.shyevhen;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@NamedQueries({ @NamedQuery(name = "Order.all", query = "SELECT o FROM Order o"),
		@NamedQuery(name = "Order.table", query = "SELECT o FROM Order o WHERE o.tableNum = :tableNum") })
@Table(name = "Orders")
public class Order {
	@Id
	@GeneratedValue
	private long id;
	@Column(name = "table_number", nullable = false)
	private int tableNum;
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn
	private Dish dish;
	@Column(nullable = false)
	private int count;
	@Column(nullable = false)
	private BigDecimal orderPrise;
	@Transient
	private EntityManager em;

	public Order(int tableNum, Dish dish, int count, EntityManager em) {
		super();
		this.tableNum = tableNum;
		this.dish = dish;
		this.count = count;
		this.em = em;
		setOrderPrise();
		addThisDich();
	}

	public Order() {
		super();
	}

	public int getTableNum() {
		return tableNum;
	}

	public void setTableNum(int tableNum) {
		this.tableNum = tableNum;
	}

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
		if (count > 0) {
			setOrderPrise();
		}
		addThisDich();
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
		if (dish != null)
			setOrderPrise();
	}

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getOrderPrise() {
		return orderPrise;
	}

	public void setOrderPrise() {
		int discount = dish.getDiscount();
		if (discount > 0) {
			this.orderPrise = (dish.getPrice().multiply(BigDecimal.valueOf(this.count * (1 - discount / 100.))))
					.setScale(2, BigDecimal.ROUND_HALF_UP);
		} else {
			this.orderPrise = (dish.getPrice().multiply(BigDecimal.valueOf(this.count))).setScale(2,
					BigDecimal.ROUND_HALF_UP);
		}
	}

	public void addThisDich() {
		dish.addOrder(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", tableNum=" + tableNum + ", dish[" + dish + "], count=" + count + ", orderPrise="
				+ orderPrise + "]";
	}

}
