package edu.wlu.graffiti.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.Insula;

/**
 * Class to extract insula information
 * 
 * @author whitej
 * 
 */
public class InsulaDao extends JdbcTemplate {
	private static final String SELECT_STATEMENT = "SELECT * "
			+ " FROM insula ORDER BY id";

	private static final String SELECT_BY_CITY_STATEMENT = "SELECT * "
			+ " FROM insula WHERE UPPER(modern_city) = UPPER(?) ORDER BY id";
	
	private static final String SELECT_BY_CITY_AND_INSULA_STATEMENT = "SELECT * "
			+ " FROM insula WHERE UPPER(modern_city) = UPPER(?) and short_name = ? ORDER BY id";
	
	private static final String SELECT_BY_INSULA_ID_STATEMENT = "SELECT * "
			+ " FROM insula WHERE id = ? ORDER BY id";

	private static final class InsulaRowMapper implements RowMapper<Insula> {
		public Insula mapRow(final ResultSet resultSet, final int rowNum)
				throws SQLException {
			final Insula insula = new Insula();
			insula.setId(resultSet.getInt("id"));
			insula.setModernCity(resultSet.getString("modern_city"));
			insula.setShortName(resultSet.getString("short_name"));
			insula.setFullName(resultSet.getString("full_name"));
			return insula;
		}
	}

	// TODO: Set up to cache this
	public List<Insula> getInsula() {
		List<Insula> results = query(SELECT_STATEMENT,
				new InsulaRowMapper());
		return results;
	}

	// TODO: Set up to cache this
	public List<Insula> getInsulaByCity(String city) {
		List<Insula> results = query(SELECT_BY_CITY_STATEMENT,
				new InsulaRowMapper(), city);
		return results;
	}
	
	// TODO: Set up to cache this
	public List<Insula> getInsulaByCityAndInsula(String city, String insulaName) {
		List<Insula> results = query(SELECT_BY_CITY_AND_INSULA_STATEMENT,
				new InsulaRowMapper(), city, insulaName);
		return results;
	}
	
	// TODO: Set up to cache this
	public Insula getInsulaById(int insula_id) {
		Insula results = queryForObject(SELECT_BY_INSULA_ID_STATEMENT,
				new InsulaRowMapper(), insula_id);
		return results;
	}
}