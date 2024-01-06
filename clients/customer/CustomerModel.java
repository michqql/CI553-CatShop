package clients.customer;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import events.BiListener;
import events.Listener;
import middle.MiddleFactory;
import middle.OrderException;
import middle.OrderProcessing;
import middle.StockException;
import middle.StockReadWriter;
import middle.StockReader;
import util.Pair;

import javax.swing.*;

import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;

/**
 * Implements the Model of the customer client
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class CustomerModel extends Observable
{
  private Product     product = null;          // Current product
  private Basket      basket  = null;          // Bought items
  private int selectedBasketIndex = 0; // The index of the item currently selected in the basket
  // The basket and the selected index
  private BiListener<Basket, Integer> basketChangeListener;

  private String      pn = "";                    // Product being processed
  private Listener<Boolean> validProductCodeListener;

  private final StockReadWriter stockReadWriter;
  private final OrderProcessing orderProcessing;
  private ImageIcon       thePic       = null;
  
  private final Set<String> queryCache = new HashSet<>();

  /*
   * Construct the model of the Customer
   * @param mf The factory to create the connection objects
   */
  public CustomerModel(MiddleFactory mf) {
    try                                          // 
    {  
      stockReadWriter = mf.makeStockReadWriter();           // Database access
      orderProcessing = mf.makeOrderProcessing();
    } catch ( Exception e )
    {
      DEBUG.error("CustomerModel.constructor\n" +
                  "Database not created?\n%s\n", e.getMessage() );
      throw new RuntimeException(e);
    }
    basket = makeBasket();                    // Initial Basket
  }
  
  /**
   * return the Basket of products
   * @return the basket of products
   */
  public Basket getBasket()
  {
    return basket;
  }
  
  public Product getProduct() {
	return product;
}
  
  public void setBasketChangeListener(BiListener<Basket, Integer> basketChangeListener) {
	  this.basketChangeListener = basketChangeListener;
  }
  
  public void setValidProductCodeListener(Listener<Boolean> validProductCode) {
	this.validProductCodeListener = validProductCode;
}
  
  /**
   * Processes the users entered text to see if it is a valid product
   * @param productNumber
   */
  public void processCheck(String productNumber) {
	  // Check if contained in cache
	  if(queryCache.contains(productNumber)) {
		  // If so, avoid checking database for product
		  doCheck(productNumber);
		  return;
	  }
	  
	  try {
		  if(!stockReadWriter.exists(productNumber)) {
			  validProductCodeListener.onChange(false);
			  return;
		  }
		  // The product exists in the database, so cache this product number
		  queryCache.add(productNumber);
		 
		  doCheck(productNumber);
	  } catch(StockException e) {
		  DEBUG.error("CustomerClient.processCheck()\n%s", e.getMessage());
	  }
  }

  /**
   * Check if the product is in Stock
   * @param productNum The product number
   */
  public void doCheck(String productNum )
  {
    String theAction = "";
    pn  = productNum.trim();                    // Product no.
    int    amount  = 1;                         //  & quantity
    try
    {
      if ( stockReadWriter.exists( pn ) )              // Stock Exists?
      {                                         // T
        validProductCodeListener.onChange(true);
    	  product = stockReadWriter.getDetails( pn ); //  Product
        if (product.getQuantity() >= amount )       //  In stock?
        { 
          theAction =                           //   Display 
            String.format( "%s : %7.2f (%2d) ", //
            		product.getDescription(),              //    description
            		product.getPrice(),                    //    price
            		product.getQuantity() );               //    quantity
          product.setQuantity( amount );             //   Require 1
          thePic = stockReadWriter.getImage( pn );     //    product
        } else {                                //  F
          theAction =                           //   Inform
        		  product.getDescription() +               //    product not
            " not in stock" ;                   //    in stock
        }
      } else {                                  // F
        theAction =                             //  Inform Unknown
          "Unknown product number " + pn;       //  product number
      }
    } catch( StockException e )
    {
      DEBUG.error("CustomerClient.doCheck()\n%s",
      e.getMessage() );
    }
    setChanged(); 
    notifyObservers(theAction);
  }

  /**
   * Clear the products from the basket
   */
  public void doClear()
  {
    String theAction = "";
    basket.clear();                        // Clear s. list
    theAction = "Enter Product Number";       // Set display
    product = null; // Clear last checked item
    thePic = null;                            // No picture
    setChanged(); 
    notifyObservers(theAction);
  }
  
  /**
   * Adds the product from the query to the basket.
   */
  public void addToBasket() {
	  String action;
	  if(product == null) {
		  // Product must not be null, inform the user they have not queried an item
		  action = "Nothing to add to basket!";
	  } else {
		  if(stockReadWriter.getProductStockLevel(product.getProductNum()) > 0) {
			  basket.add(product);
			  action = product.getDescription() + " added to basket!";
		  } else {
			  action = "Product is out of stock!";
		  }
	  }
	  
	  basketChangeListener.onChange(basket, selectedBasketIndex);
	  
	  setChanged();
	  notifyObservers(action);
  }
  
  public void removeFromBasket() {
	  String action;
	  if(basket.isEmpty()) {
		  action = "Nothing to remove from the basket!";
		  setChanged();
		  notifyObservers(action);
		  return;
	  }
	  
	  if(selectedBasketIndex < 0 || selectedBasketIndex >= basket.size()) {
		  action = "Select a product to remove using arrow keys.";
		  setChanged();
		  notifyObservers(action);
		  return;
	  }
	  
	  basket.decreaseProductQuantity(basket.get(selectedBasketIndex), 1);
	  // Bound the index to the size of the basket
	  if(selectedBasketIndex >= basket.size()) 
		  selectedBasketIndex = basket.size();
	  
	  basketChangeListener.onChange(basket, selectedBasketIndex);
	  
  }
  
  /**
   * This method allows the customer to buy their basket.
   */
  public void buyOnline() {
	  String result = "";
	  if(basket.isEmpty()) {
		  result = "Basket is empty";
		  setChanged();
		  notifyObservers(result);
		  return;
	  }
	  
	  try {
		  // Try buy the stock first
		  List<Product> unbought = stockReadWriter.buyAllStock(basket);
		  basket.removeAll(unbought); // Remove the items that could not be bought from the basket
		  
		  // Create the order with the remaining items in the basket
		  // (the ones that were bought, if any)
		  if(!basket.isEmpty()) {
			  int orderNumber = orderProcessing.uniqueNumber();
			  basket.setOrderNum(orderNumber);
			  orderProcessing.newOrder(basket);
			  result = "Order purchased, order no: #" + orderNumber;
		  }
		  
		  // Must make a new basket here as some code may rely on this basket object.
		  basket = makeBasket();
		  if(!unbought.isEmpty()) {
			  // Inform the user that X items could not be purchased
			  result += unbought.size() + " item" + 
					  (unbought.size() == 1 ? " was" : "s were") + " out of stock";
			  // Add these unbought items back to the basket
			  basket.addAll(unbought);
		  }
		  selectedBasketIndex = -1;
		  basketChangeListener.onChange(basket, selectedBasketIndex);
		  setChanged();
		  notifyObservers(result);
	  } catch(OrderException | StockException e) {
		  DEBUG.error("%s\n%s", "CashierModel.doCancel", e.getMessage());
		  setChanged(); 
		  notifyObservers(e.getMessage()); // Notify
		  return;
	  }
  }
  
  public void decreaseSelectedBasketIndex() {
	  this.selectedBasketIndex = Math.max(0, --selectedBasketIndex);
  }
  
  public void increaseSelectedBasketIndex() {
	  this.selectedBasketIndex = Math.min(basket.size() - 1, ++selectedBasketIndex);
  }
  
  /**
   * Return a picture of the product
   * @return An instance of an ImageIcon
   */ 
  public ImageIcon getPicture()
  {
    return thePic;
  }
  
  /**
   * ask for update of view called at start
   */
  private void askForUpdate()
  {
    setChanged(); notifyObservers("START only"); // Notify
  }

  /**
   * Make a new Basket
   * @return an instance of a new Basket
   */
  protected Basket makeBasket()
  {
    return new Basket();
  }
}

