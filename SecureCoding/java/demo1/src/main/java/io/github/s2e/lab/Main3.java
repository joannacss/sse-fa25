package io.github.s2e.lab;

import java.io.*;

public class Main3 {
    /**
     * Saves an object into a file
     *
     * @param filename path to the file
     * @param obj      the object to be saved (serialized)
     * @throws IOException in case the object cannot be saved.
     */
    public static void save(String filename, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        }
    }

    /**
     * Load an object from the stream
     *
     * @param filename path to where the object has been saved
     * @throws IOException            in case of IO issues.
     * @throws ClassNotFoundException the object's class is not available in the classpath.
     */
    public static Object load(String filename) throws IOException, ClassNotFoundException {
        File f = new File(filename);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new LookAheadObjectInputStream(new FileInputStream(f))) {
            return ois.readObject();
        }
    }

    public static void main(String[] args) throws Exception {
        TaskExecutor te = new TaskExecutor();
        CommandTask ct = new CommandTask(te, "open -a Calculator");
        CacheManager cm = new CacheManager(ct, "macOS", 0);

        save("exploit.txt", cm);
        load("exploit.txt");
    }
}
