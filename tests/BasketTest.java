package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import catalogue.Basket;
import catalogue.Product;

class BasketTest {

	@Test
	void testAddProduct() {
		Basket basket = new Basket();
		
		Product p1 = new Product("0001", "TV", 249.0, 1);
		Product p2 = new Product("0002", "Toaster", 29.0, 2);
		
		// In this test the size should increase to 1 as an element was added
		basket.add(p1);
		assertEquals(1, basket.size(), "Basket size incorrect after add");
		
		// In this test the size should remain as 1 as the same product was added,
		// that should increase the quantity of the product in the list only
		// and the original product object should keep the same quantity
		basket.add(p1);
		assertEquals(1, basket.size(), "Basket size incorrect after add (quantity increase)");
		assertEquals(1, p1.getQuantity(), "Product quantity incorrect after basket add");
		assertEquals(2, basket.get(0).getQuantity(), "Product in basket quantity incorrect");
		
		// In this test, a different product is added and the size should increase to 2
		basket.add(p2);
		assertEquals(2, basket.size(), "Basket size incorrect after second add");
	}

	@Test
	void testAddAllCollectionOfQextendsProduct() {
		Basket basket = new Basket();
		
		Product p1 = new Product("0001", "TV", 249.0, 1);
		Product p2 = new Product("0002", "Toaster", 29.0, 1);
		
		List<Product> list = List.of(p1, p2, p1, p1, p1, p2);
		
		basket.addAll(list);
		assertEquals(2, basket.size(), "Basket size incorrect after addAll");
		assertEquals(4, basket.get(0).getQuantity(), "Incorrect product quantity after addAll");
		assertEquals(2, basket.get(1).getQuantity(), "Incorrect product quantity after addAll");
	}

	@Test
	void testRemoveObject() {
		Basket basket = new Basket();
		
		Product p1 = new Product("0001", "TV", 249.0, 1);
		
		basket.add(p1);
		basket.remove(p1);
		assertEquals(0, basket.size(), "Basket size incorrect after remove");
		
		basket.add(p1);
		basket.add(p1);
		basket.remove(p1);
		assertEquals(0, basket.size(), 
				"Basket size incorrect after removing product with multiple quantity");
	}

	@Test
	void testDecreaseProductQuantity() {
		Basket basket = new Basket();
		
		Product p1 = new Product("0001", "TV", 249.0, 1);
		Product p2 = new Product("0002", "Toaster", 29.0, 1);
		
		List<Product> list = List.of(p1, p2, p1, p1, p1, p2);
		basket.addAll(list);
		
		basket.decreaseProductQuantity(p1, 1);
		assertEquals(2, basket.size(), "Incorrect basket size after quantity decrease");
		assertEquals(3, basket.get(0).getQuantity(), "Incorrect product quantity after decrease");
		
		basket.decreaseProductQuantity(p2, 3);
		assertEquals(1, basket.size(), "Incorrect basket size after quantity decrease");
	}

	@Test
	void testGetProductQuantity() {
		Basket basket = new Basket();
		
		Product p1 = new Product("0001", "TV", 249.0, 1);
		Product p2 = new Product("0002", "Toaster", 29.0, 1);
		
		List<Product> list = List.of(p1, p2, p1, p1, p1, p2);
		basket.addAll(list);
		
		assertEquals(4, basket.getProductQuantity(p1), "Incorrect product quantity");
		assertEquals(2, basket.getProductQuantity(p2), "Incorrect product quantity");
	}

}
