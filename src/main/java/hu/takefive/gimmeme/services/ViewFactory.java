package hu.takefive.gimmeme.services;

import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewTitle;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
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
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F02125P9ABS/template-1.png?pub_secret=c97cadee4c").altText("template-1")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme layout!"))).actionId("text-top").value("text-top")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme layout!"))).actionId("text-middle").value("text-middle"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder
            .imageUrl("https://slack-files.com/files-pri/T0202GRF98C-F01VBJ5GWT1/template-2.png?pub_secret=d8aa31cce6").altText("template-2")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme layout!"))).actionId("text-both").value("text-both")),
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Gimme layout!"))).actionId("text-bottom").value("text-bottom"))
            ))
        )
    ));

    return view;
  }

  public static View buildInputTextView(String url) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("generate-meme");
    view.setSubmit(viewSubmit(submit -> submit.type("plain_text").text("Submit").emoji(true)));
    view.setPrivateMetadata(url);

    view.setBlocks(asBlocks(
        section(section -> section.text(markdownText("*Please type your very funny text here:*"))),
        input(input -> input
            .blockId("text-block")
            .element(plainTextInput(pti -> pti.actionId("text-input").multiline(true)))
            .label(plainText(pt -> pt.text("Your text").emoji(true)))
        )
    ));

    return view;
  }

}
