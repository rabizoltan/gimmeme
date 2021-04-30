package hu.takefive.gimmeme.models;

public enum HelpText {
  GREETINGS ("The /greetings command only send you a welcome message. Nothing special..."),
  GIMMEHELP ("The /gimmehelp command list you some basic tip how to you the Gimmeme app."),
  GIMMEME ("You can send memes with given templates by type in the '/gimmeme' command and hit enter"),
  GIMMEHELPS ("GIMMEHELP is a shortcut. You can click on the bolt icon next to message bar and select the GIMMEHELP command. You are already here so..."),
  GIMMEMES ("If you click on the 'More action' (...) button of a messages which contains a picture. \n " +
          "Here you can select Gimmeme! shortcut and put some text on the picture and send it back.");

  public final String commandHelp;

  HelpText(String commandHelp) {
    this.commandHelp = commandHelp;
  }
  String commandHelp() {
    return this.commandHelp();
  }
}