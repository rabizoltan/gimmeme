package hu.takefive.gimmeme.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
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

  public Response handleInputTextView(BlockActionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      String privateMetaData = req.getPayload().getView().getPrivateMetadata();
      String actionId = req.getPayload().getActions().get(0).getActionId();

      ObjectMapper objectMapper = new ObjectMapper();
      Map<String, Object> privateMetaDataMap = objectMapper.readValue(privateMetaData, HashMap.class);
      privateMetaDataMap.put("actionId", actionId);
      String privateMetaDataJson = objectMapper.writeValueAsString(privateMetaDataMap);

      ViewsUpdateResponse viewsUpdateResponse = ctx.client()
          .viewsUpdate(r -> r
              .viewId(req.getPayload().getView().getId())
              .view(buildInputTextView(privateMetaDataJson))
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

        System.out.println(submissionData.get("fileType").toString());





        Thread memGenThread = new Thread(() -> {
          java.io.File file = ImageFactory.writeTextToImage(
              submissionData.get("imageUrl").toString(),
              submissionData.get("fileType").toString(),
              submissionData.get("actionId").toString(),
              "Arial",
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

  }
