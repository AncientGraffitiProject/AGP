package edu.wlu.graffiti.data.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import edu.wlu.graffiti.bean.GreatestHitsInfo;

public final class GreatestHItsInfoRowMapper implements RowMapper<GreatestHitsInfo> {
	public GreatestHitsInfo mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
		final GreatestHitsInfo ghi = new GreatestHitsInfo();
		ghi.setCommentary(resultSet.getString("commentary"));
		ghi.setPreferredImage(resultSet.getString("preferred_image"));
		return ghi;
	}
}