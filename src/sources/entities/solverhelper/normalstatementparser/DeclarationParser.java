package entities.solverhelper.normalstatementparser;

import entities.solverhelper.memory.*;
import entities.testdatainit.VariableTypes;
import entities.parser.object.*;
import entities.SpecialCharacter;
import entities.utils.Utils;
import entities.utils.VariableTypeUtils;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTArrayModifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTPointer;

/**
 * Ex1: "int a = 2;" <br/>
 * Ex2: "int a = y+z;" <br/>
 *
 * @author ducanhnguyen
 */
public class DeclarationParser extends StatementParser {
	private IFunctionNode function;
	/**
	 * The current scope of the statement. The value of global scope is equivalent
	 * to zero.
	 */
	private int scopeLevel = 0;

	@Override
	public void parse(IASTNode ast, VariableNodeTable table) throws Exception {
		ast = Utils.shortenAstNode(ast);
		if (ast instanceof IASTSimpleDeclaration && function != null)
			parseDeclaration((IASTSimpleDeclaration) ast, table, scopeLevel, function);
	}

	public void parseDeclaration(IASTSimpleDeclaration stm, VariableNodeTable table, int scopeLevel, ICommonFunctionNode functionNode) throws Exception {
		VariableNode var = new InternalVariableNode();
		var.setAST(stm);
		var.setParent(functionNode);
		INode correspondingNode = var.resolveCoreType();
		String realType = var.getRealType();

		if (correspondingNode == null)
			return;

		for (IASTDeclarator declarator : stm.getDeclarators()) {
			String name = declarator.getName().getRawSignature();

			SymbolicVariable v = null;

			if (VariableTypes.isNumBasic(realType)) {
				String defaultValue = "0";
				v = new NumberSymbolicVariable(name, realType, scopeLevel, defaultValue);

			} else if (VariableTypes.isChBasic(realType)) {
				String defaultValue = "0";
				v = new CharacterSymbolicVariable(name, realType, scopeLevel, defaultValue);

			} else if (VariableTypes.isNumOneDimension(realType))
				v = new OneDimensionNumberSymbolicVariable(name, realType, scopeLevel);

			else if (VariableTypes.isChOneDimension(realType))
				v = new OneDimensionCharacterSymbolicVariable(name, realType, scopeLevel);

			else if (VariableTypes.isNumOneLevel(realType))
				v = new OneLevelNumberSymbolicVariable(name, realType, scopeLevel);

			else if (VariableTypes.isChOneLevel(realType))
				v = new OneLevelCharacterSymbolicVariable(name, realType, scopeLevel);

			else if (VariableTypes.isStructureSimple(realType)) {
				if (correspondingNode instanceof StructNode)
					v = new StructSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				else if (correspondingNode instanceof ClassNode)
					v = new ClassSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				else if (correspondingNode instanceof EnumNode) {
					v = new EnumSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				} else if (correspondingNode instanceof UnionNode)
					v = new UnionSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
			}  else if (VariableTypeUtils.isStructureOneLevel(realType)) {
				if (correspondingNode instanceof StructNode)
					v = new OneLevelStructSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				else if (correspondingNode instanceof ClassNode)
					v = new OneLevelClassSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				else if (correspondingNode instanceof EnumNode) {
					v = new OneLevelEnumSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);
				} else if (correspondingNode instanceof UnionNode)
					v = new OneLevelUnionSymbolicVariable(name,
							table.getCurrentNameSpace() + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS + realType,
							scopeLevel);

				if (v != null) {
					((OneLevelSymbolicVariable) v)
							.setSize(function.getFunctionConfig().getBoundOfArray().getLower() + "");
				}
			} else {
				// dont support this realType
			}

			if (v != null) {
				table.add(v);

				IASTInitializer initialization = declarator.getInitializer();

				if (initialization != null) {
					String ini = v.getName() + initialization.getRawSignature();

					IASTNode ast = Utils.convertToIAST(ini);
					ast = Utils.shortenAstNode(ast);

					new BinaryAssignmentParser().parse(ast, table);
				}
			}
		}
	}

	/**
	 * Get type of variable. If the type of variable is <b>auto</b>, we replace this
	 * type by corresponding type
	 *
	 * @param stm3
	 *            Represent the declaration
	 * @param declarator
	 *            Represent the current declarator
	 * @return
	 */
	private String getType(IASTSimpleDeclaration stm3, IASTDeclarator declarator) {
		String decl = stm3.getDeclSpecifier().getRawSignature();
		String type;

		if (VariableTypes.isAuto(decl)) {
			String initialization = declarator.getInitializer().getChildren()[0].getRawSignature();
			/*
			 * Predict the type of variable based on its initialization
			 */
			type = VariableTypes.getTypeOfAutoVariable(initialization);

		} else {
			type = decl;
			/*
			 * Check the variable is pointer or not
			 * 
			 * The first child is corresponding to the left side. For example, considering
			 * "int a = z*y", we parse the first child (its content: "int a")
			 */
			IASTNode firstChild = declarator.getChildren()[0];
			if (firstChild instanceof CPPASTPointer)
				type += "*";

			if (declarator.getChildren().length >= 2) {
				IASTNode secondChild = declarator.getChildren()[1];
				if (secondChild instanceof CPPASTArrayModifier)
					type += "[]";
			}
		}
		return type;
	}

	public int getScopeLevel() {
		return scopeLevel;
	}

	public void setScopeLevel(int scopeLevel) {
		this.scopeLevel = scopeLevel;
	}

	public IFunctionNode getFunction() {
		return function;
	}

	public void setFunction(IFunctionNode function) {
		this.function = function;
	}
}
