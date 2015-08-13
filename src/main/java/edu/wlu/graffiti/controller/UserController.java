package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import edu.wlu.graffiti.bean.User;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.UserDao;
 
@SuppressWarnings("deprecation")
public class UserController extends SimpleFormController{
	
	@Resource
	private GraffitiDao graffitiDao;
	private UserDao userDao;

	
	public UserController(){
		setCommandClass(User.class);
		setCommandName("registrationForm");
	}
 
	@Override
	@RequestMapping("/register")  
	protected ModelAndView onSubmit(HttpServletRequest request,
		HttpServletResponse response, Object command, BindException errors)
		throws Exception {
		final ArrayList<String> user = new ArrayList<String>();
 
		User us = (User)command;
		
		user.add(us.getUserName());
		user.add(us.getPassword());
		user.add(us.getName());
		System.out.println(user.get(0));
		this.graffitiDao.insertUser(user);
		return new ModelAndView("registrationSuccess","user",us);
 
	}
 
	@Override
	protected Object formBackingObject(HttpServletRequest request)
		throws Exception {
 
		User us = new User();
 
		return us;
	}
 
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
 
		Map referenceData = new HashMap();
 
		return referenceData;
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(final UserDao userDao) {
		this.userDao = userDao;
	}
	
	public GraffitiDao getGraffitiDao() {
		return graffitiDao;
	}

	public void setGraffitiDao(final GraffitiDao graffitiDao) {
		this.graffitiDao = graffitiDao;
	}
}