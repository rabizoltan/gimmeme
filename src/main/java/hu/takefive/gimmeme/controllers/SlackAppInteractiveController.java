package hu.takefive.gimmeme.controllers;

import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet("/slack/interactive")
public class SlackAppInteractiveController extends SlackAppServlet {

  public SlackAppInteractiveController(App app) {
    super(app);
  }

}
