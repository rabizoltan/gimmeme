package hu.takefive.gimmeme.services;

import com.slack.api.model.view.View;

@FunctionalInterface
public interface UpdateViewBuilder<String> {

  View buildUpdateView(String privateMetadata);

}
