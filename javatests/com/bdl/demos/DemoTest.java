package com.bdl.demos;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DemoTest {

  @Test
  public void testAnnotationProcessorOutput() {
    assertEquals(getCurrentFilesUnderResources(), DemoTargetSub.getFoundFileNames());
  }

  private String getCurrentFilesUnderResources() {
    try {
      URI uri = getClass().getResource("resources").toURI();
      FileSystem fileSystem = getTestDataFileSystem(uri);
      String uriString = uri.toString();
      Path path = fileSystem.getPath(uriString.substring(uriString.lastIndexOf('!') + 2));
      return Files.walk(path, 1)
          .map(Path::getFileName)
          .map(Objects::toString)
          .filter(fn -> fn.endsWith(".txt"))
          .peek(fn -> System.out.printf("Found file: \"%s\".", fn))
          .sorted()
          .collect(Collectors.joining(", "));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static FileSystem getTestDataFileSystem(URI uri) {
    try {
      return FileSystems.newFileSystem(uri, new HashMap<>());
    } catch (Exception e) {
      return FileSystems.getFileSystem(uri);
    }
  }
}
