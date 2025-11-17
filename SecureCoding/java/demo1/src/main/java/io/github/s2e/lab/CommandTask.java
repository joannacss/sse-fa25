package io.github.s2e.lab;

import java.io.Serializable;

public class CommandTask implements Serializable, Runnable {
    private TaskExecutor te;
    private String cmd;

    public CommandTask(TaskExecutor te, String cmd) {
        this.te = te;
        this.cmd = cmd;
    }

    @Override
    public void run() {
        if (!cmd.isEmpty() && te != null){
            te.executeCmd(cmd);
        }
    }
}
