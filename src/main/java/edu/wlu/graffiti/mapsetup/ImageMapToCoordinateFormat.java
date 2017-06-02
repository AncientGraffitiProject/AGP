/**
 * 
 */
package edu.wlu.graffiti.mapsetup;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Processes an image map in HTML and converts to the format needed for the
 * region2mapID file.
 * 
 * Image Map Format:
 * 
 * <map name="Map" id="Map"> <area shape="poly"
 * coords="275,80,283,99,234,129,275,192,352,147,301,63" href="#"
 * alt="Central Baths" /> <area shape="poly"
 * coords="341,38,357,65,389,46,383,40,396,31,383,12" href="#"
 * alt="College of the Augustales" /> <area shape="poly"
 * coords="339,123,356,114,345,93,365,83,372,91,418,65,438,96,352,146" href="#"
 * alt="House of the Black Hall" /> </map>
 * 
 * --> Requires alt attribute set
 * 
 * @author sprenkle
 *
 */
public class ImageMapToCoordinateFormat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse("src/main/resources/herculaneum_map.xml");
			
			System.out.println(dom.getDocumentURI());

			NodeList areaList = dom.getElementsByTagName("area");
			
			if (areaList != null && areaList.getLength() > 0) {
				for (int i = 0; i < areaList.getLength(); i++) {

					// get the area element
					Element el = (Element) areaList.item(i);

					String propName = el.getAttribute("alt");
					String propCoords = el.getAttribute("coords");

					System.out.print(propName + "* " + propCoords);
					if( i < areaList.getLength() - 1) {
						System.out.print(" ");
					}
				}
			}

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

}
