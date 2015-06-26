package fr.inria.diversify.logger.branch;

import fr.inria.diversify.buildSystem.AbstractBuilder;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.logger.Comparator;
import fr.inria.diversify.logger.Diff;
import fr.inria.diversify.processor.main.BranchPositionProcessor;
import fr.inria.diversify.transformation.SingleTransformation;
import fr.inria.diversify.util.LoggerUtils;
import spoon.reflect.cu.SourcePosition;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: Simon
 * Date: 23/06/15
 * Time: 14:40
 */
public class BranchComparator implements Comparator {
    Map<String, Set<String>> testsByBranch;
    Map<String, SourcePosition> branchPosition;

    @Override
    public void init(InputProgram  originalInputProgram, AbstractBuilder originalBuilder) throws Exception {
        initTestByBranch(originalBuilder);
        intBranch(originalInputProgram);
    }

    @Override
    public Diff compare(SingleTransformation transformation, String originalLogDir, String sosieLogDir) throws Exception {
        List<TestCoverage> sosieCoverage = loadTestCoverage(sosieLogDir);
        List<TestCoverage> originalCoverage = loadTestCoverage(originalLogDir);

        BranchDiff diff = new BranchDiff();
        for(TestCoverage originalTestCoverage : originalCoverage) {
            TestCoverage sosieTestCoverage = sosieCoverage.stream()
                    .filter(tc -> tc.getTestName().equals(originalTestCoverage.getTestName()))
                    .findFirst()
                    .get();

            if(!originalTestCoverage.containsAllBranch(sosieTestCoverage)) {
                Set<String> originalBranches =  originalTestCoverage.getCoveredBranch();
                Set<String> sosieBranches = sosieTestCoverage.getCoveredBranch();

                Set<String> d1 = originalBranches.stream()
                        .filter(branch -> !sosieBranches.contains(branch))
                        .filter(branch -> transformation == null || !(branch.contains(transformation.classLocationName())
                                && branch.contains(transformation.methodLocationName())))
                        .collect(Collectors.toSet());

                Set<String> d2 = sosieBranches.stream()
                        .filter(branch -> !originalBranches.contains(branch))
                        .filter(branch -> transformation == null || !(branch.contains(transformation.classLocationName())
                                && branch.contains(transformation.methodLocationName())))
                        .collect(Collectors.toSet());

                d1.addAll(d2);

                if(!d1.isEmpty()) {
                    diff.addBranchDiff(originalTestCoverage.getTestName(), d1);
                }
            }
        }
        return diff;
    }

    protected void initTestByBranch(AbstractBuilder originalBuilder) throws InterruptedException, IOException {
        testsByBranch = new HashMap<>();
        originalBuilder.runGoals(new String[]{"clean", "test"}, true);
        List<TestCoverage> testCoverage = loadTestCoverage(originalBuilder.getDirectory() + "/log");

        for(TestCoverage tc : testCoverage) {
            for(MethodCoverage mth : tc.getCoverage().getMethodCoverages()) {
                for(Branch branch : mth.getCoveredBranchs()) {
                    String key = mth.getMethodId() + "." + branch.getId();
                    if (!testsByBranch.containsKey(key)) {
                        testsByBranch.put(key, new HashSet<>());
                    }
                    String testName = tc.getTestName();
                    int ind = testName.lastIndexOf(".");
                    testName = new StringBuilder(testName).replace(ind, ind + 1, "#").toString();
                    testsByBranch.get(key).add(testName);
                }
            }
        }
    }

    protected void intBranch(InputProgram  originalInputProgram) {
        BranchPositionProcessor processor = new BranchPositionProcessor(originalInputProgram);
        LoggerUtils.applyProcessor(originalInputProgram.getFactory(), processor);

        branchPosition = processor.getBranchPosition();
    }

    protected  List<TestCoverage> loadTestCoverage(String logDir) throws IOException {
        CoverageReader reader = new CoverageReader(logDir);
        List<TestCoverage> result = reader.loadTest();

        return result;
    }

    protected boolean branchContainsIn(SingleTransformation trans, Set<String> branches) {
        return branches.stream()
                .filter(branch -> !(branch.contains(trans.classLocationName()) && branch.contains(trans.methodLocationName())))
                .count() == 0;
    }

    public Collection<String> selectTest(SourcePosition sourcePosition) {
        return branchPosition.keySet().stream()
                .filter(branch -> include(branchPosition.get(branch),sourcePosition))
                .filter(branch -> testsByBranch.containsKey(branch))
                .flatMap(branch -> testsByBranch.get(branch).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public Diff getEmptyDiff() {
        return new BranchDiff();
    }

    //true if oThis include in oOther
    protected boolean include(SourcePosition oThis, SourcePosition oOther) {
        return oThis.getCompilationUnit().getMainType().getQualifiedName().equals(oOther.getCompilationUnit().getMainType().getQualifiedName())
                && oThis.getLine() <= oOther.getLine()
                && oThis.getEndLine() >= oOther.getEndLine();

    }
}
