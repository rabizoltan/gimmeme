package hu.takefive.gimmeme.models;

public enum TextTemplate {
  TEXT_TOP (
      "text-top",
      0.9f,
      0.15f,
      0.05f,
      0f,
      0.05f,
      0.05f,
      ""),

  TEXT_BOTTOM (
      "text-bottom",
      0.9f,
      0.15f,
      0f,
      0.05f,
      0.05f,
      0.05f,
      ""),

  TEXT_MIDDLE (
      "text-middle",
      0.9f,
      0.15f,
      0.05f,
      0.05f,
      0.05f,
      0.05f,
      "");

  public static final float FONT_ZOOM_RATIO = 0.2f;
  public static final String TEMPLATE_BG_PATH = "src/main/resources/static/fonts/";
  public static final String TEMPLATE_PREVIEW_PATH = "src/main/resources/static/fonts/preview/";

  private String actionId;
  private float maxWidth;
  private float maxHeight;
  private float posTop;
  private float posBottom;
  private float posLeft;
  private float posRight;
  private String bgImage;

  TextTemplate(
      String actionId,
      float maxWidth,
      float maxHeight,
      float posTop,
      float posBottom,
      float posLeft,
      float posRight,
      String bgImage) {
    this.actionId = actionId;
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    this.posTop = posTop;
    this.posBottom = posBottom;
    this.posLeft = posLeft;
    this.posRight = posRight;
    this.bgImage = bgImage;
  }

  public static TextTemplate getTextTemplateByActionId(String text) {
    for (TextTemplate textTemplate : TextTemplate.values()) {
      if (textTemplate.actionId.equalsIgnoreCase(text)) {
        return textTemplate;
      }
    }

    return TEXT_TOP;
  }

  public String getActionId() {
    return actionId;
  }

  public float getMaxWidth() {
    return maxWidth;
  }

  public float getMaxHeight() {
    return maxHeight;
  }

  public float getMaxHeight(String fontSize) {

    switch (fontSize) {
      case "big":
        return this.maxHeight * (1f + FONT_ZOOM_RATIO);
      case "small":
        return this.maxHeight * (1f - FONT_ZOOM_RATIO);
    }

    return this.maxHeight;
  }

  public float getPosTop() {
    return posTop;
  }

  public float getPosBottom() {
    return posBottom;
  }

  public float getPosLeft() {
    return posLeft;
  }

  public float getPosRight() {
    return posRight;
  }

  public String getBgImage() {
    return bgImage;
  }

}

