package edu.wlu.graffiti.dao;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.data.rowmapper.UserRowMapper;

public class UserDao extends JdbcTemplate {

	private static final String INSERT_USER_STATEMENT = "INSERT INTO users " + "(username,password,enabled)"
			+ " VALUES (?,?,'True')";

	private static final String GET_USER_STATEMENT = "SELECT * FROM users WHERE username = ? AND password = md5(?)";

	/**
	 * Inserts the user into the database
	 * 
	 * @param user
	 *            - list is in order: username, password, enabled
	 */
	public void insertUser(ArrayList<String> user) {
		String sql = INSERT_USER_STATEMENT;

		update(sql, user.toArray());
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return the User object if the username and password match the database;
	 *         otherwise, returns null.
	 */
	public User getUser(String username, String password) {
		List<User> userList = query(GET_USER_STATEMENT, new UserRowMapper(), username, password);
		if (userList.isEmpty()) {
			return null;
		} else {
			return userList.get(0);
		}
	}

	// A function to update an SQL statement in the SQL table
	public void changePasswordSQL(String newPassword, String username) {
		String SQL = "UPDATE USERS SET PASSWORD=md5(?) WHERE username=?";
		update(SQL, newPassword, username);
		return;
	}

}
