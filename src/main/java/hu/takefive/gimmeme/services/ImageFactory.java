package hu.takefive.gimmeme.services;

import hu.takefive.gimmeme.models.TextFont;
import hu.takefive.gimmeme.models.TextTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.UUID;

public class ImageFactory {

  public static String writeTextToImage(String url, String templateName, String fontName, String text) {
    try {

      URL imageUrl = new URL(url);
      BufferedImage image = ImageIO.read(imageUrl);
      Graphics2D g2dImage = image.createGraphics();

      System.out.println("Hash: " + UUID.randomUUID());

      Image textImage = getTextImage(image, templateName, fontName, text);

      g2dImage.drawImage(textImage, 0,0, null);
      g2dImage.dispose();

      File outputFile = new File("src/main/resources/static/saved.png");
      ImageIO.write(image, "png", outputFile);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }

    return "";
  }

  private static Image getTextImage(BufferedImage image, String actionId, String fontName, String text) {
    BufferedImage textImage = new BufferedImage(
        image.getWidth() * 8,
        image.getHeight() * 8,
        BufferedImage.TYPE_INT_ARGB);

    TextTemplate textTemplate = TextTemplate.getTextTemplateByActionId(actionId);
    Font font = TextFont.getTextFontByFontName(fontName).getFont();

    Graphics2D g2dText = textImage.createGraphics();

    int maxTextWidth = (int) (textImage.getWidth() * textTemplate.getMaxWidth());
    int maxTextHeight = (int) (textImage.getHeight() * textTemplate.getMaxHeight());
    float fontSize = calculateFontSize(g2dText, maxTextWidth, maxTextHeight, font, text);
    g2dText.setFont(font.deriveFont(font.getStyle(), fontSize));

    g2dText.setPaint(Color.white);

    FontMetrics fm = g2dText.getFontMetrics();
    int xPos = getXPosition(textImage.getWidth(), fm.stringWidth(text), textTemplate);
    int yPos = getYPosition(textImage.getHeight(), fm.getHeight(), fm.getAscent(), textTemplate);
    g2dText.drawString(text, xPos, yPos + fm.getAscent());
    g2dText.dispose();

    Image scaledTextImage = textImage.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH);

    return scaledTextImage;
  }

  private static float calculateFontSize(Graphics2D g2dText, int maxTextWidth, int maxTextHeight, Font font, String text) {
    g2dText.setFont(font);
    FontMetrics fm = g2dText.getFontMetrics();

    float originalTextWidth = fm.stringWidth(text);
    float originalFontSize = font.getSize2D();

    float textRatio = originalTextWidth / fm.getHeight();
    float maxRatio = maxTextWidth / maxTextHeight;

    if (textRatio > maxRatio) {
      return maxTextWidth / originalTextWidth * originalFontSize;
    }

    return maxTextHeight / fm.getHeight() * originalFontSize;
  }

  private static int getXPosition(int imageWidth, int stringWidth, TextTemplate textTemplate) {
    float left = imageWidth * textTemplate.getPosLeft();
    float right = imageWidth * textTemplate.getPosRight();

    if (left == 0f) {
      return (int) (imageWidth - stringWidth - right);
    } else if (right == 0f) {
      return (int) (left);
    }

    int freeSpace = imageWidth - stringWidth;
    return (int) (freeSpace / (left + right) * left);
  }

  private static int getYPosition(int imageHeight, int stringHeight, int fontAscent, TextTemplate textTemplate) {
    float top = imageHeight * textTemplate.getPosTop();
    float bottom = imageHeight * textTemplate.getPosBottom();

    if (top == 0f) {
      return (int) (imageHeight - bottom - stringHeight);
    } else if (bottom == 0f) {
      return (int) top;
    }

    int freeSpace = imageHeight - stringHeight;
    return (int) (freeSpace / (top + bottom) * top);
  }

  private boolean isDarkBackground(BufferedImage image) {

    int pixelCounter = 0;
    float luminance = 0f;
    for (int i = 0; i < image.getWidth(); i += 10) {
      for (int j = 0; j < image.getHeight(); j += 10) {
        int color = image.getRGB(i, j);
        int red   = (color >>> 16) & 0xFF;
        int green = (color >>>  8) & 0xFF;
        int blue  = (color >>>  0) & 0xFF;

        // calc luminance in range 0.0 to 1.0; using sRGB luminance constants
        luminance += (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
        pixelCounter++;
      }
    }

    if (luminance / pixelCounter < 0.5f) {
      return false;
    }

    return true;
  }

}
