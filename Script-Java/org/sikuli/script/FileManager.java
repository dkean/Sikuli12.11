package org.sikuli.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class FileManager {

  private static File jniDir = null;
  private static String libSource;
  private static String[] libPathsSettings;
  private static ArrayList<String> libPaths;
  private static StringBuffer alreadyLoaded = new StringBuffer("");
	static final int DOWNLOAD_BUFFER_SIZE = 153600;

  static {
    libSource = Settings.libSource;
    libPathsSettings = new String[]{
      slashify(System.getenv("SIKULI_HOME"), true) + "libs",
      System.getenv("SIKULI_HOME"),
			Settings.libPathMac, Settings.libPathWin};
    libPaths = new ArrayList<String>(Arrays.asList(libPathsSettings));
  }

  /**
   * System.load() the given library module either
   * <br /> from standard places
   * Settings.libPlaces or
   * <br /> from the current .jar at Settings.libSource
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
      Debug.error(FileManager.class.getName() + ".loadLibrary: Native library could not be extracted nor found: " + libname);
      System.exit(1);
    }
    try {
      System.load(lib.getAbsolutePath());
    } catch (Error e) {
      Debug.error(FileManager.class.getName() + ".loadLibrary: Native library could not be loaded: " + libname);
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
    if(alreadyLoaded.indexOf("*"+libname)<0) {
      alreadyLoaded.append("*"+libname);
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
	 * Assume the list of resources can be found at path/filelist.txt
	 * @return the local path to the extracted resources
	 */
	public static String extract(String path) throws IOException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		InputStream in = cl.getResourceAsStream(path + "/filelist.txt");
		String localPath = System.getProperty("java.io.tmpdir") + "/sikuli/" + path;
		new File(localPath).mkdirs();
		Debug.log(4, "extract resources " + path + " to " + localPath);
		writeFileList(in, path, localPath);
		return localPath + "/";
	}

	private static void writeFileList(InputStream ins, String fromPath, String outPath) throws IOException {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		BufferedReader r = new BufferedReader(new InputStreamReader(ins));
		String line;
		while ((line = r.readLine()) != null) {
			Debug.log(7, "write " + line);
			if (line.startsWith("./")) {
				line = line.substring(1);
			}
			String fullpath = outPath + line;
			File outf = new File(fullpath);
			outf.getParentFile().mkdirs();
			InputStream in = cl.getResourceAsStream(fromPath + line);
			if (in != null) {
				OutputStream out = null;
				try {
					out = new FileOutputStream(outf);
					copy(in, out);
				} catch (IOException e) {
					Debug.log("Can't extract " + fromPath + line + ": " + e.getMessage());
				} finally {
					if (out != null) {
						out.close();
					}
				}
			} else {
				Debug.log("Resource not found: " + fromPath + line);
			}
		}
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

	public static String downloadURL(URL url, String localPath) throws IOException {
		InputStream reader = url.openStream();
		String[] path = url.getPath().split("/");
		String filename = path[path.length - 1];
		File fullpath = new File(localPath, filename);
		FileOutputStream writer = new FileOutputStream(fullpath);
		byte[] buffer = new byte[DOWNLOAD_BUFFER_SIZE];
		int totalBytesRead = 0;
		int bytesRead = 0;
		while ((bytesRead = reader.read(buffer)) > 0) {
			writer.write(buffer, 0, bytesRead);
			totalBytesRead += bytesRead;
		}
		reader.close();
		writer.close();
		return fullpath.getAbsolutePath();
	}

	public static String slashify(String path, boolean isDirectory) {
		if (path == null) {
			path = "";
		}
		String p = path;
		if (File.separatorChar != '/') {
			p = p.replace(File.separatorChar, '/');
		}
		if (!p.endsWith("/") && isDirectory) {
			p = p + "/";
		}
		return p;
	}

}
