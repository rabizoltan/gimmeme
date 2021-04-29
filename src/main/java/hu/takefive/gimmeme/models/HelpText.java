package hu.takefive.gimmeme.models;

public enum HelpText {
  GIMMEHELP ("GIMMEHELP! is a shortcut. You can click on the bolt icon next to message bar...."),
  GIMMEME ("GIMMEME! is a shortcut. You click on the bolt icon next to message bar....");

  public final String commandHelp;

  HelpText(String commandHelp) {
    this.commandHelp = commandHelp;
  }
  String commandHelp() {
    return this.commandHelp();
  }
}