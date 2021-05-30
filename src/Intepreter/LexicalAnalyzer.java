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

import java.util.*;
import java.util.regex.Pattern;

public class LexicalAnalyzer { 

	static protected Scan scanner = new Scan();
	static protected ArrayList<String> lexemes = new ArrayList<String>(); // Strings passed to lexical analyzer
	static protected ArrayList<String> tokens = new ArrayList<String>();
	
	static protected Map<String,String> keyWord= new HashMap<>(); 
	static protected Map<String,String> identifiers= new HashMap<>();
	static protected Map<String,String> operation= new HashMap<>();
	// Regex for finding digits in our lexemes.
	private static Pattern pattern = Pattern.compile("-?\\d+");
	
	// Constructor that fills HashMaps
	public LexicalAnalyzer() { 
		
		keyWord.put("import", "keyword"); 
		keyWord.put("description", "keyword"); 
		keyWord.put("symbol", "keyword"); 
		keyWord.put("global", "keyword"); 
		keyWord.put("declarations", "keyword"); 
		keyWord.put("variables", "keyword"); 
		keyWord.put("define", "keyword"); 
		keyWord.put("of", "keyword"); 
		keyWord.put("type", "keyword"); 
		keyWord.put("unsigned", "keyword"); 
		keyWord.put("integer", "keyword"); 
		keyWord.put("short", "keyword"); 
		keyWord.put("long", "keyword"); 
		keyWord.put("byte", "keyword"); 
		keyWord.put("double", "keyword");
		keyWord.put("implementations", "keyword"); 
		keyWord.put("function", "keyword"); 
		keyWord.put("main", "keyword"); 
		keyWord.put("is", "keyword"); 
		keyWord.put("begin", "keyword"); 
		keyWord.put("set", "keyword"); 
		keyWord.put("display", "keyword"); 
		keyWord.put("exit", "keyword"); 
		keyWord.put("endfun", "keyword"); 

		operation.put("=", "assignment op"); 
		operation.put("band", "comparison op"); 
		operation.put("bor", "comparison op"); 
		operation.put("bxor", "comparison op"); 
		operation.put("negate", "comparison op");
		operation.put("lshift", "bitshift op");
		operation.put("rshift", "bitshift op");
		operation.put("(", "lparenth");
		operation.put(")", "rparenth");
		
	}
	
	// Called on by driver at program start-up 
	public static void createScanner()
	{
		scanner.initScanner();
	}
	// Main utility function called on by the parser
	public static void getNextLine()
	{
		lexemes = scanner.scanNextLine();

	}
	
	public static ArrayList<String> getSentence()
	{
		return lexemes;
	}
	
	public static void updateTokens()
	{
		tokens.clear();
		for (String s: lexemes)
		{
			tokens.add(lookup(s));
		}
		Parser.getTokens(tokens);
	}

	/*
	 * Goes through all lexemes in a given line
	 * Looks up the token type from the hashmaps/literal checks
	 * Prints all tokens and lexemes
	 */
	public static void printLexemes(ArrayList<String> lexemes)
	{
		for (int i = 0; i < lexemes.size(); i++)
			System.out.printf("Next token is: %s, Next lexeme is: %s\n", tokens.get(i), lexemes.get(i));
	}
	
	// Prints and formats the sentence when it is printed
	public static void printValidSentence()
	{
		String result = "";
		int rparath = -1; 
		if (lexemes.contains(")"))
			rparath = lexemes.size() - 2;
			
		for (int i = 0; i < lexemes.size(); i++)
		{
			// Takes care of not adding spaces before and after a parenthesis
			if (lexemes.get(i).equals("(") || i == rparath)
			{
				result += lexemes.get(i);
				continue;
			}
			
			result += lexemes.get(i) + ' ';
		}
		System.out.println(result + "| Statement is valid");
	}
	
	// When there is not a valid statement
	public static void printBadSentence(String errMsg)
	{
		String result = "";
		for (String lex : lexemes)
		{
			result += lex + ' ';
		}
		System.out.println(result + "| Statement is invalid: " + errMsg);
	}
	
	// Used to find the token type for a given lexeme
	public static String lookup(String keyword)
	{
		
		if (keyWord.containsKey(keyword))
		{
			return keyWord.get(keyword);
		}
		else if (operation.containsKey(keyword)) 
		{
			return operation.get(keyword);
		}
		else // This handles symbols and literals
		{
			if (keyword.contains("\""))
				return "string_literal";
			else if (keyword.contains(","))
				return "comma";
			else if (keyword.charAt(keyword.length() - 1) == 'h')
				return "hex_value";
			else if (pattern.matcher(keyword).matches())
				return "integer_value";
			else if (keyword.contains("."))
				return "double_value";
			else if (keyword.contains("(") && keyword.contains(")"))
				return "func_call";
			
			identifiers.put(keyword, "identifier");
			return "identifier";
		}
			
		
	}

} 
