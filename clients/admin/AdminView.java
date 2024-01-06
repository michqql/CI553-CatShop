package clients.admin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.util.concurrent.ExecutorService;
import java.util.logging.Handler;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import events.SimpleDocumentListener;
import middle.MiddleFactory;
import middle.admin.EmployeeManager;
import util.PromptTextField;

public class AdminView {
	
	private static final int H = 300;       // Height of window pixels
	private static final int W = 400;       // Width  of window pixels
	private static final Color VALID_COLOUR = new Color(0x17CC10);
	private static final Color INVALID_COLOUR = Color.red;
	
	private EmployeeManager employeeManager;
	
	private final Container rootWindow;
	// Widgets
	private final JButton createButton, removeButton;
	private final PromptTextField searchField;
	private final EmployeeListPane employeeList;
	
	private AdminController controller;
	
	public AdminView(RootPaneContainer root, MiddleFactory mf, int x, int y) {
		try {
			this.employeeManager = mf.makeEmployeeManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Container cp         = root.getContentPane();    // Content Pane
	    this.rootWindow = (Container) root;         // Root Window
	    cp.setLayout(null);                             // No layout manager
	    rootWindow.setSize( W, H );                     // Size of Window
	    rootWindow.setLocation( x, y );
	    
	    Font f = new Font("Monospaced",Font.PLAIN,12);  // Font f is
	    
	    createButton = new JButton("Create");
	    createButton.setBounds(10, 10, 100, 50);
	    createButton.addActionListener(e -> createEmployee());
	    rootWindow.add(createButton);
	    
	    removeButton = new JButton("Remove");
	    removeButton.setBounds(120, 10, 100, 50);
	    removeButton.addActionListener(e -> removeEmployee());
	    rootWindow.add(removeButton);
	    
	    searchField = new PromptTextField("Search employees...");
	    searchField.setBounds(230, 10, 150, 50);
	    searchField.getDocument().addDocumentListener(new SimpleDocumentListener() {
			@Override
			public void onChange() {
				employeeSearch(searchField.getText());
			}
		});
	    rootWindow.add(searchField);
	    
	    employeeList = new EmployeeListPane(employeeManager);
	    employeeList.setBounds(10, 70, 355, 180);
	    rootWindow.add(employeeList);
	    
	    rootWindow.setVisible( true );                  // Make visible
	}
	
	public void setAdminController(AdminController controller) {
		this.controller = controller;
		setupListeners();
	}
	
	private void setupListeners() {
		controller.setEmployeeCreationListener((employee, resultStr) -> {
			if(employee != null)
				employeeList.informEmployeeCreated(employee);
		});
		
		controller.setEmployeeRemovedListener((employee, success) -> {
			if(success && employee != null) {
				employeeList.informEmployeeRemoved(employee);
			}
		});
	}
	
	private void createEmployee() {
		// Display pop up window to ask for employee details
		String name = JOptionPane.showInputDialog(rootWindow, "Enter employee name");
		String passCode = JOptionPane.showInputDialog(rootWindow, "Set employee PassCode");
		
		controller.createNewEmployee(name, passCode);
	}
	
	private void removeEmployee() {
		controller.removeEmployee(employeeList.getSelectedEmployee());
	}
	
	private void employeeSearch(String query) {
		boolean results = employeeList.informSearchQuery(query);
		if(!query.isEmpty()) {
			searchField.setForeground(results ? VALID_COLOUR : INVALID_COLOUR);
		}
	}

}
