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

    app.messageShortcut("Gimmeme!", slackViewHandler::handleSelectLayoutView);

    //TODO refactor: 1 action is enough, instead of actionId, view.block.value could be unique
    app.blockAction("text-top", slackViewHandler::handleInputTextView);
    app.blockAction("text-bottom", slackViewHandler::handleInputTextView);
    app.blockAction("text-middle", slackViewHandler::handleInputTextView);

    app.viewSubmission("generate-meme", slackViewHandler::handleViewSubmission);

    return app;

  }

}
