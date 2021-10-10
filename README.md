# Annotation Processor Staleness Demo

The annotation processor `com.bdl.demos.DemoAnnotationProcess` looks in the file tree for files 
in an expected location.

The demo target includes the `resources` directory as its `resources` tag, but when the contents
are changes, this does not cause the target to rebuild, at least not such that the annotation 
processor is rerun.

To see this, make sure everything is synced and run
```
bazel test javatests/com/bdl/demos:test
```
which should pass.

Then go and rename (or add or whatever) a file in the `resources` folder and try running the 
test again.

Expected behavior:

the target detects that the resources have changed and reruns the annotation processor,
allowing the test to pass.

Observed behavior:
the generated class remains unchanged and the test fails.

Note that other changes to the `demo` target do result in a rebuild.