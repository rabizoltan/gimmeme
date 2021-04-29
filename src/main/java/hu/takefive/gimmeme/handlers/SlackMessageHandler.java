package hu.takefive.gimmeme.handlers;

import com.google.gson.JsonObject;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.response.views.ViewsUpdateResponse;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static hu.takefive.gimmeme.services.ViewFactory.commandHelpView;

@Service
public class SlackMessageHandler {

  public Response greetings(SlashCommandRequest req, Context ctx) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("channelId", req.getPayload().getChannelId());
    jsonObject.addProperty("text", ":wave: Greetings traveler!");
    return ctx.ack(jsonObject);
  }

  public Response commandHelp(BlockActionRequest req, Context ctx) {
    String commandName = req.getPayload().getActions().get(0).getSelectedOption().getValue();

    Logger logger = ctx.logger;

    try {
      ViewsUpdateResponse viewsUpdateResponse = ctx.client()
              .viewsUpdate(r -> r
                      .viewId(req.getPayload().getView().getId())
                      .view(commandHelpView(commandName))
              );
      logger.info("viewsUpdateResponse: {}", viewsUpdateResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }



//  public Response help(SlashCommandRequest req, Context ctx) throws FileNotFoundException {
//
//  }
}
