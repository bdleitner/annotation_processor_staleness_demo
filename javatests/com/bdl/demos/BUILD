java_test(
    name = "test",
    test_class = "com.bdl.demos.DemoTest",
    srcs = glob(["*.java"]),
    resources = ["//java/com/bdl/demos:resources"],
    deps = [
        "@maven_junit//:junit_junit",
        "@maven_junit//:org_hamcrest_hamcrest_library",
        "//java/com/bdl/demos:demo",
    ],
)