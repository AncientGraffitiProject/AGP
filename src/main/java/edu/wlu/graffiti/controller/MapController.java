package edu.wlu.graffiti.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.bean.Insula;
import edu.wlu.graffiti.dao.InsulaDao;
import edu.wlu.graffiti.dao.FindspotDao;

@Controller
public class MapController {

	@Resource
	private InsulaDao insulaDao;

	@Resource
	private FindspotDao propertyDao;

	@RequestMapping(value = "/pompeiiMap", method = RequestMethod.GET)
	public String viewMap(final HttpServletRequest request) {
		return "pompeiiMap";
	}

	@RequestMapping(value = "/map", method = RequestMethod.GET)
	public String processMap(final HttpServletRequest request) {
		String city = request.getParameter("city");
		String clickedRegion = request.getParameter("clickedRegion");
		List<String> highlighted = new ArrayList<String>();
		String[] h = request.getParameterValues("highlighted");
		String search = request.getParameter("search");
		if (h != null) {
			for (int i = 0; i < h.length; i++) {
				highlighted.add(h[i]);
			}
		}

		try {
			Insula insula = this.insulaDao.getInsulaById(Integer.valueOf(clickedRegion));
			String insula_name = insula.getShortName();
			clickedRegion = insula_name;
		} catch (NumberFormatException e) {
			// do nothing; clickedRegion is Herculaneum or Pompeii
		}
		//System.out.println(clickedRegion.toLowerCase() + "_map.xml");
		final InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(clickedRegion.toLowerCase() + "_map.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;

		List<String> coords = new ArrayList<String>();
		List<String> regionNames = new ArrayList<String>();
		List<String> regionIds = new ArrayList<String>();

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inStream);
			doc.getDocumentElement().normalize();

			NodeList areaList = doc.getElementsByTagName("area");

			for (int i = 0; i < areaList.getLength(); i++) {

				Element area = (Element) areaList.item(i);
				String regionCoords = area.getAttribute("coords");
				String regionName = area.getAttribute("alt");
				int regionId;
				if (clickedRegion.equals("Pompeii")) { // use insula ids
					regionId = this.insulaDao.getInsulaByCityAndInsula(city, regionName).get(0).getId();
					regionIds.add("i" + String.valueOf(regionId));
				} else { // use property ids
					if (clickedRegion.equals("Herculaneum"))// TODO fix
															// this...temporary
															// fix to highlight
															// Herculaneum map
															// on browse page
						city = "Herculaneum";
					String insulaAndProp = regionName.split(" ")[0];
					try {
						int ind = insulaAndProp.lastIndexOf(".");
						String insula = insulaAndProp.substring(0, ind);
						String property_number = insulaAndProp.substring(ind + 1);
						regionId = this.propertyDao.getPropertyByCityAndInsulaAndProperty(city, insula, property_number)
								.getId();
						regionIds.add("p" + String.valueOf(regionId));
					} catch (IndexOutOfBoundsException e) {
						// No such property is in the database
						String uniqueId = insulaAndProp + i;
						regionIds.add(uniqueId);
					}
				}

				coords.add(regionCoords);
				regionNames.add(regionName);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		request.setAttribute("coords", coords);
		request.setAttribute("mapName", clickedRegion);
		request.setAttribute("regionNames", regionNames);
		request.setAttribute("regionIds", regionIds);
		request.setAttribute("highlighted", highlighted);
		request.setAttribute("displayImage", request.getContextPath() + "/resources/images/" + clickedRegion + ".jpg");
		request.setAttribute("second", request.getParameter("second"));
		request.setAttribute("search", search);

		return "buildMap";
	}
}
