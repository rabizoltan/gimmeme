package hu.takefive.gimmeme.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class XyController {

  public XyController() {
  }

  @GetMapping("/hello")
  public String hello() {
    return "Hello";
  }
}
