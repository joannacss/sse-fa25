package io.github.s2e.lab;


import java.io.*;

public class Main1 {
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
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return ois.readObject();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Note note1 = new Note("Hello", "Lorem Ipsum");
        save("my_note.txt", note1);
        Note note2 =  (Note) load("my_note.txt");
        System.out.println("Original object " + note1);
        System.out.println("Loaded object " + note2);
    }
}
