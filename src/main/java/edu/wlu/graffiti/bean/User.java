/*
 * user.java class is used for login and authorization related functions which are found in LoginController.java,
 * userController.java, userValidator.java, approve.jsp, login.jsp, registrationForm.jsp and spring-security.xml.
 * It receives its information from users table in the database.
 */
package edu.wlu.graffiti.bean;

public class User {

	private static final String ADMIN_ROLE_NAME = "admin";
	String userName;
	String name;

	String password;
	
	// used for user validation
	String confirmPassword;
	
	boolean is_admin = false;
	String role;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
		is_admin = role.equals(ADMIN_ROLE_NAME);
	}

	public String getUserName() {
		return userName;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public boolean isAdmin() {
		return is_admin;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}


}