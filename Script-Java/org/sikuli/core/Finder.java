/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2012
 */
package org.sikuli.core;

import java.io.IOException;
import java.util.Iterator;
import org.sikuli.script.natives.FindInput;
import org.sikuli.script.natives.FindResult;
import org.sikuli.script.natives.FindResults;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.TARGET_TYPE;
import org.sikuli.script.natives.Vision;
import org.sikuli.text.TextRecognizer;
import org.sikuli.utility.Debug;
import org.sikuli.utility.LibLoader;
import org.sikuli.utility.Util;

public class Finder implements Iterator<Match> {

  private Region _region = null;
  private Pattern _pattern = null;
  private FindInput _findInput = new FindInput();
  private FindResults _results = null;
  private int _cur_result_i;
  private boolean repeating = false;

  //TODO Vision.setParameter("GPU", 1);
  static {
    LibLoader.loadLibrary("VisionProxy");
  }

  /**
   * Finder constructor (finding within an image).
   * <br />internally used with a screen snapshot
   *
   * @param imageFilename a string (name, path, url)
   */
  public Finder(String imageFilename) throws IOException {
    this(imageFilename, null);
  }

  /**
   * Finder constructor (finding within an image within the given region).
   * <br />internally used with a screen snapshot
   *
   * @param imageFilename a string (name, path, url)
   * @param region search Region within image - topleft = (0,0)
   */
  public Finder(String imageFilename, Region region) throws IOException  {
    String fname = ImageLocator.locate(imageFilename);
    _findInput.setSource(fname);
    _region = region;
  }

  /**
   * Finder constructor for internal use
   * <br />(finding in memory image within the given region).
   */
  public Finder(ScreenImage img, Region region) {
    Mat target = OpenCV.convertBufferedImageToMat(img.getImage());
    _findInput.setSource(target);
    _region = region;
  }

  public void setRepeating() {
    repeating = true;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    destroy();
  }

  public void destroy() {
  }

//TODO: find / findAll imagefile not found early abort
  /**
   * find given pattern within the stored image
   *
   * @param aPtn
   */
  public String find(Pattern aPtn) {
    if (null == aPtn.checkFile()) {
      return null;
    }
    setFindInput(aPtn);
    _results = Vision.find(_findInput);
    _cur_result_i = 0;
    return aPtn.getFilename();
  }

  /**
   *
   * @param imageOrText
   * @param minSimilarity
   */
  public String find(String imageOrText, double minSimilarity) {
    if (null == setTargetSmartly(_findInput, imageOrText)) {
      return null;
    }
    _findInput.setSimilarity(minSimilarity);
    _results = Vision.find(_findInput);
    _cur_result_i = 0;
    return _findInput.getTargetText();
  }

  public String find(String imageOrText) {
    return find(imageOrText, Settings.MinSimilarity);
  }

  /**
   *
   * @param Pattern
   * @param aPtn
   */
  public String findAll(Pattern aPtn)  {
    if (null == aPtn.checkFile()) {
      return null;
    }
    Debug timing = new Debug();
    timing.startTiming("Finder.findAll");

    setFindInput(aPtn);
    _findInput.setFindAll(true);
    _results = Vision.find(_findInput);
    _cur_result_i = 0;

    timing.endTiming("Finder.findAll");
    return aPtn.getFilename();
  }

  /**
   *
   * @param imageOrText
   * @param minSimilarity
   */
  public String findAll(String imageOrText, double minSimilarity) {
    if (null == setTargetSmartly(_findInput, imageOrText)) {
      return null;
    }
    Debug timing = new Debug();
    timing.startTiming("Finder.findAll");

    setTargetSmartly(_findInput, imageOrText);
    _findInput.setSimilarity(minSimilarity);
    _findInput.setFindAll(true);
    _results = Vision.find(_findInput);
    _cur_result_i = 0;

    timing.endTiming("Finder.findAll");
    return _findInput.getTargetText();
  }

  public String findAll(String imageOrText) {
    return findAll(imageOrText, Settings.MinSimilarity);
  }

  /**
   *
   * @return true if Finder has a next match, false otherwise
   */
  @Override
  public boolean hasNext() {
    if (_results != null && _results.size() > _cur_result_i) {
      return true;
    }
    return false;
  }

  /**
   *
   * @return the next match or null
   */
  @Override
  public Match next() {
    Match ret = null;
    if (hasNext()) {
      FindResult fr = _results.get(_cur_result_i++);
      Screen parentScreen = null;
      if (_region != null) {
        parentScreen = _region.getScreen();
      }
      ret = new Match(fr, parentScreen);
      if (_region != null) {
        ret = _region.toGlobalCoord(ret);
      }
      if (_pattern != null) {
        Location offset = _pattern.getTargetOffset();
        ret.setTargetOffset(offset);
      }
    }
    return ret;
  }

  /**
   * not implemented
   */
  @Override
  public void remove() {
  }

  private <PatternString> void setFindInput(PatternString ptn) {
    if (ptn instanceof Pattern) {
      _pattern = (Pattern) ptn;
      Mat targetMat = OpenCV.convertBufferedImageToMat(_pattern.getImage());
      _findInput.setTarget(targetMat);
      _findInput.setSimilarity(_pattern.getSimilar());
    } else if (ptn instanceof String) {
      setTargetSmartly(_findInput, (String) ptn);
      _findInput.setSimilarity(Settings.MinSimilarity);
    }
  }

  private String setTargetSmartly(FindInput fin, String target) {
    try {
      //assume it's a file first
      String filename = ImageLocator.locate(target);
      fin.setTarget(TARGET_TYPE.IMAGE, filename);
      return filename;
    } catch (IOException e) {
      if (Settings.OcrTextSearch && Util.isImageFile(target)) {
        if (!repeating) {
          Debug.error(target +
                " looks like a file, but not on disk. Assume it's text.");
        }
        // this will init text recognizer on demand
        TextRecognizer tr = TextRecognizer.getInstance();
        //assume it's text
        fin.setTarget(TARGET_TYPE.TEXT, target);
      } else {
        return null;
      }
      return target;
    }
  }
}
