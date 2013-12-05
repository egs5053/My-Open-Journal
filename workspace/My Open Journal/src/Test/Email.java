package Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFParse {
	
	public static String[] parsePDF(File file) {
		InputStream inStream = null;
		
		try {
			// Read in the file and load as a PDDocument
			inStream = new FileInputStream(file);		
			PDDocument pdf = PDDocument.load(inStream);
			
			
			int i;
			char c;
			String title = "";
	        
			
			// Build string to hold contents of pdf, and create string
			StringBuilder sb = new StringBuilder();        
	        PDFTextStripper stripper = new PDFTextStripper();		
	        sb.append(stripper.getText(pdf));        
	        String text = sb.toString();
	        
	        // Split text by newline character, so create array
	        //	with each element being a line of the pdf file
	        String[] tokens = text.split("\n");
	        
	        // The first and second lines should be title and
	        //	author, respectively
	        String theTitle = tokens[0];
	        String theAuthor = tokens[1];
	        
	        // Locate abstract
	        boolean abstractFound = false;
	        int counter = 2;
	        
	        // First test for presence of an "Abstract" headline
	        while (counter < tokens.length - 1) {
	        	if ((tokens[counter].contains("Abstract")) || (tokens[counter].contains("ABSTRACT"))){
	        		abstractFound = true;
	        		break;
	        	}
	        	counter++;
	        }        
	        
	        int startCounter = 0;
	        int endCounter = 0;
	        
	        // Abstract is typically followed by "Introduction" section, so 
	        //	if "Abstract" headline is found, continue to add the lines to
	        //	the abstract string from the headline until "Introduction" is
	        //	reached. If abstract is not denoted by "Abstract" headline, start
	        //	from the line following author until "Introduction" is reached.
	        if (abstractFound) {     
		        startCounter = counter;

		        while (counter < tokens.length - 1) {
		        	if (tokens[counter].contains("Introduction") || tokens[counter].contains("INTRODUCTION")) {
		        		break;
		        	}
		        	counter++;
		        }
		        endCounter = counter;
	        } else {
	        	int newCounter = 3;
	        	startCounter = 3;

	        	while (newCounter < tokens.length - 1) {
		        	if (tokens[newCounter].contains("Introduction") || tokens[newCounter].contains("INTRODUCTION")) {
		        		break;
		        	}
		        	newCounter++;
		        }
	        	endCounter = newCounter;
	        }
	        
	        // Declare abstract string and build it from the lines between
	        //	the two indices found above.
	        String abstractString = "";
	        
	        for (int j = startCounter; j < endCounter; j++) {
	        	abstractString = abstractString + tokens[j];
	        }
	        
	        int maxTitleSize = 100;
	        if (theTitle.length() >= maxTitleSize) {
	        	abstractString = abstractString.substring(0, maxTitleSize - 1);	        	
	        }
	        
	        
	        // Ensure that the abstract is not too large for db
	        int maxAbstractSize = 8000;
	        if (abstractString.length() >= maxAbstractSize) {
	        	abstractString = abstractString.substring(0, maxAbstractSize - 1);	        	
	        }
	        
	        
	        String[] returnString = new String[3];
	        returnString[0] = theTitle;
	        returnString[1] = theAuthor;
	        returnString[2] = abstractString;
	        
	        pdf.close();
	        return returnString;
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inStream != null) {
	            try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}