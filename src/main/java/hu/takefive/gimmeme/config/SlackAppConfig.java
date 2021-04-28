package hu.takefive.gimmeme.config;

import com.slack.api.bolt.App;
import hu.takefive.gimmeme.handlers.SlackFileHandler;
import hu.takefive.gimmeme.handlers.SlackViewHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class SlackAppConfig {

  SlackFileHandler slackFileHandler;
  SlackViewHandler slackViewHandler;

  @Bean
  public App initSlackApp() {

    App app = new App();

    app.command("/listfiles", slackFileHandler::listFiles);
    app.command("/uploadfiles", slackFileHandler::uploadFiles);

    app.messageShortcut("Gimmeme!", slackViewHandler::handleSelectLayoutView);

    app.blockAction("text-top", slackViewHandler::handleSelectFontView);
    app.blockAction("text-bottom", slackViewHandler::handleSelectFontView);
    app.blockAction("text-middle", slackViewHandler::handleSelectFontView);
    app.blockAction("text-both", slackViewHandler::handleSelectFontView);

    app.blockAction("Trade Winds", slackViewHandler::handleInputTextView);
    app.blockAction("Londrina Shadow", slackViewHandler::handleInputTextView);
    app.blockAction("Fascinate Inline", slackViewHandler::handleInputTextView);
    app.blockAction("Kranky", slackViewHandler::handleInputTextView);

    app.viewSubmission("generate-meme", slackViewHandler::handleViewSubmission);

    return app;

  }

}
