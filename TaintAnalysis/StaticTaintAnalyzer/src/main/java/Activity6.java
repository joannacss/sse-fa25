import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.*;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.callgraph.propagation.SSAPropagationCallGraphBuilder;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.FileOfClasses;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.jar.JarFile;
// mvn compile
// mvn exec:java -Dexec.mainClass=Activity6
public class Activity6 {
    public static void main(String[] args) throws IOException, ClassHierarchyException, CallGraphBuilderCancelException {

        AnalysisScope scope = AnalysisScope.createJavaAnalysisScope();
        String jarFilePath = ExampleL13.class.getResource("MusicPlayer.jar").getPath();
        scope.addToScope(ClassLoaderReference.Application, new JarFile(jarFilePath));
        String jrePath = ExampleL13.class.getResource("jdk-17.0.1/rt.jar").getPath();
        scope.addToScope(ClassLoaderReference.Primordial, new JarFile(jrePath));
        String exFilePath = ExampleL13.class.getResource("Java60RegressionExclusions.txt").getPath();
        scope.setExclusions(new FileOfClasses(new FileInputStream(exFilePath)));

        System.out.println("Building class hierarchy!");
        IClassHierarchy classHierarchy = ClassHierarchyFactory.make(scope);
        System.out.println("DONE");
        AnalysisOptions options = new AnalysisOptions();
        Iterable<Entrypoint> entrypoints = Util.makeMainEntrypoints(scope, classHierarchy);
        options.setEntrypoints(entrypoints);
        System.out.println("Building call graph...");
        SSAPropagationCallGraphBuilder builder = Util.makeNCFABuilder(2, options, new AnalysisCacheImpl(), classHierarchy, scope);
        CallGraph callGraph = builder.makeCallGraph(options, null);
        System.out.printf(CallGraphStats.getStats(callGraph));
        System.out.println("DONE");
    }
}
