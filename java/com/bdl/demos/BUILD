java_library(
    name = "demo_annotation_internal",
    srcs = ["DemoAnnotation.java"],
    visibility = ["//visibility:private"]
)


java_library(
    name = "demo_processor",
    srcs = ["DemoAnnotationProcessor.java"],
    visibility = ["//visibility:private"],
    deps = [
        ":demo_annotation_internal",
    ],
)

java_plugin(
    name = "demo_plugin",
    generates_api = 1,
    processor_class = "com.bdl.demos.DemoAnnotationProcessor",
    tags = [
        "annotation=com.bdl.demos.DemoAnnotation;",
    ],
    visibility = ["//visibility:private"],
    deps = [
        ":demo_processor",
    ],
)

java_library(
    name = "demo_annotation",
    visibility = ["//visibility:public"],
    exports = [
      ":demo_annotation_internal",
    ],
    exported_plugins = [
      ":demo_plugin",
    ],
)

filegroup(
    name = "resources",
    srcs = glob(["resources/*.*"]),
    visibility = ["//visibility:public"],
)

java_library(
    name = "demo",
    srcs = ["DemoTarget.java"],
    visibility = ["//visibility:public"],
    resources = [":resources"],
    deps = [":demo_annotation"],
)