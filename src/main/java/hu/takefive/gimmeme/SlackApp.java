package hu.takefive.gimmeme;

import com.slack.api.bolt.App;
import hu.takefive.gimmeme.handlers.SlackFileHandler;
import hu.takefive.gimmeme.handlers.SlackViewHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    app.command("/buildView", slackViewHandler::buildView);

    return app;
  }

//    app.messageShortcut("template-selected", (req, ctx) -> {
//      System.out.println(req.getPayload().toString());
//      return ctx.ack();
//    });

//    app.blockAction("v1", (req, ctx) -> {
//      ViewsOpenResponse viewsOpenResp = ctx.client()
//          .viewsOpen(r -> r
//              .triggerId(ctx.getTriggerId())
//              .view(buildNextView()));
//
//      System.out.println(req.getPayload().toString());
//      return Response.ok(viewsOpenResp);
//    });

}
