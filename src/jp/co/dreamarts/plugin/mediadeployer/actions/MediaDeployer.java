package jp.co.dreamarts.plugin.mediadeployer.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class MediaDeployer implements IObjectActionDelegate {
    @Override
    public void run(IAction actionConfiguration) {
        // Gets absolute path of the current workspace
        String workspacePath = Platform.getInstanceLocation().getURL().getPath();
        /*
         * if Operating system is windows,then Perform the following
         */
        if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WIN") != -1) {

            /*
             * The absolute path according to the path delimiter separated, only
             * the workspace folder name remained
             */
            String[] stringTemps = workspacePath.split("\\\\");
            String[] stringTemp2s = stringTemps[stringTemps.length - 1].split("/");
            String filePath = "";
            /*
             * splicing path for path.json, grunt.bat file, in fact it is the
             * workspace folder path
             */
            for (int i = 0; i < stringTemps.length - 2; i++) {
                if (stringTemps[i] != null) {
                    filePath += stringTemps[i];
                    filePath = filePath + File.separator;
                }
            }
            for (int i = 0; i < stringTemp2s.length; i++) {
                if (stringTemp2s[i].length() != 0) {
                    filePath += stringTemp2s[i];
                    filePath = filePath + File.separator;
                }
            }
            // get workspace folder's name
            String workspaceName = stringTemp2s[stringTemp2s.length - 1];
            // read path.json
            File file = new File(filePath + "path.json");
            File fileBat = new File(filePath + "start.bat");
            // write path.json
            try {
                if (!file.exists()) {
                    creatJsonBatFile(filePath, workspaceName, file, fileBat);
                } else {
                    file.delete();
                    file = new File(filePath + File.separator + "path.json");
                    creatJsonBatFile(filePath, workspaceName, file, fileBat);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
             * Detecting grunt-sdbwatcher whether or not installed ,if not
             * ,installe grunt-sdbwatcher copy package.json、Gruntfile.js to
             * workspace execute package.json
             */

            // copy package.json、Gruntfile.js to workspace
            String oldPath = filePath + "node_modules" + File.separator.toString() + "grunt-sdbwatcher"
                    + File.separator.toString();
            String newPath = filePath + File.separator.toString();
            File pluginFlag = new File(oldPath + "tasks" + File.separator + "sdbwatcher.js");
            // Detecting grunt-sdbwatcher whether or not installed ,if not
            // ,installe grunt-sdbwatcher
            if (pluginFlag.exists()) {
                copyFile(oldPath + "package.json", newPath + "package.json");
                copyFile(oldPath + "Gruntfile.js", newPath + "Gruntfile.js");
            } else {
                // for windows operating system
                String location = filePath.substring(0, filePath.indexOf(":") + 1);
                String command = "cmd /c  " + location + " && cd " + filePath
                        + " && npm install grunt-sdbwatcher";
                System.out.println(command);
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                    copyFile(oldPath + "package.json", newPath + "package.json");
                    copyFile(oldPath + "Gruntfile.js", newPath + "Gruntfile.js");
                } catch (IOException | InterruptedException e) {
                    System.out.println("sdbwatcher Installation failed");
                    e.printStackTrace();
                }
                // execute "npm install" in cmd
                try {
                    String command2 = "cmd /c " + location + " &&cd " + filePath + " && npm install ";
                    Process process = Runtime.getRuntime().exec(command2);
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Successful configuration");
        }
        /*
         * if Operating system is Mac OS X,then Perform the following
         */
        if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("MAC") != -1) {
            /*
             * The absolute path according to the path delimiter separated, only
             * the workspace folder name remained
             */
            String[] stringTemps = workspacePath.split("\\\\");
            String[] stringTemp2s = stringTemps[stringTemps.length - 1].split("/");
            String filePath = File.separator.toString();
            String workspaceName = "";
            // splicing path for path.json, grunt.bat file, in fact it is the
            // workspace folder path
            for (int i = 0; i < stringTemps.length - 2; i++) {
                if (stringTemps[i] != null) {
                    filePath += stringTemps[i];
                    filePath = filePath + File.separator;
                }
            }
            for (int i = 0; i < stringTemp2s.length; i++) {
                if (stringTemp2s[i].length() != 0) {
                    filePath += stringTemp2s[i];
                    filePath = filePath + File.separator;
                }
            }
            // get workspace folder's name
            workspaceName = stringTemp2s[stringTemp2s.length - 1];
            // read path.json
            File file = new File(filePath + "path.json");
            File fileSh = new File(filePath + "start.sh");
            File sdbwatcherConfig = new File(filePath + "sdbwatcherConfig.sh");
            File npminstall = new File(filePath + "npminstall.sh");
            // write path.json
            try {
                if (!file.exists()) {
                    creatJsonShFile(filePath, workspaceName, file, fileSh, sdbwatcherConfig, npminstall);
                } else {
                    file.delete();
                    file = new File(filePath + File.separator + "path.json");
                    creatJsonShFile(filePath, workspaceName, file, fileSh, sdbwatcherConfig, npminstall);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
             * Detecting grunt-sdbwatcher whether or not installed ,if not
             * ,installe grunt-sdbwatcher copy package.json、Gruntfile.js to
             * workspace execute package.json
             */

            // copy package.json、Gruntfile.js to workspace
            String oldPath = filePath + "node_modules" + File.separator.toString() + "grunt-sdbwatcher"
                    + File.separator.toString();
            String newPath = filePath + File.separator.toString();
            File pluginFlag = new File(oldPath + "tasks" + File.separator + "sdbwatcher.js");
            // Detecting grunt-sdbwatcher whether or not installed ,if
            // not,installe grunt-sdbwatcher copy package.json、Gruntfile.js to
            if (pluginFlag.exists()) {
                copyFile(oldPath + "package.json", newPath + "package.json");
                copyFile(oldPath + "Gruntfile.js", newPath + "Gruntfile.js");
            } else {
                try {
                    Process process = Runtime.getRuntime().exec(filePath + "sdbwatcherConfig.sh");
                    process.waitFor();
                    copyFile(oldPath + "package.json", newPath + "package.json");
                    copyFile(oldPath + "Gruntfile.js", newPath + "Gruntfile.js");
                } catch (IOException | InterruptedException e) {
                    System.out.println("sdbwatcher Installation failed ");
                    e.printStackTrace();
                }
                // execute "npm install"in shell
                try {

                    Process process = Runtime.getRuntime().exec(filePath + "npminstall.sh");
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Successful configuration");
        }
    }

    /**
     * 
     * @param filePathJs
     * @param workspacePath2
     * @param filePathJs
     * @param fileSH
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     *             in Mac OS create path.json、start.sh file
     */
    private void creatJsonShFile(String filePath, String workspacePath2, File filePathJs, File fileSH,
            File sdbWatcherConfig, File npminstall) throws IOException, FileNotFoundException,
            UnsupportedEncodingException {

        // create path.json file
        filePathJs.createNewFile();
        FileOutputStream fos = new FileOutputStream(filePathJs);
        StringBuffer sb = new StringBuffer();
        sb.append("{\n \"watchPath\":\"" + ".." + File.separator.toString() + workspacePath2 + "\",");
        sb.append("\n\"destPath\":\"" + ".." + File.separator.toString() + workspacePath2
                + File.separator.toString() + "HIBIKI_v1_0" + File.separator + "webapp" + File.separator
                + "\"");
        sb.append("\n}");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
        sb.delete(0, sb.length());

        // create start.sh file
        fileSH.createNewFile();
        fos = new FileOutputStream(fileSH);
        sb = new StringBuffer();
        sb.append("cd " + filePathJs + "\n" + "grunt");
        sb.append("\n");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
        sb.delete(0, sb.length());
        //create sdbwatcherConfig.sh file
        sdbWatcherConfig.createNewFile();
        fos = new FileOutputStream(sdbWatcherConfig);
        sb = new StringBuffer();
        sb.append("cd " + filePathJs + "\n" + "npm install grunt-sdbwatcher");
        sb.append("\n");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
        sb.delete(0, sb.length());
        //vreate npminstall.sh file
        npminstall.createNewFile();
        fos = new FileOutputStream(npminstall);
        sb = new StringBuffer();
        sb.append("cd " + filePathJs + "\n" + "npm install");
        sb.append("\n");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
        filePathJs.setReadable(true);
        npminstall.setExecutable(true);
        sdbWatcherConfig.setExecutable(true);
        fileSH.setExecutable(true);
    }

    /**
     * 
     * @param filePath
     * @param workspacePath2
     * @param file
     * @param fileBat
     * @throws IOException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     *             in Windows operating system create path.json、start.bat file
     */
    private void creatJsonBatFile(String filePath, String workspacePath2, File file, File fileBat)
            throws IOException, FileNotFoundException, UnsupportedEncodingException {
        // create path.json file
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        StringBuffer sb = new StringBuffer();
        sb.append("{\r\n \"watchPath\":\"" + workspacePath2 + "\",");
        sb.append("\r\n\"destPath\":\"" + ".." + File.separator.toString() + File.separator + workspacePath2
                + File.separator.toString() + File.separator + "HIBIKI_v1_0" + File.separator
                + File.separator + "webapp" + File.separator + File.separator + "\"");
        sb.append("\r\n}");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
        sb.delete(0, sb.length());
        fileBat.createNewFile();
        fos = new FileOutputStream(fileBat);

        // location is partition of the workspace
        String location = filePath.substring(0, filePath.indexOf(":") + 2);
        sb.append(location + "\r\n" + "cd " + filePath + "\r\n" + "grunt");
        sb.append("\r\n");
        fos.write(sb.toString().getBytes("utf-8"));
        fos.flush();
        fos.close();
    }

    /**
     * the function for copying file
     * 
     * @param oldPath
     * @param newPath
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fos = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fos.write(buffer, 0, byteread);
                }
                inStream.close();
                fos.close();
            }
        } catch (Exception e) {
            System.out.println("Copy single file operation error");
            e.printStackTrace();

        }

    }

    @Override
    public void selectionChanged(IAction arg0, ISelection arg1) {

    }

    @Override
    public void setActivePart(IAction arg0, IWorkbenchPart arg1) {

    }

}
