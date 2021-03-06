package edu.wlu.graffiti.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.EditorDao;
import edu.wlu.graffiti.dao.UserDao;

/**
 * Handles editor-related functionality
 * 
 * @author sprenkle
 */
@Controller
public class EditorController {

	@Resource
	private EditorDao editorDao;
	@Resource
	private UserDao userDao;
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminPage(final HttpServletRequest request) {
		return "admin/admin_page";
	}

	@RequestMapping(value = "/admin/AddEditor", method = RequestMethod.GET)
	public String addEditorForm(final HttpServletRequest request) {
		return "admin/addEditor";
	}
	
	@RequestMapping(value = "/admin/AddEditor", method = RequestMethod.POST)
	public String addEditor(final HttpServletRequest request) {

		String username = request.getParameter("username");
		String name = request.getParameter("name");
		String password = request.getParameter("password2");
		String role = "editor";
		// Clean the input fields
		name = clean_fields(name);
		username = clean_fields(username);
		password = clean_fields(password);

		// Check if user name exists
		if (editorDao.existingUsername(username)) {
			request.setAttribute("msg", "Username already in use. Use a different username.");
		} else {

			if (editorDao.insertUser(username, password, name, role)) {
				request.setAttribute("msg", username + " added as an editor.");
			} else {
				request.setAttribute("msg", "Check your information and try again.");
			}
		}

		return "admin/addEditor";
	}
	
	
	
	@RequestMapping(value = "/admin/changePassword", method = RequestMethod.GET)
		public String ChangePassword(final HttpServletRequest request) {
			return "admin/changePassword";
	}
	@RequestMapping(value = "/admin/changePassword", method = RequestMethod.POST)
	public String changePassword(final HttpServletRequest request) {

		String password1=request.getParameter("password1");
		String password2 = request.getParameter("password2");
		// Clean the input fields 
		password1 = clean_fields(password1);
		password2 = clean_fields(password2);

		// Check if the two passwords match
	
			if (password1.equals(password2)) {
				//Change the user object's password:
				HttpSession session =request.getSession();
				String username=(String)session.getAttribute("username");
				String password=(String)session.getAttribute("password");
				User userObject=userDao.getUser(username,password);
				userObject.setPassword(password1);
				request.setAttribute("msg", "Password changed for "+username);
				//Alter the data in the database:
				userDao.changePasswordSQL(password1,username);
				
			} else {
				request.setAttribute("msg", "Passwords do not match!");
			}
		

		return "admin/changePassword";
	}
	
	
	
	
	
	
	

	@RequestMapping(value = "/admin/RemoveEditors", method = RequestMethod.GET)
	public String listEditors(final HttpServletRequest request) {
		List<User> editors = editorDao.getEditors();
		request.setAttribute("editors", editors);
		return "admin/removeEditors";
	}

	@RequestMapping(value = "/admin/RemoveEditors", method = RequestMethod.POST)
	public String removeEditors(final HttpServletRequest request) {
		int i = 0;
		String[] usernames = request.getParameterValues("removeEditors");
		if (usernames != null) { // Handles if user click Remove Editor button
									// without choosing an editor.
			for (String username : usernames) {
				if (editorDao.deleteUser(username)) {
					i++;
				}
			}
			if (i > 0) {
				request.setAttribute("msg", "Editors Removed");
			} else
				request.setAttribute("msg", "There was an error.  Try again.");

		}
		return "/admin/removeEditors";
	}

	private String clean_fields(String data) {
		/* Cleans the input fields to avoid JS injection. */
		return data.replace("<", "").replace(">", "");
	}

}
