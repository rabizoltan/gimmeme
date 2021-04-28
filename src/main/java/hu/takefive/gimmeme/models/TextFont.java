package hu.takefive.gimmeme.models;

import java.awt.*;
import java.io.File;

public enum TextFont {
  ARIAL (
      "Arial",
      "",
      ""),

  LONDRINA_SHADOW (
      "Londrina Shadow",
      "LondrinaShadow.ttf",
      ""),

  KRANKY(
      "Kranky",
      "Kranky.ttf",
      ""),

  TRADE_WINDS(
      "Trade Winds",
      "TradeWinds.ttf",
      ""
  );

  public static final String FONT_PATH = "src/main/resources/static/fonts/";
  public static final String FONT_PREVIEW_PATH = "src/main/resources/static/fonts/preview/";

  private String fontName;
  private String fontFile;
  private String fontPreview;

  TextFont(String fontName, String fontFile, String fontPreview) {
    this.fontName = fontName;
    this.fontFile = fontFile;
    this.fontPreview = fontPreview;
  }

  public Font getFont() {
    return new Font(this.fontName, Font.PLAIN, 12);
  }

  public static TextFont getTextFontByFontName(String text) {
    for (TextFont textFont : TextFont.values()) {
      if (textFont.fontName.equalsIgnoreCase(text)) {
        return textFont;
      }
    }

    return ARIAL;
  }

  public String getFontName() {
    return fontName;
  }

  public String getFontFile() {
    return fontFile;
  }

  public String getFontPreview() {
    return fontPreview;
  }

  public boolean hasPreview() {
    return fontPreview != null
        && !fontPreview.trim().equals("")
        && new File(FONT_PREVIEW_PATH + fontPreview).isFile();
  }

}
