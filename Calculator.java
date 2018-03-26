import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

public class Calculator {
	
	private static final String OPERATOR_ADDITION = "+";
	private static final String OPERATOR_SUBTRACTION = "-";
	private static final String OPERATOR_MULTIPLICATION = "*";
	private static final String OPERATOR_DIVISION = "/";
	private static final String OPERATOR_SQRT = "sqrt";
	private static final String OPERATOR_UNDO = "undo";
	private static final String OPERATOR_CLEAR = "clear";
	
	private static final int PRECISION_STORE = 15;
	private static final int PRECISION_DISPLAY = 10;
	
	private static Stack<String> stack = new Stack<String>();
	private static Stack<String> cache = new Stack<String>();
	
	private static boolean PRE_IS_PARAM = false;
	private static boolean passed = true;
	
	private static final HashSet<String> operators = new HashSet<String> (){{
		add(OPERATOR_ADDITION);
		add(OPERATOR_SUBTRACTION);
		add(OPERATOR_MULTIPLICATION);
		add(OPERATOR_DIVISION);
		add(OPERATOR_SQRT);
		add(OPERATOR_UNDO);
		add(OPERATOR_CLEAR);
	}};
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input numbers or operators: ");
		String interruptedMsg = "the [ ";
		while(scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			boolean needWarning = false;
			if (!nextLine.isEmpty()) {
				String[] strArr = nextLine.split(" ");
				for (int i=0; i<strArr.length; i++) {
					if (!isIllegal(strArr[i])) {
						System.err.println("Illegal operators or parameters: [" + strArr[i] + "]");
						passed = false;
						break;
					}
					if (operators.contains(strArr[i]) && passed) {
						passed = calculate(stack, strArr[i]);
						if (!strArr[i].equals(OPERATOR_UNDO))
							PRE_IS_PARAM = false;
					} else if (passed) {
						PRE_IS_PARAM = true;
						stack.push(strArr[i]);
					} else {
						needWarning = true;
						interruptedMsg += strArr[i];
						if (i != strArr.length - 1)
							interruptedMsg += ",";
					}
				}
				if (!passed) {
					if (needWarning)
						System.err.println(interruptedMsg += " ] parameters or operators were not pushed on to the stack due to the previous error.");
					break;
				}
				display(stack);
			} else {
				System.err.println("input can not be empty!");
			}
		}
	}
	
	private static boolean calculate(Stack<String> stack, String operator) {
		if (((operator.equals(OPERATOR_ADDITION) || operator.equals(OPERATOR_SUBTRACTION) || operator.equals(OPERATOR_MULTIPLICATION) || operator.equals(OPERATOR_DIVISION)) 
				&& stack.size() < 2 ) || 
				((operator.equals(OPERATOR_UNDO) || operator.equals(OPERATOR_SQRT)) && stack.isEmpty())) {
			System.err.println("operator " + operator + "(position:" + stack.size() + "): insufficient parameters.");
			return false;
		} else if (operator.equals(OPERATOR_UNDO)) {
			if (!stack.isEmpty() && PRE_IS_PARAM == true) 
				stack.pop();
			else if (!cache.isEmpty() && PRE_IS_PARAM == false) {
				stack.pop();
				String cacheTop = cache.pop();
				if (!cacheTop.equals(OPERATOR_SQRT)) {
					for (int i=0; i<2; i++) {
						stack.push(cache.pop());
					}
				} else {
					stack.push(cache.pop());
				}
			}
		} else if (operator.equals(OPERATOR_CLEAR)) {
			if (!stack.isEmpty())
				stack.clear();
		} else if (operator.equals(OPERATOR_ADDITION)) {
			ArrayList<BigDecimal> items = pop(stack, 2);
			BigDecimal result = items.get(0).add(items.get(1));
			cache.push(operator);
			stack.push(result.toString());
		} else if (operator.equals(OPERATOR_SUBTRACTION)) {
			ArrayList<BigDecimal> items = pop(stack, 2);
			BigDecimal result = items.get(1).subtract(items.get(0));
			cache.push(operator);
			stack.push(result.toString());
		} else if (operator.equals(OPERATOR_MULTIPLICATION)) {
			ArrayList<BigDecimal> items = pop(stack, 2);
			BigDecimal result = items.get(0).multiply(items.get(1));
			cache.push(operator);
			stack.push(result.toString());
		} else if (operator.equals(OPERATOR_DIVISION)) {
			ArrayList<BigDecimal> items = pop(stack, 2);
			BigDecimal result = items.get(1).divide(items.get(0), PRECISION_STORE, BigDecimal.ROUND_HALF_DOWN);
			cache.push(operator);
			stack.push(result.toString());
		} else if (operator.equals(OPERATOR_SQRT)) {
			ArrayList<BigDecimal> items = pop(stack, 1);
			BigDecimal result = new BigDecimal(Math.sqrt(items.get(0).doubleValue()));
			cache.push(operator);
			stack.push(result.toString());
		} else {
			System.err.println("Unknown operators: " + operator);
			return false;
		}
		return true;
	}
	
	private static void display(Stack<String> stack) {
		String tmp = "stack: ";
		for (String str : stack)
			tmp += new BigDecimal(str).setScale(PRECISION_DISPLAY, BigDecimal.ROUND_HALF_DOWN).stripTrailingZeros().toPlainString() + " ";
		System.out.println(tmp);
	}
	
	private static ArrayList<BigDecimal> pop(Stack<String> stack, int count) {
		ArrayList<BigDecimal> list = new ArrayList<BigDecimal>();
		for (int i=0; i<count; i++) {
			BigDecimal tmp = new BigDecimal(stack.pop()).setScale(PRECISION_STORE);
			list.add(tmp);
			cache.push(tmp.toString());
		}
		return list;
	}
	
	private static boolean isIllegal(String str) {
		try {
			if (!operators.contains(str))
				new BigDecimal(str).toString();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
}
