package entities.solverhelper.solver;

import entities.normalizer.AbstractNormalizer;
import entities.solverhelper.NewVariableInSe;
import entities.utils.Utils;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author anhanh
 */
public class SmtLibv2Normalizer2 extends SmtLibv2Normalizer {
	private List<NewVariableInSe> newVariableInSes = new ArrayList<>();

	public SmtLibv2Normalizer2(String expression, List<NewVariableInSe> newVariableInSes) {
		originalSourcecode = expression;
		this.newVariableInSes = newVariableInSes;
	}

	public static void main(String[] args) {
		String[] samples = new String[] { "((x+1))!=1", "((((-((-(tvw_a)+(-1)*1+0))+0))+1+0))>0", "!(!(d->ngay==1))",
				"a!=b", "a==b", "(a)>b", "a>x[1][2]", "((tvwb_w)/((tvwhe)*(tvwhe)/10000))<19",
				"!((tvwkey)==tvwarray[(to_int*(((0)+(to_int*((tvwsize)+0)))/2+0))+0])",
				"tvwp[0+0+0][0+0+0]>=(-10)&&tvwp[0+0+0][0+0+0]<=20",
				"(to_int*(16807*((tvwseed)-(to_int*((tvwseed)/127773))*127773)-(to_int*((tvwseed)/127773))*2836))<0" };

		for (String sample : samples) {
			System.out.println(sample);
			AbstractNormalizer norm = new SmtLibv2Normalizer2(sample, new ArrayList<>());
			norm.normalize();
			System.out.println(norm.getNormalizedSourcecode() + "\n\n");
		}
	}

	@Override
	public void normalize() {
		IASTNode ast = Utils.convertToIAST(originalSourcecode);
		normalizeSourcecode = createSmtLib(ast);

		// Ex: "(1.2)" ----------->"1.2"
		normalizeSourcecode = normalizeSourcecode.replaceAll("\\(([a-zA-Z0-9_\\.]+)\\)", "$1");
	}

	protected String createSmtLib(IASTNode ast) {
		StringBuilder normalizeSc = new StringBuilder();

		if (ast.getRawSignature().equals("NULL")) {
			normalizeSc = new StringBuilder("0");

		} else if (ast.getRawSignature().equals(NEGATIVE_ONE)) {
			normalizeSc = new StringBuilder("- 1");

		} else if (ast instanceof ICPPASTName || ast instanceof IASTIdExpression
				|| ast instanceof ICPPASTLiteralExpression || ast instanceof ICPPASTFieldReference) {
			normalizeSc = new StringBuilder(ast.getRawSignature());

		} else {
			// STEP 1. Shorten expression
			boolean isNegate = false;
			boolean isUnaryExpression = ast instanceof ICPPASTUnaryExpression;

			int count = 0;
			while (ast instanceof ICPPASTUnaryExpression) {
				if (count++ > 10)
					break; // to avoid infinite loop
				ICPPASTUnaryExpression astUnary = (ICPPASTUnaryExpression) ast;
				switch (astUnary.getOperator()) {
				case IASTUnaryExpression.op_plus:
					case IASTUnaryExpression.op_bracketedPrimary:
						ast = astUnary.getOperand();
					break;
				case IASTUnaryExpression.op_minus:
					ast = Utils.convertToIAST(NEGATIVE_ONE + "*(" + astUnary.getOperand().getRawSignature() + ")");
					break;
				case IASTUnaryExpression.op_prefixIncr:
					ast = Utils.convertToIAST("1+ " + astUnary.getOperand().getRawSignature());
					break;
				case IASTUnaryExpression.op_prefixDecr:
					ast = Utils.convertToIAST(astUnary.getOperand().getRawSignature() + "-1");
					break;
					case IASTUnaryExpression.op_not:
					isNegate = !isNegate;
					ast = astUnary.getOperand();
					break;
					case IASTUnaryExpression.op_star: {
						break;
					}
					default: {
						break;
					}
				}
			}

			// STEP 2. Get operator
			String operator = "";
			if (isUnaryExpression) {
				if (isNegate) {
					operator = "not";
					normalizeSc = new StringBuilder(String.format("%s %s", operator, createSmtLib(ast)));
				} else
					normalizeSc = new StringBuilder(String.format("%s", createSmtLib(ast)));

			} else if (ast instanceof ICPPASTBinaryExpression) {
				ICPPASTBinaryExpression astBinary = (ICPPASTBinaryExpression) ast;
				switch (astBinary.getOperator()) {
				case IASTBinaryExpression.op_divide:
					operator = "div"; // integer division
					break;
				case IASTBinaryExpression.op_minus:
					operator = "-";
					break;
				case IASTBinaryExpression.op_plus:
					operator = "+";
					break;
				case IASTBinaryExpression.op_multiply:
					operator = "*";
					break;
				case IASTBinaryExpression.op_modulo:
					operator = "mod";
					break;

				case IASTBinaryExpression.op_greaterEqual:
					operator = ">=";
					break;
				case IASTBinaryExpression.op_greaterThan:
					operator = ">";
					break;
				case IASTBinaryExpression.op_lessEqual:
					operator = "<=";
					break;
				case IASTBinaryExpression.op_lessThan:
					operator = "<";
					break;
				case IASTBinaryExpression.op_equals:
					operator = "=";
					break;
				case IASTBinaryExpression.op_notequals:
					operator = "!=";
					break;

				case IASTBinaryExpression.op_logicalAnd:
					operator = "and";
					break;
				case IASTBinaryExpression.op_logicalOr:
					operator = "or";
					break;
				}

				if (operator.length() > 0)
					if (operator.equals("!="))
						if (((ICPPASTBinaryExpression) ast).getOperand1().getRawSignature().equals("NULL")) {
							normalizeSc = new StringBuilder(String.format("(> %s 0)",
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2())));

						} else if (((ICPPASTBinaryExpression) ast).getOperand2().getRawSignature().equals("NULL")) {
							normalizeSc = new StringBuilder(String.format("(> %s 0)",
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1())));

						} else
							normalizeSc = new StringBuilder(String.format("or (> %s %s) (< %s %s)",
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2()),
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
									createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2())));
					else
						normalizeSc = new StringBuilder(String.format("%s %s %s", operator,
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand1()),
								createSmtLib(((ICPPASTBinaryExpression) ast).getOperand2())));

			} else if (ast instanceof ICPPASTArraySubscriptExpression) {
				// Get all elements in array item
				List<IASTNode> elements = new ArrayList<>();

				int countWhile = 0;
				while (ast.getChildren().length > 1) {
					if (countWhile++ > 10)
						break;// to avoid infinite loop
					elements.add(0, ast.getChildren()[1]);
					ast = ast.getChildren()[0];
				}
				elements.add(ast);
				//
				IASTNode astName = elements.get(elements.size() - 1);
				normalizeSc = new StringBuilder(astName.getRawSignature());

				for (int i = elements.size() - 2; i >= 0; i--)
					normalizeSc.append(createSmtLib(elements.get(i)));
			}
		}

		normalizeSc = new StringBuilder(checkInBracket(normalizeSc.toString()) ? normalizeSc.toString() : " (" + normalizeSc + ") ");
		return normalizeSc.toString();
	}

	private boolean checkInBracket(String stm) {
		stm = stm.trim();
		if (stm.startsWith("(")) {
			int count = 0;
			for (Character c : stm.toCharArray())
				if (c == '(')
					count++;
				else if (c == ')')
					count--;
			return count == 0;
		} else
			return false;

	}

	private final String NEGATIVE_ONE = "(-1)";
}
