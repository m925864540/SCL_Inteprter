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

import java.util.ArrayList;

public class Parser {
	
	static ArrayList<String> newTokens = new ArrayList<String>();
	static ArrayList<String> displaySetIdents = new ArrayList<>();
	public static boolean compOpBool = false;
	public static boolean bitshiftBool = false;
	
	static void getTokens(ArrayList<String> tokens)
	{
		newTokens = tokens;
	}
	
	// Calls all rules in the order defined by the grammar
	static void startParse()
	{
		imports();
		symbols();
		globals();
		implementations();	
	}
	
	/*
	 * The import function validate if the current statement passed in matches with the corresponding rule and checks.
	 */
	static void imports()
	{
		
		while(LexicalAnalyzer.getSentence().size()==2)
		{
			// The rules
			if(LexicalAnalyzer.getSentence().get(0).equals("import") && newTokens.get(1).equals("string_literal")) {
				LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
				LexicalAnalyzer.printValidSentence();
				System.out.println();
				/*
				 *  Always call the first two functions at the start of the rules
				 *  It updates the the newTokens arrayList, and
				 *  prints out the next token and next lexemes for this current statement.
				 */
				LexicalAnalyzer.getNextLine();
				LexicalAnalyzer.updateTokens();
			}
			else
			{
				LexicalAnalyzer.printBadSentence(" string literal not found");
				System.exit(1);
			}
					
		}
		if (LexicalAnalyzer.getSentence().get(0).equals("import"))
		{
			LexicalAnalyzer.printBadSentence(" Not a valid import statement length");
			System.exit(1);
		}
		
			
	}
	
	/*
	 * Similar to the import functions, the symbols function will checks if the current statement passes the checks.
	 */
	static void symbols() {
		
		while (LexicalAnalyzer.getSentence().size()==3 && newTokens.get(0).equals("keyword") 
				&& LexicalAnalyzer.getSentence().get(0).equals("symbol"))
		{
			if(newTokens.get(1).equals("identifier") && (newTokens.get(2).equals("hex_value") 
					|| newTokens.get(2).equals("integer_value"))){
				LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
				LexicalAnalyzer.printValidSentence();
				System.out.println("-------------------------------------------------");
				Executor.execute(LexicalAnalyzer.getSentence());
				System.out.println();
				LexicalAnalyzer.getNextLine();
				LexicalAnalyzer.updateTokens();
			}else if(!newTokens.get(1).equals("identifier")) {
				LexicalAnalyzer.printBadSentence(" No Identifier not recognized");
				System.exit(1);
			}
			else
			{
				LexicalAnalyzer.printBadSentence("Hex or integer value not recognized"); 
				System.exit(1); 
			}
		} 
		
	}
	
	/*
	 * variableRule will be called inside the globals function if the checks is true. Otherwise, this function will not be called.
	 * This function checks that the next statement after "global declarations" should be "variable".
	 */
	private static void variableRule() {
		
		//  It update the the newTokens arrayList, and
		//  prints out the next token and next lexemes for this current statement.
		LexicalAnalyzer.updateTokens();
		LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
		
		// variable rule
		if(LexicalAnalyzer.getSentence().size()==1 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("variables")) {
			LexicalAnalyzer.printValidSentence();
			System.out.println();
			LexicalAnalyzer.getNextLine();
			while (LexicalAnalyzer.getSentence().get(0).equals("define"))
			{
				defineRule();
				System.out.println("-------------------------------------------------");
				Executor.execute(LexicalAnalyzer.getSentence());
				System.out.println();
				LexicalAnalyzer.getNextLine();
			}
		}
		else
		{
			LexicalAnalyzer.printBadSentence(" Variables keyword not recognized");
			System.exit(1);
		}
	}
	
	/*
	 * defineRule will be called inside variableRule if the checks is validated. Otherwise, it will not be called.
	 * Based on the current statement, three conditional statement will be check, once validate to true,
	 * defineRuleDataType function will be called to handle the validating of the statement.
	 */
	private static void defineRule() {
		
		// It updates the newTokens arrayList, and
		// prints out the next token and next lexemes for this current statement.
		LexicalAnalyzer.updateTokens();
		LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
		
		int sentenceSize = LexicalAnalyzer.getSentence().size();
		
		// defineRuleDataType rules.
		// define identifier ...
		if(sentenceSize==5 && newTokens.get(1).equals("identifier")) {
			
				defineRuleDataType(); // Called to validate the statement.
				return;
		}
		//define identifier...
		else if(sentenceSize==6 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("define") && newTokens.get(1).equals("identifier")) {
				defineRuleDataType(); // Called to validate the statement.
				return;
		}
		//define identifier = (integer_value / hex_value) ...
		else if(sentenceSize==7 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("define") && newTokens.get(1).equals("identifier")
			&& newTokens.get(2).equals("assignment op")) {
				if(newTokens.get(3).equals("integer_value") || newTokens.get(3).equals("hex_value")) {
					
					defineRuleDataType(); // Called to validate the statement.
					return;
				}
				else
				{
					LexicalAnalyzer.printBadSentence(" No integer or hex token type recognized");
					System.exit(1);
				}
		}
		else
		{
			LexicalAnalyzer.printBadSentence(" define statement not recognized");
			System.exit(1);
		}
	}
	
	/*
	 * defineRuleDataType will be called inside the defineRule() function with either one of the three if conditional statement validate to true.
	 * This rule define two type of array based on our subset of SCL grammar.
	 * Three switch cases will will handle the three different situation based on the length of the current statement, and validate it with the type array,
	 * once true 
	 */
	private static void defineRuleDataType() {
		
		//Based on our SCL grammar, either type1 or type2 will always be followed by:
		//	DEFINE IDENTIFIER	...
		//	DEFINE IDENTIFIER = (INTEGER / HEX_VALUE)	...
		//Therefore, type1 and type2 is created to validate what is being followed after.
		String[] type1= new String[] {"of", "type"};
		String[] type2= new String[] {"of", "type", "unsigned"};
		int j=0;
		boolean re=false;
		String last;
		int sentenceSize=LexicalAnalyzer.getSentence().size();// get the size of the current statement.
		
		switch(sentenceSize) {
			// Ex: define [identifier] of type [type]
			case 5:
				for(int i=2; i<sentenceSize-1;i++) { //check with type1 till the last element -1.
					if(LexicalAnalyzer.getSentence().get(i).equals(type1[j++])) {
						re=true;
					}
					else
						re = false;
				}
				last=LexicalAnalyzer.getSentence().get(4); // store the last lexemes of the current statement.
				
				// check value of re, and if the last lexemes equals any of the 6 terminal based on our grammar.
				if(re && (last.equals("integer")|| last.equals("short")|| last.equals("long") || last.equals("byte") 
						|| last.equals("hex_value") || last.equals("float")|| last.equals("double"))) {
					LexicalAnalyzer.printValidSentence();
					break;
				}else{
					LexicalAnalyzer.printBadSentence("Wrong type found.");
					System.exit(1);
				}
			// Ex: define [identifier] of type unsigned [type]
			case 6:
				for(int i=2; i<sentenceSize-1;i++) {
					if(LexicalAnalyzer.getSentence().get(i).equals(type2[j++])) {
						re=true;
					}
					else
						re = false;
				}
				last=LexicalAnalyzer.getSentence().get(5); // store the last lexemes of the current statement.
				
				// check value of re, and if the last lexemes equals any of the 5 terminal based on our grammar.
				if(re && (last.equals("integer")|| last.equals("short")|| last.equals("long") 
						|| last.equals("byte") || last.equals("hex_value"))) {
					LexicalAnalyzer.printValidSentence();
					break;
				}else{
					LexicalAnalyzer.printBadSentence("Wrong type found.");
					System.exit(1);
				}
			// Ex: define [identifier] = [literal/identifier] of type [type]
			case 7:
				// define 	A0 			= 			   0 		of 		 type 	  byte
				for(int i=4; i<sentenceSize-1;i++) {
					if(LexicalAnalyzer.getSentence().get(i).equals(type1[j++])) {
						re=true;
					}
					else
						re = false;
				}
				last=LexicalAnalyzer.getSentence().get(6); // store the last lexemes of the current statement.
				
				// check value of re, and if the last lexemes equals any of the 5 terminal based on our grammar.
				if(re && (last.equals("integer")|| last.equals("short")|| last.equals("long") || last.equals("byte") || last.equals("hex_value"))) {
					LexicalAnalyzer.printValidSentence();
					break;
				}else{
					LexicalAnalyzer.printBadSentence("Wrong type found.");
					System.exit(1);
				}
			default:
				System.out.println("Wrong sentence length.");
				System.exit(1);
		}
	}
	
	/*
	 * The globals function will checks if the current statement matches the rules written,
	 * if success, valid statement will be printed and another function called variableRule() will be called
	 * to validate the following statement.
	 */
	static void globals() {
		
		//global declarations
		if(LexicalAnalyzer.getSentence().size()==2 && newTokens.get(0).equals("keyword") 
				&& LexicalAnalyzer.getSentence().get(0).equals("global")) {
			if(newTokens.get(1).equals("keyword") && LexicalAnalyzer.getSentence().get(1).equals("declarations")) {
				LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
				LexicalAnalyzer.printValidSentence();
				System.out.println();
				LexicalAnalyzer.getNextLine();
				variableRule();
				return;
			}else if(!LexicalAnalyzer.getSentence().get(1).equals("declarations")) {
				LexicalAnalyzer.printBadSentence("Keyword of declarations not found");
				System.exit(1);
			}
		}
		
	}
	
	/*
     * The implementations function will check that the line contains one word that is 'implementations'.
     * If successful, a validation statement is printed and the next line to evaluate is retrieved.
     * Otherwise, the 'implementation' word is not found printing an error message
     */
	static void implementations()
	{
		//Reset the lexemes array list from LexicalAnalyzer and add new tokens. Print the sentence.
		LexicalAnalyzer.updateTokens();
		LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
		
		// implementation keyword
		if(LexicalAnalyzer.getSentence().size()==1 && newTokens.get(0).equals("keyword") 
				&& LexicalAnalyzer.getSentence().get(0).equals("implementations")) {
				LexicalAnalyzer.printValidSentence();
				System.out.println();
				LexicalAnalyzer.getNextLine();
				functionRule();
				return;
		}
		LexicalAnalyzer.printBadSentence("No implementations keyword found");
		System.exit(1);
	}
	
	/*
     * The FunctionRule function checks if the line contains 'function' in the first position, followed by
     * 'main' in the second position. Otherwise, the outer else statement catches the error if the first word
     * is not 'function' and the inner else statement catches an error if the second word is not 'main'.
     *
     * The second part of the program will execute only after calling and returning from both variableRule and
     * beginRule functions. Then functionRule will get the next line to find 'endfun main'. If 'endfun main' is found,
     * this indicates that the body of 'function main' has been exited in the SCL file.
     */
	static void functionRule()
	{
		LexicalAnalyzer.updateTokens();
		LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
		
		// function main check
		if(LexicalAnalyzer.getSentence().size()==3 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("function")) {
			if (newTokens.get(1).equals("keyword") && LexicalAnalyzer.getSentence().get(1).equals("main")  && newTokens.get(2).equals("keyword") &&
					LexicalAnalyzer.getSentence().get(2).equals("is") )
			{
				LexicalAnalyzer.printValidSentence();
				System.out.println();
				LexicalAnalyzer.getNextLine();
				variableRule();
				beginRule();
			}
			else
			{
				LexicalAnalyzer.printBadSentence("Keyword main or is not found");
				System.exit(1);
			}
			
			//Get the next line to evaluate if the new line contains 'endfun main' after reaching the 'exit' keyword in the program
			LexicalAnalyzer.getNextLine();
			
			//Reset the lexemes array list from LexicalAnalyzer and add new tokens. Print the sentence.
			LexicalAnalyzer.updateTokens();
			LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence());
			
			if (LexicalAnalyzer.getSentence().size()==2)
			{
				if(newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("endfun") && 
						newTokens.get(1).equals("keyword") && LexicalAnalyzer.getSentence().get(1).equals("main"))
				{
					LexicalAnalyzer.printValidSentence();
				}
				else
				{
					LexicalAnalyzer.printBadSentence("Keyword endfun or main was not found");
					System.exit(1);
				}
			}
			else
			{
				LexicalAnalyzer.printBadSentence("Only keywords endfun and main were expected"); 
				System.exit(1); 
			}
			System.out.println();
		}
		else
		{
			LexicalAnalyzer.printBadSentence("Function expected as the first keyword"); 
			System.exit(1);
		}

	}
	
	/*
     * The beginRule function checks if the line contains 'begin' in the first position, and that the sentence is
     * only word long. If this is true, the pactionsRule will be called to evaluate the subsequent lines contain
     * either 'set' or 'display' in the first positions
     *
     * The second part of the beginRule function is executed after returning from the pactionsRule function.
     * If this part of the function is reached, it means the pactions function could not find subsequent lines
     * with either 'set' or 'display' in the first positions. This indicates the body of 'begin' has been exited
     * in the SCL file.
     */
	static void beginRule()
	{
		LexicalAnalyzer.updateTokens(); 
        LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence()); 
 
        //The line to evaluate should only be one lexeme long with the word 'begin'
        if(LexicalAnalyzer.getSentence().size()==1 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("begin")) { 
            LexicalAnalyzer.printValidSentence(); 
            System.out.println(); 
            LexicalAnalyzer.getNextLine(); 
            pactionsRule(); 
        }
        else
        {
        	LexicalAnalyzer.printBadSentence("Keyword begin not found");
        	System.exit(1);
        }
        
      //The new line should only be one lexeme long with only the word 'exit'.
        if (LexicalAnalyzer.getSentence().size() == 1 && newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("exit"))
        	LexicalAnalyzer.printValidSentence();
        else
        {
        	LexicalAnalyzer.printBadSentence("Keyword exit not found");
        	System.exit(1);
        }
        
        System.out.println();
	}
	
	/*
     * The pactions function will evaluate the subsequent lines to find either 'set' or 'display' in the first
     * positions of the new lines.
     *
     * The pactions function will continuously do these keyword checks until the keyword 'exit' is reached in
     * the SCL file. This indicated to the function that there is nothing left to do and pactions leaves the
     * stack and the program returns to the beginRule function from which pactions was initially called.
     */
	static void pactionsRule()
	{
		LexicalAnalyzer.updateTokens(); 
        LexicalAnalyzer.printLexemes(LexicalAnalyzer.getSentence()); 
 
     	//If the new line contains 'set', call the set rule function.
        if(newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("set")) { 
 
            setRule();
          //If the new line contains 'display', call the display rule function.
        } else if(newTokens.get(0).equals("keyword") && LexicalAnalyzer.getSentence().get(0).equals("display")) { 
            displayRule();
            
            //If the keyword is not 'set', 'display', or 'exit', there is an error
        } else if(!LexicalAnalyzer.getSentence().get(0).equals("exit")) { 
        	 
        	LexicalAnalyzer.printBadSentence("Keyword must be either Set, Display, or Exit"); 
        	System.exit(1); 
        	 
        } 

	}
	
	/*
     * The set rule function will evaluate the LHS side of the set assignment statement in the sentence. It checks that
     * the second and third token is an identifier and assignment operator in this order. If this is evaluated to
     * be true, call the expression rule function to evaluate the RHS of the assignment statement.
     *
     * When the program returns from the expression rule function, this indicates the RHS is validated and setRule gets
     * the new line and calls pactions to evaluate the new line.
     */
	static void setRule()
	{
		//LHS of the assignment statement is 'set identifier'. Then check that the next token is an assignment operator.
        if(newTokens.get(1).equals("identifier") && newTokens.get(2).equals("assignment op")) {
 
        	//Add the identifier to an array list so that the display function can validate the identifier tokens in display statements
        	displaySetIdents.add(newTokens.get(1));
        	exprRule(); 
        	 
            LexicalAnalyzer.printValidSentence();
            System.out.println("-------------------------------------------------");
            Executor.execute(LexicalAnalyzer.getSentence());
            
            System.out.println(); 
            LexicalAnalyzer.getNextLine(); 
            pactionsRule();
        }
        else
        {
        	LexicalAnalyzer.printBadSentence("Keyword set must be followed by an identifier and an assignment operator");
        	System.exit(1);
        }
 
	}
	
	/*
     * The display rule function will evaluate if a sentence displaying identifiers, commas, and string literals
     * matches a legal sequence or order. Either the order of identifier, comma, and string literal space is valid, or
     * string literal (of identifier), comma, and identifier order is valid. Otherwise, the display statement is not valid and an
     * error message is printed.
     *
     * The display functions will also validate that only a string literal can be displayed, which is valid by the SCL grammar.
     * If display statements are validated, displayRule will get the next line of the SCL file and call pactions to evaluate
     * the new line.
     */
	static void displayRule()
	{
 
		//Checks for the particular sequence of 'identifier', 'comma', and 'string literal space'
		if(LexicalAnalyzer.getSentence().size() > 2 && newTokens.get(1).equals("identifier") 
				&& newTokens.get(2).equals("comma") && newTokens.get(3).equals("string_literal")) { 
				 
				int sentenceSize = LexicalAnalyzer.getSentence().size(); 
				int count = 0; 
				 
				//The identifier must be the second token in the sentence, and at every four positions in the sentence
	            // when multiple identifiers are displayed
				for(int i = 1; i < sentenceSize; i += 4) {
					
					//Validate identifier tokens in display statements with identifiers in the set assignment statements
					if(!newTokens.get(i).equals(displaySetIdents.get(count))) {
						
					displaySetIdents.clear(); 
					LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
					System.exit(1);
					
					} 
					 
					count++; 
					 
				} 
				 
				displaySetIdents.clear(); 
				 
				//The comma must be the third token in the sentence, and at every two positions in the sentence
	            // when multiple commas are displayed
				for(int i = 2; i < sentenceSize; i += 2) { 
					
					if(!newTokens.get(i).equals("comma")) {
						
					LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
					System.exit(1);
					
					} 
				 
				}
				
				//The string literal space must be the fourth token in the sentence, and at every four positions in the sentence
	            // when multiple spaces are displayed
				for(int i = 3; i < sentenceSize; i += 4) { 
					 
						if(!newTokens.get(i).equals("string_literal")) { 
						LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
						System.exit(1); 
						} 
					 
					} 
					 
				}
		
				//Checks for the particular sequence of 'string literal of identifier', 'comma', and 'identifier'
				else if(LexicalAnalyzer.getSentence().size() > 2 && newTokens.get(1).equals("string_literal") 
					&& newTokens.get(2).equals("comma") && newTokens.get(3).equals("identifier")) { 
					 
					int sentenceSize = LexicalAnalyzer.getSentence().size(); 
					int count = 0; 
					 
					//String literal position
					for(int i = 1; i < sentenceSize; i += 4) { 
						
						if(!newTokens.get(i).equals("string_literal")) { 
						LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
						System.exit(1); 
						} 
					 
					} 
					 
					//Comma position
					for(int i = 2; i < sentenceSize; i += 2) { 
					 
						if(!newTokens.get(i).equals("comma")) { 
						LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
						System.exit(1); 
						} 
					 
					} 

					//identifier position
					for(int i = 3; i < sentenceSize; i += 4) { 
						 
						if(!newTokens.get(i).equals(displaySetIdents.get(count))) { 
						displaySetIdents.clear(); 
						LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
						System.exit(1); 
						} 
						count++; 
						 
					} 
					displaySetIdents.clear(); 
						 
				} 
				//Checks when the display statement is only two lexemes long with the second lexeme token type being a string literal
				else if(LexicalAnalyzer.getSentence().size() == 2 && newTokens.get(1).equals("string_literal")) { 
					
					System.out.println(); 
				} 
					 
				else {
					
					LexicalAnalyzer.printBadSentence("Invalid Display statement"); 
					System.exit(1); 
				} 
					 
				LexicalAnalyzer.printValidSentence();
				System.out.println("-------------------------------------------------");
				Executor.execute(LexicalAnalyzer.getSentence());
				System.out.println(); 
				 
				LexicalAnalyzer.getNextLine(); 
				 
				pactionsRule(); 
				 
	} 
						 		
	/*
     * The expression rule is called by the setRule function to evaluate the RHS of the equal operator in the set
     * assignment statement. Then call the term rule function to evaluate a line that contains either a comparison
     * operator or bitshift operator.
     */
	static void exprRule()
	{
		//If the assignment statement contains a comparison operator or bitshift operator, set a boolean variable to be
        //true so that the term rule function can evaluate the line with the particular operator.
	
		if(newTokens.contains("comparison op")) { 
            compOpBool = true; 
        }
		else if (newTokens.contains("bitshift op")) { 
			bitshiftBool = true; 
		} 
		
		//If the RHS side of the set assignment statement only contains one token and this token is an element (using
        //the elementBool function to validate the element type), exprRule is called off the stack and both LHS and RHS
        //of the statement is validated.
		else if(elementBool(newTokens.get(3)) && LexicalAnalyzer.getSentence().size() == 4) 
        	return;
		else {
			LexicalAnalyzer.printBadSentence("RHS must contain either a bitshift operator, comparison operator, or single identifier"); 
			System.exit(1); 
		} 

        termRule(); 

	}
	
	/*
     * The term rule function uses boolean variables to determine if a set identifier statement should be evaluated
     * depending on whether the statement contains a comparison operator or bitshift operator.
     */
	static void termRule()
	{
		 //If the statement contains a comparison operator, call the element rule function to evaluate the RHS of that statement
		 if(compOpBool) { 
	            elementRule(); 
	        } 
		 //If the statement contains a bitshift operator, evaluate the RHS of the statement in this function.
	     if(bitshiftBool) { 
	 
	            int position = newTokens.indexOf("bitshift op");
	            //Offset of 1 on either side of the operator should be an element	
	            if(elementBool(newTokens.get(position - 1)) && elementBool(newTokens.get(position + 1))) { 
	 
	            	//Offset of 2 on either side of the operator should be left and right parentheses, or just a
	                //left offset position of two should have the assignment operator if there are no enclosed parentheses.
	                if(newTokens.get(position - 2).equals("lparenth") && newTokens.get(position + 2).equals("rparenth") || 
	                        newTokens.get(position - 2).equals("assignment op")) { 
	                	return;
	                }
	                else
	                {
	                	LexicalAnalyzer.printBadSentence("RHS of assignment not recognized");
	                	System.exit(1);
	                }
	 
	            }
	            else
	            {
	            	LexicalAnalyzer.printBadSentence("Elements not found");
	            	System.exit(1);
	            }
	        }
	}
	
	/*
     * The element rule function evaluates and validates the RHS of the set assignment statement only if the statement
     * contains a comparison operator. If the if or else-if statements are true, the element rule function is called off
     * the stack and both the LHS and RHS of the set identifier assignment statement is valid.
     */
	static void elementRule()
	{
		//If the statement has a comparison operator in the RHS, the operator must have an element on either side of it
        if (newTokens.get(4).equals("comparison op") && elementBool(newTokens.get(3)) && elementBool(newTokens.get(5))) {
        	
        	//set both boolean variables to be false so that the functions, elementRule, termRule, and by extension,
            //exprRule can be called off the stack and the program returns the the setRule function. The setRule function
            //now has the validated RHS of the statement and prints a validation statement for the entire line.
        	compOpBool = false;
        	bitshiftBool = false;
        } 
        //Or the RHS of the statement can contain the negate operator followed by an element.
        else if (LexicalAnalyzer.getSentence().get(3).equals("negate") && elementBool(newTokens.get(4))) {
        	compOpBool = false;
        	bitshiftBool = false;
        }
        else { 
            LexicalAnalyzer.printBadSentence("RHS of assignment not recognized");
            System.exit(1);
        } 

	}
	
	 /*
     * The element boolean function is helper function to return a valid element type. Using the rule specified in the
     * subset of the SCL grammar, the element can be replaced with an identifier, hex value, integer value, double value,
     * or function call.
     */
	static boolean elementBool(String s) { 

        return s.contains("identifier") || s.contains("hex_value") || s.contains("integer_value")
        		|| s.contains("double") || s.contains("func_call"); 
 
    } 

	

}
