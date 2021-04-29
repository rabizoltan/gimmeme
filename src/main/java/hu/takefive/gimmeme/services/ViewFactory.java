package hu.takefive.gimmeme.services;

import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.asElements;
import static com.slack.api.model.block.element.BlockElements.button;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
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
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F02125P9ABS/template-1.png?pub_secret=c97cadee4c").altText("layout-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-top").value("text-top")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this layout!"))).actionId("text-middle").value("text-middle"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder
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

  public static View buildSelectFontView(String privateMetadata) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("select-font");
    view.setPrivateMetadata(privateMetadata);

    view.setBlocks(asBlocks(
        //TODO implement logic to upload layout-templates (no hardcoding here!)
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF2BC4S/gimmeme-char-chooser-one.png?pub_secret=11931ca605").altText("font-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this font!"))).actionId("Trade Winds").value("Trade Winds")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this font!"))).actionId("Londrina Shadow").value("Londrina Shadow"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F020CF66RBL/gimmeme-char-chooser-two.png?pub_secret=b8c9ca1325").altText("font-2")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this font!"))).actionId("Fascinate Inline").value("Fascinate Inline")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme this font!"))).actionId("Kranky").value("Kranky"))
            ))
        )
    ));

    return view;
  }

  public static View buildInputTextView(String privateMetadata) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("generate-meme");
    view.setSubmit(viewSubmit(submit -> submit.type("plain_text").text("Gimmeme!").emoji(true)));
    view.setPrivateMetadata(privateMetadata);

    view.setBlocks(asBlocks(
        input(input -> input
            .blockId("text-block")
            .element(plainTextInput(pti -> pti.actionId("text-input").multiline(true)))
            .label(plainText(pt -> pt.text("Gimme text!").emoji(true)))
        )
    ));

    return view;
  }

}
