package clients.customer;

/**
 * The Customer Controller
 * @author M A Smith (c) June 2014
 */

public class CustomerController
{
  private CustomerModel model = null;
  private CustomerView  view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CustomerController( CustomerModel model, CustomerView view )
  {
    this.view  = view;
    this.model = model;
  }
  
  public void processCheck(String productNumber) {
	  model.processCheck(productNumber);
  }

  /**
   * Check interaction from view
   * @param pn The product number to be checked
   */
  public void doCheck( String pn )
  {
    model.doCheck(pn);
  }

  /**
   * Clear interaction from view
   */
  public void doClear()
  {
    model.doClear();
  }
  
  public void addToBasket() {
	  model.addToBasket();
  }
  
  public void removeFromBasket() {
	  model.removeFromBasket();
  }
  
  
  public void buyOnline() {
	  model.buyOnline();
  }
}

