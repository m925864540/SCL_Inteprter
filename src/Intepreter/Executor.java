/*
 * CS: 4308: Concepts of Programming Languages
 * Section W01; Spring 2021 
 * Project Deliverable 3 
 * Developers:
 * Brandon Tedeschi
 * Mike Lin
 * Nick Tibbetts
 * 
 */

package interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class Executor {
	
	static protected Map<String,Identifier> identifiers = new HashMap<>();
	
	public Executor()
	{
	}
	
	/*
	 * The statement has been verified by the parser prior to this step, so every statement passed to Executor will be a valid statement.
	 * A switch statement is implemented that will have the 4 different cases that we will have to perform operation on. The first lexemes of the
	 * statement is examined, and based on that, the corresponding cases will be executed.
	 */
	public static void execute(ArrayList<String> lexemes)
	{
		switch (lexemes.get(0))
		{
		case "symbol":
			symbol(lexemes);
			break;
		case "define":
			define(lexemes);
			break;
		case "set":
			set(lexemes);
			break;
		case "display":
			display(lexemes);
			break;
		default:
			System.out.println("Error");
			break;
			
		}
	}
	
	/*
	 * The statement has been verified by the parser prior to this step, so every statement passed to Executor will be a valid statement.
	 * A switch statement is implemented that will have the 4 different cases that we will have to perform operation on. The first lexemes of the
	 * statement is examined, and based on that, the corresponding cases will be executed.
	 */
	private static void symbol(ArrayList<String> validStatement)
	{
		if (validStatement.get(2).contains("h"))
			identifiers.put(validStatement.get(1), new Identifier(validStatement.get(1), "hex_value", validStatement.get(2)));
		else
			identifiers.put(validStatement.get(1), new Identifier(validStatement.get(1), "integer", validStatement.get(2)));
		
		System.out.println("Variable " + validStatement.get(1) + " was stored.");
	}
	
	/*
	 * Similar to the symbol function above, this define function handles the operation regarding statement that begin with the word define.
	 * Based on our subset of SCl grammar, there will be three variation for the define statement, which will be handle by the if-else statement.
	 * The condition statement check the lexeme that is second from last on this current statement, and perform the operation based on it.
	 */
	private static void define(ArrayList<String> validStatement)
	{
		if(validStatement.get(validStatement.size()-2).equals("unsigned")) {	//Example: define a of type unsigned integer
			String type = validStatement.get(validStatement.size()-2) +" "+ validStatement.get(validStatement.size()-1);
			identifiers.put(validStatement.get(1), new Identifier(validStatement.get(1), type, "null"));
		}else if(validStatement.get(validStatement.size()-2).equals("type")) {	//Example: define vara of type byte
			String type = validStatement.get(validStatement.size()-1);
			identifiers.put(validStatement.get(1), new Identifier(validStatement.get(1), type, "null"));
		}
		else {
			String type = validStatement.get(validStatement.size()-1);
			identifiers.put(validStatement.get(1), new Identifier(validStatement.get(1), type, validStatement.get(3)));
		}
		
		System.out.println("Variable " + validStatement.get(1) + " was stored.");
	}
	
	
	// Checks if a string is just digits through a regex
	private static boolean isNumeric(String string) {
	    String regex = "[0-9]+[\\.]?[0-9]*";
	    return Pattern.matches(regex, string);
	}
	
	
	/*
	 * The set function is for all set statements in the code. It handles all types of statements that are valid in the grammar. 
	 * This includes all bitwise expressions and math operations supported. The method simply checks which variation it is dealing with 
	 * before running that particular statement. The goal of the method is to save the value on the RHS of the = to the variable on the LHS
	 * inside of the identifiers map. 
	 */
	private static void set(ArrayList<String> validStatement)
	{
		// If size of the sentence is 4 and the RHS does not contain a hex value
		if(validStatement.size()== 4 && !validStatement.get(validStatement.size()-1).contains("h")) {
			
			// If the RHS contains a function (handles analog reading function from arduino_ex1.scl)
			if (validStatement.get(3).contains("("))
			{
				Random r = new Random();
				String value = String.valueOf(r.nextInt(1024));
				identifiers.get(validStatement.get(1)).setValue(value);
				System.out.println(validStatement.get(1) + " = " + value);
			}
			// If the RHS is numeric using Regex
			else if (isNumeric(validStatement.get(3)))
			{
				String setValue = validStatement.get(3);
				identifiers.get(validStatement.get(1)).setValue(setValue);
				System.out.println(validStatement.get(3) + " = " + setValue);
			}
			// If the RHS is another variable
			else
			{
				String setValue = identifiers.get(validStatement.get(3)).getValue();
				identifiers.get(validStatement.get(1)).setValue(setValue);
				System.out.println(validStatement.get(3) + " = " + setValue);
			}

		}
		// If size of the sentence is 5
		if(validStatement.size()== 5) {
	
			// If the sentence is a negate operation
			if (validStatement.contains("negate")) {
				String negateOperand = identifiers.get(validStatement.get(4)).getValue();
				String rOperandVal = negateOperand.substring(0, negateOperand.length() - 1);
	
				String bin1 = hexToBin(rOperandVal);
				int BinInt = Integer.parseInt(bin1);
				int decBin = binaryToDecimal(BinInt);
	
				int result = ~decBin;
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println("Negate: " + result);
	
			}
	
		}
	
	
		// If the size of the current sentence is 6 and DOES NOT contain a hex value in its RHS
		else if(validStatement.size()==6 && !validStatement.get(validStatement.size()-1).contains("h")) {
	
			// If in the RHS, right-hand operand is a single integer digit
			if(validStatement.get(validStatement.size()-1).length() == 1) {
	
				String left_operand = validStatement.get(3);
				String right_operand = validStatement.get(5);
	
				String lOperandVal = identifiers.get(left_operand).getValue();
				int rOperandVal = Integer.parseInt(right_operand);
	
				lOperandVal = lOperandVal.substring(0, lOperandVal.length() - 1);
	
				String bin1 = hexToBin(lOperandVal);
	
				int BinInt = Integer.parseInt(bin1);
	
				int decBin = binaryToDecimal(BinInt);
	
				// left shift operation
				if (validStatement.contains("lshift")) {
	
					int result = (decBin << rOperandVal);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println("Lshift: " + result);
	
				// left shift operation
				} else if (validStatement.contains("rshift")) {
	
					int result = (decBin >> rOperandVal);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println("Rshift: " + result);
	
				}
	
			}
			
			// If in the RHS, right-hand operand is an identifier
			else if(validStatement.get(validStatement.size()-1).length() == 2) {
	
				String left_operand = validStatement.get(3);
				String right_operand = validStatement.get(5);
	
				int lOperandVal = Integer.parseInt(identifiers.get(left_operand).getValue());
				int rOperandVal = Integer.parseInt(identifiers.get(right_operand).getValue());
	
				//Bitwise AND
				if (validStatement.contains("band")) {
	
					int result = (lOperandVal & rOperandVal);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(lOperandVal + " band " + rOperandVal + " = " + result);
	
				//Bitwise OR
				} else if (validStatement.contains("bor")) {
	
					int result = (lOperandVal | rOperandVal);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(lOperandVal + " bor " + rOperandVal + " = " + result);
	
				//Bitwise Exclusive OR
				} else if (validStatement.contains("bxor")) {
	
					int result = (lOperandVal ^ rOperandVal);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(lOperandVal+ " bxor " + rOperandVal + " = " + result);
	
				}
				
				// The sentence contains an expression with left and right operands being both identifiers on the RHS
			} else { // When validStatement.get(validStatement.size()-1).length() > 2
	
				String left_operand = validStatement.get(3);
				String right_operand = validStatement.get(validStatement.size() - 1);
	
				String lOperandVal = identifiers.get(left_operand).getValue();
				String rOperandVal = identifiers.get(right_operand).getValue();
	
				lOperandVal = lOperandVal.substring(0, lOperandVal.length() - 1);
				rOperandVal = rOperandVal.substring(0, rOperandVal.length() - 1);
	
				String bin1 = hexToBin(lOperandVal);
				String bin2 = hexToBin(rOperandVal);
	
				int BinInt = Integer.parseInt(bin1);
				int BinInt2 = Integer.parseInt(bin2);
	
				int decBin = binaryToDecimal(BinInt);
				int decBin2 = binaryToDecimal(BinInt2);
	
				// Comparison operations
				if (validStatement.contains("band")) {
	
					int result = (decBin & decBin2);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(decBin + " band " + decBin2 + " = " + result);
	
				} else if (validStatement.contains("bor")) {
	
					int result = (decBin | decBin2);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(decBin + " bor " + decBin2 + " = " + result);
	
				} else if (validStatement.contains("bxor")) {
	
					int result = (decBin ^ decBin2);
					String resultStr = Integer.toString(result);
					identifiers.get(validStatement.get(1)).setValue(resultStr);
					System.out.println(decBin + " bxor " + decBin2 + " = " + result);
	
				}
			}
	
			// If the size of the current sentence is 6 and contains a hex value in its RHS
		} else if(validStatement.size()==6 && validStatement.get(validStatement.size()-1).contains("h")
				&& validStatement.get(validStatement.size()-1).length() > 2) {
	
			String left_operand = validStatement.get(3);
			String right_operand = validStatement.get(validStatement.size() - 1);
	
			String lOperandVal = identifiers.get(left_operand).getValue();
	
			lOperandVal = lOperandVal.substring(0, lOperandVal.length() - 1);
			String rOperandVal = right_operand.substring(0, right_operand.length() - 1);
	
			String bin1 = hexToBin(lOperandVal);
			String bin2 = hexToBin(rOperandVal);
	
			int BinInt = Integer.parseInt(bin1);
			int BinInt2 = Integer.parseInt(bin2);
	
			int decBin = binaryToDecimal(BinInt);
			int decBin2 = binaryToDecimal(BinInt2);
	
			// Comparison operations
			if (validStatement.contains("band")) {
	
				int result = (decBin & decBin2);
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println(decBin + " band " + decBin2 + " = " + result);
	
			} else if (validStatement.contains("bor")) {
	
				int result = (decBin | decBin2);
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println(decBin + " bor " + decBin2 + " = " + result);
	
			} else if (validStatement.contains("bxor")) {
	
				int result = (decBin ^ decBin2);
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println(decBin + " bxor " + decBin2 + " = " + result);
	
			}
	
	
		// If the length of the sentence is length of 8
		} else if(validStatement.size()== 8 ) {
	
			String left_operand = validStatement.get(4);
			String right_operand = validStatement.get(6);
	
			String lOperandVal = identifiers.get(left_operand).getValue();
			int rOperandVal = Integer.parseInt(right_operand);
	
			lOperandVal = lOperandVal.substring(0, lOperandVal.length() - 1);
	
			String bin1 = hexToBin(lOperandVal);
	
			int BinInt = Integer.parseInt(bin1);
	
			int decBin = binaryToDecimal(BinInt);
	
			// Bitshift operations
			if (validStatement.contains("lshift")) {
	
				int result = (decBin << rOperandVal);
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println("Lshift: " + result);
	
			} else if (validStatement.contains("rshift")) {
	
				int result = (decBin >> rOperandVal);
				String resultStr = Integer.toString(result);
				identifiers.get(validStatement.get(1)).setValue(resultStr);
				System.out.println("Rshift: " + result);
	
			}
		}

	}
	
	// Convert hexadecimal value to binary
	private static String hexToBin(String str) {
		String binary = "";

		// converting the accepted Hexadecimal
		// string to upper case
		str = str.toUpperCase();

		// initializing the HashMap class
		HashMap<Character, String> hashMap = new HashMap<Character, String>();

		// storing the key value pairs
		hashMap.put('0', "0000");
		hashMap.put('1', "0001");
		hashMap.put('2', "0010");
		hashMap.put('3', "0011");
		hashMap.put('4', "0100");
		hashMap.put('5', "0101");
		hashMap.put('6', "0110");
		hashMap.put('7', "0111");
		hashMap.put('8', "1000");
		hashMap.put('9', "1001");
		hashMap.put('A', "1010");
		hashMap.put('B', "1011");
		hashMap.put('C', "1100");
		hashMap.put('D', "1101");
		hashMap.put('E', "1110");
		hashMap.put('F', "1111");

		int i;
		char ch;

		// loop to iterate through the length
		// of the Hexadecimal String
		for (i = 0; i < str.length(); i++) {
			// extracting each character
			ch = str.charAt(i);

			// checking if the character is
			// present in the keys
			if (hashMap.containsKey(ch))

				// adding to the Binary Sequence
				// the corresponding value of
				// the key
				binary += hashMap.get(ch);

				// returning Invalid Hexadecimal
				// String if the character is
				// not present in the keys
			else {
				binary = "Invalid Hexadecimal String";
				return binary;
			}
		}

		// returning the converted Binary
		return binary;
	}

	// Convert binary to decimal value
	public static int binaryToDecimal(int binary)
	{

		// variable to store the converted binary
		int decimalNumber = 0, i = 0;

		// loop to extract digits of the binary
		while (binary > 0) {

			// extracting each digit of the binary
			// by getting the remainder of division
			// by 10 and multiplying it by
			// increasing integral powers of 2
			decimalNumber += Math.pow(2, i++) * (binary % 10);

			// update condition of dividing the
			// binary by 10
			binary /= 10;
		}

		// returning the decimal
		return decimalNumber;
	}
	
	/*
	 * The display function handles the operation when the first lexeme of the current valid statement is the word display.
	 * The for-loop will starts at index 1, which is the second lexeme of the current statement. From there, based on the three condition,
	 * the result will be added to the String displayResult, and finally, display out to the screen.
	 */
	private static void display(ArrayList<String> validStatement)
	{

		String displayResult="";
		for(int i=1; i<validStatement.size(); i++) {
			if(identifiers.containsKey(validStatement.get(i))) {
				displayResult += identifiers.get(validStatement.get(i)).getValue();
			}else if((validStatement.get(i).contains(":"))) {
				displayResult += validStatement.get(i).replaceAll("^\"|\"$", "");
			}else if(validStatement.get(i).equals("\" \"")) {
				displayResult += " ";
			}
		}
		System.out.println(displayResult);
	}
	
	/*
	 * The displayMap function is a helper function which allow us to visualize the HashMap,
	 */
	static void displayMap() {
		Set<String> set = identifiers.keySet();
		for(String s: set) {
			System.out.println("Key is: "+ s + "\tvName: "+ identifiers.get(s).getName()+ "\tvType: "+identifiers.get(s).getType()+ "\tvValue: "+identifiers.get(s).getValue()); 
		}
	}


}
