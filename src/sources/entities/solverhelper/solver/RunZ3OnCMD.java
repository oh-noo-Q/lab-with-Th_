package entities.solverhelper.solver;

import entities.solverhelper.ISymbolicExecution;
import entities.UETLogger;
import entities.utils.Utils;
import com.ibm.icu.util.Calendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Chạy file smt-lib trên cmd sử dụng SMT-Solver Z3
 *
 * @author anhanh
 */
public class RunZ3OnCMD {
    final static UETLogger logger = UETLogger.get(RunZ3OnCMD.class);

    private String Z3Path;
    private String smtLibPath;
    private String result;

    public RunZ3OnCMD(String Z3Path, String smtLibPath) throws IOException, InterruptedException {
        this.Z3Path = Z3Path;
        this.smtLibPath = smtLibPath;
        result = "";
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new RunZ3OnCMD("C:\\z3\\bin\\z3", "C:/he-rang-buoc1.smt2");

    }

    public synchronized void execute() throws Exception {
        logger.debug("RunZ3OnCMD begin");
        String content = Utils.readFileContent(new File(smtLibPath));

//        if (content.contains(ISymbolicExecution.NO_SOLUTION_CONSTRAINT_SMTLIB))
//            result = ISymbolicExecution.UNSAT_IN_Z3;
//
//        else {
            Date startTime = Calendar.getInstance().getTime();

//            String output = smtLibPath + ".txt";
            Process p = null;
            if (Utils.isWindows()) {
                p = Runtime.getRuntime().exec(
                        new String[]{new File(Z3Path).getName(), "-smt2", smtLibPath/*, ">", "\""+output+"\""*/}
                        , new String[]{},
                        new File(Z3Path).getParentFile());
            } else if (Utils.isUnix()) {
                p = Runtime.getRuntime().exec(
                        new String[]{"./" + new File(Z3Path).getName(), "-smt2", smtLibPath/*, ">","\""+output+"\""*/}
                        , new String[]{},
                        new File(Z3Path).getParentFile());
            } else if (Utils.isMac()) {
                p = Runtime.getRuntime().exec(
                        new String[]{Z3Path, "-smt2", smtLibPath}
                        );
            }

//            int count = 0;
//            int MAX = 40;
//            while (!new File(output).exists()) {
//                Thread.sleep(100);
//                count++;
//                if (count > MAX)
//                    break;
//                logger.debug("Waiting solution");
//            }
//
//            if (new File(output).exists())
//                result = Utils.readFileContent(output);
            p.waitFor();

            //AbstractAutomatedTestdataGeneration.numOfSolverCalls++;
            Date end = Calendar.getInstance().getTime();
            //AbstractAutomatedTestdataGeneration.solverRunningTime += end.getTime() - startTime.getTime();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
                result += line + "\n";
//            logger.debug("Output: " + result);

            // Display errors if exists
            if (p.getErrorStream() != null) {
                BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String err;
                boolean hasError = false;
                while ((err = error.readLine()) != null) {
                    logger.error(err);
                    hasError = true;
                }
//                if (hasError)
//                    AbstractAutomatedTestdataGeneration.numOfSolverCallsbutCannotSolve++;
            }
//        }
        logger.debug("RunZ3OnCMD end");
    }

    public String getSolution() {
        return result;
    }
}
