package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    
        
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	scalars=new ArrayList<ScalarSymbol>();
    	arrays=new ArrayList<ArraySymbol>();
    	StringTokenizer str=new StringTokenizer(expr, delims, true);
    	Stack<String> tokens=new Stack<String>();
    	while(str.hasMoreTokens()){
    		tokens.push(str.nextToken());
    	}
    	while(!tokens.isEmpty()){
    		if(tokens.peek().equals("[")){
    			tokens.pop();
    			ArraySymbol addArray=new ArraySymbol(tokens.pop());
    			arrays.add(addArray);
    		}
    		else if(Character.isAlphabetic(tokens.peek().charAt(0))){
    			ScalarSymbol addScalar=new ScalarSymbol(tokens.pop());
    			scalars.add(addScalar);
    		}
    		else{
    			tokens.pop();
    		}
    	}
    	//System.out.println(scalars.toString());
    	//System.out.println(arrays.toString());
    	}
    	
   
 
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    public float evaluateArray() {
    	return 0;
    }
    
    public float evaluateSub(){
    	return 0;
    }
    
    private int toNumber(String value){
    	int number=Integer.parseInt(value);
    	return number;
    }
    
    private String toString(int value){
    	String string = Integer.toString(value);
    	return string; 
    }
    
    private float toFloat(String value){
    	float answer=Float.valueOf(value);
    	return answer;
    }
    

    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    private String evaluate(String expr, int start, int end){
    	String answer;
    	String expression=expr.substring(start,end);
    	for(int i=0; i<expression.length(); i++){
    		if(expression.charAt(i)=='['){
    			expression=expression+']'; 
    		}
    	}
    	Stack<String> numbers=new Stack<String>();
		Stack<String> operators=new Stack<String>();
		Stack<String> numbersTemp=new Stack<String>();
		Stack<String> operatorsTemp=new Stack<String>();
		String temp="";
		for(int i=0; i<expression.length(); i++){
			int a;
			int b;
			if(expression.charAt(i)!='(' && expression.charAt(i)!='['){
				temp=temp+expression.charAt(i);
			}
			else{
				if(expression.charAt(i)=='['){
					temp=temp+'['; 
				}
				a=i+1;
					while(i<expression.length() && expression.charAt(i)!=')' && expression.charAt(i)!=']' ){
						i++;
					}
				b=i;
				temp=temp+evaluate(expression, a, b);
			}
		}
		
		
		StringTokenizer tokens = new StringTokenizer(temp, delims, true);  
		while(tokens.hasMoreTokens()){
			String current=tokens.nextToken(); 
			if(Character.isDigit(current.charAt(0))){
				numbersTemp.push(current);
			}
			else if(current.equals("+") || current.equals("-") || current.equals("*") || current.equals("/")){
					if(numbersTemp.isEmpty() && current.equals("-")){
						int negative=Integer.parseInt(tokens.nextToken());
						negative=-1*negative;
						numbersTemp.push(toString(negative));
					}
					else if(numbersTemp.size()==operatorsTemp.size() && current.equals("-")){
						int negative=Integer.parseInt(tokens.nextToken());
						negative=-1*negative;
						numbersTemp.push(toString(negative));
					}
					else{
						operatorsTemp.push(current);
					}
				
			} 
			
			else if(Character.isAlphabetic(current.charAt(0))){
				numbersTemp.push(current);
			}
			else if(current.equals("[")){
				String arrayName=numbersTemp.pop();
				int arrayNumber=toNumber(tokens.nextToken());
				for(int i=0; i<arrays.size(); i++){
					if(arrays.get(i).name.equals(arrayName)){
						numbersTemp.push(toString(arrays.get(i).values[arrayNumber]));
					}
				}
			}
			}
		while(!numbersTemp.isEmpty()){ //Reverses the stack
			numbers.push(numbersTemp.pop());
		}
		
		while(!operatorsTemp.isEmpty()){ //Reverses the stack
			operators.push(operatorsTemp.pop());
		}
		

		
		while(!operators.isEmpty()){ //Multiplication and Division Precedence
			int a;
			int b;
			if(!operators.peek().equals("*") && !operators.peek().equals("/")){
				operatorsTemp.push(operators.pop());
				numbersTemp.push(numbers.pop());
			}
			else if(operators.peek().equals("*")){
				operators.pop();
				a=toNumber(numbers.pop());
				b=toNumber(numbers.pop());
				numbers.push(toString(a*b));
				while(!operatorsTemp.isEmpty()){
    				operators.push(operatorsTemp.pop());
    				numbers.push(numbersTemp.pop());
    			}
			}
			else{
				operators.pop();
				a=toNumber(numbers.pop());
				b=toNumber(numbers.pop());
				numbers.push(toString(a/b));
				while(!operatorsTemp.isEmpty()){
    				operators.push(operatorsTemp.pop());
    				numbers.push(numbersTemp.pop());
    			}
			}
		}
		
		while(!numbersTemp.isEmpty()){ //Reverses the stack again
			numbers.push(numbersTemp.pop());
		}
		
		while(!operatorsTemp.isEmpty()){ //Reverses the stack again
			operators.push(operatorsTemp.pop());
		}
		

		while(!operators.isEmpty()){ //Does operations without * or /
			String top=operators.peek();
			
			int x=toNumber(numbers.pop());
			int y=toNumber(numbers.pop());
			if(top.equals("+")){
				numbers.push(toString(x+y));
			}
			else{
 				numbers.push(toString(x-y));
			}
			operators.pop(); 
		}
		//System.out.println(numbers.pop());
		answer=numbers.pop();
		return answer;
    }
    
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		float answer; 
    		
    		for(int i=0; i<scalars.size(); i++){
    			expr=expr.replace(scalars.get(i).name, ""+scalars.get(i).value);
    		}
    	
    		
    		Stack<String> numbers=new Stack<String>();
    		Stack<String> operators=new Stack<String>();
    		Stack<String> numbersTemp=new Stack<String>();
    		Stack<String> operatorsTemp=new Stack<String>();
    		String temp="";
    		String temp2="";
    		for(int i=0; i<expr.length(); i++){
    			int a;
    			int b;
    			if(expr.charAt(i)!='['){
    				temp2=temp2+expr.charAt(i);
    			}
    			else{
    				temp2=temp2+expr.charAt(i);
    				a=i+1;
    				while(i<expr.length()-1 && expr.charAt(i)!=']'){
						i++;
					}
				b=i;
				temp2=temp2+evaluate(expr, a, b); 
    			}
    		}
    		for(int i=0; i<temp2.length(); i++){ // Parenthesis
    			int a;
    			int b;
    			if(temp2.charAt(i)!='('){
    				temp=temp+temp2.charAt(i);
    			}
    			else{
    				a=i+1;
    					while(i<temp2.length()-1 && temp2.charAt(i)!=')'){
    						i++;
    					}
    				b=i;
    				temp=temp+evaluate(temp2, a, b);
    			}
    		}
    		
    		StringTokenizer tokens = new StringTokenizer(temp, delims, true);  
    		while(tokens.hasMoreTokens()){
    			String current=tokens.nextToken(); 
    				if(Character.isDigit(current.charAt(0))){
    					numbersTemp.push(current);
    				}
    				else if(current.equals("+") || current.equals("-") || current.equals("*") || current.equals("/")){
    					if(numbersTemp.isEmpty() && current.equals("-")){
    						int negative=Integer.parseInt(tokens.nextToken());
    						negative=-1*negative;
    						numbersTemp.push(toString(negative));
    					}
    					else if(numbersTemp.size()==operatorsTemp.size() && current.equals("-")){
    						int negative=Integer.parseInt(tokens.nextToken());
    						negative=-1*negative;
    						numbersTemp.push(toString(negative));
    					}
    					else{
    					operatorsTemp.push(current);
    					}
    				} 
    				else if(Character.isAlphabetic(current.charAt(0))){
    					numbersTemp.push(current);
    				}
    				else if(current.equals("[")){
    					String arrayName=numbersTemp.pop();
    					int arrayNumber=toNumber(tokens.nextToken());
    					for(int i=0; i<arrays.size(); i++){
    						if(arrays.get(i).name.equals(arrayName)){
    							numbersTemp.push(toString(arrays.get(i).values[arrayNumber]));
    						}
    					}
    				}
    			}
    		while(!numbersTemp.isEmpty()){ //Reverses the stack
    			numbers.push(numbersTemp.pop());
    		}
    		
    		while(!operatorsTemp.isEmpty()){ //Reverses the stack
    			operators.push(operatorsTemp.pop());
    		}
    		

    		
    		while(!operators.isEmpty()){ //Multiplication and Division Precedence
    			int a;
    			int b;
    			if(!operators.peek().equals("*") && !operators.peek().equals("/")){
    				operatorsTemp.push(operators.pop());
    				numbersTemp.push(numbers.pop());
    			}
    			else if(operators.peek().equals("*")){
    				operators.pop();
    				a=toNumber(numbers.pop());
    				b=toNumber(numbers.pop());
    				numbers.push(toString(a*b));
    				while(!operatorsTemp.isEmpty()){
        				operators.push(operatorsTemp.pop());
        				numbers.push(numbersTemp.pop());
        			}
    			}
    			else{
    				operators.pop();
    				a=toNumber(numbers.pop());
    				b=toNumber(numbers.pop());
    				numbers.push(toString(a/b));
    				while(!operatorsTemp.isEmpty()){
        				operators.push(operatorsTemp.pop());
        				numbers.push(numbersTemp.pop());
        			}
    			}
    		}
    		
    		while(!numbersTemp.isEmpty()){ //Reverses the stack again
    			numbers.push(numbersTemp.pop());
    		}
    		
    		while(!operatorsTemp.isEmpty()){ //Reverses the stack again
    			operators.push(operatorsTemp.pop());
    		}
    		

    		while(!operators.isEmpty()){ //Does operations without * or /
    			String top=operators.peek();
    			if(top.equals("-") && numbers.size()==1){
    				operators.pop();
    				String negate=numbers.pop();
    				negate="-"+negate;
    				numbers.push(negate);
    			}
    			else{
    				int x=toNumber(numbers.pop());
    				int y=toNumber(numbers.pop());
    				if(top.equals("+")){
    					numbers.push(toString(x+y));
    				}
    				else{
    					numbers.push(toString(x-y));
    				}
    				operators.pop(); 
    			}	
    		}
    		//System.out.println(numbers.pop());
    		answer=toFloat(numbers.pop());
    		return answer;
    }
    

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}