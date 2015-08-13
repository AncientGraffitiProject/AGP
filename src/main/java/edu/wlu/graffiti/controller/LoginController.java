package edu.wlu.graffiti.controller;

import java.util.*;
import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.GraffitiDao;

@Controller
public class LoginController {

	@Resource
	private GraffitiDao graffitiDao;

	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String printWelcome(ModelMap model, Principal principal) {

		String name = principal.getName();
		model.addAttribute("username", name);
		model.addAttribute("message", "Spring Security Custom Form example");
		return "hello";

	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {

		return "login";

	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");
		return "login";

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {

		return "login";

	}

	@RequestMapping(value = "/approve", method = RequestMethod.GET)
	public String approve(final HttpServletRequest request) {
		final List<User> users = this.graffitiDao
				.getPendingUsers();

		request.setAttribute("users", users);
		return "approve";
	}
	
	@RequestMapping(value = "/approveComplete", method = RequestMethod.GET)
	public String approveComplete(final HttpServletRequest request) {
		final String[] users = request.getParameterValues("userName");
		this.graffitiDao.approveUsers((Object[])users);
		return "approveComplete";
	}

	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}

}