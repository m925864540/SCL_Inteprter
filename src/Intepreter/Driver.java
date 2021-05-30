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

public class Driver { 
	 
	final static LexicalAnalyzer la = new LexicalAnalyzer(); // Initializes the hashmaps
	public static void main(String[] args) { 
 	
		LexicalAnalyzer.createScanner();
		LexicalAnalyzer.getNextLine(); // Gets the first line
		LexicalAnalyzer.updateTokens(); // Update the token ArrayList with new tokens
 		Parser.startParse(); // Begins parsing the input file
		System.out.println("Next token is: EOF, Next lexeme is: -1"); // Output when the end of the file is reached
   } 
} 
