package clients.cashier;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import catalogue.Basket;
import middle.OrderProcessing;
import middle.StockReadWriter;

public class CashierTillView extends JPanel {
	
	private static final String CHECK  = "Check";
	private static final String BUY    = "Buy";
	private static final String BOUGHT = "Bought";
	
	private CashierController cont;
	private final StockReadWriter stock;
	private final OrderProcessing order;
	
	// Swing components
	private final JLabel      theAction  = new JLabel();
	private final JTextField  theInput   = new JTextField();
	private final JTextArea   theOutput  = new JTextArea();
	private final JScrollPane theSP      = new JScrollPane();
	private final JButton     theBtCheck = new JButton( CHECK );
	private final JButton     theBtBuy   = new JButton( BUY );
	private final JButton     theBtBought= new JButton( BOUGHT );
	
	public CashierTillView(StockReadWriter stock, OrderProcessing order) {
		this.stock = stock;
		this.order = order;
		
		// Initialise swing components
		Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is

	    theBtCheck.setBounds( 16, 25+60*0, 80, 40 );    // Check Button
	    theBtCheck.addActionListener(                   // Call back code
	      e -> cont.doCheck( theInput.getText() ) );
	    add( theBtCheck );                           //  Add to canvas

	    theBtBuy.setBounds( 16, 25+60*1, 80, 40 );      // Buy button 
	    theBtBuy.addActionListener(                     // Call back code
	      e -> cont.doBuy() );
	    add( theBtBuy );                             //  Add to canvas

	    theBtBought.setBounds( 16, 25+60*3, 80, 40 );   // Clear Button
	    theBtBought.addActionListener(                  // Call back code
	      e -> cont.doBought() );
	    add( theBtBought );                          //  Add to canvas

	    theAction.setBounds( 110, 25 , 270, 20 );       // Message area
	    theAction.setText( "" );                        // Blank
	    add( theAction );                            //  Add to canvas

	    theInput.setBounds( 110, 50, 270, 40 );         // Input Area
	    theInput.setText("");                           // Blank
	    add( theInput );                             //  Add to canvas

	    theSP.setBounds( 110, 100, 270, 160 );          // Scrolling pane
	    theOutput.setText( "" );                        //  Blank
	    theOutput.setFont( f );                         //  Uses font  
	    add( theSP );                                //  Add to canvas
	    theSP.getViewport().add( theOutput );           //  In TextArea
	    setVisible( true );                  // Make visible
	    theInput.requestFocus();                        // Focus is here
	}
	
	public void setController(CashierController cont) {
		this.cont = cont;
	}
	
	private void update(Basket basket, String message) {
		theAction.setText(message);
		if(basket == null) {
			theOutput.setText("Customers order");
		} else {
			theOutput.setText(basket.getDetails());
		}
		theInput.requestFocus();
	}

}
