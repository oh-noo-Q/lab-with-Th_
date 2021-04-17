package entities.testdata;

import entities.SpecialCharacter;

/**
 * Other unsupported data types
 */
public class OtherUnresolvedDataNode extends UnresolvedDataNode {

    @Override
    public boolean haveValue() {

        //return userCode != null;
        return true;
    }

    @Override
    public String getInputForGoogleTest() {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

//        if (userCode == null) return "";
//        String userCodeContent = "";//userCode.getContent();
//
//        if (userCodeContent == null || userCodeContent.trim().length() == 0)
//            if (isPassingVariable())
//                return getRawType() + " " + getVituralName() + SpecialCharacter.END_OF_STATEMENT;
//            else
//                return "";
//        else if (userCodeContent.trim().endsWith(DEFAULT_USER_CODE)) {
//            return "/*No code*/";
//        } else {
//            String normalize = userCodeContent.replace(VALUE_TAG, getVituralName());
//
//            if (normalize.trim().matches("sizeof\\(.+\\)=.+")) {
//                return String.format("/* Sizeof usage error: %s */", normalize);
//            }
//
//            return normalize;
//        }
        return "";
    }

//    @Override
//    public void setUserCode(AbstractUserCode userCode) {
//        this.userCode = userCode;
//    }

//    @Override
//    public AbstractUserCode getUserCode() {
//        if (userCode == null) {
//            userCode = new ParameterUserCode();
//            userCode.setContent(generateInitialUserCode() + DEFAULT_USER_CODE);
//        }
//
//        return userCode;
//    }

//    @Override
//    public String getContextPath() {
//        UnitNode unitNode = getUnit();
//        String filePath;
//
//        if (unitNode != null) {
//            filePath = unitNode.getSourceNode().getAbsolutePath();
//        } else {
//            String[] pathItems = getCorrespondingVar().getAbsolutePath().split(File.separator);
//            filePath = pathItems[1];
//        }
//
//        return filePath;
//    }

    /**
     * [0] included path
     * [1] temporary path
     */
//    @Override
//    public String getTemporaryPath() {
//        UnitNode unitNode = getUnit();
//        String filePath;
//        String temporaryPath;
//
//        if (unitNode != null) {
//            filePath = unitNode.getSourceNode().getAbsolutePath();
//            temporaryPath = ProjectClone2.getClonedFilePath(filePath);
//        } else {
//            String[] pathItems = getCorrespondingVar().getAbsolutePath().split(File.separator);
//            filePath = pathItems[1];
//            temporaryPath = SystemLibrary.getLibrariesDirectory() + filePath + SystemLibrary.LIBRARY_EXTENSION;
//        }
//
//        int lastSeparator = temporaryPath.lastIndexOf(File.separator) + 1;
//        temporaryPath = temporaryPath.substring(0, lastSeparator) + "temporary.cpp";
//
//        return temporaryPath;
//    }

//    @Override
//    public String generateInitialUserCode() {
//        String input = "";
//
//        String typeVar = getRawType();
//
//        if (isExternel())
//            typeVar = "";
//
//        // generate the statement
//        if (this.isPassingVariable()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        } else if (this.isAttribute()) {
//            input += getVituralName() + " = ";
//
//        } else if (this.isArrayElement()) {
//            input += getVituralName() + " = ";
//
//        } else if (isSTLListBaseElement()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        } else if (this.isInConstructor()) {
//            input += typeVar + " " + getVituralName() + " = ";
//
//        }
//
//        return input;
//    }
}
