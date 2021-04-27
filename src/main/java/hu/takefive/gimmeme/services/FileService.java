package hu.takefive.gimmeme.services;

import com.slack.api.model.File;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Data
public class FileService {

  List<File> files;

}
