package hu.takefive.gimmeme.handlers;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.files.FilesListResponse;
import com.slack.api.methods.response.files.FilesSharedPublicURLResponse;
import com.slack.api.methods.response.files.FilesUploadResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SlackFileHandler {

  public Response uploadFile(Context ctx, File file, String channelId) {
    Logger logger = ctx.logger;
    logger.error(channelId);
    try {
      FilesUploadResponse filesUploadResponse = ctx.client().filesUpload(r -> r
          .token(System.getenv("SLACK_BOT_TOKEN"))
          .channels(Arrays.asList(channelId))
          .initialComment("Here's my file :smile:")
          .file(file)
          .filename(file.getName())
      );
      logger.info("filesUploadResponse: {}", filesUploadResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response listFiles(SlashCommandRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      FilesListResponse fileListResponse = ctx.client().filesList (r -> r
          .token(ctx.getBotToken())
      );
      logger.info("fileListResponse: {}", fileListResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public Response uploadFiles(SlashCommandRequest req, Context ctx) {
    Logger logger = ctx.logger;

    try {
      List<File> staticFileList = getStaticFileList();

      for (File file : staticFileList) {
        FilesUploadResponse filesUploadResponse = ctx.client().filesUpload(r -> r
            .token(System.getenv("SLACK_BOT_TOKEN"))
            .channels(Arrays.asList(req.getPayload().getChannelId()))
            .initialComment("Here's my file :smile:")
            .file(new File(String.valueOf(file)))
            .filename(file.getName())
        );
        logger.info("filesUploadResponse: {}", filesUploadResponse);

        FilesSharedPublicURLResponse filesSharedPublicURLResponse = ctx.client().filesSharedPublicURL(r -> r
            .token(System.getenv("SLACK_USER_TOKEN"))
            .file(filesUploadResponse.getFile().getId())
        );
        logger.info("filesSharedPublicURLResponse: {}", filesSharedPublicURLResponse);
      }

    } catch(IOException | SlackApiException e){
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  public static List<File> getStaticFileList() {
    List<File> result = new ArrayList<>();
    File dir = new File("src/main/resources/static/images");
    File[] directoryListing = dir.listFiles();

    if (directoryListing != null) {
      for (File child : directoryListing) {
        result.add(child);
      }
      return result;

    } else {
      return null;
    }

  }

}
