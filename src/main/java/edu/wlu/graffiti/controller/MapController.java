package edu.wlu.graffiti.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Controller
public class MapController {

	@RequestMapping(value = "/map", method = RequestMethod.GET)
	public String processMap(final HttpServletRequest request) {
		String clickedRegion = request.getParameter("clickedRegion");
		String currRegion = request.getParameter("currRegion");
		List<String> highlighted = new ArrayList<String>();
		String[] h = request.getParameterValues("highlighted");
		String search = request.getParameter("search");
		if (h != null) {
			for (int i = 0; i < h.length; i++) {
				highlighted.add(h[i]);
			}
		}

		
		System.out.println(clickedRegion.toLowerCase() + "_map.xml");
		// Get the map file
		final InputStream inStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(clickedRegion.toLowerCase() + "_map.xml");

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;

		List<String> coords = new ArrayList<String>();
		List<String> regionNames = new ArrayList<String>();

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inStream);
			doc.getDocumentElement().normalize();

			NodeList areaList = doc.getElementsByTagName("area");

			for (int i = 0; i < areaList.getLength(); i++) {

				Element area = (Element) areaList.item(i);
				String regionCoords = area.getAttribute("coords");
				String regionName = area.getAttribute("alt");

				coords.add(regionCoords);
				regionNames.add(regionName);
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Old way of reading file -- was a text file
		/*
		 * String line = findLine(clickedRegion); // System.out.println("LINE: "
		 * + line);
		 * 
		 * if (line == null) { if (highlighted.contains(clickedRegion))
		 * highlighted.remove(clickedRegion); else
		 * highlighted.add(clickedRegion); clickedRegion = currRegion; line =
		 * findLine(clickedRegion); }
		 * 
		 * int idx1 = 0, idx2 = 0, idx3 = 0; while ((idx1 = line.indexOf('*',
		 * idx1)) != -1) { idx2 = line.indexOf(' ', idx1 + 2); if (idx2 == -1)
		 * idx2 = line.length();
		 * 
		 * idx3 = line.lastIndexOf(' ', idx1);
		 * 
		 * coords.add(line.substring(idx1 + 2, idx2));
		 * regionNames.add(line.substring(idx3 + 1, idx1)); idx1 = idx2; }
		 */

		request.setAttribute("coords", coords);
		request.setAttribute("mapName", clickedRegion);
		request.setAttribute("regionNames", regionNames);
		request.setAttribute("highlighted", highlighted);

		if (search != null) {
			request.setAttribute("search", search);
		}
		if (request.getParameter("second") != null) {
			return "buildSidebarResultsMap";
		} else {
			return "buildMap";
		}
	}

	private static String findLine(final String regionName) {
		String line = "";
		try {
			// this file needs to be read into memory once in an init method
			final InputStream inStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream("region2mapID");
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					inStream));

			while ((line = br.readLine()) != null) {
				int spaceIdx = line.indexOf(' ');
				if (regionName.equals(line.substring(0, spaceIdx)))
					break;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;

	}
}
