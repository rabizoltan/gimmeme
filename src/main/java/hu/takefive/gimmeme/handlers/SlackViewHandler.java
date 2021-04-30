package hu.takefive.gimmeme.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.app_backend.interactive_components.payload.GlobalShortcutPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
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

import static hu.takefive.gimmeme.services.ViewFactory.buildSelectImageView;
import static hu.takefive.gimmeme.services.ViewFactory.buildSelectLayoutView;
import static hu.takefive.gimmeme.services.ViewFactory.helpView;

@Service
@AllArgsConstructor
public class SlackViewHandler {

  SlackFileHandler slackFileHandler;

  public Response handleSelectImageView(SlashCommandRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewOpenThread = new Thread(() -> {
      try {
        SlashCommandPayload payload = req.getPayload();
        String channelId = payload.getChannelId();
        String teamId = payload.getTeamId();

        ViewsOpenResponse viewsOpenResponse = ctx.client()
            .viewsOpen(r -> r
                .token(System.getenv("SLACK_BOT_TOKEN"))
                .triggerId(payload.getTriggerId())
                .view(buildSelectImageView(channelId, teamId))
            );
        logger.info("viewsOpenResponse: {}", viewsOpenResponse);

      } catch (IOException | SlackApiException e) {
        logger.error("error: {}", e.getMessage(), e);
      }
    });
    viewOpenThread.start();

    return ctx.ack();
  }

  public Response handleSelectLayoutView(MessageShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewCreateThread = new Thread(() -> {
      File uploadedFile;
      MessageShortcutPayload payload = req.getPayload();

      try {
        String channelId = payload.getChannel().getId();

        //TODO fix to handle the rest of the files in the message
        if (payload.getMessage().getFiles() == null) {
          throw new IllegalArgumentException("Please select a picture!");
        }

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

         String permaLinkPublic = uploadedFile.getUrlPrivateDownload()
            + "?pub_secret="
            + uploadedFile.getPermalinkPublic().replaceAll(".+\\-([^\\-]+)$", "$1");

        ViewsOpenResponse viewsOpenResponse = ctx.client()
            .viewsOpen(r -> r
                .token(System.getenv("SLACK_BOT_TOKEN"))
                .triggerId(payload.getTriggerId())
                .view(buildSelectLayoutView(permaLinkPublic, channelId, fileType))
            );

        logger.info("viewsOpenResponse: {}", viewsOpenResponse);

      } catch (IllegalArgumentException e) {
        sendErrorView(ctx, payload.getTriggerId(), e.getMessage(), ViewFactory.buildAlertView);
      } catch (IOException | SlackApiException e) {
        logger.error("error: {}", e.getMessage(), e);
      }
    });
    viewCreateThread.start();

    return ctx.ack();
  }

  public Response handleSelectLayoutView(BlockActionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewSelectThread = new Thread(() -> {
      try {
        BlockActionPayload payload = req.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> privateMetadataMap = objectMapper.readValue(payload.getView().getPrivateMetadata(), Map.class);
        String channelId = privateMetadataMap.get("channelId");

        BlockActionPayload.Action action = payload.getActions().get(0);
        String fileType = action.getActionId().substring(action.getActionId().length() - 3);
        String permaLinkPublic = action.getValue();

        ViewsUpdateResponse viewsUpdateResponse = ctx.client()
            .viewsUpdate(r -> r
                .viewId(req.getPayload().getView().getId())
                .view(buildSelectLayoutView(permaLinkPublic, channelId, fileType))
            );
        logger.info("viewsUpdateResponse: {}", viewsUpdateResponse);

      } catch (IOException | SlackApiException e) {
        logger.error("error: {}", e.getMessage(), e);
      }
    });
    viewSelectThread.start();

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

    Thread viewUpdateThread = new Thread(() -> {
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
    });
    viewUpdateThread.start();

    return ctx.ack();
  }

  private void sendErrorView(Context ctx, String triggerId, String errorMessage, UpdateViewBuilder<String> viewBuilder) {
    Logger logger = ctx.logger;

      try {
        ViewsOpenResponse viewsOpenResponse = ctx.client()
            .viewsOpen(r -> r
                .token(System.getenv("SLACK_BOT_TOKEN"))
                .triggerId(triggerId)
                .view(viewBuilder.buildUpdateView(errorMessage))
            );
        logger.info("viewsOpenResponse: {}", viewsOpenResponse);

      } catch (IOException | SlackApiException e) {
        logger.error("error: {}", e.getMessage(), e);
      }

    ctx.ack();
  }

  private String getUpdatedPrivateMetadata(BlockActionRequest req, String key)
      throws JsonProcessingException {
    String privateMetadata = req.getPayload().getView().getPrivateMetadata();
    BlockActionPayload.Action action = req.getPayload().getActions().get(0);

    String actionId = "static_select".equals(action.getType())
        ? action.getSelectedOption().getValue()
        : action.getActionId();

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> privateMetadataMap = objectMapper.readValue(privateMetadata, Map.class);
    privateMetadataMap.put(key, actionId);

    return objectMapper.writeValueAsString(privateMetadataMap);
  }

  public Response handleViewSubmission(ViewSubmissionRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewSubmitThread = new Thread(() -> {
      try {
        String privateMetadata = req.getPayload().getView().getPrivateMetadata();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> submissionData = objectMapper.readValue(privateMetadata, Map.class);

        String text = req.getPayload().getView().getState().getValues().get("text-block").get("text-input").getValue();

          java.io.File file = ImageFactory.writeTextToImage(
              submissionData.get("imageUrl"),
              submissionData.get("fileType"),
              submissionData.get("actionId"),
              submissionData.get("fontName"),
              submissionData.getOrDefault("fontSize", ""),
              text
          );
          slackFileHandler.uploadFile(ctx, file, submissionData.get("channelId"));

      } catch (IOException e) {
        logger.error("error: {}", e.getMessage(), e);
      }
    });
    viewSubmitThread.start();

    return ctx.ack();
  }

  public Response handleHelpLayout(MessageShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewHelpThread = new Thread(() -> {
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
    });
    viewHelpThread.start();

    return ctx.ack();
  }

  public Response handleHelpLayout(GlobalShortcutRequest req, Context ctx) {
    Logger logger = ctx.logger;

    Thread viewHelpThread = new Thread(() -> {
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
    });
    viewHelpThread.start();

    return ctx.ack();
  }

}
