package hu.takefive.gimmeme.config;

import hu.takefive.gimmeme.models.TextFont;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class FontConfig {

  private GraphicsEnvironment localGE;

  @Bean
  public void initFonts() throws IOException, FontFormatException {
    this.localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();

    for (TextFont font : TextFont.values()) {
      if (!isFontInstalled(font.getFontName()))
        this.registerFont(font);
    }
  }

  private boolean isFontInstalled(String fontName) {
    Long matchingFontNamesCount = Arrays.stream(localGE.getAvailableFontFamilyNames())
        .filter(f -> f.equals(fontName))
        .count();

    return matchingFontNamesCount > 0L;
  }

  private boolean registerFont(TextFont font) throws IOException, FontFormatException {
    if (font.getFontFile() == null || font.getFontFile().isBlank())
      throw new IllegalArgumentException("Missing font filename in TextFont enum for font: " + font.getFontName());

    File fontFile = new File(TextFont.FONT_PATH + font.getFontFile());
    if (!fontFile.isFile())
      throw new IllegalArgumentException("Missing font file: " + TextFont.FONT_PATH + font.getFontFile());

    localGE.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));

    return true;
  }
}
