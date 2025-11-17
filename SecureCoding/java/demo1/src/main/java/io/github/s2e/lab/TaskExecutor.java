package io.github.s2e.lab;

import java.io.IOException;
import java.io.Serializable;

public class TaskExecutor implements Serializable {

    public void executeCmd(String c){
        try {
            System.out.println("Executing " + c);
            Runtime.getRuntime().exec(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
