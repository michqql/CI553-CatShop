package clients.cashier;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import admin.Employee;
import catalogue.Basket;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;
import middle.admin.EmployeeManager;
import util.PromptTextField;


/**
 * View of the model
 * @author  M A Smith (c) June 2014  
 */
public class CashierView implements Observer
{
  private static final int H = 300;       // Height of window pixels
  private static final int W = 400;       // Width  of window pixels
  
  private static final String LOGIN = "Login";
  private static final String PASS_PROMPT = "Enter PassCode...";
  private static final String CHECK  = "Check";
  private static final String BUY    = "Buy";
  private static final String BOUGHT = "Bought";

  // Login components
  private final JLabel loginLabel = new JLabel(LOGIN);
  private final JComboBox<EmployeeWrapper> employeeSelectComboBox = new JComboBox<>();
  private final PromptTextField passInput = new PromptTextField(PASS_PROMPT);
  private final JButton loginButton = new JButton(LOGIN);
  private Color defaultForegroundColor = Color.gray;
  
  // Till components
  private final Container contentPane;
  private final JLabel      theAction  = new JLabel();
  private final JTextField  theInput   = new JTextField();
  private final JTextArea   theOutput  = new JTextArea();
  private final JScrollPane theSP      = new JScrollPane();
  private final JButton     theBtCheck = new JButton( CHECK );
  private final JButton     theBtBuy   = new JButton( BUY );
  private final JButton     theBtBought= new JButton( BOUGHT );

  private StockReadWriter theStock     = null;
  private OrderProcessing theOrder     = null;
  private EmployeeManager employeeManager;
  private CashierController cont       = null;
  
  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-coordinate of position of window on screen 
   * @param y     y-coordinate of position of window on screen  
   */
          
  public CashierView(  RootPaneContainer rpc,  MiddleFactory mf, int x, int y  )
  {
    try                                           // 
    {      
      theStock = mf.makeStockReadWriter();        // Database access
      theOrder = mf.makeOrderProcessing();        // Process order
      employeeManager = mf.makeEmployeeManager();
    } catch ( Exception e )
    {
      System.out.println("Exception: " + e.getMessage() );
    }
    this.contentPane = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    contentPane.setLayout(null);                             // No layout manager
    rootWindow.setSize( W, H );                     // Size of Window
    rootWindow.setLocation( x, y );
    
    rootWindow.setVisible(true);

    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
    
    // Initialise login panel first
    loginLabel.setBounds(10, 10, 100, 40);
    contentPane.add(loginLabel);
    
    DefaultComboBoxModel<EmployeeWrapper> comboBoxModel = new DefaultComboBoxModel<>();
    try {
    	List<Employee> employees = employeeManager.getAllEmployees();
    	for(Employee em : employees) {
    		comboBoxModel.addElement(new EmployeeWrapper(em));
    	}
    } catch(Exception e) {
    	e.printStackTrace();
    }
    employeeSelectComboBox.setModel(comboBoxModel);
    employeeSelectComboBox.setBounds(W/2 - 200/2, 25, 200, 40);
    employeeSelectComboBox.updateUI();
    contentPane.add(employeeSelectComboBox);
    employeeSelectComboBox.repaint();
    defaultForegroundColor = employeeSelectComboBox.getForeground();
    
    passInput.setBounds(W/2 - 200/2, 75, 200, 40);
    contentPane.add(passInput);
    passInput.repaint();
    
    loginButton.setBounds(W/2 - 200/2, 125, 200, 40);
    loginButton.addActionListener(e -> loginClicked());
    contentPane.add(loginButton);
    loginButton.repaint();
    
    // Initialise the other components
    theBtCheck.setBounds( 16, 25+60*0, 80, 40 );    // Check Button
    theBtCheck.addActionListener(                   // Call back code
      e -> cont.doCheck( theInput.getText() ) );
                               //  Add to canvas

    theBtBuy.setBounds( 16, 25+60*1, 80, 40 );      // Buy button 
    theBtBuy.addActionListener(                     // Call back code
      e -> cont.doBuy() );
                                 //  Add to canvas

    theBtBought.setBounds( 16, 25+60*3, 80, 40 );   // Clear Button
    theBtBought.addActionListener(                  // Call back code
      e -> cont.doBought() );
                              //  Add to canvas

    theAction.setBounds( 110, 25 , 270, 20 );       // Message area
    theAction.setText( "" );                        // Blank
                                //  Add to canvas

    theInput.setBounds( 110, 50, 270, 40 );         // Input Area
    theInput.setText("");                           // Blank
                                 //  Add to canvas

    theSP.setBounds( 110, 100, 270, 160 );          // Scrolling pane
    theOutput.setText( "" );                        //  Blank
    theOutput.setFont( f );                         //  Uses font  
                                    //  Add to canvas
    theSP.getViewport().add( theOutput );           //  In TextArea
    rootWindow.setVisible( true );                  // Make visible
  }

  /**
   * The controller object, used so that an interaction can be passed to the controller
   * @param c   The controller
   */

  public void setController( CashierController c )
  {
    cont = c;
    setupListeners();
  }
  
  private void setupListeners() {
	  cont.setLoginSuccessListener((success, em) -> {
		  if(success) {
			  System.out.println("Successful login");
			  successfulLogin();
		  } else {
			  passInput.setForeground(Color.RED);
		  }
	  });
  }
  
  /**
   * Method to handle the login button being clicked
   */
  private void loginClicked() {
	  // Check if user is already logged in
	  if(cont.isLoggedIn()) 
		  return;
	  
	  employeeSelectComboBox.setForeground(defaultForegroundColor);
	  passInput.setForeground(defaultForegroundColor);
	  
	  // Get employee name string
	  EmployeeWrapper wrapper = (EmployeeWrapper) employeeSelectComboBox.getSelectedItem();
	  if(wrapper == null) {
		  employeeSelectComboBox.setForeground(Color.red);
		  return;
	  }
	  
	  // Get the employee object from database for updated password
	  Employee fromDatabase;
	  try {
		  fromDatabase = employeeManager.getEmployee(wrapper.em.getId());
	  } catch(Exception e) {
		  e.printStackTrace();
		  return;
	  }
	  
	  // Get the inputed passCode
	  String input = passInput.getText();
	  
	  cont.doLogin(fromDatabase, input);
  }
  
  private void successfulLogin() {
	  contentPane.removeAll();
	  contentPane.repaint();
	  contentPane.add(theBtCheck);
	  contentPane.add(theBtBuy);
	  contentPane.add(theBtBought);
	  contentPane.add(theAction);
	  contentPane.add(theInput);
	  contentPane.add(theSP);
	  contentPane.repaint();
	  
	  theInput.requestFocus();
	  
  }

  /**
   * Update the view
   * @param modelC   The observed model
   * @param arg      Specific args 
   */
  @Override
  public void update( Observable modelC, Object arg )
  {
    CashierModel model  = (CashierModel) modelC;
    String      message = (String) arg;
    theAction.setText( message );
    Basket basket = model.getBasket();
    if ( basket == null )
      theOutput.setText( "Customers order" );
    else
      theOutput.setText( basket.getDetails() );
    
    theInput.requestFocus();               // Focus is here
  }
  
  private static class EmployeeWrapper {
	  final Employee em;
	  
	  EmployeeWrapper(Employee em) {
		  this.em = em;
	  }
	  
	  @Override
	public String toString() {
		  return em.getName();
	  }
  }

}
