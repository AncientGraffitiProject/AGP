package edu.wlu.graffiti.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.wlu.graffiti.bean.DrawingTag;
import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Theme;
import edu.wlu.graffiti.dao.DrawingTagsDao;
import edu.wlu.graffiti.dao.FindspotDao;
import edu.wlu.graffiti.dao.GraffitiDao;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.PropertyTypesDao;
import edu.wlu.graffiti.dao.ThemeDao;

@Controller
public class AdminController {
	
	@Resource
	// The @Resource injects an instance of the GraffitiDao at runtime. The
	// GraffitiDao instance is defined in graffiti-servlet.xml.
	private GraffitiDao graffitiDao;

	@Resource
	private DrawingTagsDao drawingTagsDao;

	@Resource
	private PropertyTypesDao propertyTypesDao;

	@Resource
	private FindspotDao findspotDao;

	@Resource
	private InsulaDao insulaDao;
	
	@Resource
	private ThemeDao themeDao;
	
	@RequestMapping(value = "/AdminFunctions", method = RequestMethod.GET)
	public String adminFunctions(final HttpServletRequest request) {
		return "admin/AdminFunctions";
	}

	
	@RequestMapping(value = "/admin/editGraffito", method = RequestMethod.GET)
	public String editGraffito(final HttpServletRequest request) {

		return "admin/editGraffito";

	}

	// Update a graffito page - sharmas
	@RequestMapping(value = "/admin/updateGraffito", method = RequestMethod.GET)
	public String updateGraffito(final HttpServletRequest request) {

		String id = request.getParameter("edrID");
		if (id == null || id.equals("")) {
			request.setAttribute("msg", "Please enter an EDR number");
			return "admin/editGraffito";
		}

		request.getSession().setAttribute("edrID", id);

		Inscription inscription = graffitiDao.getInscriptionByEDR(id);

		if (inscription == null) {
			request.setAttribute("msg", "Not a valid EDR number");
			return "admin/editGraffito";
		}

		request.setAttribute("graffito", inscription);

		addDrawingTagsAndThemesToRequest(request, inscription);

		return "admin/updateGraffito";

	}


	private void addDrawingTagsAndThemesToRequest(final HttpServletRequest request, Inscription inscription) {
		Set<DrawingTag> drawingTags = inscription.getAgp().getFiguralInfo().getDrawingTags();
		List<Integer> drawingTagIds = new ArrayList<Integer>();

		for (DrawingTag i : drawingTags) {
			int dtId = i.getId();
			drawingTagIds.add(dtId);
		}
		
		List<Theme> themes = inscription.getAgp().getThemes();
		List<Integer> themeIds = new ArrayList<Integer>();
		List<Integer> allThemeIds = themeDao.getAllThemeIds();
		
		for (Theme t : themes) {
			int tId = t.getId();
			themeIds.add(tId);
		}
		
		request.setAttribute("drawingTags", drawingTagsDao.getDrawingTags());
		request.setAttribute("drawingTagIds", drawingTagIds);
		request.setAttribute("themes", themeDao.getThemes());
		request.setAttribute("inscriptionThemeIds", themeIds);
		request.setAttribute("allThemeIds", allThemeIds);
	}

	// Update a graffito controller
	@RequestMapping(value = "/admin/updateGraffito" , method = RequestMethod.POST)
	public String adminUpdateGraffito(final HttpServletRequest request) {

		// updating AGP Inscription Information
		String edrID = (String) request.getSession().getAttribute("edrID");

		// updating AGP Inscriptions
		String summary = request.getParameter("summary");
		String commentary = request.getParameter("commentary");
		String cil = request.getParameter("cil");
		String langner = request.getParameter("langner");
		String epidoc = request.getParameter("epidocContent");
		String floor_to_graffito_height = request.getParameter("floor_to_graffito_height");
		String content_translation = request.getParameter("content_translation");
		String graffito_height = request.getParameter("graffito_height");
		String graffito_length = request.getParameter("graffito_length");
		String letter_height_min = request.getParameter("letter_height_min");
		String letter_height_max = request.getParameter("letter_height_max");
		String charHeights = request.getParameter("character_heights");
		String figural = request.getParameter("figural");
		String ghFig = request.getParameter("gh_fig");
		String ghTrans = request.getParameter("gh_trans");
		String theme = request.getParameter("themed");

		boolean hasFiguralComponent = false;
		boolean isfeaturedHitFig = false;
		boolean isfeaturedHitTrans = false;
		boolean isThemed = false;

		if (figural != null) {
			hasFiguralComponent = true;
		}
		if (ghFig != null) {
			isfeaturedHitFig = true;
		}
		if (ghTrans != null) {
			isfeaturedHitTrans = true;
		}
		if (theme != null) {
			isThemed = true;
		}

		List<Object> agpOneDimArrList = new ArrayList<Object>();
		agpOneDimArrList.add(summary);
		agpOneDimArrList.add(content_translation);
		agpOneDimArrList.add(cil);
		agpOneDimArrList.add(langner);
		agpOneDimArrList.add(epidoc.replaceAll("\r|\n", ""));
		agpOneDimArrList.add(floor_to_graffito_height);
		agpOneDimArrList.add(graffito_height);
		agpOneDimArrList.add(graffito_length);
		agpOneDimArrList.add(letter_height_min);
		agpOneDimArrList.add(letter_height_max);
		agpOneDimArrList.add(charHeights);
		agpOneDimArrList.add(commentary);
		agpOneDimArrList.add(hasFiguralComponent);
		agpOneDimArrList.add(isfeaturedHitFig);
		agpOneDimArrList.add(isfeaturedHitTrans);
		agpOneDimArrList.add(isThemed);

		graffitiDao.updateAgpInscription(agpOneDimArrList, edrID);

		if (hasFiguralComponent) {
			String drawingDescriptionLatin = request.getParameter("drawing_description_latin");
			String drawingDescriptionEnglish = request.getParameter("drawing_description_english");

			graffitiDao.updateDrawingInfo(drawingDescriptionLatin, drawingDescriptionEnglish, edrID);

		}

		if (isfeaturedHitFig || isfeaturedHitTrans) {
			String ghCommentary = request.getParameter("gh_commentary");
			String ghPreferredImage = request.getParameter("gh_preferred_image");
			graffitiDao.updateGreatestHitsInfo(edrID, ghCommentary, ghPreferredImage);
		}

		// updating drawing tags
		String[] drawingTags = request.getParameterValues("drawingCategory");
		graffitiDao.clearDrawingTags(edrID);

		if (drawingTags != null && hasFiguralComponent) {
			graffitiDao.insertDrawingTags(edrID, drawingTags);
		}

		// updating themes
		String[] themes = request.getParameterValues("themes");
		graffitiDao.clearThemes(edrID);

		if (themes != null && isThemed) {
			graffitiDao.insertThemes(edrID, themes);
		}
		
		request.setAttribute("msg", "The graffito has been successfully updated in the database");

		Inscription element = graffitiDao.getInscriptionByEDR(edrID);

		request.setAttribute("graffito", element);
		
		addDrawingTagsAndThemesToRequest(request, element);

		return "admin/updateGraffito";

	}
}
