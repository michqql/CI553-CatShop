package clients.cashier;

import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import admin.Employee;
import middle.admin.EmployeeManager;

public class EmployeeGridPanel extends JPanel {
	
	public EmployeeGridPanel(EmployeeManager manager) {
		JPanel grid = new JPanel(new GridLayout(0, 3));
		
		try {
			List<Employee> employees = manager.getAllEmployees();
			
			for(Employee em : employees) {
				grid.add(new GridCell(em));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		JScrollPane scrollPane = new JScrollPane(grid, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane);
	}
	
	private static class GridCell extends JPanel {
		public GridCell(Employee em) {
			super(new GridLayout(1, 1));
			
			JLabel label = new JLabel(em.getName());
			add(label);
		}
	}

}
