package hu.takefive.gimmeme.config;

import com.slack.api.bolt.App;
import hu.takefive.gimmeme.handlers.SlackFileHandler;
import hu.takefive.gimmeme.handlers.SlackMessageHandler;
import hu.takefive.gimmeme.handlers.SlackViewHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class SlackAppConfig {

  SlackFileHandler slackFileHandler;
  SlackViewHandler slackViewHandler;
  SlackMessageHandler slackMessageHandler;

  @Bean
  public App initSlackApp() {

    App app = new App();

    app.command("/listFiles", slackFileHandler::listFiles);
    app.command("/uploadfiles", slackFileHandler::uploadFiles);
    app.command("/greetings", slackMessageHandler::greetings);

    app.command("/rabi-gimmeme", slackViewHandler::buildView);
    app.blockAction("pickTemplate", slackViewHandler::updateView);

    return app;
  }

//    app.messageShortcut("template-selected", (req, ctx) -> {
//      System.out.println(req.getPayload().toString());
//      return ctx.ack();
//    });

}
