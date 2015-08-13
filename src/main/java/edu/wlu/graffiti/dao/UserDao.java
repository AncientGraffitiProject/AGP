package edu.wlu.graffiti.dao;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

public class UserDao extends JdbcTemplate {

	public void insertUser(ArrayList<String> user) {
		String sql = "INSERT INTO users " + "(username,password,enabled)"
				+ " VALUES (?,?,'True')";

		update(sql, user.toArray());
	}
}
