package clients.admin;

import admin.Employee;
import events.BiListener;
import events.Listener;

public class AdminController {
	
	private AdminModel model;
	private AdminView view;
	
	public AdminController(AdminModel model, AdminView view) {
		this.model = model;
		this.view = view;
	}
	
	public void setEmployeeCreationListener(BiListener<Employee, String> listener) {
		model.setEmployeeCreationListener(listener);
	}
	
	public void setEmployeeRemovedListener(BiListener<Employee, Boolean> listener) {
		model.setEmployeeRemovedListener(listener);
	}
	
	public void createNewEmployee(String name, String passCode) {
		model.createNewEmployee(name, passCode);
	}
	
	public void removeEmployee(Employee employee) {
		model.removeEmployee(employee);
	}
	
	public void passcodeChanged(Employee employee) {
		model.passcodeChanged(employee);
	}

}
