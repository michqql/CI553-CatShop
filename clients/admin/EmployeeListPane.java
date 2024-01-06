package clients.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

import admin.Employee;
import debug.DEBUG;
import middle.admin.EmployeeManager;

public class EmployeeListPane extends JPanel {
	
	private static final int LIST_ENTRY_HEIGHT = 40;
	private static final Color SELECTED_CELL_COLOUR = new Color(0xFBF719);
	
	private final EmployeeManager manager;
	private final List<Employee> employeeCache = new ArrayList<>();
	private JList<ListEntry> list;
	private JScrollBar scrollBar;
	
	public EmployeeListPane(EmployeeManager manager) {
		super(new BorderLayout());
		this.manager = manager;
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		loadEmployees(width, height);
	}
	
	/**
	 * Informs this component that the data set has changed and to update the list
	 * @param employee - the employee created
	 */
	public void informEmployeeCreated(Employee employee) {
		employeeCache.add(employee);
		reloadEmployees(employeeCache);
	}
	
	/**
	 * Informs this component that the data set has changed and to update the list
	 * @param employee - the employee removed
	 */
	public void informEmployeeRemoved(Employee employee) {
		employeeCache.remove(employee);
		reloadEmployees(employeeCache);
	}
	
	/**
	 * Informs this component that the user has entered a search query
	 * should only show data that matches this search query
	 * @param searchQuery - the query
	 * @return {@code true} if results found, false otherwise
	 */
	public boolean informSearchQuery(String searchQuery) {
		// If the search query is empty, show all results
		if(searchQuery == null || searchQuery.isEmpty()) {
			reloadEmployees(employeeCache);
			return true;
		}
		
		// Get possible search techniques
		final boolean idSearch = searchQuery.startsWith("#"); // search by employee id
		final AtomicLong id = new AtomicLong(-1);
		if(idSearch) {
			try {
				long val = Long.valueOf(searchQuery.substring(1));
				id.set(val);
			} catch(NumberFormatException e) {
				// Invalid number specified, show all results
				reloadEmployees(employeeCache);
				return false;
			}
		}
		final boolean passUnset = searchQuery.startsWith("!"); // search by employees with no passcode
		// Use stream API to filter employees based on the query
		List<Employee> results = employeeCache.stream().filter(em -> {
			// Only show employees that don't have their password set
			if(passUnset) return !em.isPassCodeSet();
			
			// Only show the employee with matching id
			if(idSearch) return em.getId() == id.get();
			
			// Check if name is null first to avoid NPE
			if(em.getName() == null) return false;
			
			// Return true if employee name starts with search query
			return em.getName().toLowerCase().startsWith(searchQuery.toLowerCase());
		}).toList();
		
		// If there are no results, show all employees
		if(results.isEmpty()) {
			reloadEmployees(employeeCache);
			return false;
		}
		
		// Show the results
		reloadEmployees(results);
		return true;
	}
	
	/**
	 * Should only be called after this component has been setup
	 * otherwise this will throw a NullPointerException
	 */
	private void reloadEmployees(List<Employee> reloaded) {
		DefaultListModel<ListEntry> model = new DefaultListModel<>();
		// Add the data to the data model
		for(Employee em : reloaded) {
			// Add the component
			ListEntry entry = new ListEntry(getWidth(), em);
			model.addElement(entry);
		}
		
		list.setModel(model); // update the data model
		
		// Check the selected index, if out of bounds: reset
		if(list.getSelectedIndex() >= model.getSize()) 
			list.setSelectedIndex(-1);
		
		scrollBar.revalidate(); // force the component to update the list
	}
	
	/**
	 * Loads the employees and adds them to the list
	 * @param height
	 */
	private void loadEmployees(int width, int height) {
		// Clean up if this is a reload
		try {
			this.employeeCache.clear();
			this.employeeCache.addAll(manager.getAllEmployees());
		} catch(SQLException e) {
			DEBUG.error("Error loading employees in EmployeeListPane", e);
			return;
		}
		
		DefaultListModel<ListEntry> model = new DefaultListModel<>();
		
		for(Employee em : employeeCache) {
			// Add the component
			ListEntry entry = new ListEntry(width, em);
			model.addElement(entry);
		}
		
		this.list = new JList<>(model);
		list.setFixedCellHeight(LIST_ENTRY_HEIGHT);
		list.setFixedCellWidth(width - 20);
		list.setSelectedIndex(-1);
		list.setCellRenderer(new ListEntryCellRenderer());
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleJListMouseClickEvent(list, e);
			}
		});
		
		final JScrollPane scroll = new JScrollPane();
		// The bounds and viewport view need to be set so that the scroll pane
		// does not overflow outside of the bounds of this pane
		scroll.setBounds(getBounds());
		scroll.setViewportView(list);
		scroll.setPreferredSize(new Dimension(width, height));
		scroll.setBounds(getX(), getY(), width, height);
		
		this.scrollBar = scroll.getVerticalScrollBar();
		
		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * Method to handle the JList mouse click event
	 * @param list - the JList
	 * @param e - the MouseEvent
	 */
	private void handleJListMouseClickEvent(JList list, MouseEvent e) {
		// Gets the index of the NEAREST item, not necessarily the item clicked
		int index = list.locationToIndex(e.getPoint());
		if(index < 0) return; // There are no entries
		
		ListEntry entry = (ListEntry) list.getModel().getElementAt(index);
		if(!checkBounds(index, entry, e.getPoint())) {
			// No item was actually clicked, so clear the selection and return
			list.clearSelection();
			return;
		}
		
		// Needs the list entries position (x = 0, y = index * height)
		// Again, the height is stored as a negative and in the Y variable
		boolean buttonClicked = entry.handleComponentClick(
				new Point(0, index * Math.abs(entry.getY())), e);
		if(buttonClicked) list.clearSelection();
	}
	
	/**
	 * For some reason, the bounds are stored like: x = -width, y = -height, w = 0, h = 0
	 * @param index - The index of this list entry in the list
	 * 				  As there is no relative position for this component,
	 *                we have to use the index to calculate it
	 * @param e - the list entry component
	 * @param p - the clicked point
	 * @return {@code True} if the clicked point is inside of the components bounds
	 */
	private boolean checkBounds(int index, ListEntry e, Point p) {
		int width = Math.abs(e.getX()); // The negative width is stored in X
		int height = Math.abs(e.getY()); // The negative height is stored in Y
		
		// X is always 0
		int y = index * height;
		
		// Check if point is inside this area
		Rectangle rect = new Rectangle(0, y, width, height);
		return rect.contains(p);
	}
	
	public Employee getSelectedEmployee() {
		int index = list.getSelectedIndex();
		if(index < 0) return null; // There are no entries
		
		ListEntry entry = (ListEntry) list.getModel().getElementAt(index);
		return entry.employee;
	}
	
	public void moveScrollToEmployee(Employee employee) {
		long id = employee.getId();
		
		int index = -1;
		for(int i = 0; i < list.getModel().getSize(); i++) {
			ListEntry entry = list.getModel().getElementAt(i);
			if(entry.employee.getId() == id) {
				index = i;
				break;
			}
		}
		
		if(index == -1) {
			// Index shouldn't be -1, but if it is just return from this method
			return;
		}
		
		scrollBar.setValue(LIST_ENTRY_HEIGHT * index);
	}
	
	/**
	 * ListEntry component that displays a single employee entry in the list
	 */
	private class ListEntry extends JPanel {
		
		final Employee employee;
		final JButton passCodeButton;
		
		public ListEntry(int width, Employee employee) {
			this.employee = employee;
			
			setBounds(0, 0, width, LIST_ENTRY_HEIGHT);
			setBorder(BorderFactory.createLineBorder(Color.black));
			setLayout(new GridLayout(1, 0, 5, 0));
			
			JLabel idLabel = new JLabel(" #" + employee.getId());
			JLabel nameLabel = new JLabel(employee.getName() == null ? "No name" : employee.getName());
			JLabel passCodeSetLabel = new JLabel(
					"PassCode " + (employee.isPassCodeSet() ? "" : "not ") + "set");
			
			if(employee.isPassCodeSet()) {
				// Button to change pass-code
				passCodeButton = new JButton("Change");
				passCodeButton.addActionListener(e -> setPassCode("Change"));
			} else {
				// Button to set pass-code
				passCodeButton = new JButton("Set");
				passCodeButton.addActionListener(e -> setPassCode("Set"));
			}
			
			add(idLabel);
			add(nameLabel);
			add(passCodeSetLabel);
			add(passCodeButton);
		}
		
		/**
		 * Shows an input dialog pop-up window prompting the user for a pass code
		 * Once entered, the pass code is set
		 * @param type
		 */
		private void setPassCode(String type) {
			String message = type + " PassCode for employee " + 
					(employee.getName() == null ? "#" + employee.getId() : employee.getName());
			String result = JOptionPane.showInputDialog(EmployeeListPane.this, message);
			if(result == null) return; // User cancelled the dialog
			
			boolean success = employee.setPassCode(result);
			if(success) {
				JOptionPane.showMessageDialog(EmployeeListPane.this, type + " PassCode successful");
			} else {
				// The pass code was not valid, ask the user if they would like to try again
				int yesNoResult = JOptionPane.showConfirmDialog(
						EmployeeListPane.this, 
						new String[] {
								"Invalid PassCode format, enter again?",
								"PassCode must be 4-16 characters long",
								"and only consist of digits (0-9)"
						}, 
						"Unsuccessful", JOptionPane.YES_NO_OPTION);
				if(yesNoResult == JOptionPane.YES_OPTION) {
					setPassCode(type); // Recursively try again, otherwise method will exit
				}
			}
		}
		
		/**
		 * Handles the click event, if the clicked position is inside of the pass code
		 * button bounds, the button will be clicked
		 * @param parentPosition - the position of the ListEntry component
		 * @param e - the click event
		 * @return {@code true} if the button was clicked
		 */
		private boolean handleComponentClick(Point parentPosition, MouseEvent e) {
			// Calculate the position and bounds of the button
			int buttonX = (int) (passCodeButton.getX() + parentPosition.getX());
			int buttonY = (int) (passCodeButton.getY() + parentPosition.getY());
			int width = passCodeButton.getWidth();
			int height = passCodeButton.getHeight();
			
			// Check if the clicked position is inside of the bounds
			Rectangle rect = new Rectangle(buttonX, buttonY, width, height);
			if(rect.contains(e.getPoint())) {
				// Clicked location was inside of the bounds, click the button!
				passCodeButton.doClick();
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Private inner class that tells the JList how to render our ListEntry component
	 */
	private class ListEntryCellRenderer implements ListCellRenderer<ListEntry> {

		@Override
		public Component getListCellRendererComponent(JList<? extends ListEntry> list, ListEntry value, 
				int index, boolean isSelected, boolean cellHasFocus) {
			JPanel panel = (JPanel) value;
			panel.setBackground(isSelected ? SELECTED_CELL_COLOUR : list.getBackground());
			return panel;
		}
		
	}
}
