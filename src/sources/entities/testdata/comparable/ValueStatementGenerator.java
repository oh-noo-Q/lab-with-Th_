package entities.testdata.comparable;

import entities.testdata.ValueDataNode;
import entities.utils.VariableTypeUtils;

public class ValueStatementGenerator extends StatementGenerator implements IValueComparable {

    public ValueStatementGenerator(ValueDataNode node) {
        super(node);
    }

    @Override
    public String assertEqual(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        // Get assert method
        String assertMethod;
        String realType = getRealType();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);

//            if (Environment.getInstance().isC()) {
//
//                if (VariableTypeUtils.isNumBasicFloat(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_DOUBLE_EQUAL;
//                else if (VariableTypeUtils.isChOneLevel(realType)
//                        || VariableTypeUtils.isChOneDimension(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_STR_EQUAL;
//                else if (VariableTypeUtils.isOneDimension(realType)
//                        || VariableTypeUtils.isMultipleDimension(realType)
//                        || VariableTypeUtils.isPointer(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_PTR_EQUAL;
//                else
//                    assertMethod = AssertMethod.CU.ASSERT_EQUAL;
//
//                String cuAssertStm;
//                if (assertMethod.equals(AssertMethod.CU.ASSERT_DOUBLE_EQUAL)) {
//                    cuAssertStm = String.format("%s(%s, %s, 0.000001);", assertMethod, actualName, expectedName);
//                } else {
//                    cuAssertStm = String.format("%s(%s, %s);", assertMethod, actualName, expectedName);
//                }
//
//                assertion.append(cuAssertStm);
//
//            } else {
//
//                if (VariableTypeUtils.isNumBasicFloat(realType))
//                    assertMethod = AssertMethod.GTest.EXPECT_DOUBLE_EQ;
//                else if (VariableTypeUtils.isChOneLevel(realType)
//                        || VariableTypeUtils.isChOneDimension(realType)
//                        || VariableTypeUtils.isStr(realType))
//                    assertMethod = AssertMethod.GTest.EXPECT_STREQ;
//                else
//                    assertMethod = AssertMethod.GTest.EXPECT_EQ;
//
//                String gTestAssertStm = String.format("%s(%s, %s)", assertMethod, actualName, expectedName);
//
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }

    @Override
    public String assertNotEqual(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        // Get assert method
        String assertMethod;
        String realType = getRealType();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);
//            if (Environment.getInstance().isC()) {
//
//                if (VariableTypeUtils.isNumBasicFloat(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_DOUBLE_NOT_EQUAL;
//                else if (VariableTypeUtils.isChOneLevel(realType)
//                        || VariableTypeUtils.isChOneDimension(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_STR_NOT_EQUAL;
//                else if (VariableTypeUtils.isOneDimension(realType)
//                        || VariableTypeUtils.isMultipleDimension(realType)
//                        || VariableTypeUtils.isPointer(realType))
//                    assertMethod = AssertMethod.CU.ASSERT_PTR_NOT_EQUAL;
//                else
//                    assertMethod = AssertMethod.CU.ASSERT_NOT_EQUAL;
//
//                String cuAssertStm;
//                if (assertMethod.equals(AssertMethod.CU.ASSERT_DOUBLE_NOT_EQUAL)) {
//                    cuAssertStm = String.format("%s(%s, %s, 0.000001);", assertMethod, actualName, expectedName);
//                } else {
//                    cuAssertStm = String.format("%s(%s, %s);", assertMethod, actualName, expectedName);
//                }
//
//                assertion.append(cuAssertStm);
//
//            } else {
//
//                if (VariableTypeUtils.isChOneLevel(realType)
//                        || VariableTypeUtils.isChOneDimension(realType)
//                        || VariableTypeUtils.isStr(realType))
//                    assertMethod = AssertMethod.GTest.EXPECT_STRNE;
//                else
//                    assertMethod = AssertMethod.GTest.EXPECT_NE;
//
//                String gTestAssertStm = String.format("%s(%s, %s)", assertMethod, actualName, expectedName);
//
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }

    @Override
    public String assertLower(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);
//            if (Environment.getInstance().isC()) {
//                String cuAssertStm = String.format("%s(%s, %s);", AssertMethod.CU.ASSERT_LOWER, actualName, expectedName);
//                assertion.append(cuAssertStm);
//
//            } else {
//                String gTestAssertStm = String.format("%s(%s, %s)", AssertMethod.GTest.EXPECT_LT, actualName, expectedName);
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }

    @Override
    public String assertGreater(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);
//            if (Environment.getInstance().isC()) {
//                String cuAssertStm = String.format("%s(%s, %s);", AssertMethod.CU.ASSERT_GREATER, actualName, expectedName);
//                assertion.append(cuAssertStm);
//
//            } else {
//                String gTestAssertStm = String.format("%s(%s, %s)", AssertMethod.GTest.EXPECT_GT, actualName, expectedName);
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }

    @Override
    public String assertLowerOrEqual(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);
//            if (Environment.getInstance().isC()) {
//                String cuAssertStm = String.format("%s(%s, %s);", AssertMethod.CU.ASSERT_LOWER_OR_EQUAL, actualName, expectedName);
//                assertion.append(cuAssertStm);
//
//            } else {
//                String gTestAssertStm = String.format("%s(%s, %s)", AssertMethod.GTest.EXPECT_LE, actualName, expectedName);
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }


    @Override
    public String assertGreaterOrEqual(String source, String target) {
        StringBuilder assertion = new StringBuilder();

        if (haveValue()) {
            String expectedName = getVirtualName();
            String actualName = expectedName.replace(source, target);
//            if (Environment.getInstance().isC()) {
//                String cuAssertStm = String.format("%s(%s, %s);", AssertMethod.CU.ASSERT_GREATER_OR_EQUAL, actualName, expectedName);
//                assertion.append(cuAssertStm);
//
//            } else {
//                String gTestAssertStm = String.format("%s(%s, %s)", AssertMethod.GTest.EXPECT_GE, actualName, expectedName);
//                assertion.append(gTestAssertStm);
//
//                assertion.append(IGTestConstant.LOG_FUNCTION_CALLS);
//
//            }

            String resultExportStm = getExportExeResultStmByType(getRealType(), actualName, expectedName);
            assertion.append(resultExportStm);
        }

        return assertion.toString();
    }

    private String getExportExeResultStmByType(String realType, String actualName, String expectedName) {
        String statement;

        if (VariableTypeUtils.isNumBasicFloat(realType))
            statement = getExportExeDoubleResultStm(actualName, expectedName, expectedName);
        else if (VariableTypeUtils.isChOneLevel(realType)
                || VariableTypeUtils.isChOneDimension(realType))
            statement = getExportExeResultStm(actualName, expectedName, expectedName);
        else if (VariableTypeUtils.isOneDimension(realType)
                || VariableTypeUtils.isMultipleDimension(realType)
                || VariableTypeUtils.isPointer(realType))
            statement = getExportExePtrResultStm(actualName, expectedName, expectedName);
        else
            statement = getExportExeResultStm(actualName, expectedName, expectedName);

        return statement;
    }

    protected boolean haveValue() {
        return node.haveValue();
    }
}
