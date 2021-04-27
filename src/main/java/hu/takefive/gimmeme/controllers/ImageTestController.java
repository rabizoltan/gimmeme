package hu.takefive.gimmeme.controllers;

import hu.takefive.gimmeme.services.ImageFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ImageTestController {
  @GetMapping("/image")
  public String saveImage() throws IOException {
    String url = "https://slack-files.com/files-pri/T0202GRF98C-F020NA985LY/3.jpg?pub_secret=3557b740a2";
    ImageFactory.writeTextToImage(
        url,
        "text-bottom",
        "Londrina Shadow",
        "It's a very funny text");

    return "Image saved";
  }
}
