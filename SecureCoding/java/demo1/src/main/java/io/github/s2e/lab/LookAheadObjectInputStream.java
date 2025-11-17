package io.github.s2e.lab;

import java.io.*;
import java.util.Set;


public class LookAheadObjectInputStream extends ObjectInputStream {

    private Set ALLOWED_CLASSES = Set.of(
            "io.github.s2e.lab.Note"
    );

    public LookAheadObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();

        // Perform lookahead checks on the class being deserialized
        if (!ALLOWED_CLASSES.contains(className)) {
            throw new InvalidClassException("Unauthorized deserialization attempt for class: " + className);
        }

        return super.resolveClass(desc);
    }

}
