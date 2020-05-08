package com.hnyf.wrsp;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SuUtils {

    /**
     * 检测手机是否root
     *
     * @param ctx 上下文
     * @return
     */
    public static boolean hasRootAccess(Context ctx) {
        final StringBuilder res = new StringBuilder();
        try {
            if (runScriptAsRoot(ctx, "exit 0", res) == 0)
                return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static int runScriptAsRoot(Context ctx, String script, StringBuilder res) {
        final File file = new File(ctx.getCacheDir(), "secopt.sh");
        final ScriptRunner runner = new ScriptRunner(file, script, res);
        runner.start();
        try {
            runner.join(40000);
            if (runner.isAlive()) {
                runner.interrupt();
                runner.join(150);
                runner.destroy();
                runner.join(50);
            }
        } catch (InterruptedException ex) {
        }
        return runner.exitcode;
    }

    private static final class ScriptRunner extends Thread {
        private final File file;
        private final String script;
        private final StringBuilder res;
        public int exitcode = -1;
        private Process exec;

        public ScriptRunner(File file, String script, StringBuilder res) {
            this.file = file;
            this.script = script;
            this.res = res;
        }

        @Override
        public void run() {
            try {
                file.createNewFile();
                final String abspath = file.getAbsolutePath();
                Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();
                final OutputStreamWriter out = new OutputStreamWriter(
                        new FileOutputStream(file));
                if (new File("/system/bin/sh").exists()) {
                    out.write("#!/system/bin/sh\n");
                }
                out.write(script);
                if (!script.endsWith("\n"))
                    out.write("\n");
                out.write("exit\n");
                out.flush();
                out.close();

                exec = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(exec.getOutputStream());
                os.writeBytes(abspath);
                os.flush();
                os.close();

                InputStreamReader r = new InputStreamReader(exec.getInputStream());
                final char buf[] = new char[1024];
                int read = 0;
                while ((read = r.read(buf)) != -1) {
                    if (res != null)
                        res.append(buf, 0, read);
                }

                r = new InputStreamReader(exec.getErrorStream());
                read = 0;
                while ((read = r.read(buf)) != -1) {
                    if (res != null)
                        res.append(buf, 0, read);
                }

                if (exec != null)
                    this.exitcode = exec.waitFor();
            } catch (InterruptedException ex) {
                if (res != null)
                    res.append("\nOperation timed-out");
            } catch (Exception ex) {
                if (res != null)
                    res.append("\n" + ex);
            } finally {
                destroy();
            }
        }

        public synchronized void destroy() {
            if (exec != null)
                exec.destroy();
            exec = null;
        }
    }


}
