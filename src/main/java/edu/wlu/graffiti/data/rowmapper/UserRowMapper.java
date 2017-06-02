package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.User;

public final class UserRowMapper implements RowMapper<User> {
	public User mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final User us = new User();
		us.setUserName(resultSet.getString("username"));
		us.setName(resultSet.getString("name"));
		us.setRole(resultSet.getString("role"));
		return us;
	}
}