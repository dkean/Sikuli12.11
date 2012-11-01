package org.sikuli.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import org.sikuli.core.Settings;

public class LibLoader {

    private static File jniDir = null;
    private static String libSource;
    private static String[] libPathsSettings;
    private static ArrayList<String> libPaths;
    private static StringBuffer alreadyLoaded = new StringBuffer("");

    static {
        libSource = Settings.libSource;
        libPathsSettings = new String[]{
            Util.slashify(System.getenv("SIKULI_HOME"), true) + "libs",
            System.getenv("SIKULI_HOME"),
            Settings.libPathMac, Settings.libPathWin};
        libPaths = new ArrayList<String>(Arrays.asList(libPathsSettings));
    }

    /**
     * System.load() the given library module either <br /> from standard places
     * Settings.libPlaces or <br /> from the current .jar at Settings.libSource
     *
     * @param libname
     * @param doLoad = true: load it here
     * @throws IOException
     */
    public static void loadLibrary(String libname) {
        File lib = null;
        boolean libFound = false;
        try {
            lib = extractJni(libname);
            if (lib == null) {
                Debug.log(2, "Native library already loaded: " + libname);
                return;
            }
            Debug.log(2, "Native library found: " + libname);
            libFound = true;
        } catch (IOException ex) {
            Debug.error(LibLoader.class.getName() + ".loadLibrary: Native library could not be extracted nor found: " + libname);
            System.exit(1);
        }
        try {
            System.load(lib.getAbsolutePath());
        } catch (Error e) {
            Debug.error(LibLoader.class.getName() + ".loadLibrary: Native library could not be loaded: " + libname);
            if (libFound) {
                Debug.error("Since native library was found, it might be a problems with needed dependent libraries");
                e.printStackTrace();
            }
            System.exit(1);
        }
        Debug.log(2, "Native library loaded: " + libname);
    }

    /**
     * extract a JNI library from the classpath <br /> Mac: default is .jnilib
     * (.dylib as fallback)
     *
     * @param libname System.loadLibrary() compatible library name
     * @return the extracted File object
     * @throws IOException
     */
    private static File extractJni(String libname) throws IOException {
        if (alreadyLoaded.indexOf("*" + libname) < 0) {
            alreadyLoaded.append("*" + libname);
        } else {
            return null;
        }
        String mappedlib = System.mapLibraryName(libname);
        File outfile = new File(getJniDir(), mappedlib);

        /* on darwin, the default mapping is to .jnilib; but
         * if we don't find a .jnilib, try .dylib instead.
         */
        if (!outfile.exists()) {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            URL res = cl.getResource(libSource + mappedlib);
            if (res == null) {
                if (mappedlib.endsWith(".jnilib")) {
                    mappedlib = mappedlib.substring(0, mappedlib.length() - 7) + ".dylib";
                    String jnilib = mappedlib.toString();
                    outfile = new File(getJniDir(), jnilib);
                    if (!outfile.exists()) {
                        if (ClassLoader.getSystemClassLoader().getResource(libSource + mappedlib) == null) {
                            throw new IOException("Library " + mappedlib + " not on classpath nor in default location");
                        } // else copy lib from jar
                    } else {
                        return outfile;
                    }
                } else {
                    throw new IOException("Library " + mappedlib + " not on classpath nor in default location");
                }
            } // else copy lib from jar
        } else {
            return outfile;
        }
        File ret = extractJniResource(libSource + mappedlib, outfile);
        return ret;
    }

    /**
     * Gets the working directory to use for jni extraction. <br /> standard
     * locations: <br /> Mac: "/Applications/Sikuli-IDE.app/Contents/Frameworks"
     * <br /> Windows / Linux & Mac (environment): %SIKULI_HOME% / $SIKULI_HOME
     * <br /> if not exists: java.io.tmp/tmplib
     *
     * @return jni working dir
     * @throws IOException if there's a problem creating the dir
     */
    private static File getJniDir() throws IOException {
        if (jniDir == null) {
            Debug.log(2, "Checking JNI library working directory");
            for (String path : libPaths) {
                if (path == null || "".equals(path)) {
                    continue;
                }
                jniDir = new File(path);
                if (jniDir.exists()) {
                    System.setProperty("java.library.tmpdir", path);
                    Debug.log(2, "Using as JNI library working directory: '" + jniDir + "'");
                    return jniDir;
                }
            }
            String libTmpDir = System.getProperty("java.io.tmpdir") + "/tmplib";
            System.setProperty("java.library.tmpdir", libTmpDir);
            jniDir = new File(libTmpDir);
            Debug.log(2, "Initialised JNI library working directory to '" + jniDir + "'");
        }

        if (!jniDir.exists()) {
            if (!jniDir.mkdirs()) {
                throw new IOException("Unable to create JNI library working directory " + jniDir);
            }
        }
        return jniDir;
    }

    /**
     * extract a resource to a writable file
     *
     * @param resourcename the name of the resource on the classpath
     * @param outputfile the file to copy to
     * @return the extracted file
     * @throws IOException
     */
    private static File extractJniResource(String resourcename, File outputfile) throws IOException {
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcename);
        if (in == null) {
            throw new IOException("Resource " + resourcename + " not on classpath");
        }
        Debug.log(2, "Extracting '" + resourcename + "' to '" + outputfile.getAbsolutePath() + "'");
        OutputStream out = new FileOutputStream(outputfile);
        copy(in, out);
        out.close();
        in.close();
        return outputfile;
    }

    /**
     * extract a resource to an absolute path
     *
     * @param resourcename the name of the resource on the classpath
     * @param outputname the path of the file to copy to
     * @return the extracted file
     * @throws IOException
     */
    private static File extractJniResource(String resourcename, String outputname) throws IOException {
        return extractJniResource(resourcename, new File(outputname));
    }

    /**
     * copy an InputStream to an OutputStream.
     *
     * @param in InputStream to copy from
     * @param out OutputStream to copy to
     * @throws IOException if there's an error
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] tmp = new byte[8192];
        int len = 0;
        while (true) {
            len = in.read(tmp);
            if (len <= 0) {
                break;
            }
            out.write(tmp, 0, len);
        }
    }
}
