/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.text;

import java.awt.image.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import org.sikuli.core.Match;
import org.sikuli.core.OpenCV;
import org.sikuli.core.Region;
import org.sikuli.core.ScreenImage;
import org.sikuli.core.Settings;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.OCRWord;
import org.sikuli.script.natives.OCRWords;
import org.sikuli.script.natives.Vision;
import org.sikuli.utility.Debug;
import org.sikuli.utility.LibLoader;
import org.sikuli.utility.ResourceExtractor;

// Singleton
public class TextRecognizer {

  protected static TextRecognizer _instance = null;

  static {
    LibLoader.loadLibrary("VisionProxy");
//TODO         TextRecognizer tr = TextRecognizer.getInstance();
  }

  protected TextRecognizer() {
    init();
  }
  boolean _init_succeeded = false;

  public void init() {
    Debug.info("Text Recognizer inited.");
    try {
      String path = ResourceExtractor.extract("tessdata");
      // TESSDATA_PREFIX doesn't contain tessdata/
      if (path.endsWith("tessdata/")) {
        path = path.substring(0, path.length() - 9);
      }
      Settings.OcrDataPath = path;
      Debug.log(3, "OCR data path: " + path);

      Vision.initOCR(Settings.OcrDataPath);
      _init_succeeded = true;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static TextRecognizer getInstance() {
    if (_instance == null) {
      _instance = new TextRecognizer();
    }
    return _instance;
  }

  public enum ListTextMode {

    WORD, LINE, PARAGRAPH
  };

  public List<Match> listText(ScreenImage simg, Region parent) {
    return listText(simg, parent, ListTextMode.WORD);
  }

  //TODO: support LINE and PARAGRAPH
  // listText only supports WORD mode now.
  public List<Match> listText(ScreenImage simg, Region parent, ListTextMode mode) {
    Mat mat = OpenCV.convertBufferedImageToMat(simg.getImage());
    OCRWords words = Vision.recognize_as_ocrtext(mat).getWords();
    List<Match> ret = new LinkedList<Match>();
    for (int i = 0; i < words.size(); i++) {
      OCRWord w = words.get(i);
      Match m = new Match(parent.x + w.getX(), parent.y + w.getY(), w.getWidth(), w.getHeight(),
              w.getScore(), parent.getScreen(), w.getString());
      ret.add(m);
    }
    return ret;
  }

  public String recognize(ScreenImage simg) {
    BufferedImage img = simg.getImage();
    return recognize(img);
  }

  public String recognize(BufferedImage img) {
    if (_init_succeeded) {
      Mat mat = OpenCV.convertBufferedImageToMat(img);
      return Vision.recognize(mat).trim();
    } else {
      return "";
    }
  }

  public String recognizeWord(ScreenImage simg) {
    BufferedImage img = simg.getImage();
    return recognizeWord(img);
  }

  public String recognizeWord(BufferedImage img) {
    if (_init_succeeded) {
      Mat mat = OpenCV.convertBufferedImageToMat(img);
      return Vision.recognizeWord(mat).trim();
    } else {
      return "";
    }
  }
}
