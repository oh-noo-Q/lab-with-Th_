package entities.testdata;

import entities.SpecialCharacter;
import entities.testdata.comparable.AssertMethod;
import entities.testdata.comparable.INullableComparable;
import entities.testdata.comparable.NullableStatementGenerator;
import entities.utils.Utils;
import entities.utils.VariableTypeUtils;

/**
 * Represent variable as pointer (one level, two level, etc.)
 *
 * @author ducanhnguyen
 */
public abstract class PointerDataNode extends ValueDataNode implements INullableComparable
{
	public static final int NULL_VALUE = -1;

	protected int level;

	/**
	 * The allocated size, including '\0'.
	 *
	 * Ex1: node="xyz" ---> allocatedSize = 4 <br/>
	 * Ex2: node="" ---> allocatedSize = 1
	 */
	private int allocatedSize;

	private boolean sizeIsSet = false;

	public boolean isSetSize() {
		return sizeIsSet;
	}

	public void setSizeIsSet(boolean sizeIsSet) {
		this.sizeIsSet = sizeIsSet;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getAllocatedSize() {
		return this.allocatedSize;
	}

	public void setAllocatedSize(int allocatedSize) {
		this.allocatedSize = allocatedSize;
	}

	public boolean isNotNull() {
		return this.allocatedSize >= 1;
	}

	@Override
	public String generareSourcecodetoReadInputFromFile() throws Exception {
		StringBuilder output = new StringBuilder();
		for (IDataNode child : this.getChildren())
			output.append(child.generareSourcecodetoReadInputFromFile());
		return output.toString();
	}

	@Override
	public String getInputForGoogleTest() throws Exception {
//		if (isUseUserCode()) {
//			return getUserCodeContent();
//		}

//		if (Environment.getInstance().isC())
			return getCInput();
//		else
//			return getCppInput();
	}

	private String getCInput() throws Exception {
		String input = "";

		String type = VariableTypeUtils
				.deleteStorageClassesExceptConst(getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

		String coreType = "";
		if (getChildren() != null && !getChildren().isEmpty())
			coreType = ((ValueDataNode) getChildren().get(0)).getRawType();
		else {
			int index = type.lastIndexOf('*');
			if (index < 0)
				index = getRealType().lastIndexOf('*');
			coreType = type.substring(0, index).trim();
		}

		if (this instanceof PointerStructureDataNode) {
			type = type.replace(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS, SpecialCharacter.EMPTY);
			coreType = coreType.replace(SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS, SpecialCharacter.EMPTY);

//			INode correspondingType = getCorrespondingType();
//			if (correspondingType instanceof StructureNode && !((StructureNode) correspondingType).haveTypedef()) {
//				String prefix = SpecialCharacter.EMPTY;
//				if (correspondingType instanceof StructNode) {
//					prefix = "struct ";
//				} else if (correspondingType instanceof EnumNode) {
//					prefix = "enum ";
//				} else if (correspondingType instanceof UnionNode) {
//					prefix = "union ";
//				}
//
//				if (!type.startsWith(prefix))
//					type = prefix + type;
//				if (!coreType.startsWith(prefix))
//					coreType = prefix + coreType;
//			}
		}

		if (isExternel())
			type = "";

		if (isPassingVariable() || isSTLListBaseElement() || isInConstructor() || isGlobalExpectedValue() || isSutExpectedArgument()) {
			String allocation = "";

			if (this.isNotNull())
				allocation = String.format("%s %s = malloc(%d * sizeof(%s))" + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName(), this.getAllocatedSize(), coreType);
			else {
				allocation = String.format("%s %s = " + IDataNode.NULL_POINTER_IN_C + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName());
			}
			input += allocation;
		} else if (isArrayElement() || isAttribute()) {
			String allocation;

			if (this.isNotNull())
				allocation = String.format("%s = malloc(%d * sizeof(%s))" + SpecialCharacter.END_OF_STATEMENT,
						this.getVituralName(), this.getAllocatedSize(), coreType);
			else
				allocation = String.format("%s = " + IDataNode.NULL_POINTER_IN_C + SpecialCharacter.END_OF_STATEMENT
						, this.getVituralName());
			input += allocation;
		} else if (isVoidPointerValue()) {
			String allocation = "";

			if (this.isNotNull())
				allocation = String.format("%s %s = malloc(%d * sizeof(%s))" + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName(), this.getAllocatedSize(), coreType);
			else {
				allocation = String.format("%s %s = " + IDataNode.NULL_POINTER_IN_C + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName());
			}
			input += allocation;
		} else {
			if (this.isNotNull())
				input = String.format("%s = malloc(%d * sizeof(%s))" + SpecialCharacter.END_OF_STATEMENT,
						this.getVituralName(), this.getAllocatedSize(), coreType);
			else
				input += String.format("%s = " + IDataNode.NULL_POINTER_IN_C + SpecialCharacter.END_OF_STATEMENT
						, this.getVituralName());
		}

		return input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
	}

	private String getCppInput() throws Exception {
		String input = "";

		String type = VariableTypeUtils
				.deleteStorageClassesExceptConst(getRawType().replace(IDataNode.REFERENCE_OPERATOR, ""));

		String coreType = "";
		if (getChildren() != null && !getChildren().isEmpty())
			coreType = ((ValueDataNode) getChildren().get(0)).getRawType();
		else
			coreType = type.substring(0, type.lastIndexOf('*'));

		if (isExternel())
			type = "";

		if (isPassingVariable() || isSTLListBaseElement() || isInConstructor() || isGlobalExpectedValue() || isSutExpectedArgument()) {
			String allocation = "";

			if (this.isNotNull())
				allocation = String.format("%s %s = new %s[%s]" + SpecialCharacter.END_OF_STATEMENT, type,
						this.getVituralName(), coreType, this.getAllocatedSize());
			else {
				allocation = String.format("%s %s = " + IDataNode.NULL_POINTER_IN_CPP + SpecialCharacter.END_OF_STATEMENT,
						type, this.getVituralName());
			}
			input += allocation;
		} else if (isArrayElement() || isAttribute()) {
			String allocation;

			if (this.isNotNull())
				allocation = String.format("%s = new %s[%s]" + SpecialCharacter.END_OF_STATEMENT,
						this.getVituralName(), coreType, this.getAllocatedSize());
			else
				allocation = String.format("%s = " + IDataNode.NULL_POINTER_IN_CPP + SpecialCharacter.END_OF_STATEMENT
						, this.getVituralName());
			input += allocation;
		} else {
			if (this.isNotNull())
				input += getVituralName() + " = new " + coreType + Utils.asIndex(this.getAllocatedSize())
						+ SpecialCharacter.END_OF_STATEMENT;
			else
				input += getVituralName() + " = " + IDataNode.NULL_POINTER_IN_CPP + SpecialCharacter.END_OF_STATEMENT;
		}

		return input + SpecialCharacter.LINE_BREAK + super.getInputForGoogleTest();
	}

	@Override
	public String assertNull(String name) {
		return new NullableStatementGenerator(this).assertNull(name);
	}

	@Override
	public String assertNotNull(String name) {
		return new NullableStatementGenerator(this).assertNotNull(name);
	}

	@Override
	public String getAssertion() {
		if (isVoidPointerValue()) {
			return SpecialCharacter.EMPTY;
		}

		String actualName = getActualName();

		String output = SpecialCharacter.EMPTY;

		String assertMethod = getAssertMethod();
		if (assertMethod != null) {
			switch (assertMethod) {
				case AssertMethod.ASSERT_NULL:
					output = assertNull(actualName);
					break;

				case AssertMethod.ASSERT_NOT_NULL:
					output = assertNotNull(actualName);
					break;

//				case AssertMethod.USER_CODE:
//					output = getAssertUserCode().normalize();
//					break;
			}
		}

		return output + super.getAssertion();
	}

	@Override
	public PointerDataNode clone() {
		PointerDataNode clone = (PointerDataNode) super.clone();
		clone.level = level;
		return clone;
	}

}
