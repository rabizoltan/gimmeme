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

//TODO finish all Views (nb: actionId <-> value)
public class ViewFactory {

  public static View buildSelectLayoutView(String imageUrl, String channelId) {
    View view = new View();
    view.setType("modal");
    view.setTitle((ViewTitle.builder().type(PlainTextObject.TYPE).text("Gimmeme!").build()));
    view.setCallbackId("select-layout");

    String imageUrlJson = String.format("{ \"imageUrl\" : \"%s\", \"channelId\" : \"%s\" }", imageUrl, channelId);
    view.setPrivateMetadata(imageUrlJson);

    view.setBlocks(asBlocks(
        section(section -> section.text(markdownText("*Please choose a meme layout:*"))),
        divider(),
        image(imageElementBuilder -> imageElementBuilder.imageUrl(imageUrl).altText("text-top")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Choose layout"))).actionId("text-top").value("XXX"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder.imageUrl(imageUrl).altText("text-bottom")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Choose layout"))).actionId("text-bottom").value("pickTemplate"))
            ))
        ),
        divider(),
        image(imageElementBuilder -> imageElementBuilder.imageUrl(imageUrl).altText("text-middle")),
        actions(actions -> actions
            .elements(asElements(
                button(b -> b.text(plainText(pt -> pt.emoji(true).text("Choose layout"))).actionId("text-middle").value("pickTemplate"))
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
