package edu.wlu.graffiti.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.data.rowmapper.ThemeRowMapper;

/**
 * Class to extract themes from the DB
 * 
 * @author Hammad Ahmad
 * 
 */
public class ThemeDao extends JdbcTemplate {

	private static final String SELECT_STATEMENT = "SELECT * " + "FROM themes ORDER BY name";
	
	private static final String SELECT_BY_ID = "SELECT * " + "FROM themes WHERE theme_id = ?";
	
	private static final String SELECT_BY_NAME = "SELECT * " + "FROM themes WHERE name = ?";

	public static final String SELECT_BY_EDR_ID = "SELECT themes.theme_id, themes.name, themes.description "
			+ "FROM graffititothemes, themes WHERE graffito_id = ? "
			+ "AND themes.theme_id = graffititothemes.theme_id ORDER BY name;";

	private List<Theme> themes = null;

	//@Cacheable("themes")
	public List<Theme> getThemes() {
		themes = query(SELECT_STATEMENT, new ThemeRowMapper());
		return themes;
	}
	
	//@Cacheable("themes")
	public Theme getThemeById(int theme_id) {
		Theme theme = queryForObject(SELECT_BY_ID, new ThemeRowMapper(), theme_id);
		return theme;
	}
	
	public Theme getThemeByName(String theme_name) {
		Theme theme = queryForObject(SELECT_BY_NAME, new ThemeRowMapper(), theme_name);
		return theme;
	}
	
	//@Cacheable("themes")
	public List<Theme> getThemesByEDR(String edr) {
		themes = query(SELECT_BY_EDR_ID, new ThemeRowMapper(), edr);
		return themes;
	}
	
	public List<Integer> getAllThemeIds() {
		List<Integer> themeIds = new ArrayList<Integer>();
		for(Theme t : this.getThemes()) {
			themeIds.add(t.getId());
		}
		return themeIds;
	}

}
