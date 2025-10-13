import com.ibm.wala.cast.tree.impl.CAstNodeTypeMapRecorder;
import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ipa.slicer.*;
import com.ibm.wala.ssa.*;
import com.ibm.wala.types.*;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.HashSetFactory;
import com.ibm.wala.util.config.FileOfClasses;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.util.graph.traverse.BFSPathFinder;
import com.ibm.wala.util.strings.Atom;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import static com.ibm.wala.types.TypeReference.findOrCreate;

/**
 * Example coded in class - Lecture 15.
 * Walked through WALA's PDG, SDG data structures and how to compute slices.
 * Running it from terminal:
 * - mvn compile
 * - mvn exec:java -Dexec.mainClass=LiveExampleL15
 * @author Joanna C. S. Santos
 */
public class LiveExampleL15 {

    public static void main(String[] args) throws IOException, ClassHierarchyException, CancelException {
        //Create the analysis scope
        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String jarFilePath = LiveExampleL15.class.getResource("Example4.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(jarFilePath));
        String jrePath = LiveExampleL15.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(jrePath));
        String exFilePath = LiveExampleL15.class.getResource("Java60RegressionExclusions.txt").getPath();
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


        // TODO: Compute the SDG of the program (data only)
        SDG sdg = new SDG(callGraph,
                builder.getPointerAnalysis(),
                Slicer.DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
                Slicer.ControlDependenceOptions.NONE);

        System.out.println(sdg.getNumberOfNodes());
        // TODO: find sources and sinks
        Set<Statement> sources = findSources(sdg);
        Set<Statement> sinks = findSinks(sdg);
        System.out.println("SOURCES: " + sources);


        // TODO: slice the SDG and compute a pruned SDG



        // TODO: find vulnerable paths



        // TODO: print vulnerable paths


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
        // We want to find a statement with the following features:
        // - it is within the main method `main([Ljava/lang/String;)V`
        // - it is an ArrayLoadInstruction
        // - the array is the program's arguments, ie., `args` (v1)
        Set<Statement> result = new HashSet<>();
        // TODO: iterate over the statements in the SDG to find source(s)
        for(Statement statement : sdg) {
            if(isApplicationScope(statement.getNode().getMethod().getDeclaringClass())){
                if(statement instanceof NormalStatement){ // Alternative: statement.getKind().equals(Statement.Kind.NORMAL
                    SSAInstruction instruction = ((NormalStatement) statement).getInstruction();
                    if(instruction instanceof SSAArrayLoadInstruction){
                        int varNo = instruction.getUse(0);
                        IMethod m = statement.getNode().getMethod();
                        if(m.getSelector().toString().equals("main([Ljava/lang/String;)V") && varNo == 1){
                            result.add(statement);
                        }
                    }
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
            // TODO: IN CLASS EXERCISE: Find a statement with the following features:
            // - it is within the application scope
            // - it is an invocation (SSAAbstractInvokeInstruction)
            // - the declared target method is equals to `sinkReference` constructed above
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
    public static Set<List<Statement>> getVulnerablePaths(Graph<Statement> G, Set<Statement> sources, Set<Statement> sinks) {
        Set<List<Statement>> result = HashSetFactory.make();
        for (Statement src : G) {
            if (sources.contains(src)) {
                for (Statement dst : G) {
                    if (sinks.contains(dst)) {
                        BFSPathFinder<Statement> paths = new BFSPathFinder<>(G, src, dst);
                        List<Statement> path = paths.find();
                        if (path != null) {
                            result.add(path);
                        }
                    }
                }
            }
        }
        return result;
    }
}
