package hu.takefive.gimmeme.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.app_backend.interactive_components.payload.GlobalShortcutPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.files.FilesSharedPublicURLResponse;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.methods.response.views.ViewsUpdateResponse;
import com.slack.api.model.File;
import com.slack.api.model.view.ViewState;
import hu.takefive.gimmeme.services.ImageFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static hu.takefive.gimmeme.services.ViewFactory.buildInputTextView;
import static hu.takefive.gimmeme.services.ViewFactory.buildSelectLayoutView;
import static hu.takefive.gimmeme.services.ViewFactory.helpView;
import static hu.takefive.gimmeme.services.ViewFactory.buildSelectFontView;

@Service
@AllArgsConstructor
public class SlackViewHandler {

  SlackFileHandler slackFileHandler;

  public Response handleSelectLayoutView(MessageShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;
    File uploadedFile;

    try {
      MessageShortcutPayload payload = req.getPayload();
      String teamId = payload.getTeam().getId();
      String channelId = payload.getChannel().getId();
      //TODO fix to handle the rest of the files in the message
      String fileType = payload.getMessage().getFiles().get(0).getFiletype();

      FilesSharedPublicURLResponse filesSharedPublicURLResponse = ctx.client().filesSharedPublicURL(r -> r
              .token(System.getenv("SLACK_USER_TOKEN"))
              .file(payload.getMessage().getFiles().get(0).getId())
      );
      logger.info("filesSharedPublicURLResponse: {}", filesSharedPublicURLResponse);

      if (!filesSharedPublicURLResponse.isOk()) {
        uploadedFile = payload.getMessage().getFiles().get(0);
      } else {
        uploadedFile = filesSharedPublicURLResponse.getFile();
      }
      //TODO bug: cannot handle numbers and spaces in filename ???
      String permaLinkPublic = String.format("https://slack-files.com/files-pri/%s-%s/%s?pub_secret=%s", teamId, uploadedFile.getId(), uploadedFile.getName(), uploadedFile.getPermalinkPublic().substring(uploadedFile.getPermalinkPublic().length() - 10));

      ViewsOpenResponse viewsOpenResponse = ctx.client()
          .viewsOpen(r -> r
              .token(System.getenv("SLACK_BOT_TOKEN"))
              .triggerId(payload.getTriggerId())
              .view(buildSelectLayoutView(permaLinkPublic, channelId, fileType))
          );

      logger.info("viewsOpenResponse: {}", viewsOpenResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response handleSelectFontView(BlockActionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      String actionId = req.getPayload().getActions().get(0).getActionId();
      String privateMetadataString = req.getPayload().getView().getPrivateMetadata();

      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> privateMetadataMap = objectMapper.readValue(privateMetadataString, HashMap.class);
      privateMetadataMap.put("actionId", actionId);
      String privateMetadataJson = objectMapper.writeValueAsString(privateMetadataMap);

      ViewsUpdateResponse viewsUpdateResponse = ctx.client()
              .viewsUpdate(r -> r
                  .viewId(req.getPayload().getView().getId())
                  .view(buildSelectFontView(privateMetadataJson))
              );
      logger.info("viewsUpdateResponse: {}", viewsUpdateResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response handleInputTextView(BlockActionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      String privateMetadata = req.getPayload().getView().getPrivateMetadata();
      String fontName = req.getPayload().getActions().get(0).getActionId();

      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> privateMetadataMap = objectMapper.readValue(privateMetadata, HashMap.class);
      privateMetadataMap.put("fontName", fontName);
      String privateMetadataJson = objectMapper.writeValueAsString(privateMetadataMap);

      ViewsUpdateResponse viewsUpdateResponse = ctx.client()
          .viewsUpdate(r -> r
              .viewId(req.getPayload().getView().getId())
              .view(buildInputTextView(privateMetadataJson))
          );
      logger.info("viewsUpdateResponse: {}", viewsUpdateResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response handleViewSubmission(ViewSubmissionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    String privateMetadata = req.getPayload().getView().getPrivateMetadata();
    Map<String, Map<String, ViewState.Value>> stateValues = req.getPayload().getView().getState().getValues();
    String text = stateValues.get("text-block").get("text-input").getValue();

    //TODO figure this out
//    Map<String, String> errors = new HashMap<>();
//    if (!errors.isEmpty()) {
//      return ctx.ack(r -> r.responseAction("errors").errors(errors));
//    } else {

      try {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> submissionData = objectMapper.readValue(privateMetadata, HashMap.class);

        //TODO: get font size from view
        Thread memGenThread = new Thread(() -> {
          java.io.File file = ImageFactory.writeTextToImage(
              submissionData.get("imageUrl").toString(),
              submissionData.get("fileType").toString(),
              submissionData.get("actionId").toString(),
              submissionData.get("fontName").toString(),
              submissionData.getOrDefault("fontSize", "").toString(),
              text
          );
          slackFileHandler.uploadFile(ctx, file, submissionData.get("channelId").toString());
        });
        memGenThread.start();

      } catch (IOException e) {
        logger.error("error: {}", e.getMessage(), e);
      }

      return ctx.ack();
    }

  public Response handleHelpLayout(MessageShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      MessageShortcutPayload payload = req.getPayload();

      ViewsOpenResponse viewsOpenResponse = ctx.client()
              .viewsOpen(r -> r
                      .token(System.getenv("SLACK_BOT_TOKEN"))
                      .triggerId(payload.getTriggerId())
                      .view(helpView())
              );

      logger.info("viewsOpenResponse: {}", viewsOpenResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();

  }

  public Response handleHelpLayout(GlobalShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      GlobalShortcutPayload payload = req.getPayload();

      ViewsOpenResponse viewsOpenResponse = ctx.client()
              .viewsOpen(r -> r
                      .token(System.getenv("SLACK_BOT_TOKEN"))
                      .triggerId(payload.getTriggerId())
                      .view(helpView())
              );

      logger.info("viewsOpenResponse: {}", viewsOpenResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();

  }

  }
