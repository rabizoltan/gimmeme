package hu.takefive.gimmeme.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.files.FilesSharedPublicURLResponse;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.methods.response.views.ViewsUpdateResponse;
import com.slack.api.model.File;
import hu.takefive.gimmeme.services.ImageFactory;
import hu.takefive.gimmeme.services.UpdateViewBuilder;
import hu.takefive.gimmeme.services.ViewFactory;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static hu.takefive.gimmeme.services.ViewFactory.buildSelectLayoutView;

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
      logger.error("channelid: " + channelId);
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

    return updateView(req, ctx, "actionId", ViewFactory.buildSelectFontView);
  }

  public Response handleSelectFontSizeView(BlockActionRequest req, Context ctx) {

    return updateView(req, ctx, "fontName", ViewFactory.buildSelectFontSizeView);
  }

  public Response handleInputTextView(BlockActionRequest req, Context ctx) {

    return updateView(req, ctx, "fontSize", ViewFactory.buildInputTextView);
  }

  private Response updateView(BlockActionRequest req, Context ctx, String actionId, UpdateViewBuilder<String> viewBuilder) {
    Logger logger = ctx.logger;
    try {
      String privateMetadataJson = getUpdatedPrivateMetadata(req, actionId);

      ViewsUpdateResponse viewsUpdateResponse = ctx.client()
          .viewsUpdate(r -> r
              .viewId(req.getPayload().getView().getId())
              .view(viewBuilder.buildUpdateView(privateMetadataJson))
          );
      logger.info("viewsUpdateResponse: {}", viewsUpdateResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response handleViewSubmission(ViewSubmissionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    //TODO figure this out
//    Map<String, String> errors = new HashMap<>();
//    if (!errors.isEmpty()) {
//      return ctx.ack(r -> r.responseAction("errors").errors(errors));
//    } else {

      try {
        String privateMetadata = req.getPayload().getView().getPrivateMetadata();
        ObjectMapper objectMapper = new ObjectMapper();
        Map submissionData = objectMapper.readValue(privateMetadata, Map.class);

        String text = req.getPayload().getView().getState().getValues().get("text-block").get("text-input").getValue();

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

    private String getUpdatedPrivateMetadata(BlockActionRequest req, String key)
        throws JsonProcessingException {
      String privateMetadata = req.getPayload().getView().getPrivateMetadata();
      BlockActionPayload.Action action = req.getPayload().getActions().get(0);
      System.out.println("action type: " + action.getType());
      String actionId = action.getType() == "static_select"
          ? action.getSelectedOption().getValue()
          : action.getActionId();

      ObjectMapper objectMapper = new ObjectMapper();
      Map privateMetadataMap = objectMapper.readValue(privateMetadata, Map.class);
      privateMetadataMap.put(key, actionId);

      return objectMapper.writeValueAsString(privateMetadataMap);
    }

  }
