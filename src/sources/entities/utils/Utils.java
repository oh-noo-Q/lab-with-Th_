package entities.utils;

import com.vnu.fit.graph.models.ast.FunctionNode;
import entities.IRegex;
import entities.SpecialCharacter;
import entities.UETLogger;
import entities.common.IGTestConstant;
import entities.parser.IProjectType;
import entities.parser.SourcecodeFileParser;
import entities.parser.dependency.Dependency;
import entities.parser.object.*;
import entities.cfg.*;
import entities.solverhelper.ExpressionRewriterUtils;
import entities.solverhelper.memory.*;

import entities.search.Search;
import entities.testdata.*;
import entities.testdata.stl.ListBaseDataNode;
import org.apache.commons.io.FileDeleteStrategy;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTExpressionStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTProblemDeclaration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils implements IRegex
{
    /**
     *
     */
    public static final int UNDEFINED_TO_INT = -9999;
    public static final float UNDEFINED_TO_DOUBLE = -9999;
    final static UETLogger logger = UETLogger.get(Utils.class);
    public static boolean containFunction = false;
    static boolean containBlock = false;
    /**
     *
     */
    public static IASTNode output = null;

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean isUnix() {
        return System.getProperty("os.name").toLowerCase().contains("nix")
                || System.getProperty("os.name").toLowerCase().contains("nux")
                || System.getProperty("os.name").toLowerCase().contains("aix")
                || System.getProperty("os.name").toLowerCase().contains("centos");
    }

    public static boolean isSolaris() {
        return System.getProperty("os.name").toLowerCase().contains("sunos");
    }

    public static String normalizePath(String path) {
        path = path.replace("\r", "").replace("\n", "");
        String singleBackSlash = "\\";
        String doubleBackSlash = singleBackSlash + singleBackSlash;
        String singleSlash = "/";

        return path.replace(singleBackSlash, File.separator)
                .replace(singleSlash, File.separator)
                .replace(doubleBackSlash, File.separator);
    }

    public static String doubleNormalizePath(String path) {
        String singleBackSlash = "\\";
        String doubleBackSlash = singleBackSlash + singleBackSlash;
        String singleSlash = "/";

        String result = normalizePath(path);

        if (!File.separator.equals(singleSlash)) {
            result = result.replace(File.separator, doubleBackSlash);
        }

        return result;
    }

    public static Class<?>[] getAllSubClass(Class<?> c, String... packages) {
        List<Class<?>> subTypes = new ArrayList<>();

//        if (packages.length == 0) {
//            Reflections reflections = new Reflections(c);
//            subTypes.addAll(reflections.getSubTypesOf(c));
//        } else {
//            for (String p : packages) {
//                Reflections reflections = new Reflections(p, c);
//                subTypes.addAll(reflections.getSubTypesOf(c));
//            }
//        }

        return subTypes.toArray(new Class<?>[0]);
    }

    /**
     * Shorten ast node. <br/>
     * Ex:"(a)" -----> "a" <br/>
     * Ex: "(!a)" --------> "!a"
     *
     * @param ast
     * @return
     */
    public static IASTNode shortenAstNode(IASTNode ast) {
        IASTNode tmp = ast;
        /*
         * Ex:"(a)" -----> "a"
         *
         * Ex: "(!a)" --------> !a
         */
        while ((tmp instanceof CPPASTExpressionStatement || tmp instanceof ICPPASTUnaryExpression
                && tmp.getRawSignature().startsWith("(") && tmp.getRawSignature().endsWith(")"))
                && tmp.getChildren().length == 1 && !tmp.getRawSignature().startsWith("!"))
            tmp = tmp.getChildren()[0];

        return tmp;
    }

    public static int countCharIn(String str, char ch) {
        return (int) str.chars().filter(c -> c == ch).count();
    }

    public static boolean isCondition(String content) {
        /*
         * Get type of the statement
         */
        boolean isCondition = false;

        // special case: content= "a"
        if (content.matches(IRegex.NAME_REGEX))
            isCondition = true;

        else
            /*
             * Ex: char c = static_cast<char>(x)
             *
             * Ex: char c = static_cast<char>(x);
             *
             * Ex: cout << "A";
             */
            if (content.endsWith(SpecialCharacter.END_OF_STATEMENT) || content.contains(" = ")
                    || Utils.containRegex(content, "\\b=\\b") || content.startsWith("cout ") || content.startsWith("cout<<")
                    || content.startsWith("std::"))
                isCondition = false;
            else {
                final String[] CONDITION_SIGNALS = new String[] { "!=", "<=", ">=", "==", ">", "<", "!" };

                for (String conditionSignal : CONDITION_SIGNALS)
                    if (content.contains(conditionSignal))
                        isCondition = true;
            }
        return isCondition;
    }

    /**
     * Check whether a string contain regex or not
     *
     * @param src
     * @param regex
     * @return
     */
    public static boolean containRegex(String src, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(src);

        return m.find();
    }

    /**
     * Convert a string into regex
     *
     * @param str
     * @return
     */
    public static String toRegex(String str) {
        str = str.replace("[", "\\[").replace("]", "\\]").replace("(", "\\(").replace(")", "\\)").replace(".", "\\.")
                .replace("*", "\\*").replace(" ", "\\s*").replace("_", "\\_");

        /*
         * Add bound of word at the beginning
         */
        if (str.toCharArray()[0] >= 'A' && str.toCharArray()[0] <= 'Z'
                || str.toCharArray()[0] >= 'a' && str.toCharArray()[0] <= 'z')
            str = "\\b" + str;

        /*
         * Add bound of word at the end
         */
        int last = str.toCharArray().length - 1;
        if (str.toCharArray()[last] >= 'A' && str.toCharArray()[last] <= 'Z'
                || str.toCharArray()[last] >= 'a' && str.toCharArray()[last] <= 'z')
            str += "\\b";
        return str;
    }

    public static String asIndex(int i) {
        return "[" + i + "]";
    }

    public static String asIndex(String str) {
        return "[" + str + "]";
    }

    /**
     * Convert List<String> into String[]
     *
     * @param list
     * @return
     */
    public static String[] convertToArray(List<String> list) {
        String[] strarray = list.toArray(new String[list.size()]);
        return strarray;
    }

    public static File copy(String originalFolder) throws IOException {
        originalFolder = Utils.normalizePath(originalFolder);

        String copyFolder = originalFolder;
        if (originalFolder.endsWith(File.separator))
            copyFolder = originalFolder.substring(0, originalFolder.length() - 1);

        copyFolder += "_copy";
        while (new File(copyFolder).exists())
            copyFolder += "1";
        Utils.copyFolder(new File(originalFolder), new File(copyFolder));
        return new File(copyFolder);
    }

    public static void copyFolder(File src, File dest) throws IOException {

        if (src.isDirectory()) {

            // if directory not exists, create it
            if (!dest.exists())
                dest.mkdir();

            // list all the directory contents
            String[] files = src.list();

            for (String file : files) {
                // construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // recursive copy
                Utils.copyFolder(srcFile, destFile);
            }

        } else {
            // if file, then copy it
            // Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            // copy the file content in bytes
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);

            in.close();
            out.close();
        }
    }

    public static void deleteFileOrFolder(File path) {
        if (path != null && path.exists())
            try {
                FileDeleteStrategy.FORCE.delete(path);
                // FileUtils.deleteDirectory(new File(path));
                if (!path.exists()) {
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(30);
                    Utils.deleteFileOrFolder(path);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
    }

    /**
     * Láº¥y mÃ£ ASCII cá»§a kÃ­ tá»±
     *
     * @param ch
     * @return
     */
    public static int getASCII(char ch) {
        return ch;
    }

    public static INode getClassvsStructvsNamesapceNodeParent(INode n) {
        if (n == null)
            return null;
        else if (n instanceof ClassNode || n instanceof StructNode || n instanceof NamespaceNode)
            return n;
        else {
            if (n instanceof AbstractFunctionNode)
                if (((AbstractFunctionNode) n).getRealParent() != null)
                    return Utils.getClassvsStructvsNamesapceNodeParent(((AbstractFunctionNode) n).getRealParent());
            return Utils.getClassvsStructvsNamesapceNodeParent(n.getParent());
        }
    }

    public static INode getTopLevelClassvsStructvsNamesapceNodeParent(INode n) {
        if (n == null)
            return null;
        else if (n instanceof ClassNode || n instanceof StructNode || n instanceof NamespaceNode)
            if (n.getParent() != null && n.getParent() instanceof SourcecodeFileNode)
                return n;
            else
                return Utils.getTopLevelClassvsStructvsNamesapceNodeParent(n.getParent());
        else if (n instanceof IFunctionNode)
            return Utils.getTopLevelClassvsStructvsNamesapceNodeParent(((IFunctionNode) n).getRealParent());
        else
            return Utils.getTopLevelClassvsStructvsNamesapceNodeParent(n.getParent());
    }

    /**
     * Get ast corresponding to statement, e.g., x=y+2
     *
     * @param content
     * @return
     */
    public static IASTNode convertToIAST(String content) {
        IASTNode ast;

        /*
         * Get type of the statement
         */
        boolean isCondition = Utils.isCondition(content);
        /*
         * The statement is assignment
         */
        if (!isCondition) {
            content += content.endsWith(SpecialCharacter.END_OF_STATEMENT) ? "" : SpecialCharacter.END_OF_STATEMENT;

            ICPPASTFunctionDefinition fn = getFunctionsinAST(("void test(){" + content + "}").toCharArray())
                    .get(0);
            ast = fn.getBody().getChildren()[0];
        } else
            /*
             * The statement is condition
             */ {
            ICPPASTFunctionDefinition fn = getFunctionsinAST(
                    ("void test(){if (" + content + "){}}").toCharArray()).get(0);
            ast = fn.getBody().getChildren()[0].getChildren()[0];
        }
        return ASTUtils.shortenAstNode(ast);
    }

    /**
     * Láº¥y danh sÃ¡ch táº¥t cáº£ má»�i hÃ m á»Ÿ Ä‘á»‹nh
     * dáº¡ng AST
     *
     * @param sourcecode
     * @return
     */
    public static List<ICPPASTFunctionDefinition> getFunctionsinAST(char[] sourcecode) {
        List<ICPPASTFunctionDefinition> output = new ArrayList<>();

        try {
            IASTTranslationUnit unit = Utils.getIASTTranslationUnitforCpp(sourcecode);

            if (unit.getChildren()[0] instanceof CPPASTProblemDeclaration)
                unit = Utils.getIASTTranslationUnitforC(sourcecode);

            ASTVisitor visitor = new ASTVisitor() {
                @Override
                public int visit(IASTDeclaration declaration) {
                    if (declaration instanceof ICPPASTFunctionDefinition) {
                        output.add((ICPPASTFunctionDefinition) declaration);
                        return ASTVisitor.PROCESS_SKIP;
                    }
                    return ASTVisitor.PROCESS_CONTINUE;
                }
            };

            visitor.shouldVisitDeclarations = true;

            unit.accept(visitor);
        } catch (Exception e) {

        }
        return output;
    }

    public static IASTTranslationUnit getIASTTranslationUnitforCpp(char[] code) throws Exception {
        File filePath = new File("");
        FileContent fc = FileContent.create(filePath.getAbsolutePath(), code);
        Map<String, String> macroDefinitions = new HashMap<>();
        String[] includeSearchPaths = new String[0];
        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GPPLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
    }

    public static IASTTranslationUnit getIASTTranslationUnitforC(char[] code) throws Exception {
        File filePath = new File("");
        FileContent fc = FileContent.create(filePath.getAbsolutePath(), code);
        Map<String, String> macroDefinitions = new HashMap<>();
        String[] includeSearchPaths = new String[0];
        IScannerInfo si = new ScannerInfo(macroDefinitions, includeSearchPaths);
        IncludeFileContentProvider ifcp = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex idx = null;
        int options = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();
        return GCCLanguage.getDefault().getASTTranslationUnit(fc, si, ifcp, idx, options, log);
    }

    /**
     * Láº¥y danh sÃ¡ch chá»‰ sá»‘ máº£ng
     *
     * @param origin
     *            VD: a[3][2]
     * @return VD: 3,2
     * @problem ChÆ°a xá»­ lÃ½ chá»‰ sá»‘ máº£ng chá»©a
     *          chá»‰ sá»‘ máº£ng khÃ¡c. VD: a[1+b[2]]
     */
    public static List<String> getIndexOfArray(String origin) {
        List<String> output = new ArrayList<>();

        // "a[]" --- > "a[<something>]"
        final int DEFAULT_INDEX = 23424131;
        String constraint = origin.replaceAll("\\[\\s*\\]", "[" + DEFAULT_INDEX + "]");

        // add prefix to analyze cases such as "int[3]"
        final String PREFIX = "AKA_PREFIX";
        constraint = PREFIX + constraint;

        IASTNode ast = Utils.convertToIAST(constraint + "==" + 0);
        if (ast instanceof ICPPASTBinaryExpression) {
            IASTExpression left = ((ICPPASTBinaryExpression) ast).getOperand1();
            if (left instanceof ICPPASTArraySubscriptExpression) {
                int maxCount = 0;
                while (left instanceof ICPPASTArraySubscriptExpression) {
                    if (++maxCount >= 10)
                        break; // avoid infinite loop

                    ICPPASTArraySubscriptExpression castedLeft = (ICPPASTArraySubscriptExpression) left;
                    ICPPASTInitializerClause index = castedLeft.getArgument();

                    if (index.getRawSignature().equals(DEFAULT_INDEX+""))
                        output.add(0, "");
                    else
                        output.add(0, index.getRawSignature());

                    left = castedLeft.getArrayExpression();
                }
            }
        } else if (ast instanceof IASTProblemHolder && origin.contains(SpecialCharacter.POINTER)) {
            output = getIndexOfArray(origin.replaceAll(IRegex.POINTER, SpecialCharacter.EMPTY));
        }
        return output;
    }

    public static void main(String[] args) {
        System.out.println(Utils.getIndexOfArray("int[4][2]"));
        System.out.println(Utils.getNameVariable("int[4][2]"));
    }
    /**
     * Get name of variable
     * Ex: "a[2]" ----->"a"
     * Ex: "a.b[2]" ----->"a.b"
     * @param variableName
     * @return
     */
    public static String getNameVariable(String variableName) {
        String name = variableName;

        // "a[]" --- > "a[<something>]"
        final int DEFAULT_INDEX = 23424131;
        variableName = variableName.replaceAll("\\[\\s*\\]", "[" + DEFAULT_INDEX + "]");

        // add prefix to analyze cases such as "int[3]"
        final String PREFIX = "AKA_PREFIX";
        variableName = PREFIX + variableName;

        IASTNode ast = Utils.convertToIAST(variableName + "==" + 0);
        if (ast instanceof ICPPASTBinaryExpression) {
            IASTExpression left = ((ICPPASTBinaryExpression) ast).getOperand1();
            if (left instanceof ICPPASTArraySubscriptExpression) {
                int maxCount = 0;
                while (left instanceof ICPPASTArraySubscriptExpression) {
                    if (++maxCount >= 10)
                        break; // avoid infinite loop

                    ICPPASTArraySubscriptExpression castedLeft = (ICPPASTArraySubscriptExpression) left;
                    ICPPASTInitializerClause index = castedLeft.getArgument();

                    left = castedLeft.getArrayExpression();
                }

                name = left.getRawSignature();
            }
        }

        name = name.replaceFirst(PREFIX, ""); // remove prefix
        return name;
    }

    public static INode getRoot(INode n) {
        if (n == null)
            return null;
        else if (n.getParent() == null)
            return n;
        else
            return Utils.getRoot(n.getParent());

    }

    public static String getRelativePath(INode node) {
        String path = node.getAbsolutePath();

        INode root = Utils.getRoot(node);

        path = path.substring(root.getAbsolutePath().length());

        if (path.startsWith(File.separator))
            path = path.substring(1);

        return path;
    }

    /**
     * Get the source code file containing a specified node
     *
     * @param n
     * @return
     */
    public static ISourcecodeFileNode getSourcecodeFile(INode n) {
        if (n == null)
            return null;
        else if (n instanceof ISourcecodeFileNode)
            return (ISourcecodeFileNode) n;
        else
            return Utils.getSourcecodeFile(n.getParent());

    }

    public static INode getProjectNode(INode n) {
        if (n == null)
            return null;
        else if (n instanceof IProjectNode)
            return n;
        else
            return Utils.getProjectNode(n.getParent());
    }

    /**
     * Lay node cha la class | struct
     */
    public static INode getStructureParent(INode n) {
        if (n == null)
            return null;
        else if (n instanceof ClassNode || n instanceof StructNode)
            return n;
        else
            return Utils.getStructureParent(n.getParent());

    }

    public static <T> boolean isAvailable(List<T> l) {
        return l != null && l.size() != 0;
    }

    public static boolean isAvailable(String s) {
        return s != null && s.length() != 0;
    }

    public static boolean isSpecialChInVisibleRange(int ASCII) {
        return ASCII == 34 /* nhay kep */ || ASCII == 92 /* gach cheo */
                || ASCII == 39 /* nhay don */;
    }

    /**
     * Check whether the character corresponding to ASCII can be shown in screen or
     * not
     *
     * @param ASCII
     * @return
     */
    public static boolean isVisibleCh(int ASCII) {
        return ASCII >= 32 && ASCII <= 126;
    }

    public static String putInString(String str) {
        return "\"" + str + "\"";
    }

    public static String readFileContent(File file) {
        return Utils.readFileContent(file.getAbsolutePath());
    }

    public static String readFileContent(INode n) {
        return Utils.readFileContent(n.getAbsolutePath());
    }

    public static String readResourceContent(String relativePath) {
        InputStream in = Utils.class.getResourceAsStream(relativePath);

        StringBuilder template = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while ((line = reader.readLine()) != null)
                template.append(line).append(SpecialCharacter.LINE_BREAK);
        } catch (IOException ex) {
            logger.error("Cant read resource content from " + relativePath);
        }

        return template.toString();
    }

    /**
     * Doc noi dung file
     *
     * @param filePath
     *            duong dan tuyet doi file
     * @return noi dung file
     */
    public static String readFileContent(String filePath) {
        StringBuilder fileData = new StringBuilder(3000);
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[10];
            int numRead;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fileData.toString();
        }
    }

    /**
     * Cusom splitting
     *
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> split(String str, String delimiter) {
        List<String> output = new ArrayList<>();

		/*
		  In case we need split a path into tokens
		 */
        if (delimiter.equals("\\") || delimiter.equals("/")) {
            str = str.replace("\\", "/");
            delimiter = "/";
        }
        if (str.contains(delimiter)) {

            String[] elements = str.split(delimiter);
            for (String element : elements)
				/*
				  Nhá»¯ng xÃ¢u cÃ³ Ä‘á»™ dÃ i báº±ng 0 thÃ¬ bá»� qua
				 */

                if (element.length() > 0)
                    output.add(element);

        } else
            output.add(str);
        return output;
    }

    public static int toInt(String str) {
		/*
		  Remove bracket from negative number. Ex: Convert (-2) into -2
		 */
        str = str.replaceAll("\\((" + IRegex.NUMBER_REGEX + ")\\)", "$1");

        /*
         */
        boolean isNegative = false;
        if (str.startsWith("-")) {
            str = str.substring(1);
            isNegative = true;
        } else if (str.startsWith("+"))
            str = str.substring(1);
        /*

         */
        int n;
        try {
            n = Integer.parseInt(str);
            if (isNegative)
                n = -n;
        } catch (Exception e) {
            n = Utils.UNDEFINED_TO_INT;
        }
        return n;
    }

    public static double toDouble(String str) {
		/*
		  Remove bracket from negative number. Ex: Convert (-2) into -2
		 */
        str = str.replaceAll("\\((" + IRegex.NUMBER_REGEX + ")\\)", "$1");

        /*

         */
        boolean isNegative = false;
        if (str.startsWith("-")) {
            str = str.substring(1);
            isNegative = true;
        } else if (str.startsWith("+"))
            str = str.substring(1);
        /*

         */
        double n;
        try {
            n = Double.parseDouble(str);
            if (isNegative)
                n = -n;
        } catch (Exception e) {
            n = Utils.UNDEFINED_TO_DOUBLE;
        }
        return n;
    }

    public static String toUpperFirstCharacter(String str) {
        StringBuilder output;
        char[] c = str.toCharArray();

        output = new StringBuilder((c[0] + "").toUpperCase());
        for (int i = 1; i < c.length; i++)
            output.append(c[i]);

        return output.toString();
    }

    /**
     * Specify type of project that belong to which IDE (e.g., Eclipse, Dev-Cpp,
     * Code block, Visual studio)
     *
     //	 * @param projectPath
     *            : path of Project
     * @return
     */
    public static int getTypeOfProject(String projectPath) {
        File dir = new File(projectPath);

		/*
		  Project is created without using IDE or not. It only has a makefile.
		 */
        final String[] MAKEFILE_PROJECT_SIGNAL = new String[] { "Makefile" };
        for (String signal : MAKEFILE_PROJECT_SIGNAL) {
            if (new File(projectPath + File.separator + signal).exists()) {
                logger.debug("Is custom makefile project");
                return IProjectType.PROJECT_CUSTOMMAKEFILE;
            }
        }

		/*
		  Project is created by using IDE Dev-Cpp
		 */
        final String[] DEV_CPP_PROJECT_SIGNAL = new String[] { ".win" };
        for (String signal : DEV_CPP_PROJECT_SIGNAL)
            if (dir.listFiles((dir1, name) -> name.endsWith(signal)).length > 0) {
                logger.debug("Is DevCpp project");
                return IProjectType.PROJECT_DEV_CPP;
            }

		/*
		  Project is created by using IDE Code block
		 */
        final String[] CODE_BLOCK_PROJECT_SIGNAL = new String[] { ".cbp" };
        for (String signal : CODE_BLOCK_PROJECT_SIGNAL)
            if (dir.listFiles((dir1, name) -> name.endsWith(signal)).length > 0) {
                logger.debug("Is code block project");
                return IProjectType.PROJECT_CODEBLOCK;
            }

		/*
		  Project is created by using IDE Visual Studio
		 */
        final String[] CODE_VISUAL_STUDIO_PROJECT_SIGNAL = new String[] { ".vcxproj", ".sln" };
        for (String signal : CODE_VISUAL_STUDIO_PROJECT_SIGNAL)
            if (dir.listFiles((dir1, name) -> name.endsWith(signal)).length > 0) {
                logger.debug("Is Visual studio project");
                return IProjectType.PROJECT_VISUALSTUDIO;
            }

		/*
		  Project is created by using IDE Eclipse
		 */
        final String[] ECLIPSE_STUDIO_PROJECT_SIGNAL = new String[] { ".cproject", ".project" };
        for (String signal : ECLIPSE_STUDIO_PROJECT_SIGNAL)
            if (dir.listFiles((dir1, name) -> name.endsWith(signal)).length > 0) {
                logger.debug("Is Eclipse project");
                return IProjectType.PROJECT_ECLIPSE;
            }

        return IProjectType.PROJECT_UNKNOWN_TYPE;
    }

    public static boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                return true;
            } catch (IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }

    public static void writeContentToFile(String content, INode n) {
        Utils.writeContentToFile(content, n.getAbsolutePath());
    }

    public static void writeContentToFile(String content, String filePath) {
        try {
            new File(filePath).getParentFile().mkdirs();
            PrintWriter out = new PrintWriter(filePath);
            out.println(content);
            out.close();
        } catch (FileNotFoundException e) {
            logger.error("Cant write content to " + filePath + " because " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean isProjectNode(INode node) {
        for (INode nodeChild : node.getChildren())
            if (nodeChild.getParent() == null || nodeChild.getNewType().endsWith(".vcxproj")
                    || nodeChild.getNewType().endsWith("win"))
                return true;
        return false;
    }

    /**
     * @see #{CustomJevalTest.java}
     * @param expression
     * @return
     */
    public static String transformFloatNegativeE(String expression) {
        Matcher m = Pattern.compile("\\d+E-\\d+").matcher(expression);
        while (m.find()) {
            String beforeE = expression.substring(0, expression.indexOf("E-"));
            String afterE = expression.substring(expression.indexOf("E-") + 2);

            StringBuilder newValue = new StringBuilder();

            if (Utils.toInt(afterE) != Utils.UNDEFINED_TO_INT) {
                int numDemicalPoint = Utils.toInt(afterE);

                if (numDemicalPoint == 0) {
                    newValue = new StringBuilder(beforeE);

                } else if (beforeE.length() > numDemicalPoint) {
                    for (int i = 0; i < beforeE.length() - numDemicalPoint; i++)
                        newValue.append(beforeE.toCharArray()[i]);
                    newValue.append(".");

                    for (int i = beforeE.length() - numDemicalPoint; i < beforeE.length(); i++) {
                        newValue.append(beforeE.toCharArray()[i]);
                    }
                } else {
                    newValue.append("0.");
                    for (int i = 0; i <= numDemicalPoint - 1 - beforeE.length(); i++) {
                        newValue.append("0");
                    }
                    newValue.append(beforeE);
                }
            }

            expression = expression.replace(m.group(0), newValue.toString());
        }
        return expression;
    }

    public static String transformFloatPositiveE(String expression) {
        Matcher m = Pattern.compile("\\d+E\\+\\d+").matcher(expression);
        while (m.find()) {
            String beforeE = expression.substring(0, expression.indexOf("E+"));
            String afterE = expression.substring(expression.indexOf("E+") + 2);

            StringBuilder newValue = new StringBuilder();

            if (Utils.toInt(afterE) != Utils.UNDEFINED_TO_INT) {
                int numDemicalPoint = Utils.toInt(afterE);

                if (numDemicalPoint == 0) {
                    newValue = new StringBuilder(beforeE);

                } else {
                    newValue = new StringBuilder(beforeE);
                    for (int i = 0; i < numDemicalPoint; i++)
                        newValue.append("0");
                }
            }

            expression = expression.replace(m.group(0), newValue.toString());
        }
        return expression;
    }

    public static void openFolderorFileOnExplorer(String path){// throws OpenFileException {
//        if (new File(path).exists()) {
//            if (Utils.isWindows()) {
//                try {
//                    Runtime.getRuntime().exec(new String[]{"notepad.exe", new File(path).getName()},
//                            null,
//                            new File(path).getParentFile());
//                } catch (IOException e) {
//                    throw new OpenFileException("Unexpected error when opening the target " + path + ". Error code: " + e.getMessage());
//                }
//
//            } else if (Utils.isMac()) {
//                try {
//                    Runtime.getRuntime().exec(new String[]{"open", new File(path).getName()},
//                            null,
//                            new File(path).getParentFile());
//                } catch (IOException e) {
//                    throw new OpenFileException("Unexpected error when opening the target " + path + ". Error code: " + e.getMessage());
//                }
//
//            } else if (Utils.isUnix()) {
//                try{
//                    Runtime.getRuntime().exec(new String[]{"nautilus", new File(path).getName()},
//                            null,
//                            new File(path).getParentFile());
//                } catch (IOException e) {
//                    throw new OpenFileException("Unexpected error when opening the target " + path + ". Error code: " + e.getMessage());
//                }
//
//            } else {
//                throw new OpenFileException("Does not support to open the target " + path + " on this OS");
//            }
//        }else{
//            throw new OpenFileException("The target " + path + " does not exist!");
//        }
    }

    /**
     * Get the reduce index of array item
     * <p>
     * Ex: a[1+2][3] --------> [3][3]
     *
     * @param arrayItem
     * @param table
     * @return
     * @throws Exception
     */
    public static String getReducedIndex(String arrayItem, IVariableNodeTable table) throws Exception {
        StringBuilder index = new StringBuilder();
        List<String> indexes = Utils.getIndexOfArray(arrayItem);

        for (String indexItem : indexes) {
            indexItem = ExpressionRewriterUtils.rewrite(table, indexItem);
            index.append(Utils.asIndex(indexItem));
        }
        return index.toString();
    }

    public static String computeMd5(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash){
                sb.append(String.format("%02x", b&0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return digest;
    }

    public static void viewDependency(INode node){
        // export dependency to string
        String dependencyStr = "class " + node.getClass().getSimpleName() + "\n\n";
        for (Dependency d : node.getDependencies()) {
            String content = "[" + d.getClass().getSimpleName() + "]\n";
            content += "start: " + d.getStartArrow().getAbsolutePath() + "\n";
            content += "end: " + d.getEndArrow().getAbsolutePath() + "\n\n";
            if (!dependencyStr.contains(content)) {
                dependencyStr += content;
            }
        }

        if (node instanceof IFunctionNode) {
            dependencyStr += "\n\n----------------\nDependency of arguments:\n";
            for (IVariableNode var : ((IFunctionNode) node).getArguments())
                for (Dependency d : var.getDependencies()) {
                    String content = "[" + d.getClass().getSimpleName() + "]\n";
                    content += "start: " + d.getStartArrow().getAbsolutePath() + "\n";
                    content += "end: " + d.getEndArrow().getAbsolutePath() + "\n\n";
                    if (!dependencyStr.contains(content)) {
                        dependencyStr += content;
                    }
                }

            dependencyStr += "\n\n----------------\nSource code:\n";
            dependencyStr += ((IFunctionNode) node).getAST().getRawSignature();
        }

        // export description of dependency to file, then load on screen
        String tmpFile ="";// new WorkspaceConfig().fromJson().getDependencyDirectory() + File.separator + "dependency_tmp.log";
        Utils.writeContentToFile(dependencyStr, tmpFile);

        // waiting for the generation of dependency file
        int maxLoading = 10;
        while (!new File(tmpFile).exists() && maxLoading >= 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            maxLoading--;
        }

        if (new File(tmpFile).exists())
            try {
                logger.debug("open " + tmpFile + " to show dependency of " + node.getAbsolutePath());
                Utils.openFolderorFileOnExplorer(tmpFile);
            } catch (Exception e) {
//                e.printStackTrace();
//                UIController.showErrorDialog("Can not load dependency of " + node.getAbsolutePath(), "Dependency loader failed", "Dependency loader");
            }
        else {
//            UIController.showErrorDialog("Do not find the dependency file " + tmpFile + " of " + node.getAbsolutePath(), "Dependency loader failed", "Dependency loader");
        }
    }

    /**
     *
     * @param expectedStartLine the line where we want to put the content in, [1..]
     * @param expectedNodeOffset the offset where we want to put the content in
     * @param oldFunction the content
     * @return
     */
    public static String insertSpaceToFunctionContent(int expectedStartLine, int expectedNodeOffset, String oldFunction) {
        String addition = "";
        for (int i = 0; i < expectedStartLine - 2; i++) {
            addition += "\n";
        }
        int additionalOffset = expectedNodeOffset - addition.length() - 1;
        for (int i = 0; i < additionalOffset; i++) {
            addition += " ";
        }
        if (expectedStartLine > 1)
            addition += "\n";
        return addition + oldFunction;
    }

    public static IASTFunctionDefinition disableMacroInFunction(IASTFunctionDefinition astFunctionNode, IFunctionNode functionNode){
        if (astFunctionNode.getFileLocation() == null)
            return astFunctionNode;

//    	if (functionNode != null)
//    		logger.debug("disableMacroInFunction " + functionNode.getAbsolutePath());
//    	else
//    		logger.debug("disableMacroInFunction");

        IASTFunctionDefinition output = null;
        // insert spaces to ensure that the location of new function and of the old function are the same
        try {
            int startLine = astFunctionNode.getFileLocation().getStartingLineNumber();
            int startOffset = astFunctionNode.getFileLocation().getNodeOffset();
            String content = astFunctionNode.getRawSignature();
            if (functionNode != null && (functionNode instanceof ConstructorNode || functionNode instanceof DestructorNode)
                    && functionNode.getParent() instanceof ClassNode) {
                // put the function in a class to void error when constructing ast
                String className = functionNode.getParent().getName();
                content = "class " + className + "{" + content + "};";
                String newContent = Utils.insertSpaceToFunctionContent(startLine,
                        startOffset - new String("class " + className + "{").length(), content);
                IASTTranslationUnit unit = new SourcecodeFileParser().getIASTTranslationUnit(newContent.toCharArray());
                //                logger.debug("Reconstructed tree: ");
//                ASTUtils.printTreeFromAstNode(unit, "\t");
                output = (IASTFunctionDefinition) unit.getChildren()[0].
                        getChildren()[0].getChildren()[1];

            } else {
                String newContent = Utils.insertSpaceToFunctionContent(startLine, startOffset, content);
                IASTTranslationUnit unit = new SourcecodeFileParser().getIASTTranslationUnit(newContent.toCharArray());
                output = (IASTFunctionDefinition) unit.getChildren()[0];
            }
        } catch (Exception e) {
//			e.printStackTrace();
            output = astFunctionNode;
        } finally {
            // log
            if (output == null || output instanceof CPPASTProblemDeclaration || output.getFileLocation() == null ||
                    output.getFileLocation().getStartingLineNumber() != astFunctionNode.getFileLocation().getStartingLineNumber()
                            && output.getFileLocation().getNodeOffset() != astFunctionNode.getFileLocation().getNodeOffset()) {
                logger.error("Fail to instrument [" + astFunctionNode.getClass() + "] content = " + astFunctionNode.getRawSignature());
            }
            return output;
        }
    }
    /**
     * Create CFG of a function.
     *
     * This function may call to a macro function or not.
     *
     * In case of a call to macro functions, CDT might parse the macro call inside the function, which
     * might lead to the incorrect CFG.
     * For example:
     * #define MACRO_CALL(a) if (a>0) return 1; else return 0;
     * int test(){return MACRO_CALL(a);}
     * Consider test(), we need to get CFG of test() only, without considering the body of MACRO_CALL(a).
     *
     *
     *
     * Therefore, to disable the problem of macro expansion in CFG generation of the function,
     * we need to disable macro.
     */
    public static ICFG createCFG(IFunctionNode fn, String coverageType) throws Exception {
        if (fn == null)
            return null;

        /**
         * Find existing cfg of the function node
         */
        ICFG cfg = null;
//        switch (coverageType) {
//            case EnviroCoverageTypeNode.STATEMENT:
//            case EnviroCoverageTypeNode.BRANCH:
//            case EnviroCoverageTypeNode.BASIS_PATH:{
//                cfg = Environment.getInstance().getCfgsForBranchAndStatement().get(fn.getAbsolutePath());
//                break;
//            }
//            case EnviroCoverageTypeNode.MCDC: {
//                cfg = Environment.getInstance().getCfgsForMcdc().get(fn.getAbsolutePath());
//                break;
//            }
//        }
        if (cfg == null) {
            // STEP 1: Create a function with disable macro flag
            FunctionNode tmpFunction = new FunctionNode();
//            tmpFunction.setAST(disableMacroInFunction(fn.getAST(), fn));
//            tmpFunction.setAbsolutePath(tmpFunction.getAbsolutePath());

            // STEP 2: generate CFG of the alternative function
//            switch (coverageType) {
//                case EnviroCoverageTypeNode.STATEMENT:
//                case EnviroCoverageTypeNode.BRANCH:
//                case EnviroCoverageTypeNode.BASIS_PATH: {
//                    cfg = new CFGGenerationforBranchvsStatementvsBasispathCoverage(tmpFunction).generateCFG();
//                    Environment.getInstance().getCfgsForBranchAndStatement().put(fn.getAbsolutePath(), cfg);
//                    break;
//                }
//
//                case EnviroCoverageTypeNode.MCDC: {
//                    cfg = new CFGGenerationforSubConditionCoverage(tmpFunction).generateCFG();
//                    Environment.getInstance().getCfgsForMcdc().put(fn.getAbsolutePath(), cfg);
//                    break;
//                }
//            }
        }

        if (cfg != null) {
            cfg.setFunctionNode(fn);
            cfg.resetVisitedStateOfNodes();
            cfg.setIdforAllNodes();
        }
        return cfg;
    }

    public static String generateVariableDeclaration(String type, String name) {
        String parameterDeclaration;

        List<String> indexes = Utils.getIndexOfArray(type);

        if (indexes.size() > 0) {
            int idx = type.length() - 1;
            while (type.charAt(idx) == SpecialCharacter.CLOSE_SQUARE_BRACE
                    || type.charAt(idx) == SpecialCharacter.OPEN_SQUARE_BRACE
                    || Character.isDigit(type.charAt(idx)))
                idx--;
            parameterDeclaration = type.substring(0, idx + 1) + " " + name;
            for (String index : indexes)
                parameterDeclaration += "[" + index + "]";

        } else {
            parameterDeclaration = type + " " + name;
        }

        return parameterDeclaration;
    }

    /**
     * Ex: "test(a, b)"
     *
     * @param functionNode
     * @return
     */
    public static StringBuilder generateCallOfArguments(ICommonFunctionNode functionNode){
        StringBuilder functionCall = new StringBuilder();
        functionCall.append("(");
        for (IVariableNode v : functionNode.getArguments())
            if (VariableTypeUtilsForStd.isUniquePtr(v.getRawType()))
                functionCall.append(String.format("std::move(%s),", v.getName()));

            else if (VariableTypeUtils.isNullPtr(v.getRawType())) {
                functionCall.append(NullPointerDataNode.NULL_PTR).append(",");

            } else if (v.resolveCoreType() instanceof FunctionPointerTypeNode && v.getName().isEmpty())
                functionCall.append(((FunctionPointerTypeNode) v.resolveCoreType()).getFunctionName()).append(",");
            else
                functionCall.append(v.getName()).append(",");
        functionCall.append(")");
        functionCall = new StringBuilder(functionCall.toString().replace(",)", ")") + SpecialCharacter.END_OF_STATEMENT);
        return functionCall;
    }

    public static String getFullFunctionCall(ICommonFunctionNode functionNode) {
        INode realParent = functionNode.getParent();

        if (functionNode instanceof IFunctionNode) {
            INode tmpRealParent = ((IFunctionNode) functionNode).getRealParent();
            if (tmpRealParent != null)
                realParent = tmpRealParent;
        }

        StringBuilder functionCall = new StringBuilder();

        if (realParent instanceof SourcecodeFileNode) {
            functionCall.append(functionNode.getSimpleName())
                    .append(generateCallOfArguments(functionNode));

        } else if (realParent instanceof NamespaceNode) {
            // find a list of namespace
            INode namespaceRoot = realParent;
            List<String> namespaces = new ArrayList<>();
            while (namespaceRoot.getParent() != null && namespaceRoot.getParent() instanceof NamespaceNode) {
                namespaces.add(namespaceRoot.getName());
                namespaceRoot = namespaceRoot.getParent();
            }
            namespaces.add(namespaceRoot.getName());

            // generate function call
            StringBuilder scope = new StringBuilder();
            for (String namespace : namespaces)
                scope.insert(0, namespace + SpecialCharacter.STRUCTURE_OR_NAMESPACE_ACCESS);

            functionCall.append(scope)
                    .append(functionNode.getSimpleName())
                    .append(generateCallOfArguments(functionNode));

        } else if (realParent instanceof StructureNode) {
            if (functionNode instanceof ConstructorNode) {
                functionCall = new StringBuilder("new ");
                String type = Search.getScopeQualifier(realParent);
                functionCall.append(type);
            } else {
                String instanceVarName = Search.getScopeQualifier(realParent)
                        .replaceAll("[^\\w]", SpecialCharacter.UNDERSCORE);
                instanceVarName = IGTestConstant.INSTANCE_VARIABLE + SpecialCharacter.UNDERSCORE + instanceVarName;

                functionCall = new StringBuilder(instanceVarName);
                functionCall.append(SpecialCharacter.POINT_TO).append(functionNode.getSingleSimpleName());
            }

            functionCall.append(generateCallOfArguments(functionNode));
        }

        return functionCall.toString();
    }

    public static String[] parseIndexesInput(DataNode node, String input) throws Exception {
        final int MAX_INDEX = 50;

        int dimensions;

        if (node instanceof MultipleDimensionDataNode)
            dimensions = ((MultipleDimensionDataNode) node).getDimensions();
        else
            dimensions = 1;

        String[] expandIndexes = new String[dimensions];

        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(input);

        int dim = 0;

        while (matcher.find()) {
            List<String> indexes = new ArrayList<>();
            String[] items = matcher.group().substring(1, matcher.group().length()-1).split(",");

            for (int j = 0; j< items.length; j++) {
                if (items[j].contains("..")) {
                    int step = 1;

                    if (items[j].contains("/")) {
                        step = Integer.parseInt(items[j].substring(items[j].indexOf("/") + 1));
                        items[j] = items[j].substring(0, items[j].indexOf("/"));
                    }

                    String[] bounds = items[j].split("\\Q..\\E");
                    int start = bounds[0].isEmpty() ? 0 : Integer.parseInt(bounds[0]);
                    int end = -1;

                    if (bounds.length == 1) {
                        if (node instanceof MultipleDimensionDataNode)
                            end = ((MultipleDimensionDataNode) node).getSizes()[dim];
                        else if (node instanceof OneDimensionDataNode)
                            end = ((OneDimensionDataNode) node).getSize();
                        else if (node instanceof PointerDataNode)
                            end = ((PointerDataNode) node).getAllocatedSize();
                        else if (node instanceof ListBaseDataNode)
                            end = ((ListBaseDataNode) node).getSize();
                        else if (node instanceof ValueDataNode)
                            throw new Exception("Don't support to expand " + ((ValueDataNode) node).getRawType());
                    } else if (bounds.length == 2) {
                        end = Integer.parseInt(bounds[1]);
                    } else
                        throw new Exception("Invalid input");

                    if (start < 0 /*|| end > MAX_INDEX*/ || end < 0)
                        throw new Exception("Invalid input");

                    if (end - start > MAX_INDEX)
                        throw new Exception("Expand up to 50 items");

                    for (int i = start; i <= end; i+=step)
                        if (!indexes.contains(String.valueOf(i)))
                            indexes.add(String.valueOf(i));

                } else if (!indexes.contains(items[j])) {
//					if (Integer.parseInt(items[j]) <= MAX_INDEX)
                    indexes.add(items[j]);
//					else
//						throw new Exception("Invalid input");
                }
            }

            expandIndexes[dim] = String.join(",", indexes);
            dim++;
        }

        return expandIndexes;
    }

    public static List<String> getAllFiles(String folder){
        try (Stream<Path> walk = Files.walk(Paths.get(folder))) {

            return walk.map(Path::toString)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static double round (double value, int precision) {
//        String format = "%." + precision + "f";
//        String str = String.format(format, value);
//        return Double.parseDouble(str);

		int scale = (int) Math.pow(10, precision);
		return (double) Math.round(value * scale) / scale;
    }
}
