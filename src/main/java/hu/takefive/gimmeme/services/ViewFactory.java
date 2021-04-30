package hu.takefive.gimmeme.services;

import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;
import hu.takefive.gimmeme.models.HelpText;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.asOptions;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.option;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
import static com.slack.api.model.block.element.BlockElements.staticSelect;
import static com.slack.api.model.view.Views.viewSubmit;

public class ViewFactory {

  public static View buildSelectImageView(String channelId, String teamId) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("select-image");

    String privateMetadataUrlJson = String.format("{ \"channelId\" : \"%s\", \"teamId\" : \"%s\" }", channelId, teamId);
    view.setPrivateMetadata(privateMetadataUrlJson);

    view.setBlocks(asBlocks(
        //TODO implement logic to upload layout-templates (no hardcoding here!)
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020A5PK60N/pic-template-two.png?pub_secret=6e18565c57")
            .altText("image-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this template!")))
                    .actionId("1.jpg")
                    .value("https://slack-files.com/files-pri/T0202GRF98C-F020UGQ5GC9/1.jpg?pub_secret=fa98aec0d0")),
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this template!")))
                    .actionId("2.png")
                    .value("https://slack-files.com/files-pri/T0202GRF98C-F020E0S5J93/2.png?pub_secret=e08ac164fe"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F0216NNVD0Q/pic-template-one.png?pub_secret=3496f63ca7")
            .altText("image-2")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this template!")))
                    .actionId("3.jpg")
                    .value("https://slack-files.com/files-pri/T0202GRF98C-F020H69DKU3/3.jpg?pub_secret=9a939c6a9b")),
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this template!")))
                    .actionId("4.jpg")
                    .value("https://slack-files.com/files-pri/T0202GRF98C-F0216PSEPA4/4.jpg?pub_secret=125b2735e0"))
            ))
        )
    ));

    return view;
  }

  public static View buildSelectLayoutView(String imageUrl, String channelId, String fileType) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("select-layout");

    String privateMetadataUrlJson = String
        .format("{ \"imageUrl\" : \"%s\", \"channelId\" : \"%s\", \"fileType\" : \"%s\" }", imageUrl, channelId, fileType);
    view.setPrivateMetadata(privateMetadataUrlJson);

    view.setBlocks(asBlocks(
        //TODO implement logic to upload layout-templates (no hardcoding here!)
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F02125P9ABS/template-1.png?pub_secret=c97cadee4c")
            .altText("layout-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this layout!")))
                    .actionId("text-top")
                    .value("text-top")),
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this layout!")))
                    .actionId("text-middle")
                    .value("text-middle"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01VBJ5GWT1/template-2.png?pub_secret=d8aa31cce6")
            .altText("layout-2")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this layout!")))
                    .actionId("text-both")
                    .value("text-both")),
                button(b -> b.text(plainText(pt -> pt.emoji(true)
                    .text("Gimme this layout!")))
                    .actionId("text-bottom")
                    .value("text-bottom"))
            ))
        )
    ));

    return view;
  }

  public static UpdateViewBuilder<String> buildSelectFontView = (privateMetaData) -> View.builder()
      .type("modal")
      .title((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()))
      .callbackId("select-font")
      .privateMetadata(privateMetaData)
      .blocks(asBlocks(
          //TODO implement logic to upload layout-templates (no hardcoding here!)
          image(imageElementBuilder -> imageElementBuilder
              .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF2BC4S/gimmeme-char-chooser-one.png?pub_secret=11931ca605")
              .altText("font-1")),
          actions(actions -> actions
              .elements(asElements(
                  button(b -> b.text(plainText(pt -> pt.emoji(true)
                      .text("Gimme this font!")))
                      .actionId("Trade Winds")
                      .value("Trade Winds")),
                  button(b -> b.text(plainText(pt -> pt.emoji(true)
                      .text("Gimme this font!")))
                      .actionId("Londrina Shadow")
                      .value("Londrina Shadow"))
              ))
          ),
          divider(),
          image(imageElementBuilder -> imageElementBuilder
              .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF66RBL/gimmeme-char-chooser-two.png?pub_secret=b8c9ca1325")
              .altText("font-2")),
          actions(actions -> actions
              .elements(asElements(
                  button(b -> b.text(plainText(pt -> pt.emoji(true)
                      .text("Gimme this font!")))
                      .actionId("Fascinate Inline")
                      .value("Fascinate Inline")),
                  button(b -> b.text(plainText(pt -> pt.emoji(true)
                      .text("Gimme this font!")))
                      .actionId("Kranky")
                      .value("Kranky"))
              ))
          )
      ))
      .build();

  public static UpdateViewBuilder<String> buildSelectFontSizeView = (privateMetaData) -> View.builder()
      .type("modal")
      .title((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()))
      .privateMetadata(privateMetaData)
      .blocks(asBlocks(
          section(s -> s.text(markdownText("## Choose font size"))
              .accessory(staticSelect(select -> select
                  .actionId("select-font-size")
                  .placeholder(plainText(pt -> pt.emoji(true).text("Select font size")))
                  .options(asOptions(
                      option(plainText("BIG"), "big"),
                      option(plainText("Default"), "default"),
                      option(plainText("small"), "small")
                      )
                  )
              ))
          )
      ))
      .build();

  public static UpdateViewBuilder<String> buildInputTextView = (privateMetaData) -> View.builder()
      .type("modal")
      .title((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()))
      .callbackId("generate-meme")
      .submit(viewSubmit(submit -> submit.type("plain_text").text("Gimmeme!").emoji(true)))
      .privateMetadata(privateMetaData)
      .blocks(asBlocks(
          input(input -> input
              .blockId("text-block")
              .element(plainTextInput(pti -> pti.actionId("text-input").multiline(true)))
              .label(plainText(pt -> pt.text("Gimme text!").emoji(true)))
          )
      ))
      .build();

  public static UpdateViewBuilder<String> buildAlertView = (errorMessage) -> View.builder()
      .type("modal")
      .title((ViewTitle.builder().type(PlainTextObject.TYPE).text("Alert!").build()))
      .blocks(asBlocks(
          section(s -> s.text(markdownText(errorMessage)))
      ))
      .build();

  public static View helpView() {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmehelp!").build()));
    view.setCallbackId("select-layout");

    view.setBlocks(asBlocks(
        divider(),
        section(section -> section.text(markdownText("These are the available commands in the GimMeme app:"))),
        section(section -> section.text(plainText("/gimmehelp"))),
        section(section -> section.text(plainText("/gimmeme"))),
        section(section -> section.text(plainText("/mittomeme"))),
        divider(),
        section(section -> section.text(markdownText("Select a command for more information"))
            .accessory(staticSelect(staticSelect -> staticSelect
                .actionId("command-selection-action")
                .placeholder(plainText("Select..."))
                .options(asOptions(
                    option(plainText("/Gimmehelp"), "GIMMEHELP"),
                    option(plainText("/Gimmeme"), "GIMMEME")
                ))
            )))
    ));

    return view;
  }

  public static View commandHelpView(String commandName) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmehelp!").build()));
    view.setCallbackId("select-layout");

    String helpText = HelpText.valueOf(commandName).commandHelp;

    view.setBlocks(asBlocks(
        divider(),
        section(section -> section.text(markdownText(helpText))),
        divider(),
        section(section -> section.text(markdownText("Select a command for more information"))
            .accessory(staticSelect(staticSelect -> staticSelect
                .actionId("command-selection-action")
                .placeholder(plainText("Select..."))
                .options(asOptions(
                    option(plainText("/Gimmehelp"), "GIMMEHELP"),
                    option(plainText("/Gimmeme"), "GIMMEME")
                ))
            )))
    ));

    return view;
  }
}
