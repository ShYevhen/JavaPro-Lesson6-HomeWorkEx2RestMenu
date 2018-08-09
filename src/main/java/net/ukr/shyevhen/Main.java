package net.ukr.shyevhen;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.persistence.*;

public class Main {
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPAMenu");
		EntityManager em = emf.createEntityManager();
		try {
			while (true) {
				System.out.println("Main list");
				System.out.println("1: Menu list\r\n2: Order list\r\nexit: Enter");
				System.out.print("->");
				String choosed = sc.nextLine();
				if (choosed.equals("1")) {
					menuList(em);
				} else if (choosed.equals("2")) {
					orderList(em);
				} else {
					break;
				}
			}
		} finally {
			em.close();
			emf.close();
		}

	}

	private static void menuList(EntityManager em) {
		while (true) {
			try {
				System.out.println("Menu list");
				System.out
						.println("1: Get all menu\r\n2: Get menu from 'min' to 'max' price\r\n3: Get only discount dish"
								+ "\r\n4: Add dish\r\n5: Change dish\r\n6: Delete dish\r\nexit: Enter");
				System.out.print("->");
				String choosed = sc.nextLine();
				if (choosed.equals("1")) {
					allOrDiscountMenu(em, choosed);
				} else if (choosed.equals("2")) {
					fromMinToMax(em);
				} else if (choosed.equals("3")) {
					allOrDiscountMenu(em, choosed);
				} else if (choosed.equals("4")) {
					addDish(em);
				} else if (choosed.equals("5")) {
					changeDish(em);
				} else if (choosed.equals("6")) {
					deleteDish(em);
				} else {
					break;
				}
			} catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
				e.printStackTrace();
			}
		}
	}

	private static void fromMinToMax(EntityManager em) {
		Query query = em.createNamedQuery("Menu.price", Dish.class);
		System.out.print("Input min price\r\n->");
		query.setParameter("min", sc.nextBigDecimal());
		sc.nextLine();
		System.out.print("Input max price\r\n->");
		query.setParameter("max", sc.nextBigDecimal());
		sc.nextLine();
		List<Dish> menu = (List<Dish>) query.getResultList();
		for (Dish restMenu : menu) {
			System.out.println(restMenu);
		}
	}

	private static void allOrDiscountMenu(EntityManager em, String choosed) {
		Query query = null;
		if (choosed.equals("1")) {
			query = em.createNamedQuery("Menu.all", Dish.class);
		} else if (choosed.equals("3")) {
			query = em.createNamedQuery("Menu.discount", Dish.class);
		}
		List<Dish> menu = (List<Dish>) query.getResultList();
		for (Dish restMenu : menu) {
			System.out.println(restMenu);
		}
	}

	private static void addDish(EntityManager em)
			throws IllegalArgumentException, IllegalStateException, NoSuchElementException {
		System.out.print("Input name of the dish\r\n->");
		String name = sc.nextLine();
		System.out.print("Input price of the dish\r\n->");
		BigDecimal price = sc.nextBigDecimal();
		sc.nextLine();
		System.out.print("Input weight(g) of dish\r\n->");
		int weight = Integer.parseInt(sc.nextLine());
		System.out.print("Input discount or press Enter\r\n->");
		String discuontS = sc.nextLine();
		int discount = 0;
		if (!"".equals(discuontS)) {
			discount = Integer.parseInt(discuontS);
		}
		if (weight <= 0 || price.doubleValue() <= 0) {
			throw new IllegalArgumentException();
		}
		em.getTransaction().begin();
		try {
			Dish restMenu = new Dish(name, price, weight, discount);
			em.persist(restMenu);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
		}
	}

	private static void changeDish(EntityManager em) throws NumberFormatException {
		System.out.print("Input dish id\r\n->");
		long id = Long.parseLong(sc.nextLine());
		Dish rm = em.find(Dish.class, id);
		if (rm == null) {
			System.out.println("Dish not found");
			return;
		} else if (rm.getOrders().size() > 0) {
			System.out.println("You can't change ordered dish");
			return;
		}
		em.getTransaction().begin();
		try {
			changeDishPartTwo(rm);
			em.getTransaction().commit();
		} catch (NumberFormatException | IllegalStateException | NoSuchElementException e) {
			em.getTransaction().rollback();
		}
	}

	private static void changeDishPartTwo(Dish rm)
			throws NumberFormatException, IllegalStateException, NoSuchElementException {
		while (true) {
			System.out.println(
					"1: Change name\r\n2: Change price\r\n3: Change weight\r\n4: Change discount\r\nexit: Enter");
			String choosed = sc.nextLine();
			if (choosed.equals("1")) {
				System.out.print("Input new name\r\n->");
				rm.setName(sc.nextLine());
			} else if (choosed.equals("2")) {
				System.out.print("Input new price\r\n->");
				rm.setPrice(sc.nextBigDecimal());
				sc.nextLine();
			} else if (choosed.equals("3")) {
				System.out.print("Input new weight(g)\r\n->");
				rm.setWeight(Integer.parseInt(sc.nextLine()));
			} else if (choosed.equals("4")) {
				System.out.print("Input new discount\r\n->");
				String discuontS = sc.nextLine();
				int discount = 0;
				if (!"".equals(discuontS)) {
					discount = Integer.parseInt(discuontS);
				}
				rm.setDiscount(discount);
			} else {
				break;
			}
		}
	}

	private static void deleteDish(EntityManager em) {
		System.out.print("Input dish id\r\n->");
		long id = Long.parseLong(sc.nextLine());
		em.getTransaction().begin();
		Dish dish = em.getReference(Dish.class, id);
		if (dish == null) {
			System.out.println("Dish not found");
			return;
		}
		try {
			em.remove(dish);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
		}
	}

	private static void orderList(EntityManager em) {
		while (true) {
			try {
				System.out.println("Orders list");
				System.out.println("1: Get all orders\r\n2: Get all orders from the table\r\n3: Add order"
						+ "\r\n4: Change order\r\n5: Delete order\r\nexit: Enter");
				System.out.print("->");
				String choosed = sc.nextLine();
				if (choosed.equals("1")) {
					allOrders(em);
				} else if (choosed.equals("2")) {
					tableOrders(em);
				} else if (choosed.equals("3")) {
					addOrder(em);
				} else if (choosed.equals("4")) {
					changeOrder(em);
				} else if (choosed.equals("5")) {
					deleteOrder(em);
				} else {
					break;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}

	private static void allOrders(EntityManager em) {
		Query query = em.createNamedQuery("Order.all", Order.class);
		List<Order> orders = (List<Order>) query.getResultList();
		for (Order order : orders) {
			System.out.println(order);
		}
	}

	private static void tableOrders(EntityManager em) throws NumberFormatException {
		Query query = em.createNamedQuery("Order.table", Order.class);
		System.out.print("Input table number\r\n->");
		query.setParameter("tableNum", Integer.parseInt(sc.nextLine()));
		List<Order> orders = (List<Order>) query.getResultList();
		for (Order order : orders) {
			System.out.println(order);
		}
		BigDecimal totalPrice = new BigDecimal("0");
		int totalWeight = 0;
		for (Order order : orders) {
			totalPrice = totalPrice.add(order.getOrderPrise());
			totalWeight += order.getDish().getWeight() * order.getCount();
		}
		System.out.println("Total weight = " + totalWeight + "\t| Total prise = " + totalPrice);
	}

	private static void addOrder(EntityManager em) throws NumberFormatException {
		System.out.print("Input table number\r\n->");
		int tableNum = Integer.parseInt(sc.nextLine());
		System.out.print("Input dish id\r\n->");
		long dId = Long.parseLong(sc.nextLine());
		Dish dish = em.find(Dish.class, dId);
		System.out.print("Input number of servings\r\n->");
		int count = Integer.parseInt(sc.nextLine());
		if (count <= 0) {
			return;
		}
		if (checkWeight(em, tableNum, dish, count)) {
			em.getTransaction().begin();
			try {
				Order order = new Order(tableNum, dish, count, em);
				em.persist(order);
				em.getTransaction().commit();
			} catch (Exception e) {
				em.getTransaction().rollback();
				e.printStackTrace();
			}
		}
	}

	private static boolean checkWeight(EntityManager em, int tableNum, Dish dish, int count) {
		Query query = em.createNamedQuery("Order.table", Order.class);
		query.setParameter("tableNum", tableNum);
		List<Order> orders = (List<Order>) query.getResultList();
		int totalWeight = dish.getWeight() * count;
		for (Order o : orders) {
			totalWeight += o.getDish().getWeight() * o.getCount();
		}
		if (totalWeight > 1000) {
			System.out.println("You can't add this order. Max total weight 1 kg");
			return false;
		}
		return true;
	}

	private static void changeOrder(EntityManager em) throws NumberFormatException {
		System.out.print("Input order id\r\n->");
		long id = Long.parseLong(sc.nextLine());
		System.out.print("Input new number of servings\r\n->");
		int count = Integer.parseInt(sc.nextLine());
		if (count <= 0) {
			return;
		}
		Order order = em.find(Order.class, id);
		if (order == null) {
			System.out.println("Order not found");
			return;
		}
		em.getTransaction().begin();
		order.setCount(count);
		em.getTransaction().commit();
	}

	private static void deleteOrder(EntityManager em) {
		System.out.print("Input order id\r\n->");
		long id = Long.parseLong(sc.nextLine());
		em.getTransaction().begin();
		Order order = em.find(Order.class, id);
		if (order == null) {
			System.out.println("Order not found!");
			return;
		}
		try {
			em.remove(order);
			order.getDish().getOrders().remove(order);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
		}
	}
}
