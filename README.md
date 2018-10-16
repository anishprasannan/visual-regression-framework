
# Visual Regression Testing

## What is it

Visual regression tests as the name suggests can be used to check if elements or pages of your website look the same,
before and after a change to the code. `visual-regression-framework` is a repo, of visual regression tests.
The repo has three flavours of tests implemented, as explained later.

### Setting Up a Local Copy

1. Clone the repo.

2. Build the project. (gradle build file is supplied)

### Repo structure

The tests are implemented in there respective folders under
`visual-regression-framework/src/`, while `visual-regression-framework/src/main` contains the shared classes,
used by all the tests.

## Running a test

Using concordion as the flavour of choice, here is how to use the repo.

### Baselining

Baselining is the process of creating a source of truth for the tests to run
against. Use the below gradle command. The application running at the same location as `base.url`

```
./gradlew concordion
          -Dbase.url=<URL>
          -Dconcordion.single=Index
          -Dconcordion.output.dir=build/test-results/concordion/
          -Dwebdriver.browser.name=firefox
          -Dwebdriver.browser.props=--headless
          -Dbaseline.output.dir=src/concordion/resources/baseline/
          -DisBaseline=true
```

Options of interest here are

```
-Dbaseline.output.dir=src/concordion/resources/baseline/
-DisBaseline=true
```

The directory to which the source of truth will be stored to is specified by the
first option `-Dbaseline.output.dir`, and `-DisBaseline=true` sets the baseline mode.

### Testing

During testing, the baselined images are compared with current elements. Use the below gradle command.
The application running at the same location as `base.url`

```
./gradlew concordion
       -Dbase.url=<URL>
       -Dconcordion.single=Index
       -Dconcordion.output.dir=build/test-results/concordion/
       -Dwebdriver.browser.name=firefox
       -Dwebdriver.browser.props=--headless
       -Dbaseline.output.dir=src/concordion/resources/baseline/
       -DisBaseline=false
```

Options of interest here are

```
-Dconcordion.output.dir=build/test-results/concordion/
-Dbaseline.output.dir=src/concordion/resources/baseline/
-DisBaseline=false
```

The baseline directory specified by option `-Dbaseline.output.dir`
as the source of truth, `-Dconcordion.output.dir` as directory to store the
test results, and `-DisBaseline=false` sets the test mode.

### VisualUtil.java

This file contains some levers that can be used to alter the behavior of image comparison.
`TOLERANCE_FACTOR` can be used to set an acceptable level of difference between the same pixel positions on the
baseline image and the image we are comparing.
`NEGLIGANCE_PERCENTAGE` and `negligancePercentage` can be used to set an acceptable level of difference between the
baseline image and the image we are comparing.
`MASKING_COLOUR` can be used to mask off areas in the baseline image, that needs to be omitted from testing.

## The different flavours

The three different flavours available in the repo are

```
1. Concordion
2. Vanilla
3. Cross browser
```

### Concordion test

The tests in this flavor are Concordion tests, with specification in html.
The testing is done in Firefox (refer https://github.com/mozilla/geckodriver/#selenium), by default.
Concordion (refer https://concordion.org/) is used to drive the tests,
and provides good documentation, of test results.
Gradle command as below

```
./gradlew concordion
          -Dbase.url=<URL>
          -Dconcordion.single=Index
          -Dconcordion.output.dir=build/test-results/concordion/
          -Dwebdriver.browser.name=firefox
          -Dwebdriver.browser.props=--headless
          -Dbaseline.output.dir=src/concordion/resources/baseline/
          -DisBaseline=false
```

### Vanilla test

The tests in this flavor are simple JUnit tests.The testing is done in
Firefox (refer https://github.com/mozilla/geckodriver/#selenium), by default.
Gradle command as below

```
./gradlew vanilla
          -Dbase.url=<URL>
          -Dwebdriver.browser.name=firefox
          -Dwebdriver.browser.props=--headless
          -Dtest.output.dir=build/test-results/vanilla/
          -Dbaseline.output.dir=src/vanilla/resources/baseline/
          -DisBaseline=false
```

### Cross browser test

The tests in this flavor are again JUnit tests, using
browserstack (refer https://www.browserstack.com/automate/java#getting-started)
for cross browser testing and Extentreport (refer http://extentreports.com/docs/versions/2/java/) for test reporting.
The testing is done in browser stack. Extent reports is used for creating
test report. The browsers of interest are specified in the beginning of `RegressionTest.java` .

```
./gradlew xbrowser
          -Dbase.url=<URL>
          -Dremote.browserstack.url=<your browser stack url with id and key>
          -Dhttp.proxyHost=<proxy host>
          -Dhttp.proxyPort=<proxy port>
          -Dtest.output.dir=build/test-results/xBrowser/
          -Dbaseline.output.dir=src/xbrowser/resources/baseline/
          -DisBaseline=false
```

## Running the sample tests

Run the sample concordion or vanilla tests with the below html file.

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sample</title>
    <style>
        #span1 {
            display: inline-block;
            background-color: blue;
            padding: 30px;
            border: 2px solid black;
        }
        #span1 > span {
            color: white;
        }
    </style>
</head>
<body>
    <span id="span1">
        <span> Input 1 </span>
        <input id="input1"/>
    </span>
</body>
</html>
```

Use https://www.npmjs.com/package/serve or similar to serve the page.
Run the tests in the baseline mode, to create the baseline, and then in test mode.
