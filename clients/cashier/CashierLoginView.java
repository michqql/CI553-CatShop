package clients.cashier;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.RootPaneContainer;

import middle.MiddleFactory;
import middle.admin.EmployeeManager;

public class CashierLoginView {
	private static final int WIDTH = 400, HEIGHT = 300;
	
	private EmployeeManager employeeManager;
	
	private final EmployeeGridPanel employeeGridPanel;
	
	public CashierLoginView(RootPaneContainer rpc, MiddleFactory mf, int x, int y) {
		try {
			employeeManager = mf.makeEmployeeManager();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Container cp = rpc.getContentPane();
		Container rootWindow = (Container) rpc;
		cp.setLayout(new GridLayout(1, 2));
		rootWindow.setSize(WIDTH, HEIGHT);
		rootWindow.setLocation(x, y);
		
		this.employeeGridPanel = new EmployeeGridPanel(employeeManager);
		cp.add(employeeGridPanel);
	}

}
