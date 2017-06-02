package edu.wlu.graffiti.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.UserDao;

@Controller
public class LoginController {

	@Resource
	private UserDao userDao;

	@RequestMapping(value = "/LoginValidator", method = RequestMethod.POST)
	public String loginValidator(final HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession();

		String error_msg = "";
		boolean error = false;
		if (username == null || username.equals("")) {
			error_msg += "Username is required.<br/>";
			error = true;
		}
		if (password == null || password.equals("")) {
			error_msg += "Password is required.<br/>";
			error = true;
		}
		if (error) {
			request.setAttribute("error_msg", error_msg);
			return "/login";
		}

		User user = userDao.getUser(username, password);

		if (user != null) {
			session.setAttribute("authenticated", true);
			session.setAttribute("name", user.getName());
			session.setAttribute("role", user.getRole());
			session.setMaxInactiveInterval(15 * 60);
			return "/admin/admin_page";
		} else {
			error_msg = "Username and/or Password is not correct";
			request.setAttribute("error_msg", error_msg);
			return "/login";
		}

	}

	/*
	 * @RequestMapping(value = "/welcome", method = RequestMethod.GET) public
	 * String printWelcome(ModelMap model, Principal principal) { String name =
	 * principal.getName(); model.addAttribute("username", name);
	 * model.addAttribute("message", "Spring Security Custom Form example");
	 * return "hello"; }
	 */

	@RequestMapping(value = "/login")
	public String login(ModelMap model) {
		return "/login";
	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");
		return "/login";

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getSession().invalidate();
		return "/index";
	}

}