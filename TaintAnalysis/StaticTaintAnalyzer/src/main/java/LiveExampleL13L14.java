import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.FileOfClasses;
import com.sun.security.jgss.GSSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.jar.JarFile;

/**
 * Example coded in class - Lecture 14.
 * Walked through WALA's main data structures and how to create call graphs.
 *
 * @author Joanna C. S. Santos
 */
public class LiveExampleL13L14 {

    public static void printCallGraph(CallGraph cg, String callgraphName) {
        System.out.println("================ " + callgraphName + " call graph ===================");
        System.out.println(CallGraphStats.getStats(cg));
        System.out.println("Call Graph (application scope only): ");
        for (CGNode n : cg) {

            if (n.getMethod().getDeclaringClass().getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
                cg.getSuccNodes(n).forEachRemaining(next -> {
                    System.out.print("\"" + n.getMethod().getSignature() + "\"");
                    System.out.println(" -> \"" + next.getMethod().getSignature() + "\"");
                });
                System.out.println("");
            }
        }
        System.out.println("===================================================");
    }


    public static void main(String[] args) throws Exception {
        // TODO: create the analysis scope to analyze the Example1.jar
        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String path = LiveExampleL13L14.class.getResource("Example1.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(new File(path)));
        String rtPath = LiveExampleL13L14.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(new File(rtPath)));
        // TODO: add exclusions file and show ch size
        String exPath = LiveExampleL13L14.class.getResource("Java60RegressionExclusions.txt").getPath();
        scope.setExclusions(new FileOfClasses(new FileInputStream(exPath)));
        System.out.println(scope);


        // TODO: create the class hierarchy
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);

        // TODO: print the number of cla sses in the class hierarchy
        System.out.println(classHierarchy.getNumberOfClasses());


        // TODO: iterate over the class hierarchy and print the classes in the application scope
        // print their methods
        for (IClass c : classHierarchy) {
            if (c.getClassLoader().getReference().equals(ClassLoaderReference.Application)) {
                System.out.println(c.getName());
                Collection<? extends IMethod> declaredMethods = c.getDeclaredMethods();
            }
        }


        // TODO: compute entrypoint methods
        Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, classHierarchy);


        // TODO: create the CHA call graph
//        CHACallGraph chaCallGraph = new CHACallGraph(classHierarchy, false);
//        chaCallGraph.init(entrypoints);
//        printCallGraph(chaCallGraph, "CHA Call Graph");


        // TODO: create the RTA call graph
        AnalysisOptions options = new AnalysisOptions();
        options.setAnalysisScope(scope);
        options.setEntrypoints(entrypoints);
        AnalysisCache cache = new AnalysisCacheImpl();

//        CallGraphBuilder<InstanceKey> rtaBuilder = Util.makeRTABuilder(options, cache, classHierarchy, scope);
//        CallGraph rtaCg = rtaBuilder.makeCallGraph(options, null);
//        printCallGraph(rtaCg, "RTA Call Graph");

        // TODO: create the 1-CFA call graph
        SSAPropagationCallGraphBuilder nCfaBuilder = Util.makeNCFABuilder(1, options, cache, classHierarchy, scope);
        CallGraph oneCfaCg = nCfaBuilder.makeCallGraph(options, null);
//        printCallGraph(oneCfaCg, "One CFA Call Graph");

        // TODO: print all stats
//        System.out.println(CallGraphStats.getStats(chaCallGraph));
//        System.out.println(CallGraphStats.getStats(rtaCg));
        System.out.println(CallGraphStats.getStats(oneCfaCg));

        // TODO: print the IR of the main method
        Collection<CGNode> entrypointNodes = oneCfaCg.getEntrypointNodes();
        CGNode mainNode =  entrypointNodes.iterator().next();
        IR ir = mainNode.getIR();
        System.out.println(ir);


        // TODO: getting CFG

    }
}
