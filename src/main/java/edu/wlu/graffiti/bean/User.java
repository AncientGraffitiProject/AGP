/*
 * user.java class is used for login and authorization related functions which are found in LoginController.java,
 * userController.java, userValidator.java, approve.jsp, login.jsp, registrationForm.jsp and spring-security.xml.
 * It receives its information from users table in the database.
 */
package edu.wlu.graffiti.bean;
 
public class User{
 
	String userName;
	String name;

	String password;
	String confirmPassword;
 
	public String getUserName() {
		return userName;
	}
	
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void name(String name) {
		this.name = name;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}