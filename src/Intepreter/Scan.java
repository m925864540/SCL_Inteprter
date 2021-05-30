/*
 * CS: 4308: Concepts of Programming Languages
 * Section W01; Spring 2021 
 * Project Deliverable 2 
 * Developers:
 * Brandon Tedeschi
 * Mike Lin
 * Nick Tibbetts
 * 
 */

package interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Scan {
	
	// Protected
	protected String localDir;
	protected File file;
	protected BufferedReader br;
	
	private static final int EOF = -1; // Token value for EOF
	
	// This function sets up the IO for input from the scl file
	// This is needed at program start-up
	final void initScanner()
	{
		
		try
		{
			localDir = System.getProperty("user.dir");
			file = new File(localDir + "//src//bitops1.scl"); 
			br = new BufferedReader(new FileReader(file));
			
		} 
		catch(Exception e) 
		{ 
			e.printStackTrace();
		}
	}
	
	// Main function called on by the LexicalAnalyzer and was in the Driver class before
	protected ArrayList<String> scanNextLine()
	{
		String lex = ""; // Used to determine the lexeme that will go in lexemes[]
		ArrayList<String> lexemes = new ArrayList<String>();
		char nextChar;
		
		try
		{
			String currentLine;
			if ((currentLine = br.readLine()) == null)
			{
				lexemes.add(String.valueOf(EOF));
				br.close();
				return lexemes;
			}
			
  		  // Handles comment blocks
  		  if (currentLine.contains("description"))
  		  {
      		
  			  while ((currentLine = br.readLine()) != null)
  			  {
  				  if (currentLine.contains("*/"))
  					  break;
  			  }
  			 return scanNextLine();
  		  }
      	
  		  // Format line before looking at each character
  		  currentLine = currentLine.replaceAll("\\s+", " ");
  		  currentLine = currentLine.trim();
      	
  		  // Skips over lines that start with a line comment
  		  if (currentLine.length() >= 2)
  			  if (currentLine.charAt(0) == '/' && currentLine.charAt(1) == '/')
  				return scanNextLine();
      	
  		  // Loops through the formatted line to get each lexeme into the lexemes array
  		  for(int i = 0; i < currentLine.length(); i++) 
  		  { 
      	   
              nextChar = currentLine.charAt(i);
              
              //Checks line comments and if there is one then go to the next line
              if (lex.contains("//"))
              {
            	  if (!lexemes.isEmpty())
            	  {
            		  return lexemes;
            	  }
            	  lex = "";
            	  break;
              }
              
              // The three if-elif statements handle commas and ()
              if (nextChar == ',')
              {
            	  lexemes.add(lex);
            	  lexemes.add(Character.toString(nextChar));
            	  lex = "";
            	  continue;
              }
            	
              if (nextChar == '(')
              {
            	  if (!lex.isEmpty())
            	  {
            		  lex += nextChar;
            		  continue;
            	  }
            	  lexemes.add(Character.toString(nextChar));
            	  continue;
              }
              else if (nextChar == ')')
              {
            	  if (lex.contains("("))
            	  {
            		  lex += nextChar;
            		  lexemes.add(lex);
            		  return lexemes;
            	  }
            		  
            	  lexemes.add(lex);
            	  lexemes.add(Character.toString(nextChar));
            	  return lexemes;
              }
              
              // Figures out if there is a string
              if (nextChar == '"')
              {
            	  do 
            	  {
            		  lex += currentLine.charAt(i);
            		 
            	  }
            	  while (currentLine.charAt(++i) != '"'); 
 
                	  lex += currentLine.charAt(i);
                	  continue;
                	  
              }
              
	              // Recognize the space that separates the lexemes on a line
	            if (Character.isWhitespace(nextChar))
	            {
	          	  if (lex.isEmpty()) // This handles empty string literals
	          		  continue;
	          	  lexemes.add(lex);
	          	  lex = "";
	          	  continue;
	            }
	
	      	  lex += nextChar;     	  
	            	  
  		  }
  		  
  		// Makes sure the last lexeme on a line is added and then prints all lexemes in a line
  		  if (!lex.isEmpty())
  		  {
        	  	lexemes.add(lex);
        	  	return lexemes;
        	  	
  		  }
  		  
  		  if (lexemes.isEmpty())
  			  return scanNextLine();
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return lexemes;
	}
	
	

}
