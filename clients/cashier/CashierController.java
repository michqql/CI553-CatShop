package clients.cashier;

import admin.Employee;
import events.BiListener;

/**
 * The Cashier Controller
 * @author M A Smith (c) June 2014
 */

public class CashierController
{
  private CashierModel model = null;
  private CashierView  view  = null;

  /**
   * Constructor
   * @param model The model 
   * @param view  The view from which the interaction came
   */
  public CashierController( CashierModel model, CashierView view )
  {
    this.view  = view;
    this.model = model;
  }
  
  public void setLoginSuccessListener(BiListener<Boolean, Employee> loginSuccessListener) {
		model.setLoginSuccessListener(loginSuccessListener);
	}
  
  public boolean isLoggedIn() {
	  return model.isLoggedIn();
  }
  
  public void doLogin(Employee employee, String passCode) {
	  model.doLogin(employee, passCode);
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
   * Buy interaction from view
   */
  public void doBuy()
  {
    model.doBuy();
  }
  
   /**
   * Bought interaction from view
   */
  public void doBought()
  {
    model.doBought();
  }
}
