package hu.takefive.gimmeme;

import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.methods.response.views.ViewsUpdateResponse;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;
import hu.takefive.gimmeme.handlers.SlackFileHandler;
import hu.takefive.gimmeme.handlers.SlackViewHandler;
import lombok.AllArgsConstructor;
import lombok.var;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;

@Configuration
@AllArgsConstructor
public class SlackApp {

  SlackFileHandler slackFileHandler;
  SlackViewHandler slackViewHandler;

  @Bean
  public App initSlackApp() throws Exception {

    App app = new App();

    app.command("/listFiles", slackFileHandler::listFiles);
    app.command("/uploadfiles", slackFileHandler::uploadFiles);

    app.command("/walami", slackViewHandler::buildView);

    app.blockAction("v1", (req, ctx) -> {
      String externalId = req.getPayload().getView().getExternalId();
//      System.out.println(req.getPayload().getView().getExternalId());

      var viewsUpdateResp = ctx.client()
          .viewsUpdate(r -> r
              .externalId(externalId)
              .view(buildNextView()));
//      return Response.ok(viewsUpdateResp);
      return ctx.ack();

    });

    return app;
  }

//    app.messageShortcut("template-selected", (req, ctx) -> {
//      System.out.println(req.getPayload().toString());
//      return ctx.ack();
//    });


  private View buildNextView() {
    View view = new View();

    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimme 5").build()));
    view.setCallbackId("template-selected");

    view.
        setBlocks(asBlocks(
            section(section -> section.text(markdownText("*Compose your mem!*"))),
            divider(),
            image(imageElementBuilder -> imageElementBuilder.imageUrl("https://wallup.net/wp-content/uploads/2017/11/22/389070-Pepe_meme-FeelsBadMan-memes.jpg").altText("pepe")),
            actions(actions -> actions
                .elements(asElements(
                    button(b -> b.text(plainText(pt -> pt.emoji(true).text("Go!"))).actionId("v1").value("v1"))
                ))
            )
        ));
    return view;
  }


}
