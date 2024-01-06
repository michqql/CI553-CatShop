package clients;

import catalogue.Basket;
import catalogue.Product;

public class TestMain {
	
	public static void main(String[] args) {
		Basket basket = new Basket();
		
		Product pr = new Product("1", "Toaster", 10.0, 81);
		
		long before = System.nanoTime();
		basket.add(pr);
		System.out.println("Time taken: " + (System.nanoTime() - before) + " ns");
	}
	
	// Test 1 (for loop):   35100   ns
	// Test 2 (stream):   1646600 ns

}
