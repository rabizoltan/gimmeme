package hu.takefive.gimmeme.services;

import com.slack.api.model.ModelConfigurator;
import com.slack.api.model.block.ImageBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;
import static com.slack.api.model.block.element.BlockElements.*;
import static com.slack.api.model.view.Views.viewSubmit;

public class ViewFactory {

  public static View buildSelectLayoutView(String imageUrl, String channelId, String fileType) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("select-layout");

    String privateMetadataUrlJson = String.format("{ \"imageUrl\" : \"%s\", \"channelId\" : \"%s\", \"fileType\" : \"%s\" }", imageUrl, channelId, fileType);
    view.setPrivateMetadata(privateMetadataUrlJson);

    view.setBlocks(asBlocks(
        //TODO implement logic to upload layout-templates (no hardcoding here!)
        image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F02125P9ABS/template-1.png?pub_secret=c97cadee4c").altText("layout-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-top").value("text-top")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-middle").value("text-middle"))
            ))
        ),
        divider(),
        image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01VBJ5GWT1/template-2.png?pub_secret=d8aa31cce6").altText("layout-2")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-both").value("text-both")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-bottom").value("text-bottom"))
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
          image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) imageElementBuilder -> imageElementBuilder
              .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF2BC4S/gimmeme-char-chooser-one.png?pub_secret=11931ca605").altText("font-1")),
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
          image((ModelConfigurator<ImageBlock.ImageBlockBuilder>) imageElementBuilder -> imageElementBuilder
              .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF66RBL/gimmeme-char-chooser-two.png?pub_secret=b8c9ca1325").altText("font-2")),
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
      .callbackId("select-font-size")
      .privateMetadata(privateMetaData)
      .blocks(asBlocks(
          section(s -> s.text(markdownText("*Choose font size*"))
              .accessory(staticSelect(select -> select
                  .actionId("fontSize")
                  .placeholder(plainText(pt -> pt.emoji(true).text("Choose font size")))
                  .options(asOptions(
                      option(o -> o.text(plainText(pt -> pt.emoji(true).text("BIG"))).value("big")),
                      option(o -> o.text(plainText(pt -> pt.emoji(true).text("Default"))).value("default")),
                      option(o -> o.text(plainText(pt -> pt.emoji(true).text("small"))).value("small"))
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
}
