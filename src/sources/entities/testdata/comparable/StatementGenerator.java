package entities.testdata.comparable;

import entities.common.DriverConstant;
import entities.testdata.ValueDataNode;

public abstract class StatementGenerator {

    protected final ValueDataNode node;

    public StatementGenerator(ValueDataNode node) {
        this.node = node;
    }

    protected String getVirtualName() {
        return node.getVituralName();
    }

    protected String getRealType() {
        return node.getRealType();
    }

    protected String getRawType() {
        return node.getRawType();
    }

    public static String getExportExeResultStm(String actualName, String expectedName, String expectedValue) {
        return String.format(DriverConstant.ASSERT + "(\"%s\", %s, \"%s\", %s);\n",
                actualName, actualName, expectedName, expectedValue);
    }

    public static String getExportExePtrResultStm(String actualName, String expectedName, String expectedValue) {
        return String.format(DriverConstant.ASSERT_PTR + "(\"%s\", %s, \"%s\", %s);\n",
                actualName, actualName, expectedName, expectedValue);
    }

    public static String getExportExeDoubleResultStm(String actualName, String expectedName, String expectedValue) {
        return String.format(DriverConstant.ASSERT_DOUBLE + "(\"%s\", %s, \"%s\", %s);\n",
                actualName, actualName, expectedName, expectedValue);
    }
}
