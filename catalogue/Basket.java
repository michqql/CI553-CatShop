package catalogue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Formatter;
import java.util.Locale;
import java.util.Optional;

/**
 * A collection of products from the CatShop.
 *  used to record the products that are to be/
 *   wished to be purchased.
 * @author  Mike Smith University of Brighton
 * @version 2.2
 *
 */
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1;
  private int    theOrderNum = 0;          // Order number
  
  /**
   * Constructor for a basket which is
   *  used to represent a customer order/ wish list
   */
  public Basket()
  {
    theOrderNum  = 0;
  }
  
  /**
   * Constructor for a basket which takes another baskets
   * items and adds them to this.
   * @param collection the other basket to add
   */
  public Basket(Collection<Product> collection) {
	  super(collection);
  }
  
  /**
   * Set the customers unique order number
   * Valid order Numbers 1 .. N
   * @param anOrderNum A unique order number
   */
  public void setOrderNum( int anOrderNum )
  {
    theOrderNum = anOrderNum;
  }

  /**
   * Returns the customers unique order number
   * @return the customers order number
   */
  public int getOrderNum()
  {
    return theOrderNum;
  }
  
  /**
   * Add a product to the Basket.
   * Product is appended to the end of the existing products
   * in the basket.
   * Unless the list already contains the product, in which case the product quantity
   * is increased instead.
   * @param pr A product to be added to the basket
   * @return true if successfully adds the product
   */
  // Will be in the Java doc for Basket
  @Override
  public boolean add(Product pr) {
	  // Check if product is already in the list
	  Product found = null;
	  for(Product inList : this) { // can use this keyword as this class extends ArrayList
		  if(inList.isSameItem(pr)) {
			  found = inList;
			  break;
		  }
	  }
	  
	  // If found is null, product was not in the list, so add it
	  if(found == null) {
		  return super.add(pr.copy()); // Need to take a copy as quantity may get modified later
	  } else {
		  // found is non-null and in the list, increase quantity
		  found.setQuantity(found.getQuantity() + pr.getQuantity());
		  return true;
	  } 
  }
  
  @Override
	public boolean addAll(Collection<? extends Product> c) {
		boolean changedFlag = false;
		for(Product p : c) {
			changedFlag = add(p) ? true : changedFlag;
		}
		return changedFlag;
	}
  
  @Override
	public boolean remove(Object o) {
	  // Check if objects are same type, if not can early return
	  if(!(o instanceof Product)) 
		  return false;
	  
	  // The object passed will not be the same as the one stored in list,
	  // as a copy is made before adding the product to the list,
	  // therefore we need to check by item type
	  Product found = null;
	  for(Product inList : this) {
		  if(inList.isSameItem((Product) o)) {
			  found = inList;
			  break;
		  }
	  }
	  
	  // Found a matching product in the list, remove the object from the list
	  if(found != null)
		  return super.remove(found);
	  
	  return false;
	}
  
  /**
   * Will decrease the quantity of the product in the list by one
   * If the quantity of the product becomes zero, the product will be removed from the list
   * @param pr - the product to decrease
   * @param amount - the amount to decrease by (should keep as positive)
   * @return true if the product was removed or the quantity decreased
   */
  public boolean decreaseProductQuantity(Product pr, int amount) {
	  // Find the product in the list that matches this product
	  Product found = null;
	  for(Product inList : this) {
		  if(inList.isSameItem(pr)) {
			  found = inList;
			  break;
		  }
	  }
	  
	  if(found == null) {
		  // Product is not in list, so cannot be decreased
		  return false;
	  }
	  
	  // In case the user supplied a negative amount, take the absolute value
	  found.setQuantity(found.getQuantity() - Math.abs(amount));
	  
	  // Remove the product is the quantity has reached zero,
	  // as you cannot have a product with zero quantity
	  if(found.getQuantity() <= 0) {
		  // We know that the found object is in the list,
		  // so can call super.remove to directly remove this object
		  return super.remove(found);
	  }
	  
	  return true;
  }

  /**
   * Returns a description of the products in the basket suitable for printing.
   * @return a string description of the basket products
   */
  public String getDetails()
  {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder(256);
    Formatter     fr = new Formatter(sb, uk);
    String csign = (Currency.getInstance( uk )).getSymbol();
    double total = 0.00;
    if ( theOrderNum != 0 )
      fr.format( "Order number: %03d\n", theOrderNum );
      
    if ( this.size() > 0 )
    {
      for ( Product pr: this )
      {
        int number = pr.getQuantity();
        fr.format("%-7s",       pr.getProductNum() );
        fr.format("%-14.14s ",  pr.getDescription() );
        fr.format("(%3d) ",     number );
        fr.format("%s%7.2f",    csign, pr.getPrice() * number );
        fr.format("\n");
        total += pr.getPrice() * number;
      }
      fr.format("----------------------------\n");
      fr.format("Total                       ");
      fr.format("%s%7.2f\n",    csign, total );
      fr.close();
    }
    return sb.toString();
  }
  
  public BasketDetails getBasketDetails() {
	  return new BasketDetails(this);
  }
}
