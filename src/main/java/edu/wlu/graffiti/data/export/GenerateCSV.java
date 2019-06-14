/**
 * 
 */
package edu.wlu.graffiti.data.export;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import edu.wlu.graffiti.bean.Inscription;
import edu.wlu.graffiti.bean.Property;

/**
 * This class serializes Inscription objects and returns a string in CSV format to represent
 * the objects.
 * 
 * @author Hammad Ahmad
 *
 */
public class GenerateCSV {
	
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	// the fields	
	private static final Object[] FILE_HEADER = {"agpId","cityName","cityPleiadesId","content",
			"edrFindspot", "dateBeginning", "dateEnd","languageInEnglish","writingStyleInEnglish"};
	
	private static final Object[] FILE_HEADER_PROPERTY = {"city", "insula", " number", "name",
			"type", "link"};
	
	//citation
	private static final Object[] CITATION = {"Citation: The Ancient Graffiti Project, http://ancientGraffiti.org/ [accessed: " + new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date()) + "]"};
	
	/**
	 * Serializes a list of inscriptions to CSV.
	 * 
	 * @param inscriptions The list of inscription
	 * @return the string representation in CSV format
	 */
	public String serializeToCSV(List<Inscription> inscriptions) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER);
			for(Inscription i : inscriptions) {
				writeInscriptionToCSV(i, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	/**
	 * Serializes an inscription to CSV.
	 * 
	 * @param i The inscription
	 * @return the string representation in CSV format
	 */
	public String serializeToCSV(Inscription i) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER);
			writeInscriptionToCSV(i, csvFilePrinter);
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";	
	}

	/**
	 * Writes individual fields from an inscription to the CSV export.
	 * 
	 * @param i The inscription
	 * @param csvFilePrinter The file printer
	 * @throws IOException
	 */
	private void writeInscriptionToCSV(Inscription i, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> inscriptionRecord = new ArrayList<Object>();
		
		// fill in the fields
		inscriptionRecord.add(i.getAgp().getAgpId());
		inscriptionRecord.add(i.getAgp().getProperty().getInsula().getCity().getName());
		inscriptionRecord.add(i.getAgp().getProperty().getInsula().getCity().getPleiadesId());
		inscriptionRecord.add(i.getContent());
		inscriptionRecord.add(i.getEDRFindSpot());
		inscriptionRecord.add(i.getDateBeginning());
		inscriptionRecord.add(i.getDateEnd());
		inscriptionRecord.add(i.getAgp().getLanguageInEnglish());
		inscriptionRecord.add(i.getAgp().getWritingStyleInEnglish());
		
		// not adding these fields for now... might change in the future
		/**
		inscriptionRecord.add(i.getEdrId());
		inscriptionRecord.add(i.getBibliography());
		inscriptionRecord.add(i.getApparatus());
		inscriptionRecord.add(i.getAgp().getCaption());
		inscriptionRecord.add(i.getAgp().getCommentary());
		inscriptionRecord.add(i.getAgp().getContentTranslation());
		inscriptionRecord.add(i.getAgp().getWritingStyleInEnglish());
		inscriptionRecord.add(i.getAgp().getLanguageInEnglish());
		inscriptionRecord.add(i.getAgp().getGraffitoHeight());
		inscriptionRecord.add(i.getAgp().getGraffitoLength());
		inscriptionRecord.add(i.getAgp().getMinLetterHeight());
		inscriptionRecord.add(i.getAgp().getMaxLetterHeight());
		inscriptionRecord.add(i.getAgp().getMinLetterWithFlourishesHeight());
		inscriptionRecord.add(i.getAgp().getMaxLetterWithFlourishesHeight());
		inscriptionRecord.add(i.getAgp().getCil());
		inscriptionRecord.add(i.getAgp().getLangner());
		inscriptionRecord.add(i.getAgp().getProperty().getPropertyNumber());
		inscriptionRecord.add(i.getAgp().getProperty().getPropertyName());
		inscriptionRecord.add(i.getAgp().getProperty().getPleiadesId());
		inscriptionRecord.add(i.getAgp().getProperty().getItalianPropertyName());
		inscriptionRecord.add(i.getAgp().getProperty().getCommentary());
		inscriptionRecord.add(i.getAgp().getProperty().getInsula().getShortName());
		inscriptionRecord.add(i.getAgp().getProperty().getInsula().getFullName());
		inscriptionRecord.add(i.getAgp().getProperty().getInsula().getCity().getDescription());
		inscriptionRecord.add(Integer.toString(i.getId()));
		inscriptionRecord.add(i.getAncientCity());
		inscriptionRecord.add(i.getFindSpot());
		inscriptionRecord.add(Integer.toString(i.getFindSpotPropertyID()));
		inscriptionRecord.add(i.getMeasurements());
		inscriptionRecord.add(i.getLanguage());
		inscriptionRecord.add(i.getWritingStyle());
		inscriptionRecord.add(i.getApparatusDisplay());
		inscriptionRecord.add(Integer.toString(i.getNumberOfImages()));
		*/
		
		// write the inscription record
		csvFilePrinter.printRecord(inscriptionRecord);
	}
	
	/**
	 * Serializes a list of properties to CSV.
	 * 
	 * @param properties The list of properties
	 * @return the string representation in CSV format
	 */
	public String serializePropertiesToCSV(List<Property> properties) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_PROPERTY);
			for(Property p : properties) {
				writePropertyToCSV(p, csvFilePrinter);
			}
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	/**
	 * Serializes an property to CSV.
	 * 
	 * @param p The property
	 * @return the string representation in CSV format
	 */
	public String serializeToCSV(Property p) {
		
		StringBuilder stringBuilder = new StringBuilder();
		CSVPrinter csvFilePrinter = null;
		
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
		
		try {
			csvFilePrinter = new CSVPrinter(stringBuilder, csvFileFormat);
			csvFilePrinter.printRecord(CITATION);
			csvFilePrinter.printRecord(FILE_HEADER_PROPERTY);
			writePropertyToCSV(p, csvFilePrinter);
			csvFilePrinter.close();
			
			return stringBuilder.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return "";	
	}

	/**
	 * Writes individual fields from an property to the CSV export.
	 * 
	 * @param p The property
	 * @param csvFilePrinter The file printer
	 * @throws IOException
	 */
	private void writePropertyToCSV(Property p, CSVPrinter csvFilePrinter) throws IOException {
		
		List<Object> propertyRecord = new ArrayList<Object>();
		
		// fill in the fields
		propertyRecord.add(p.getInsula().getCity().getName());
		propertyRecord.add(p.getInsula().getShortName());
		propertyRecord.add(p.getPropertyNumber());
		propertyRecord.add(p.getPropertyName());
		propertyRecord.add(p.getPropertyTypesAsString());
		propertyRecord.add(p.getUri());
		
		// write the inscription record
		csvFilePrinter.printRecord(propertyRecord);
	}
	
	
	public GenerateCSV() {
		
	}

}

