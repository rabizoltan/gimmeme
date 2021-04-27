package hu.takefive.gimmeme.handlers;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;
import lombok.var;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Service
public class SlackViewHandler {

  public Response buildView(SlashCommandRequest req, Context ctx) {

    var logger = ctx.logger;

    try {
      var payload = req.getPayload();

      var viewsOpenResponse = ctx.client().viewsOpen(r -> r
          .token(System.getenv("SLACK_BOT_TOKEN"))
          .triggerId(payload.getTriggerId())
          .view(buildSelectImageView())
      );
      logger.info("viewsOpenResponse: {}", viewsOpenResponse);

    } catch (IOException | SlackApiException e) {
      logger.error("error: {}", e.getMessage(), e);
    }

    return ctx.ack();
  }

  private View buildSelectImageView() {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimme 5").build()));
    view.setCallbackId("template-selected");

    view.
        setBlocks(asBlocks(
            section(section -> section.text(markdownText("*Please choose a meme template:*"))),
            divider(),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://lh3.googleusercontent.com/cvfpnTKw3B67DtM1ZpJG2PNAIjP6hVMOyYy403X4FMkOuStgG1y4cjCn21vmTnnsip1dTZSVsWBA9IxutGuA3dVDWhg=w128-h128-e365-rj-sc0x00ffffff").altText("pepe")),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://wallup.net/wp-content/uploads/2017/11/22/389070-Pepe_meme-FeelsBadMan-memes.jpg").altText("pepe")),
            actions(actions -> actions
                .elements(asElements(
                    button(b -> b.text(plainText(pt -> pt.emoji(true).text("Choose long text here bro! Choose long text here bro!"))).actionId("v1").value("v1"))
                ))
            ),
            divider(),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01UY5RTR9D/1111.jpg?pub_secret=d0a8b00875").altText("pepe")),
            actions(actions -> actions
                .elements(asElements(
                    button(b -> b.text(plainText(pt -> pt.emoji(true).text("Choose"))).actionId("v2").value("v2"))
                ))
            ),
            divider(),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01VBCKFELA/gimme-five.png?pub_secret=f01b0adf40").altText("pepe")),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01UY5RTR9D/1111.jpg?pub_secret=d0a8b00875").altText("pepe")),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020NA985LY/3.jpg?pub_secret=3557b740a2").altText("pepe"))));

    return view;
  }

}
