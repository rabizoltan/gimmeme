package hu.takefive.gimmeme.handlers;

import com.google.gson.JsonObject;
import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.view.View;
import org.springframework.stereotype.Service;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.*;

import java.io.FileNotFoundException;

@Service
public class SlackMessageHandler {

  public Response greetings(SlashCommandRequest req, Context ctx) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("channelId", req.getPayload().getChannelId());
    jsonObject.addProperty("text", ":wave: Greetings traveler!");
    return ctx.ack(jsonObject);
  }

  private View helpView() {
    return view(view -> view
            .callbackId("meeting-arrangement")
            .type("modal")
            .notifyOnClose(true)
            .title(viewTitle(title -> title.type("plain_text").text("Meeting Arrangement").emoji(true)))
            .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)))
            .close(viewClose(close -> close.type("plain_text").text("Cancel").emoji(true)))
            .privateMetadata("{\"response_url\":\"https://hooks.slack.com/actions/T1ABCD2E12/330361579271/0dAEyLY19ofpLwxqozy3firz\"}")
            .blocks(asBlocks(
                    section(section -> section
                            .blockId("category-block")
                            .text(markdownText("Select a category of the meeting!"))
                            .accessory(staticSelect(staticSelect -> staticSelect
                                    .actionId("category-selection-action")
                                    .placeholder(plainText("Select a category"))
                                    .options(asOptions(
                                            option(plainText("Customer"), "customer"),
                                            option(plainText("Partner"), "partner"),
                                            option(plainText("Internal"), "internal")
                                    ))
                            ))
                    ),
                    input(input -> input
                            .blockId("agenda-block")
                            .element(plainTextInput(pti -> pti.actionId("agenda-action").multiline(true)))
                            .label(plainText(pt -> pt.text("Detailed Agenda").emoji(true)))
                    )
            ))
    );
  }

  public Response help(SlashCommandRequest req, Context ctx) throws FileNotFoundException {

  }
}
