package hu.takefive.gimmeme.services;

import hu.takefive.gimmeme.models.TextFont;
import hu.takefive.gimmeme.models.TextTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Slf4j
public class ImageFactory {

  public static final int TEXT_ZOOM = 8;
  public static final String APP_HOME = System.getenv("APP_HOME");

  public static File writeTextToImage(
      String url,
      String fileType,
      String templateName,
      String fontName,
      String fontSize,
      String text) {

    File outputFile = null;

    try {
      BufferedImage image = downloadImage(url);

      Graphics2D g2dImage = image.createGraphics();
      String[] textParts = text.split(" *\n *");

      if (textParts.length > 1) {
        text = text.replaceAll(textParts[0] + " *\n *", "");
        templateName = "text-bottom";

        Image textImage = getTextImage(image, "text-top", fontName, fontSize, textParts[0]);
        g2dImage.drawImage(textImage, 0,0, null);
      }

      Image textImage = getTextImage(image, templateName, fontName, fontSize, text);
      g2dImage.drawImage(textImage, 0,0, null);
      g2dImage.dispose();

      outputFile = new File(APP_HOME + "src/main/resources/static/images/gallery/" + UUID.randomUUID() + "." + fileType);
      ImageIO.write(image, fileType, outputFile);
    }
    catch (Exception e) {
      log.error(String.valueOf(e));
      log.error(url);
    }

    return outputFile;
  }

  private static BufferedImage downloadImage(String url) throws IOException {
    BufferedImage image = null;
    URL imageUrl = new URL(url);
    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
    try {
      connection.setRequestProperty(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:37.0) Gecko/20100101 Firefox/37.0");
      image = ImageIO.read(connection.getInputStream());

    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to load remote file: " + url, e);
    } finally {
      connection.disconnect();
    }

    return image;
  }

  private static Image getTextImage(BufferedImage image, String actionId, String fontName, String fontSize, String text) {
    BufferedImage textImage = new BufferedImage(
        image.getWidth() * ImageFactory.TEXT_ZOOM,
        image.getHeight() * ImageFactory.TEXT_ZOOM,
        BufferedImage.TYPE_INT_ARGB);

    TextTemplate textTemplate = TextTemplate.getTextTemplateByActionId(actionId);
    Font font = TextFont.getTextFontByFontName(fontName).getFont();

    Graphics2D g2dText = textImage.createGraphics();

    int maxTextWidth = (int) (textImage.getWidth() * textTemplate.getMaxWidth());
    int maxTextHeight = (int) (textImage.getHeight() * textTemplate.getMaxHeight(fontSize));
    float actualFontSize = getFontSize(g2dText, maxTextWidth, maxTextHeight, font, text);
    g2dText.setFont(font.deriveFont(font.getStyle(), actualFontSize));

    FontMetrics fm = g2dText.getFontMetrics();
    int stringWidth = fm.stringWidth(text);
    int textPosX = getXPosition(textImage.getWidth(), stringWidth, textTemplate);
    int textPosY = getYPosition(textImage.getHeight(), fm.getHeight(), fm.getAscent(), textTemplate);

    int[] bgShape = getTextBackgroundShape(text, g2dText, fm, stringWidth, textPosX, textPosY);
    boolean isBgDark = isImageDark(image, bgShape);

    int textBgColor = isBgDark ? 0 : 255;
    g2dText.setColor(new Color(textBgColor, textBgColor, textBgColor, 90));
    g2dText.fillRoundRect(bgShape[0], bgShape[1], bgShape[2], bgShape[3], bgShape[4], bgShape[4]);

    g2dText.setColor(isBgDark ? Color.WHITE : Color.BLACK);
    g2dText.drawString(text, textPosX, textPosY + fm.getAscent());
    g2dText.dispose();

    Image scaledTextImage = textImage.getScaledInstance(image.getWidth(), image.getHeight(), Image.SCALE_SMOOTH);

    return scaledTextImage;
  }

  private static int[] getTextBackgroundShape(
      String text,
      Graphics2D g2dText,
      FontMetrics fm,
      int stringWidth,
      int textPosX,
      int textPosY) {
    int bgOffsetX = (int) (stringWidth * 0.025f);
    float ascentMulti = text.matches(".*[qpgjy]+.*") ? 1.75f : 1.5f;
    int bgOffsetY = fm.getHeight() / 2 - (int) (fm.getAscent() / ascentMulti);

    return new int[]{
        textPosX < bgOffsetX ? 0 : textPosX - bgOffsetX,
        textPosY + bgOffsetY,
        stringWidth + 2 * bgOffsetX,
        fm.getHeight(),
        fm.getHeight() / 3,
    };
  }

  private static float getFontSize(Graphics2D g2dText, int maxTextWidth, int maxTextHeight, Font font, String text) {
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

  private static boolean isImageDark(BufferedImage image, int[] bgShape) {

    int pixelCounter = 0;
    float luminance = 0f;

    int shapeRightX = (bgShape[0] + bgShape[2]) / TEXT_ZOOM;
    int shapeBottomY = (bgShape[1] + bgShape[3]) / TEXT_ZOOM;

    for (int x = (int) bgShape[0] / TEXT_ZOOM;
         x < shapeRightX && x < image.getWidth();
         x += 10) {
      for (int y = (int) bgShape[1] / TEXT_ZOOM;
           y < shapeBottomY && y < image.getHeight();
           y += 10) {
        int color = image.getRGB(x, y);
        int red   = (color >>> 16) & 0xFF;
        int green = (color >>>  8) & 0xFF;
        int blue  = (color) & 0xFF;

        // calc luminance in range 0.0 to 1.0; using sRGB luminance constants
        luminance += (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;
        pixelCounter++;
      }
    }

    return luminance / (float) pixelCounter < 0.5f;
  }

}
