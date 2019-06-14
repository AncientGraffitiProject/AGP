package edu.wlu.graffiti.data.setup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.data.setup.Utils;
import edu.wlu.graffiti.data.setup.main.ImportEDRData;

/**
 * Reads in xml files, parses them, and puts their information into the database appropriately
 * 
 * @author Trevor Stalnaker
 *
 */
public class ReadFromEpidoc {
	
	private static HashMap<String, String> alphaNumeral = new HashMap<String, String>();
	
	private static final String INSERT_INTO_EDR_INSCRIPTIONS = "INSERT INTO edr_inscriptions " + "(edr_id, ancient_city, "
			+ "bibliography, date_beginning, date_end, content) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String INSERT_INTO_AGP_INSCRIPTION_INFO = "INSERT INTO agp_inscription_info " + "(edr_id, comment, "
			+ "content_translation, writing_style_in_english, graffito_height, graffito_length, letter_height_min, letter_height_max,"
			+ "lang_in_english, content_epidocified) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static Document dom; 
	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;
	
	private static String[] xmlFiles = {"t1.2", "t2.1", "t6.2", "t8.1", "t8.2", "t9.1", "t9.2", "t9.3", "tg2.1", "tx.1"};
	private static ArrayList<Node> allRootsChildren = new ArrayList<Node>();
	//This is really just for testing purposes
	public static void main(String[] args) {	
		//parseEpidoc("allLocal");
		for (String file : xmlFiles) {
			parseEpidoc(file);
		}
	}

	
	//Code that extracts fields from epidoc and saves them to the database
	public static void parseEpidoc(String file) {
		
		//Fields in edr_inscriptions
		String edrId, ancient_city, content, bibliography,  date_beginning, date_end;
		//String find_spot,apparatus_displayed;
		
		//Fields in agp_inscription_info
		String comment, writing_style_in_english, graffito_height, graffito_length;
		String content_translation = "", letter_height_min = "", letter_height_max = "",
				lang_in_english, summary= "";
		//String lang_in_english, height_from_ground, cil, langner, summary;
		//String individual_letter_heights, letter_with_flourishes_height_min, letter_with_flourishes_height_max;
		
		init();
		
		try {
			
			// Create a document factory
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			// parse using builder to get DOM representation of the XML file
			dom = db.parse("src/main/resources/smyrna_epidocs/"+ file +".xml");
			
			//Create Prepared Statements
			PreparedStatement pstmt = newDBCon.prepareStatement(INSERT_INTO_EDR_INSCRIPTIONS);
			PreparedStatement pstmt2 = newDBCon.prepareStatement(INSERT_INTO_AGP_INSCRIPTION_INFO);

			//Find all the nodes of the <TEI> tag type --- these mark individual graffiti
			NodeList list = dom.getElementsByTagName("TEI");
			for(int y=0; y < list.getLength(); y++) {
				
				//Get the root node for the current graffito and create a list of its children
				Node root = list.item(y);
				allRootsChildren = returnAllChildNodesExcludingText(root);
				System.out.println("Length: " + allRootsChildren.size());
				
				edrId = "";
				// If reading in from the file containing all inscriptions pull id from titleStmt
				if(file.equals("all")) {
					edrId = getTextFromTagAndParent("title", "titleStmt");
				}
				// Create Distinct EDR ID for the current graffito (for Smyrna)
				else {
					String fileName = file.replaceAll("\\.", "").toUpperCase();
					Matcher matcher = Pattern.compile("([A-Za-z]+)([0-9]+)").matcher(fileName);
					edrId = "SMY";
					if (matcher.find()) {
						int zeroCount = 9 - (fileName.length() + 3);
						edrId += matcher.group(1);
						while (zeroCount > 0) {
							edrId += "0";
							zeroCount--;
						}
						edrId += matcher.group(2);
					}
				}

				System.out.println("\n\nEdr ID: " + edrId);
				pstmt.setString(1, edrId);
				pstmt2.setString(1, edrId);
				
				//Find the language the current graffito is written in in English
				lang_in_english = "";
				Node editionNode = getNodeByTagAndAttribute("div", "type", "edition");
				if (editionNode != null) {
					String lang = getAttributeValueForNode(editionNode, "xml:lang");
					String[] languages = lang.split(" ");
					for (int i = 0; i < languages.length; i++) {
						if (languages[i].equals("la")) {
							lang_in_english += "Latin";
						}
						if (languages[i].equals("grk")) {
							lang_in_english += "Greek";
						}
						if (i < languages.length - 1) {
							lang_in_english += "/";
						}
					}
				}
				System.out.println("Formatted Lang: " + lang_in_english);
				pstmt2.setString(9, lang_in_english);
				
				//Find the origPlace element of the current graffito
				ancient_city = getTextFromTag("origPlace");
				System.out.println("Ancient City: " + ancient_city);
				pstmt.setString(2, ancient_city);
				
				//Find the bibliography of the current graffito
				bibliography = getTextFromTagAndParentAttribute("p", "div", "type", "bibliography");
				System.out.println("Bibliography: " + bibliography);
				pstmt.setString(3, bibliography);
				
				//Find the date information of the current graffito
				Node dateNode = getNodeByTag("origDate");
				if (dateNode != null) {
					date_beginning = getAttributeValueForNode(dateNode, "notBefore-custom");
					System.out.println("Start Date: " + date_beginning);
					pstmt.setString(4, date_beginning);
					date_end = getAttributeValueForNode(dateNode, "notAfter-custom");
					System.out.println("End Date: " + date_end);
					pstmt.setString(5, date_end);
				}
				
				//Find the commentary of the current graffito
				comment = getTextFromTagAndParentAttribute("ab", "div", "type", "commentary");
				comment += getTextFromTagAndParentAttribute("p", "div", "type", "commentary");
				System.out.println("Commentary: " + comment);
				pstmt2.setString(2, comment);
				
				//Find and print the translation of the current graffito
				content_translation = getTextFromTagAndParentAttribute("ab", "div", "type", "translation");
				content_translation += getTextFromTagAndParentAttribute("p", "div", "type", "translation");
				System.out.println("Translation: " + content_translation);
				pstmt2.setString(3, content_translation);
				
				//Find the writing style of the current graffito
				writing_style_in_english = getTextFromTag("rs");
				System.out.println("Writing Style: " + writing_style_in_english);
				//Temporary Code to handle errors caused by differences in conventions (This will need to be remedied in the database)
				if (writing_style_in_english.length() > 30) {
					writing_style_in_english = "";
				}
				pstmt2.setString(4, writing_style_in_english);
				
				//Find the length and height of the current graffito
				graffito_height = getTextFromTagAndParent("height", "dimensions");
				graffito_length = getTextFromTagAndParent("width", "dimensions");
				System.out.println("Graffito Height: " + graffito_height);
				System.out.println("Graffio Length: " + graffito_length);
				pstmt2.setString(5, graffito_height);
				pstmt2.setString(6, graffito_length);
				
				//Find the letter height information of the current graffito
				ArrayList<Node> letterNodes = getNodesByTagAndParent("height", "handNote");
				for (Node node : letterNodes) {
					String attr = getAttributeValueForNode(node, "scope");
					if (attr.equals("letter")) {
						if (hasAttribute(node, "min") && hasAttribute(node, "max")) {
							letter_height_min = getAttributeValueForNode(node, "min");
							letter_height_max = getAttributeValueForNode(node, "max");
						}
						else {
							letter_height_min = node.getTextContent();
							letter_height_max = letter_height_min;
						}
						System.out.println("Min: " + letter_height_min);
						System.out.println("Max: " + letter_height_max);
						pstmt2.setString(7, letter_height_min);
						pstmt2.setString(8, letter_height_max);
					}
					if (attr.equals("individualletter")) {
						//TO-DO
					}
				}
				
				//Find the content of the current graffito
				content = getContent(getNodeByTagAndParentAttribute("ab", "div", "type", "edition"));
				System.out.println("Content: \n" + content);
				pstmt.setString(6, content);
				
				//Find the epidocified content of the current graffito
				String epidoc = preserveEpidoc(content);
				System.out.println("Epidocified: " + epidoc);
				pstmt2.setString(10, epidoc);
				
				//We can't use this because this field was removed from the database
				//Find the summary information for the current graffito
				//if (file.equals("all")){
					//summary = getTextFromTagAndParentAttribute("ab", "div", "type", "summary");
					//summary += getTextFromTagAndParentAttribute("p", "div", "type", "summary");
				//}
				//Find title information of current graffito and save it as summary (for Smyrna)
				//else{
					//summary = getTextFromTagAndParent("title", "titleStmt").replaceAll("[^\\s]+\\.[0-9]+\\.\\:[ ]", "");
				//}
				//System.out.println("Summary: " + summary);
				//pstmt2.setString(11, summary);				
			
//				pstmt.executeUpdate();
//				pstmt.close();
//			
//				pstmt2.executeUpdate();
//				pstmt2.close();
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		

	}
	
	/**Methods for extracting information from the epidoc files*/
	
	//Return the node with the given tag
	private static Node getNodeByTag(String tag) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)){
				return node;
			}
		}
		return null;
	}
	
	//Return the node with the given tag and attribute/value pair
	private static Node getNodeByTagAndAttribute(String tag, String attr, String val) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)){
				if (hasAttribute(node, attr) && getAttributeValueForNode(node, attr).equals(val)) {
					return node;
				}
			}
		}
		return null;
	}
	
	//Return the node with the given tag and parent tag
	private static Node getNodeByTagAndParent(String tag, String parent) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)){
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text") &&
							((Element) nodes.item(i)).getTagName().equals(tag)){
						return nodes.item(i);
					}
				}
			}
		}
		return null;
	}
	
	//Return the node with the given tag and parent tag with the provided attribute/value pair
	private static Node getNodeByTagAndParentAttribute(String tag, String parent, String attr, String val) {
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)){
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text") &&
							((Element) nodes.item(i)).getTagName().equals(tag) &&
							hasAttribute(node, attr) &&
							getAttributeValueForNode(node, attr).equals(val)){
						return nodes.item(i);
					}
				}
			}
		}
		return null;
	}
	
	//Return all nodes with a given tag
	private static ArrayList<Node> getNodesByTag(String tag){
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(tag)){
				returnLyst.add(node);
			}
		}
		return returnLyst;
	}
	
	//Return all nodes with the given tag and parent tag
	private static ArrayList<Node> getNodesByTagAndParent(String tag, String parent){
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : allRootsChildren) {
			if (((Element) node).getTagName().equals(parent)){
				NodeList nodes = node.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					if (!nodes.item(i).getNodeName().equals("#text") &&
							((Element) nodes.item(i)).getTagName().equals(tag)){
						returnLyst.add(nodes.item(i));
					}
				}
			}
		}
		return returnLyst;
	}
	
	//Returns the text from a node or the empty string
	private static String getTextFromNode(Node node) {
		if (node != null) {
			return node.getTextContent();
		}
		return "";
	}
	
	//Returns the text from the node with the given tag
	private static String getTextFromTag(String tag) {
		return getTextFromNode(getNodeByTag(tag));
	}
	
	//Returns the text from the node with the given tag and parent tag
	private static String getTextFromTagAndParent(String tag, String parent) {
		return getTextFromNode(getNodeByTagAndParent(tag, parent));
	}
	
	//Returns the text from the node with the given tag and parent tag with the provided attribute/value pair
	private static String getTextFromTagAndParentAttribute(String tag, String parent, String attr, String val) {
		return getTextFromNode(getNodeByTagAndParentAttribute(tag, parent, attr, val));
	}

	/**Helper methods for getting node and element information*/
	
	//Checks if a node has the given attribute (Doesn't check the value of that attribute)
	private static boolean hasAttribute(Node node, String attr) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes.getLength() == 0) {return false;}
		for (int i = 0; i < attributes.getLength(); i++) {
			String attrStr = attributes.item(i).toString().substring(0, attributes.item(i).toString().indexOf("="));
			if(attr.equals(attrStr)) {
				return true;
			}
		}
		return false;
	}
	
	//Returns the value of an attribute of a single tag element
	private static String getAttributeValueForNode(Node node, String atr) {
		//Create a list of nodes of specified tag type
		String value = null;
		//Iterate through the list if it is not empty
		value = ((Element) node).getAttribute(atr);
		return value;
	}
	
	//Returns an ArrayList of all of a given node's children and grandchildren
	private static ArrayList<Node> returnAllChildNodes(Node root, ArrayList<Node> stack){
		stack.add(root);
		if (root.hasChildNodes()) {
			NodeList templist = root.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				returnAllChildNodes(templist.item(i), stack);
			}	
		}
	return stack;
	}
	
	//Returns an ArrayList of all of a given node's children and grandchildren, excluding text
	private static ArrayList<Node> returnAllChildNodesExcludingText(Node root){
		ArrayList<Node> children = returnAllChildNodes(root, new ArrayList<Node>());
		ArrayList<Node> returnLyst = new ArrayList<Node>();
		for (Node node : children) {
			if (!node.getNodeName().equals("#text")) {
				returnLyst.add(node);
			}
		}
		return returnLyst;
	}
	
	//Returns an ArrayList of the immediate children of a given node
	private static ArrayList<Node> getDirectChildren(Node localRoot){
		ArrayList<Node> nodes = new ArrayList<Node>();
		if (localRoot != null && localRoot.hasChildNodes()) {
			NodeList templist = localRoot.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				nodes.add(templist.item(i));
			}	
		}
		return nodes;
	}
	
	//Returns an ArrayList of the immediate children of a given node excluding text
	private static ArrayList<Node> getDirectChildrenExcludingText(Node node) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		if (node.hasChildNodes()) {
			NodeList templist = node.getChildNodes();
			for (int i = 0; i < templist.getLength(); i++) {
				if (!(templist.item(i).getNodeName().equals("#text"))) {
					nodes.add(templist.item(i));
				}			
			}
		}
		return nodes;
	}
	
	/**Methods for reading and translating epidocified content*/
	
	//Provided with the root node, returns the translated content
	public static String getContent(Node localRoot) {
		String content = "";
		if (localRoot != null) {
			ArrayList<Node> nodes = getDirectChildren(localRoot);
			for (Node node: nodes) {
				content += translateContent(node);
			}
			Matcher matcher = Pattern.compile("(\\[[^\\[\\]]+\\])+").matcher(content);
			while (matcher.find()) {
				String temp = matcher.group(0);
				content = content.replace(temp, "[" + temp.replaceAll("\\[|\\]", "") + "]");
			}
		}
		return content;
	}
	
	//Refer to Epidoc Content Conversion Document for Translation Information
	private static String translateContent(Node node) {
		String translation = "";
		
		//Plain text nodes
		//Cleans excess white space, but leaves single spaces
		if (node.getNodeName().equals("#text")) {
			if(node.getTextContent().equals(" ")) {
				return " ";
			}
			return trimWhiteSpace(node.getTextContent());
		}
		
		String tag = ((Element) node).getTagName();
		
		//Newlines and line breaks
		if (tag.equals("lb")) {
			//Special case for lines written vertically
			if (getAttributeValueForNode(node, "style").equals("text-direction:vertical")){
				return "<:ad perpendiculum>\n";
			}
			if(!getAttributeValueForNode(node, "n").equals("1")) {
				return "\n";
			}else {
				return "";
			}
		}
		
		//Spaces left blank
		if (tag.equals("space")) {
			//Space caused by a door
			if (hasAttribute(node, "type") && getAttributeValueForNode(node, "type").equals("door")) {
				return "<:ianua>";
			}
			//Spaces left intentionally blank
			return "<:vacat>";
		}
		
		//Puts dots under characters that are marked as unclear
		//Dot character: '\u0323'  Note: If these are printed in the console they may appear one off, but they work on the site
		if (tag.equals("unclear")) {
			String tempstr = node.getTextContent();
			for (int i = 0; i < tempstr.length(); i++) {
				translation += (tempstr.charAt(i) + "\u0323");
			}
			return translation;
		}
		
		//Notation for nodes marked as supplied
		if (tag.equals("supplied")) {
			if (getAttributeValueForNode(node, "reason").equals("lost")) {
				//Put [] around characters
				translation = "[";
				ArrayList<Node> recurseNodes = getDirectChildren(node);
				for (Node recurseChild : recurseNodes) {
					translation += translateContent(recurseChild);
				}
				if (hasAttribute(node, "cert")) {
					if (getAttributeValueForNode(node, "cert").equals("low")) {
						translation += "?]";
					}
				}
				else {
					translation += "]";
				}
				return translation;
			}
			if (getAttributeValueForNode(node, "reason").equals("undefined")) {
				//Underline characters -- Macron Symbol: '\u0331' or combining low line \u0332
				String tempstr = node.getTextContent();
				for (int i =0; i < tempstr.length(); i++) {
					translation += (tempstr.charAt(i) + "\u0332");
				}
				return translation;
			}
			if (getAttributeValueForNode(node, "reason").equals("subaudible")) {
				return "<:" + node.getTextContent() + ">";
			}
		}
		
		//Inserts gap notation for areas marked as a gap
		if (tag.equals("gap")) {
			if (getAttributeValueForNode(node, "reason").equals("lost")) {
				//Put [---]
				if (getAttributeValueForNode(node, "unit").equals("character")) {
					translation = "[";
					if (hasAttribute(node, "quantity")) {
						if (hasAttribute(node, "precision")) {
							translation += "+" + getAttributeValueForNode(node, "quantity")+"?+";
						}
						else {
							// Dot character to replace '-' with '•' once the classicists are ready
							int quant = Integer.parseInt(getAttributeValueForNode(node, "quantity"));
							for (int x = 0; x < quant; x++) {
								translation += "•";
							}	 
						}
					}else {
						translation += "---";
					}
					translation += "]";
					return translation;
				}
				if (getAttributeValueForNode(node, "unit").equals("line")) {
					if (hasAttribute(node, "extent") && getAttributeValueForNode(node, "extent").equals("unknown")) {
						return translation + "- - - - - -";
					}else {
						int ex = Integer.parseInt(getAttributeValueForNode(node, "extent"));
						for (int x = 0; x < ex; x++) {
							translation += "[------]\n";
						}	
						return translation;
					}
					
				}	
			}
			if (getAttributeValueForNode(node, "reason").equals("illegible")) {
				if (hasAttribute(node, "extent")) {
					if(getAttributeValueForNode(node, "extent").equals("unknown")) {
						return "<:textus non legitur>";
					}
				}
				if (hasAttribute(node, "quantity")) {
					int quant = Integer.parseInt(getAttributeValueForNode(node, "quantity"));
					for (int i=0; i < quant; i++) {
						translation += "+";
					}
				}
				return translation;
			}
		}
		
		//Notations for nodes marked expan
		if (tag.equals("expan")) {
			Boolean symbol =false;
			ArrayList<Node> nodes = getDirectChildren(node);
			for(Node child : nodes) {
				//The part of the abbreviation outside of parens (if not a symbol)
				if(((Element) child).getTagName().equals("abbr")) {
					//Check if abbr tag has any children at all (there are a few cases where it doesn't)
					if(child.hasChildNodes()) {
						//Check if the child element of the child node is text
						if (!getDirectChildren(child).get(0).getNodeName().equals("#text")) {
							//Determine if child element should be represented as a symbol
							if(((Element) getDirectChildren(child).get(0)).getTagName().equals("am")) {
								symbol = true;
							}
							//Recurse through any nested nodes within abbr tag
							else {
								ArrayList<Node> grandChildren = getDirectChildren(child);
								for (Node grandChild : grandChildren) {
									translation += translateContent(grandChild);
								}	
							}
						}
						//Child is text and this is a basic abbreviation
						else {
							ArrayList<Node> recurseNodes = getDirectChildren(child);
							for (Node recurseChild : recurseNodes) {
								translation += translateContent(recurseChild);
							}
						}
					}
				}
				//Part of the abbreviation within parens (if not a symbol)
				if(((Element) child).getTagName().equals("ex")) {
					//Return notation for symbols
					if (symbol) {
						return "((" + child.getTextContent() + "))";
					}
					//Construct Notation for abbreviations
					else {
						translation += "(";
						ArrayList<Node> recurseNodes = getDirectChildren(child);
						for (Node recurseChild : recurseNodes) {
							translation += translateContent(recurseChild);
						}
						//Check for uncertainty, represented with a '?'
						if(getAttributeValueForNode(child, "cert").equals("low")) {
							//Add a question mark
							translation += "?";
						}
						translation += ")";
					}
				}
			}
			return translation;
		}
		
		//Notation for nodes marked del
		if(tag.equals("del")) {
			NodeList nodes = node.getChildNodes();
			for(int i=0; i < nodes.getLength(); i++) {
				if(((Element) nodes.item(i)).getTagName().equals("supplied")) {
					//Put text in double brackets
					translation = "〚";
					ArrayList<Node> recurseNodes = getDirectChildren(nodes.item(i));
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild);
					}
					translation += "〛";
				}
			}
			return translation;
		}
		
		//Notation for nodes marked as a figure
		if(tag.equals("figure")) {
			NodeList nodes = node.getChildNodes();
			for(int i=0; i < nodes.getLength(); i++) {
				if(((Element) nodes.item(i)).getTagName().equals("figDesc")) {
					//Put text in ((:abc))
					translation += "((:";
					ArrayList<Node> recurseNodes = getDirectChildren(nodes.item(i));
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild);
					}
					translation += "))";
				}
			}
			return translation;
		}
		
		//Notation for nodes marked orig
		if(tag.equals("orig")) {
			//Capitalize all letters within this tag
			ArrayList<Node> recurseNodes = getDirectChildren(node);
			for (Node recurseChild : recurseNodes) {
				translation += translateContent(recurseChild);
			}
			return translation.toUpperCase();
		}
		
		//Notation for the choice tag
		if(tag.equals("choice")) {
			ArrayList<Node> nodes = getDirectChildren(node);
			String end = "";
			for(Node child : nodes) {
				if (((Element) child).getTagName().equals("reg")) {
					end += " (:";
					if (hasAttribute(child, "cert")) {
						if(getAttributeValueForNode(child, "cert").equals("low")) {
							end += child.getTextContent() + "?";
						}
					}
					else {
						ArrayList<Node> recurseNodes = getDirectChildren(child);
						for (Node recurseChild : recurseNodes) {
							end += translateContent(recurseChild);
						}
					}	
					end += ")";
							
				}
				if (((Element) child).getTagName().equals("orig")) {
					ArrayList<Node> recurseNodes = getDirectChildren(child);
					for (Node recurseChild : recurseNodes) {
						translation += translateContent(recurseChild);
					}
					translation +=  end;
				}
			}
			return translation;
		}
		
		//Notation for nodes marked hi
		if(tag.equals("hi")) {
			//Add a carot to characters in this tag
			String tempstr = node.getTextContent();
			for (int i = 0; i < tempstr.length(); i++) {
				translation += tempstr.charAt(i);
				if(i < tempstr.length()-1) {
					translation += "\u0302";
				}
			}
			return translation;
		}
		
		//Notation for nodes marked surplus
		if(tag.equals("surplus")) {
			return "{" + node.getTextContent() + "}";
		}
		
		//Notation for nodes marked as abbreviations (the expansion of which is unknown)
		if(tag.equals("abbr")) {
			return node.getTextContent() + "(---)";
		}
		
		//Handles the persName tag
		if(tag.equals("persName")) {
			Node child = getDirectChildren(node).get(0);
			ArrayList<Node> nodes = getDirectChildren(child);
			for (Node grandchild : nodes) {
				translation += translateContent(grandchild);
			}
			return translation;
		}
		
		//Handles the placeName tag
		if(tag.equals("placeName")) {
			ArrayList<Node> nodes = getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child);
			}
			return translation;
		}
		
		//Handles the geogName tag
		if(tag.equals("geogName")) {
			ArrayList<Node> nodes = getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child);
			}
			return translation;
		}
		
		//Handles the w tag
		if(tag.equals("w")) {
			ArrayList<Node> nodes = getDirectChildren(node);
			for (Node child : nodes) {
				translation += translateContent(child);
			}
			return translation;
		}
		
		//Handles Columns 
		if(tag.equals("div")) {
			if(hasAttribute(node, "subtype") && getAttributeValueForNode(node, "subtype").equals("column")) {
				String level = convertFromAlphaToNumeral(getAttributeValueForNode(node, "n"));
				translation += "\n<:columna " + level + ">\n"; //Add the first newline, because otherwise the code will exclude it
				ArrayList<Node> nodes = getDirectChildren(node);
				for (Node child : nodes) {
					translation += translateContent(child);
				}
			}
			return translation;
		}
		
		//return an empty string if node matches none of the above
		return translation;
	}	

	//Builds a map used for the conversion between alphabet characters and numerals
	private static void createAlphaToNumeralMap() {
		String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
				"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
		String[] numerals = {"I", "II", "III", "VI", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII",
				"XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV",
				"XXV", "XXVI"};	
		for (int i = 0; i < alphabet.length; i++) {
			alphaNumeral.put(alphabet[i], numerals[i]);
		}
	}
	
	//Converts alphabetic characters in column information to roman numerals
	private static String convertFromAlphaToNumeral(String str) {
		createAlphaToNumeralMap();
		return alphaNumeral.get(str);
	}
	
	//Trim white space
	private static String trimWhiteSpace(String str) {
		Matcher matcher = Pattern.compile("[ ]?.*[ ]?").matcher(str);
		if (matcher.find()){
			return matcher.group(0);
		}
		return "";
	}
	
	//Preserve the parts of the epidoc that we don't save in our database, or would otherwise be lost
	private static String preserveEpidoc(String content) {
		String epidoc = ImportEDRData.transformContentToEpidoc(content);
		//Restore Name Tags to Content
		ArrayList<Node> names = getNodesByTag("persName");
		String nymRef = "", persName_type = "", name_type = "", personName = "";
		if (names != null && names.size() != 0) {
			for (Node name : names) {
				ArrayList<Node> children = getDirectChildrenExcludingText(name);
				persName_type = getAttributeValueForNode(name, "type");
				for (Node child : children) {
					String tag = ((Element) child).getTagName();
					if (tag.equals("name")) {
						nymRef = getAttributeValueForNode(child, "nymRef");
						name_type = getAttributeValueForNode(child, "type");
						personName = ImportEDRData.transformContentToEpidoc(translateContent(name)).replaceAll("<lb n='1'/>", "");
						//personName = child.getTextContent();  //Text located within tags
					}
				}
				//Convert the contents of the node to text, then to epidoc, remove new line, and finally use to replace
				
				epidoc = epidoc.replace(personName, "<persName type='" + persName_type + "'><name nymRef='" + nymRef + "' type='" + name_type +
						"'>" + personName + "</name></persName>");
			}	
		}
		//Restore Place Tags to Content
		ArrayList<Node> places = getNodesByTagAndParent("placeName", "ab");
		String lemma = "", idno = "", idno_type = "", placeName = "";
		if (places != null && places.size() != 0) {
			for (Node place : places) {
				ArrayList<Node> children = getDirectChildrenExcludingText(place);
				for (Node child : children) {
					String tag = ((Element) child).getTagName();
					if (tag.equals("w")) {
						lemma = getAttributeValueForNode(child, "lemma");
						placeName = ImportEDRData.transformContentToEpidoc(translateContent(place)).replaceAll("<lb n='1'/>", "");
						//placeName = child.getTextContent();  //Text located within tags
					}
					if (tag.equals("idno")) {
						idno = child.getTextContent();
						idno_type = getAttributeValueForNode(child, "type");
					}
				}
				//Convert the contents of the node to text, then to epidoc, remove new line, and finally use to replace
				
				epidoc = epidoc.replaceAll(placeName, "<placeName><w lemma='" + lemma + "'>" + placeName + "</w><idno type='" +
				idno_type + "'>" + idno + "</idno></placeName>");
			}
		}
		//Restore GeoName Tags to Content
		ArrayList<Node> geoNames = getNodesByTagAndParent("geogName", "ab");
		String type = "", geogName = "";
		if (geoNames != null && geoNames.size() != 0) {
			for (Node name : geoNames) {
				type = getAttributeValueForNode(name, "type");
				//Convert the contents of the node to text, then to epidoc, remove new line, and finally use to replace
				geogName = ImportEDRData.transformContentToEpidoc(translateContent(name)).replaceAll("<lb n='1'/>", "");
				epidoc = epidoc.replace(geogName, "<geogName type='" + type + "'>" + geogName + "</geogName>");
			}
		}
		//Restore plain W Tags to Content
		ArrayList<Node> wTags = getNodesByTagAndParent("w", "ab");
		String wlemma = "", contents = "";
		if (wTags != null && wTags.size() != 0) {
			for (Node name : wTags) {
				wlemma = getAttributeValueForNode(name, "lemma");
				//Convert the contents of the node to text, then to epidoc, remove new line, and finally use to replace
				contents = ImportEDRData.transformContentToEpidoc(translateContent(name)).replaceAll("<lb n='1'/>", "");
				epidoc = epidoc.replace(contents, "<w lemma='" + wlemma + "'>" + contents + "</w>");
			}
		}
		return epidoc;
	}
	
	/**Methods for database operations*/
	
	private static void init() {
		getConfigurationProperties();

		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			newDBCon = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void getConfigurationProperties() {
		Properties prop = Utils.getConfigurationProperties();

		DB_DRIVER = prop.getProperty("db.driverClassName");
		DB_URL = prop.getProperty("db.url");
		DB_USER = prop.getProperty("db.user");
		DB_PASSWORD = prop.getProperty("db.password");
	}
}


