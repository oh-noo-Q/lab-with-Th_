package entities.normalizer;

import entities.changes.ChangedToken;
import entities.testdatainit.VariableTypes;
import entities.testdatainit.VariableTypes.BASIC.NUMBER.INTEGER;
import entities.parser.ProjectParser;
import entities.parser.dependency.finder.IVariableSearchingSpace;
import entities.parser.dependency.finder.Level;
import entities.parser.dependency.finder.VariableSearchingSpace;
import entities.parser.object.EnumNode;
import entities.parser.object.IFunctionNode;
import entities.parser.object.INode;
import entities.parser.object.IVariableNode;
import entities.search.Search;
import entities.search.condition.EnumNodeCondition;
import entities.search.condition.FunctionNodeCondition;
import entities.utils.Utils;
import entities.UETLogger;

import java.io.File;
import java.util.List;

/**
 * Convert enum value in the function into another representation
 *
 * @author ducanhnguyen
 */
public class EnumNormalizer extends AbstractFunctionNormalizer implements IFunctionNormalizer {
	final static UETLogger logger = UETLogger.get(EnumNormalizer.class);
	public static int DEFAULT_ID_TOKEN = 672;
	public static int ID_TOKEN = EnumNormalizer.DEFAULT_ID_TOKEN;

	public EnumNormalizer() {
	}

	public EnumNormalizer(IFunctionNode functionNode) {
		this.functionNode = functionNode;
	}

	public static void main(String[] args) {
//		ProjectParser parser = new ProjectParser(new File(Paths.RETURN_ENUM));
//		// parser.getIgnoreFolders()
//		// .add(new File(
//		// "F:\\Repo-Java\\Project\\ava\\data-test\\tsdv\\SampleSource-2017A\\return_enum\\"));
//		IFunctionNode function = (IFunctionNode) Search
//				.searchNodes(parser.getRootTree(), new FunctionNodeCondition(), "SimpleTest(Color)").get(0);
//
//		System.out.println(function.getAST().getRawSignature());
//		EnumNormalizer normalizer = new EnumNormalizer(function);
//		normalizer.normalize();
//
//		System.out.println(normalizer.getTokens());
//		System.out.println(normalizer.getNormalizedSourcecode());
	}

	@Override
	public void normalize() {
		if (functionNode != null) {
			EnumNormalizer.ID_TOKEN = EnumNormalizer.DEFAULT_ID_TOKEN;

			normalizeSourcecode = functionNode.getAST().getRawSignature();

			IVariableSearchingSpace space = new VariableSearchingSpace(functionNode);

			for (Level level : space.getSpaces())
				for (INode n : level) {
					// logger.debug("Parse " + n.getAbsolutePath());

					List<INode> enums = Search.searchNodes(n, new EnumNodeCondition());

					for (INode enumItem : enums) {
						if (enumItem instanceof EnumNode) {
							/*
							  Increase id of token to prevent the restore test data to make mistakes
							 */
							EnumNormalizer.ID_TOKEN += 100;
							/*
							  Convert value of item in enum into interger value
							 */
							for (String value : ((EnumNode) enumItem).getAllNameEnumItems()) {
								String newName = EnumNormalizer.ID_TOKEN++ + "";

								String oldName = value;
								normalizeSourcecode = normalizeSourcecode.replaceAll("\\b" + oldName + "\\b", newName);

								String regex = "return\\s*\\b" + newName + "\\b";
								normalizeSourcecode = normalizeSourcecode.replaceAll(regex, "return " + oldName);
								tokens.add(new ChangedToken(newName, oldName));
							}
							/*
							 * Convert type enum into a specified number type
							 */
							List<String> basicNumberTypes = VariableTypes.getAllBasicFieldNames(INTEGER.class);
							basicNumberTypes.remove("bool");

							for (String basicNumberType : basicNumberTypes)
								/*
								 * Our aim is to replace the type of enum in the function with a name of integer
								 * type (e.g., int, long).
								 *
								 * Therefore, we need check the replaced integer type to be in the function or
								 * not. And also, this type must be unique because of the function may access
								 * multiple enums.
								 */
								if (!normalizeSourcecode.contains(basicNumberType)
										&& !tokens.containNewName(basicNumberType)) {
									String ALTERNATIVE_TYPE = basicNumberType;
									for (IVariableNode var : getFunctionNode().getArguments())
										if (enumItem.getNewType().length() > 0
												&& var.getReducedRawType().endsWith(enumItem.getNewType())) {
											normalizeSourcecode = normalizeSourcecode
													.replaceAll(Utils.toRegex(var.getFullType()), ALTERNATIVE_TYPE);
											tokens.add(new ChangedToken(ALTERNATIVE_TYPE, var.getFullType()));
										}
									break;
								}

						}
					}
				}
		} else
			normalizeSourcecode = INormalizer.ERROR;
	}
}
