import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.*;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ssa.*;
import com.ibm.wala.types.*;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.GraphSlicer;
import com.ibm.wala.util.graph.traverse.BFSPathFinder;
import com.ibm.wala.util.strings.Atom;
import viz.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import static com.ibm.wala.types.TypeReference.findOrCreate;

/**
 * Answer to the example coded in class - Lecture 15.
 * Walked through WALA's PDG, SDG data structures and how to compute slices.
 *
 * @author Joanna C. S. Santos
 */
public class ExampleL15 {

    public static void main(String[] args) throws IOException, ClassHierarchyException, CancelException {
        //Create the analysis scope
        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String jarFilePath = ExampleL15.class.getResource("Example4.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(jarFilePath));
        String jrePath = ExampleL15.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(jrePath));
        String exFilePath = ExampleL15.class.getResource("Java60RegressionExclusions.txt").getPath();
        scope.setExclusions(new FileOfClasses(new FileInputStream(exFilePath)));

        // Create the class hierarchy
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);

        // Create the 1-CFA call graph
        AnalysisOptions options = new AnalysisOptions();
        Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, classHierarchy);
        options.setEntrypoints(entrypoints);
        SSAPropagationCallGraphBuilder builder = Util.makeNCFABuilder(1, options, new AnalysisCacheImpl(), classHierarchy, scope);
        CallGraph callGraph = builder.makeCallGraph(options, null);
        ExampleL13.printCallGraph(callGraph, "1-CFA");


        // Get the IR of the main method
        CGNode mainNode = callGraph.getEntrypointNodes().iterator().next();
        IR ir = mainNode.getIR();
        SSAInstruction[] instructions = ir.getInstructions();
        SSACFG cfg = ir.getControlFlowGraph();


        // TODO: Compute the SDG of the program (data only)
        SDG sdg = new SDG(callGraph, builder.getPointerAnalysis(), DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS, ControlDependenceOptions.NONE);

        // TODO: find sources and sinks
        Set<Statement> sinks = findSinks(sdg);
        Set<Statement> sources = findSources(sdg);

        // TODO: slice the SDG
        Set<Statement> slice = new HashSet<>(Slicer.computeBackwardSlice(sdg, sinks));
        Graph<Statement> slicedSdg = GraphSlicer.prune(sdg, s -> slice.contains(s));

        // TODO: find vulnerable paths
        Set<List<Statement>> vulnerablePaths = getVulnerablePaths(slicedSdg, sources, sinks);

        // TODO: print vulnerable paths
        for (List<Statement> path : vulnerablePaths) {
            System.out.println("VULNERABLE PATH");
            for (Statement s : path) {
                System.out.println("\t" + s);
            }
            System.out.println("------------------------------");
        }


        StatementNodeLabeller nodeLabeller = new StatementNodeLabeller(sdg);
        StatementEdgeHighlighter edgeHighlighter = new StatementEdgeHighlighter(sdg);
        StatementNodeHighlighter nodeHighlighter = new StatementNodeHighlighter(slice);
        StatementNodeRemover nodeRemover = new StatementNodeRemover();

        GraphVisualizer<Object> visualizer = new GraphVisualizer<>("SDG of main", nodeLabeller, nodeHighlighter, edgeHighlighter, nodeRemover);
        visualizer.generateVisualGraph(sdg, new File("./target/Example4_main_sdg.dot"));
    }


    /**
     * True if the IClass is under the application-scope ({@code ClassLoaderReference.Application}).
     *
     * @param iClass
     * @return
     */
    public static boolean isApplicationScope(IClass iClass) {
        return iClass != null && iClass.getClassLoader().getReference().equals(ClassLoaderReference.Application);
    }

    /**
     * Find all the sources in the SDG.
     *
     * @param sdg SDG
     * @return Set of sources
     */
    public static Set<Statement> findSources(SDG<InstanceKey> sdg) {

        Set<Statement> result = new HashSet<>();
        for (Statement s : sdg) {
            if (s.getKind().equals(Statement.Kind.NORMAL) && isApplicationScope(s.getNode().getMethod().getDeclaringClass())) {
                SSAInstruction instruction = ((NormalStatement) s).getInstruction();
                if (instruction instanceof SSAArrayLoadInstruction) {
                    int varNo = instruction.getUse(0);
                    String method = s.getNode().getMethod().getSelector().toString();
                    if (varNo == 1 && method.equals("main([Ljava/lang/String;)V"))
                        result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * Find all the sinks in the SDG.
     *
     * @param sdg SDG
     * @return Set of sinks
     */
    public static Set<Statement> findSinks(SDG<InstanceKey> sdg) {
        TypeReference JavaLangRuntime =
                findOrCreate(ClassLoaderReference.Application, TypeName.string2TypeName("Ljava/lang/Runtime"));
        MethodReference sinkReference =
                MethodReference.findOrCreate(JavaLangRuntime,
                        Atom.findOrCreateUnicodeAtom("exec"),
                        Descriptor.findOrCreateUTF8("(Ljava/lang/String;)Ljava/lang/Process;"));

        Set<Statement> result = new HashSet<>();
        for (Statement s : sdg) {
            if (s.getKind().equals(Statement.Kind.NORMAL) && isApplicationScope(s.getNode().getMethod().getDeclaringClass())) {
                SSAInstruction instruction = ((NormalStatement) s).getInstruction();
                if (instruction instanceof SSAAbstractInvokeInstruction) {
                    if (((SSAAbstractInvokeInstruction) instruction).getDeclaredTarget().equals(sinkReference))
                        result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * Compute vulnerable paths in the program
     *
     * @param G       sliced SDG
     * @param sources source statements
     * @param sinks   sink statements
     * @return set of vulnerable paths
     */
//    public static Set<List<Statement>> getVulnerablePaths(Graph<Statement> G, Set<Statement> sources, Set<Statement> sinks) {
//        Set<List<Statement>> result = HashSetFactory.make();
//        for (Statement src : G) {
//            if (sources.contains(src)) {
//                for (Statement dst : G) {
//                    if (sinks.contains(dst)) {
//                        BFSPathFinder<Statement> paths = new BFSPathFinder<>(G, src, dst);
//                        List<Statement> path = paths.find();
//                        if (path != null) {
//                            result.add(path);
//                        }
//                    }
//                }
//            }
//        }
//        return result;
//    }
    public static Set<List<Statement>> getVulnerablePaths(Graph<Statement> G, Set<Statement> sources, Set<Statement> sinks) {
        Set<List<Statement>> result = HashSetFactory.make();
        for (Statement src : sources) {
            for (Statement dst: sinks ) {
                BFSPathFinder<Statement> paths = new BFSPathFinder<>(G, src, dst);
                List<Statement> path = paths.find();
                if (path != null) {
                    result.add(path);
                }
            }
        }

        return result;
    }
}
