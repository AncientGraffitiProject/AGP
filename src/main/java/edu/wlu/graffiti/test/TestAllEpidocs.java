package edu.wlu.graffiti.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.wlu.graffiti.data.setup.ReadFromEpidoc;
import edu.wlu.graffiti.data.setup.Utils;
import edu.wlu.graffiti.data.setup.main.ImportEDRData;

/**
 * Tests the Epidoc Generation for graffiti
 * 
 * @author Trevor Stalnaker
 */

public class TestAllEpidocs {
	
	private static String GET_EDR_IDS_AND_CONTENT = "SELECT edr_id, content FROM edr_inscriptions";
	
	//All the example graffiti from the google doc on conventions and some
	//Enough Graffiti to test all conventions
	private static String ADD_CONVENTIONS = " WHERE edr_id IN ('EDR154368', 'EDR151877', 'EDR151899', 'EDR124996', "
			+ "'EDR154568', 'EDR124997', 'EDR151886', 'EDR149110', 'EDR151048', 'EDR163885', 'EDR140140', 'EDR140198', "
			+ "'EDR158823', 'EDR124993', 'EDR140205', 'EDR150505', 'EDR124984', 'EDR081349', 'EDR142440', 'EDR140046', "
			+ "'EDR124991', 'EDR128752', 'EDR145429', 'EDR167324', 'EDR152815', 'EDR128562', 'EDR073355', 'EDR140061', "
			+ "'EDR151901')";

	private static String DB_DRIVER;
	private static String DB_URL;
	private static String DB_USER;
	private static String DB_PASSWORD;
	static Connection newDBCon;
	
	private static HashMap<Integer, String> genErrors = new HashMap<Integer, String>();
	
	private static ArrayList<String> ids = new ArrayList<String>();
	private static ArrayList<ArrayList<String>> errors = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<String>> solutions = new ArrayList<ArrayList<String>>();
	private static ArrayList<String> contents = new ArrayList<String>();
	private static ArrayList<String> epidocifiedContents = new ArrayList<String>();
	
	private static ArrayList<String> errorLyst;
	private static ArrayList<String> solutionsLyst;
	
	public static void main(String args[]) throws SQLException, FileNotFoundException {
		init();
		comprehensiveTest();
		//checkForSyntaxErrors();
		//System.out.print(test(GET_EDR_IDS_AND_CONTENT));
		//test(GET_EDR_IDS_AND_CONTENT + ADD_CONVENTIONS);
		//testTranslationProcess(GET_EDR_IDS_AND_CONTENT);
		//testTranslationProcess(GET_EDR_IDS_AND_CONTENT + ADD_CONVENTIONS);
	}
	
	
	private static void comprehensiveTest() throws SQLException, FileNotFoundException {
		System.out.print("Testing Epidoc Creation for all graffiti...");
		String epidocTest = test(GET_EDR_IDS_AND_CONTENT);
		printToFile("Epidoc_Test", epidocTest);
		System.out.println("Done!");
		System.out.print("Testing Translation From Content to Epidoc and Back Again...");
		String twoWayTest = testTranslationProcess(GET_EDR_IDS_AND_CONTENT);
		printToFile("To_Epidoc_And_Back_Again", twoWayTest);
		System.out.println("Done!");
		System.out.print("Scanning Graffiti Content For Potential Errors...");
		String potErrors = checkForSyntaxErrors();
		printToFile("Potential_Syntax_And_Convention_Errors", potErrors);
		System.out.println("Done!");
		System.out.println("All results printed to files");
	}
	
	
	//Prints all graffiti content and epidocified content, then displays any errors encountered
	private static String test(String t) throws SQLException {
		Statement stmt = newDBCon.createStatement();
		ResultSet rs = stmt.executeQuery(t);
		String returnString = "";
		int j = 1;
		while (rs.next()) {
			String id = rs.getString("edr_id");
			String content = rs.getString("content");
			if (content != null) {
				returnString += "----------\n";
				returnString += "Count: " + j + "\n";
				returnString += "EDR_ID: " + id + "\n";
				returnString += "\nContent: " + StringEscapeUtils.unescapeHtml4(content) + "\n";
				String contentEpidocified = ImportEDRData.transformContentToEpidoc(content);
				String temp = "";
				for (int x = 0; x < contentEpidocified.length(); x++) {
					if (contentEpidocified.charAt(x) == '<') {
						temp += "\n";
					}
					temp += contentEpidocified.charAt(x);	
				}
				returnString += "\nEpidocified Content: " + temp + "\n";
				returnString += "----------\n";
				j++;
			}
			try {
				SAXBuilder contentBuilder = new SAXBuilder();
				contentBuilder.build(new StringReader("<ab>" + ImportEDRData.transformContentToEpidoc(content) +"</ab>"));
			}catch (Exception e){
				genErrors.put(j, id);
			}
		}
		//Print Graffiti that Cause an Error (ie don't epidocify content at all)
		//Will look like: '<div type="edition" xml:space="preserve" xml:lang="la"/>'
		returnString += "\nContent Not Epidocified:\n";
		returnString += "COUNT     EDR ID\n";
		returnString += "----------------\n";
		for(Integer count : genErrors.keySet()) {
			returnString += String.format("%05d%14s", (count+1), genErrors.get(count)) + "\n";
			
		}
		return returnString;
	}
	
	//Check for extra spaces between the two parts of the standard spelling convention
	private static void checkForExtraWhiteSpaceInStandardSpelling(String content) {
		Pattern pattern = Pattern.compile("[^\\s\\>]*[ ]{2,}\\(\\:[^\\s\\?]*\\??\\)");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Remove extra spaces between elements");
		}
	}
	
	//Check that content with the lost characters number uncertain convention are formatted properly
	private static void checkFormOfLostCharactersNumberUncertain(String content) {
		Pattern pattern = Pattern.compile("\\[\\+([0-9]+)\\+\\?\\]");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Switch to [+" + matcher.group(1) + "?+]");
		}
	}
	
	//Check for illegally underlined characters
	private static void checkForIllegalUnderlines(String content) {
		Pattern pattern = Pattern.compile("([^a-zA-Z0-9\\;])\\&\\#818\\;");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Remove Underline on '" + matcher.group(1) + "'");
		}
	}
	
	//Check for illegally underlined characters
	private static void checkForDashesWhereShouldBeDots(String content) {
		Pattern pattern = Pattern.compile("((\\[[ ]?(-[ ]?){1,2}\\])|(\\[[ ]?(-[ ]?){4,5}\\])|(\\[[ ]?(-[ ]?){7,}\\]))");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Consider changing '" + matcher.group(0) + "' to '"
					+ matcher.group(0).replaceAll("-", "•") + "' to meet our current conventions");
		}
	}
	
	//Check for characters marked as surplus that should probably be in standard spelling notation
	private static void checkForSurplusCharacters(String content) {
		Pattern pattern = Pattern.compile("\\{([^\\s]+)\\}[ ]?([^\\s]+)");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Consider changing '" + matcher.group(0) + "' to '"
			+ matcher.group(1) + matcher.group(2) + " (:" + matcher.group(2) + ")' to meet our standard conventions");
		}
	}
	
	//Check for characters that should probably be marked with the abbr tag, but are of the wrong form
	private static void checkForAbbrWithQuestionMark(String content) {
		Pattern pattern = Pattern.compile("\\(([ ]?-[ ]?){3}\\?\\)");
		Matcher matcher = pattern.matcher(content);
		if(matcher.find()) {
			errorLyst.add(matcher.group(0));
			solutionsLyst.add("Consider changing '" + matcher.group(0) + "' to '"
			+ "(---)' to meet our standard conventions");
		}
	}
	
	//Catch any symbols that are left after epidocification, with a few exceptions
	private static void catchSymbolsLeftAfterTranslation(String content) {
		Pattern pattern = Pattern.compile("[\\?\\[\\]\\(\\)\\:\\{\\}\\-\\+]|(\\&\\#60\\;|"
				+ "〈)+:*[^(\\&\\#60\\;)(\\&\\#62\\;)〈〉<>]*(\\&\\#62\\;|"
				+ "〉)+|(\\&\\#60\\;|〈)+:*|(\\&\\#62\\;|〉)+");
		Matcher matcher = pattern.matcher(ImportEDRData.transformContentToEpidoc(content));
		String returnStr = "";
		while(matcher.find()) {
			String temp= "";
			//Exclude things found within figural description tags (these are usually fine)
			Pattern figPattern = Pattern.compile("<figDesc>(.+)</figDesc>");
			Matcher figMatcher = figPattern.matcher(ImportEDRData.transformContentToEpidoc(content));
			temp += matcher.group(0).replaceAll("\\&\\#60\\;", "<").replaceAll("\\&\\#62\\;", ">");
			while (figMatcher.find()) {
				if (figMatcher.group(0).contains(matcher.group(0))) {
					temp = "";
				}
			}
			//Also exclude <lb n='1' style='text-direction:vertical'/>
			Pattern vertPattern = Pattern.compile("<lb n\\=\\'[0-9]+\\' style\\=\\'text-direction\\:vertical\\'\\/>");
			Matcher vertMatcher = vertPattern.matcher(ImportEDRData.transformContentToEpidoc(content));
			while (vertMatcher.find()) {
				if (vertMatcher.group(0).contains(matcher.group(0))) {
					temp = "";
				}
			}
			if(!returnStr.equals("") && !temp.equals("")) {
				returnStr += ", " + temp;
			}
			if(returnStr.equals("") && !temp.equals("")) {
				returnStr += temp;
			}
		}
		if (!returnStr.equals("")) {
			if (!errorLyst.contains(returnStr)) {
				errorLyst.add(returnStr);
				solutionsLyst.add("");
			}
		}
	}
	
	//Checks for errors in the format of content
	private static String checkForSyntaxErrors() throws SQLException {
		Statement stmt = newDBCon.createStatement();
		ResultSet rs = stmt.executeQuery(GET_EDR_IDS_AND_CONTENT);
		while (rs.next()) {
			String id = rs.getString("edr_id");
			String content = rs.getString("content");
			errorLyst= new ArrayList<String>();
			solutionsLyst= new ArrayList<String>();
			if (content != null) {
				
				checkForExtraWhiteSpaceInStandardSpelling(content);
				checkFormOfLostCharactersNumberUncertain(content);
				catchSymbolsLeftAfterTranslation(content);
				checkForIllegalUnderlines(content);
				checkForDashesWhereShouldBeDots(content);
				checkForSurplusCharacters(content);
				checkForAbbrWithQuestionMark(content);
					
			}
			if (errorLyst.size() != 0) {
				ids.add(id);
				errors.add(errorLyst);
				solutions.add(solutionsLyst);
				contents.add(StringEscapeUtils.unescapeHtml4(content));
				String epidocifiedContent = ImportEDRData.transformContentToEpidoc(content);
				Matcher matcher = Pattern.compile("<lb n=\\'[0-9&&[^1]]\\'\\/>").matcher(epidocifiedContent);
				while (matcher.find()) {
					String temp = matcher.group(0);
					epidocifiedContent = epidocifiedContent.replaceAll(temp, "\n"+temp);
				}
				epidocifiedContents.add(epidocifiedContent);
			}
		}
		return syntaxErrorsToString();
	}
	
	private static String syntaxErrorsToString() {
		String resultString = "";
		resultString += "\nPotential Syntax And Convention Errors:\n";
		resultString += String.format("%70s", " ").replaceAll(" ", "-");
		for(int x = 0; x < ids.size(); x++) {
			resultString += "\nID: " + ids.get(x) + "\n";
			resultString += "Errors: \n";
			for (int y=0; y < errors.get(x).size(); y++) {
				if (y>0){resultString += "\n";}
				resultString += (y+1) + ".)  " + errors.get(x).get(y);
				if (!solutions.get(x).get(y).equals("")) {
					resultString += "\t-Potential Solution: " + solutions.get(x).get(y);
				}
			}
			resultString += "\nContent:\n";
			resultString += contents.get(x) + "\n";
			resultString += "\nEpidocified Content:\n";
			resultString += epidocifiedContents.get(x);
			if (x < ids.size()-1) {
				resultString += "\n\n";
			}
		}
		return resultString;	
	}
	
	private static String testTranslationProcess(String t) throws SQLException {
		String resultString = "";
		Statement stmt = newDBCon.createStatement();
		ResultSet rs = stmt.executeQuery(t);
		while (rs.next()) {
			String id = rs.getString("edr_id");
			String contentBefore = rs.getString("content");
			if (contentBefore != null) {
				for (String valId : genErrors.values()) {
					if (!id.equals(valId)) {
						String contentEpidocified = ImportEDRData.transformContentToEpidoc(contentBefore);
						String contentAfter = translateFromEpidoc(contentEpidocified);
						contentBefore = ImportEDRData.normalize(contentBefore).replaceAll("\\&\\#60\\;", "<").replaceAll("\\&\\#62\\;", ">");
						String contentEpidocifiedAgain = ImportEDRData.transformContentToEpidoc(contentAfter);
						if (!contentBefore.equals(contentAfter) && !contentEpidocified.equals(contentEpidocifiedAgain)) {
							resultString += "\n";
							resultString += "Some Error -- Not a Perfect Copy\n";
							resultString += "ID: " + id + "\n";
							resultString += "Content Before Epidocification: \n";
							resultString += contentBefore + "\n";
							resultString += "Epidocified Content: \n";
							Matcher matcher = Pattern.compile("<lb n=\\'[0-9&&[^1]]\\'\\/>").matcher(contentEpidocified);
							while (matcher.find()) {
								String temp = matcher.group(0);
								contentEpidocified = contentEpidocified.replaceAll(temp, "\n"+temp);
							}
							resultString += contentEpidocified + "\n";
							resultString += "Content After Epidocificaion: \n";
							resultString += contentAfter + "\n";
							resultString += "\n";
						}
					}
				}
			}	
		}
		if (resultString.equals("")) {
			return "--No Errors Detected--";
		}
		return resultString;
	}
	
	
	//Translate epidocified content into an actual xml file and then translate it back to content
	private static String translateFromEpidoc(String epidocifiedContent) {
		//Return the content of the graffito after it has been re-epidocified (for testing)
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader("<ab>" + epidocifiedContent + "</ab>"));
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			org.w3c.dom.Document dom2;
			dom2 = db.parse(is);
			return ReadFromEpidoc.getContent(dom2.getElementsByTagName("ab").item(0));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
			
	}

	private static void printToFile(String file, String str) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter("src/main/resources/epidoc_tests/" + file +".txt");
		writer.println("File: " + file + ".txt");
		Calendar cal = Calendar.getInstance();
		writer.println("Date: " + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR));
		writer.print("\n");
		writer.print(str);
		writer.close();
	}
	

	
	
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
