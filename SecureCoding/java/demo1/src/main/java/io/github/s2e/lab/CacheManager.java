package io.github.s2e.lab;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class CacheManager implements Serializable {

    private Runnable r;
    private String os;
    private long timestamp;

    public CacheManager(Runnable r, String os, long timestamp) {
        this.r = r;
        this.os = os;
        this.timestamp = timestamp;
    }

    private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        if (!os.equals("windows"))
            r.run();
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.defaultWriteObject();
    }
}
