/**
 * 
 */
package edu.wlu.graffiti.controller;

import javax.servlet.http.HttpServletRequest;

/**
 * A place for methods that the controllers can use that don't make sense in a
 * particular class.
 * 
 * @author sprenkle
 *
 */
public class ControllerUtils {

	public static String getFullRequest(HttpServletRequest req) {
		StringBuilder reqUrl = new StringBuilder();
		reqUrl.append(req.getRequestURI());
		String queryString = req.getQueryString();
		if (queryString != null) {
			reqUrl.append("?");
			reqUrl.append(queryString);
		}
		return reqUrl.toString();
	}
}
