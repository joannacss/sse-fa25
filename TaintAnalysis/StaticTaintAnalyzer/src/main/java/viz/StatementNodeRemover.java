package viz;

import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.types.ClassLoaderReference;

public class StatementNodeRemover implements GraphVisualizer.NodeRemover<Statement> {


    @Override
    public boolean isIrrelevantNode(Statement s) {
        return s.getNode().getMethod().getDeclaringClass().getClassLoader().getReference().equals(ClassLoaderReference.Primordial);
    }
}
