package clients.admin;

import java.sql.SQLException;

import admin.Employee;
import events.BiListener;
import events.Listener;
import middle.MiddleFactory;
import middle.admin.EmployeeManager;

public class AdminModel {

	private final EmployeeManager employeeManager;
	
	// The created employee object and the result as a string
	private BiListener<Employee, String> employeeCreationListener;
	// The employee object removed and success flag
	private BiListener<Employee, Boolean> employeeRemovedListener;
	
	public AdminModel(MiddleFactory mf) {
		try {
			employeeManager = mf.makeEmployeeManager();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Could not get employee manager");
		}
	}
	
	public void setEmployeeCreationListener(BiListener<Employee, String> listener) {
		this.employeeCreationListener = listener;
	}
	
	public void setEmployeeRemovedListener(BiListener<Employee, Boolean> listener) {
		this.employeeRemovedListener = listener;
	}
	
	public void createNewEmployee(String name, String passCode) {
		if(!EmployeeManager.isPassCodeValid(passCode)) {
			employeeCreationListener.onChange(null, "PassCode is invalid: " + passCode);
			return;
		}
		
		try {
			Employee employee = employeeManager.createNewEmployee(name, passCode);
			employeeCreationListener.onChange(employee, "Success");
		} catch (SQLException e) {
			e.printStackTrace();
			employeeCreationListener.onChange(null, "Error occured with database when creating employee");
		}
	}
	
	public void removeEmployee(Employee employee) {
		try {
			boolean deleted = employeeManager.deleteEmployee(employee.getId());
			employeeRemovedListener.onChange(employee, deleted);
		} catch(SQLException e) {
			e.printStackTrace();
			employeeRemovedListener.onChange(employee, false);
		}
	}
	
}
