package clients.cashier;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import admin.Employee;
import middle.admin.EmployeeManager;

public class CashierLoginPanel extends JPanel {
	
	private EmployeeManager employeeManager;
	
	private final EmployeeGridPanel employeeGridPanel;
	private final JTextField passInput;
	private final JButton loginButton;
	
	public CashierLoginPanel(EmployeeManager employeeManager) {
		super(new GridLayout(1, 2));
		this.employeeManager = employeeManager;
		
		JPanel grid = new JPanel(new GridLayout(0, 1));
		grid.setSize(0, 300);
		fillEmployees(grid);
		JScrollPane scrollPane = new JScrollPane(grid, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
		
		this.employeeGridPanel = new EmployeeGridPanel(employeeManager);
		//add(employeeGridPanel);
		
		this.passInput = new JTextField();
		add(passInput);
		
		this.loginButton = new JButton("Login");
		add(loginButton);
		
		RightPanel rightPanel = new RightPanel();
		//add(rightPanel);
	}
	
	private void fillEmployees(JPanel panel) {
		try {
			List<Employee> employees = employeeManager.getAllEmployees();
			
			for(Employee em : employees) {
				panel.add(new JButton(em.getName()));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static class RightPanel extends JPanel {
		public RightPanel() {
			JTextField input = new JTextField();
			input.setBounds(10, 10, 100, 50);
			add(input);
			
			JButton button = new JButton("Login");
			button.setBounds(10, 60, 100, 50);
			add(button);
		}
	}
}
