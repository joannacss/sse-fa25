import com.ibm.wala.classLoader.IClass;
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
import viz.CFGVisualizer;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.JarFile;

/**
 * Example coded in class - Lecture 14.
 * Walked through WALA's main data structures and how to create call graphs.
 *
 * @author Joanna C. S. Santos
 */
public class ExampleL13 {

    public static void printCallGraph(CallGraph cg, String callgraphName) {
        System.out.println("================ " + callgraphName + " call graph ===================");
        System.out.println(CallGraphStats.getStats(cg));
        System.out.println("Call Graph (application scope only): ");
        for (CGNode node : cg) {
            // only prints the nodes & edges in the application scope
            if (node.getMethod().getDeclaringClass().getClassLoader().getReference().equals(ClassLoaderReference.Application)) {

                if (cg.getSuccNodeCount(node) > 0)
                    System.out.println(node.getMethod().getSignature());

                cg.getSuccNodes(node).forEachRemaining(succ -> {
                    System.out.println("  -> " + succ.getMethod().getSignature());
                });
            }
        }
        System.out.println("===================================================");
    }

    public static void main(String[] args) throws Exception {

        // TODO: create the analysis scope
        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String jarFilePath = ExampleL13.class.getResource("Example1.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(jarFilePath));
        String jrePath = ExampleL13.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(jrePath));
        String exFilePath = ExampleL13.class.getResource("Java60RegressionExclusions.txt").getPath();
        scope.setExclusions(new FileOfClasses(new FileInputStream(exFilePath)));

        // TODO: create the class hierarchy
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);

        // TODO: print the number of classes in the class hierarchy
        System.out.println("Number of classes: " + classHierarchy.getNumberOfClasses());
        for (IClass iClass : classHierarchy) {
            if (iClass.getClassLoader().getReference().equals(ClassLoaderReference.Application))
                System.out.println(iClass.getName());
        }


        // TODO: create the CHA call graph
        CHACallGraph chaCallGraph = new CHACallGraph(classHierarchy, false);
        Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, classHierarchy);
        chaCallGraph.init(entrypoints);
        printCallGraph(chaCallGraph, "CHA");


        // TODO: create the RTA call graph
        AnalysisOptions options = new AnalysisOptions();
        options.setEntrypoints(entrypoints);
        CallGraphBuilder<InstanceKey> rtaBuilder = Util.makeRTABuilder(options, new AnalysisCacheImpl(), classHierarchy, scope);
        CallGraph rtaCallGraph = rtaBuilder.makeCallGraph(options, null);
        printCallGraph(rtaCallGraph, "RTA");

        // TODO: create the 1-CFA call graph
        SSAPropagationCallGraphBuilder oneCfaBuilder = Util.makeNCFABuilder(1, options, new AnalysisCacheImpl(), classHierarchy, scope);
        CallGraph oneCfaCallGraph = oneCfaBuilder.makeCallGraph(options, null);
        printCallGraph(oneCfaCallGraph, "1-CFA");


        // TODO: print the IR of the main method
        CGNode mainNode = oneCfaCallGraph.getEntrypointNodes().iterator().next();
        IR ir = mainNode.getIR();
        System.out.println(ir.toString());


        new CFGVisualizer(mainNode, false).generateVisualGraph(new File("target/Example1-BB-not-pruned-cfg.dot"));
    }
}
