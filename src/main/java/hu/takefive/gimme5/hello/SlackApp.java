package hu.takefive.gimmeme.hello;

import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.model.Message;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.Blocks.actions;

import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Configuration
public class SlackApp {
  @Bean
  public App initSlackApp() {
    App app = new App();
    app.command("/hello", (req, ctx) -> {
      System.out.println(req.getPayload().toString());
      return ctx.ack(":wave: What's up?");
    });
    app.messageShortcut("generate-meme", (req, ctx) -> {
      String userId = req.getPayload().getUser().getId();
      Message message = req.getPayload().getMessage();
      System.out.println(req.getPayload().toString());
      ViewsOpenResponse viewsOpenResp = ctx.client().viewsOpen(r -> r.triggerId(ctx.getTriggerId())
              .view(buildView(message)));
      if (!viewsOpenResp.isOk()) {
        String errorCode = viewsOpenResp.getError();
        ctx.logger.error("Failed to open a modal view for user: {} - error: {}", userId, errorCode);
        ctx.respond(":x: Failed to open a modal view because of " + errorCode);
      }
      return Response.ok(viewsOpenResp);
    });
    app.messageShortcut("template-selected", (req, ctx) -> {
      System.out.println(req.getPayload().toString());
      return ctx.ack();
    });
    app.blockAction("farmhouse", (req, ctx) -> {
      System.out.println(req.getPayload().toString());
      return ctx.ack();
    });
    return app;
  }

  private View buildView(Message message) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Title").build()));
    view.setCallbackId("template-selected");
    if (message.getFile() != null) {
      String imageUrl = message.getFile().getUrlPrivateDownload();
      view.setPrivateMetadata("{\"image\"=\"" + imageUrl + "\"}");
    }
    view.setBlocks(asBlocks(
            section(section -> section.text(markdownText("*Please select a restaurant:*"))),
            divider(),
            actions(actions -> actions
                    .elements(asElements(
                            button(b -> b.text(plainText(pt -> pt.emoji(true).text("Farmhouse"))).actionId("farmhouse").value("v1")),
                            button(b -> b.text(plainText(pt -> pt.emoji(true).text("Kin Khao"))).actionId("khao").value("v2"))
                    ))
            )
    ));

    System.out.println(view);
    return view;
  }
}