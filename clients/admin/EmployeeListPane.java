package clients.admin;

import java.awt.Color;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import admin.Employee;
import middle.admin.EmployeeManager;

public class EmployeeListPane extends JScrollPane {
	
	private static final int LIST_ENTRY_HEIGHT = 40;
	
	private final JList<ListEntry> list = new JList<>();
	private final EmployeeManager manager;
	private List<Employee> cache;
	
	public EmployeeListPane(EmployeeManager manager) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, 
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.manager = manager;
		setViewportView(list);
		list.setLayoutOrientation(JList.VERTICAL);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		loadEmployees(width, height);
	}
	
	/**
	 * Loads the employees and adds them to the list
	 * @param height
	 */
	private void loadEmployees(int width, int height) {
		List<Employee> employees;
		try {
			// Set the cache at the same time
			cache = employees = manager.getAllEmployees();
		} catch(SQLException e) {
			return;
		}
		
		list.removeAll(); // clear the current entries from the list
		
		for(int i = 0; i < 10; i++) {
			list.add(new ListEntry(width, employees.get(0)));
		}
		
		for(Employee em : employees) {
			// Add the component
			ListEntry entry = new ListEntry(width, em);
			list.add(entry);
		}
		list.revalidate();
		list.repaint();
		revalidate();
		repaint();
	}
	
	private class ListEntry extends JPanel {
		JLabel idLabel;
		public ListEntry(int width, Employee employee) {
			setBounds(0, 0, width, 40);
			setBorder(BorderFactory.createLineBorder(Color.black));
			idLabel = new JLabel(String.valueOf(employee.getId()));
			add(idLabel);
			
		}
	}
}
