import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.FileOfClasses;

import java.io.FileInputStream;
import java.util.Collection;
import java.util.jar.JarFile;

/**
 * Example coded in class - Lecture 14.
 * Walked through WALA's main data structures and how to create call graphs.
 *
 * @author Joanna C. S. Santos
 */
public class LiveExampleL13 {

    public static void printCallGraph(CallGraph cg, String callgraphName) {
        System.out.println("================ " + callgraphName + " call graph ===================");
        System.out.println(CallGraphStats.getStats(cg));
        System.out.println("Call Graph (application scope only): ");
        // TODO
        System.out.println("===================================================");
    }


    public static void main(String[] args) throws Exception {
        // TODO: create the analysis scope to analyze the Example1.jar
        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String appPath = LiveExampleL13.class.getResource("Example1.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(appPath));
        String primordialPath = LiveExampleL13.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(primordialPath));
        System.out.println(scope);



        // TODO: create the class hierarchy

        // TODO: print the number of classes in the class hierarchy


        // TODO: add exclusions file and show ch size

        // TODO: iterate over the class hierarchy and print the classes in the application scope
        // print their methods

        // TODO: compute entrypoint methods


        // TODO: create the CHA call graph


        // TODO: create the RTA call graph


        // TODO: create the 1-CFA call graph


        // TODO: print the IR of the main method


        // TODO: getting CFG

    }
}
