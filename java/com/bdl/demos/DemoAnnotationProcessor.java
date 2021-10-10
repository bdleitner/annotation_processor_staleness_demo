package com.bdl.demos;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

/**
 * Demo Annotation Processor for staleness issue.
 *
 * @author Ben Leitner
 */
@SupportedAnnotationTypes("com.bdl.demos.DemoAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DemoAnnotationProcessor extends AbstractProcessor {

  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(DemoAnnotation.class)) {
      if (element.getKind() != ElementKind.CLASS
          || !element.getModifiers().contains(Modifier.ABSTRACT)) {
        messager.printMessage(
            Diagnostic.Kind.ERROR,
            "UsesTestData Annotation applied to an element that is not an abstract class.",
            element);
        return true;
      }
      processElement((TypeElement) element);
    }

    return true;
  }

  private void processElement(TypeElement element) {
    String pkg = element.getEnclosingElement().toString();
    List<String> parts = new ArrayList<>();
    parts.add(new File("").getAbsolutePath());
    parts.add("java");
    parts.addAll(Arrays.asList(pkg.split("\\.")));
    parts.add("resources");

    File dir = new File(String.join(File.separator, parts));

    List<String> foundFiles =
        Arrays.stream(dir.listFiles()).map(File::getName).collect(Collectors.toList());

    String parentClassName = element.getSimpleName().toString();
    write(
        String.format("%s.%sSub", pkg, parentClassName),
        getContent(pkg, parentClassName, foundFiles));
  }

  private String getContent(String pkg, String parentClassName, List<String> fileNames) {
    StringBuilder s = new StringBuilder();
    s.append(String.format("package %s;\n\n", pkg));
    s.append(String.format("public class %1$sSub extends %1$s {\n", parentClassName));
    s.append(String.format("  private %sSub() {}\n\n", parentClassName));
    s.append("  public static String getFoundFileNames() {\n");
    s.append(
        String.format(
            "    return \"%s\";\n",
            fileNames.stream()
                .sorted()
                .peek(
                    fn -> {
                      messager.printMessage(Kind.NOTE, String.format("found file \"%s\"", fn));
                    })
                .collect(Collectors.joining(", "))));
    s.append("  }\n");
    s.append("}\n");
    return s.toString();
  }

  private void write(String canonicalClassName, String content) {
    try {
      JavaFileObject jfo = processingEnv.getFiler().createSourceFile(canonicalClassName);
      Writer fileWriter = jfo.openWriter();
      fileWriter.write(content);
      fileWriter.close();
    } catch (IOException ex) {
      processingEnv
          .getMessager()
          .printMessage(
              Diagnostic.Kind.ERROR,
              String.format("Error in annotation processor: %s\n", ex.getMessage()));
    }
  }
}
