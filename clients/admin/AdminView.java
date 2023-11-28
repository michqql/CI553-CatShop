package clients.admin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import middle.MiddleFactory;
import middle.admin.EmployeeManager;
import util.PromptTextField;

public class AdminView {
	
	private static final int H = 300;       // Height of window pixels
	private static final int W = 400;       // Width  of window pixels
	
	private EmployeeManager employeeManager;
	
	// Widgets
	private final JButton createButton, removeButton;
	private final PromptTextField searchField;
	private final EmployeeListPane employeeList;
	
	public AdminView(RootPaneContainer root, MiddleFactory mf, int x, int y) {
		try {
			this.employeeManager = mf.makeEmployeeManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Container cp         = root.getContentPane();    // Content Pane
	    Container rootWindow = (Container) root;         // Root Window
	    cp.setLayout(null);                             // No layout manager
	    rootWindow.setSize( W, H );                     // Size of Window
	    rootWindow.setLocation( x, y );
	    
	    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
	    
	    createButton = new JButton("Create");
	    createButton.setBounds(10, 10, 100, 50);
	    rootWindow.add(createButton);
	    
	    removeButton = new JButton("Remove");
	    removeButton.setBounds(120, 10, 100, 50);
	    rootWindow.add(removeButton);
	    
	    searchField = new PromptTextField("Search employees...");
	    searchField.setBounds(230, 10, 150, 50);
	    rootWindow.add(searchField);
	    
	    employeeList = new EmployeeListPane(employeeManager);
	    employeeList.setBounds(10, 70, 300, 200);
	    rootWindow.add(employeeList);
	    
	    rootWindow.setVisible( true );                  // Make visible
	}

}
