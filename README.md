# doozer

Dead simple UI testing framework. ([doozer.softtest.dev](https://doozer.softtest.dev/))

## Installation

Doozer is released into Maven central repository and can be installed by adding this dependency into
pom.xml file:

```
<dependency>
    <groupId>dev.softtest</groupId>
    <artifactId>doozer</artifactId>
    <version>0.2.6</version>
</dependency>
```

Current version - 0.2.6 - is not final but it is functional enough to try it out.

## Setup

Prerequisites:

- Java SDK
- Maven

Steps:

0. Create Maven [project](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

    - If you are creating a project from scratch then you need to update the Java version used by compiler
    (at least to version 8) in the pom.xml:

    ```xml
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    ```

    - Ensure all works well by running `mvn test` command.

1. Add `doozer` dependency into pom.xml
2. Create `MyDoozerTest` class that extends `DoozerTest`

    ```java
    import dev.softtest.doozer.DoozerTest;

    public class MyDoozerTest extends DoozerTest {}
    ```

    This class is an entry point for various customizations, as long as you are not doing them it
    stays empty like at the example above.


## First Test

Doozer test has a following structure:

```
firstTest                 (test directory)
├── firstTest.doozer      (main test script named the same way as a directory + .doozer suffix)
└── goldens               (directory for reference files, like images, a11y snapshots, logs, etc)
    ├── screenshot-1.png  (exemplary golden file)
```

Steps:

1. Create a test directory, for example `firstTest`
2. Inside this directory, create
    - a test script called: `firstTest.doozer`
    - a `goldens` directory for reference files
3. Edit `firstTest.doozer` and add first actions

    ```bash
    url args:"https://doozer.softtest.dev/test-pages/webform.html"
    assertPageTitle args:"doozerByExample - exemplary page to testing doozer actions"
    ```

4. Run the test with the following command:

    ```bash
    mvn test -Ddoozer.test=<path_to_your_test>/firstTest/firstTest.doozer -Ddoozer.browser=chrome
    ```

    or if you are running on Windows, add quotes around command parameters, like this:

    ```bash
    mvn test "-Ddoozer.test=<path_to_your_test>/firstTest/firstTest.doozer" "-Ddoozer.browser=chrome"
    ```

    > **_NOTE:_**  Running the test with `-Ddoozer.browser=chrome` tells the doozer to use
    Chrome as a browser for testing. By default the test would run on Chrome in headless mode so
    you'll not see much, with this parameter the browser is in headful mode.

5. The results will be displayed in the console but also you can check all the logs at:
`<your_working_directory>/target/doozer-tests`, feel free to explore.


## Syntax

Positional parameters:
```
<action>[?]
<action>[?] "<selector>"
<action>[?] "<selector>" "<options>"
```


Named parameters:

```
<action>[?]
<action>[?] selector:"<selector>"
<action>[?] args:"<options>"
<action>[?] selector:"<selector>" args:"<options>"
```

Examples:

```
click "By.cssSelector('button')"
click "By.cssSelector('button')" "button=2,offsetX=10,offsetY=10"

click selector:"By.cssSelector('button')"
click selector:"By.cssSelector('button')" args:"button=2,offsetX=10,offsetY=10"
click selector:"Sl({button 'Accept *'})"
```

`?` - optional parameter indicating the execution should proceed despite action failure

### Actions

- Assertions
  - `assertCurrentUrl`
  - `assertInnerText`
  - `assertPageTitle`

- Interactions
  - `alert`
  - `clear`
  - `click`
  - `contextClick`
  - `doubleClick`
  - `hover`
  - `hoverByOffset`
  - `keyDown`
  - `keyUp`
  - `select`
  - `sendKeys`
  - `type`

- Navigation
  - `navigateBack`
  - `navigateForward`
  - `navigateTo`
  - `refresh`
  - `url`

- Generics
  - `iframe`
  - `import`
  - `set`
  - `takeScreenshot`
  - `wait`
  - `waitForElement`

### Selectors

- `By.` - By selectors
- `Sl.` - Semantic locators
- `""` - empty selector, for actions that don't use it


### Options

One of:

- single parameter
- a list of `key=value` pairs separated with `,`.

## Launch parameters

- `doozer.directory` - defines a directory with doozer scripts
- `doozer.test` - defines a doozer script to run
- `doozer.browser` - defines a browser to use during test, default: `chrome-headless`
- `doozer.failOnPixelDiff` - when set to `false` all the `takeScreenshot` actions are treated as optional,
default: `true`

## License

The project is licensed with The Apache License, Version 2.0.
(http://www.apache.org/licenses/LICENSE-2.0.txt).


## Release Notes

### 0.2.6 Bug fixes / Improvements

- Fix missing icon on TestRun, TestCase pages on Windows

### 0.2.5 Bug fixes / Improvements

- Fix Doozer actions based on Selenium Actions (like keyUp, keDown etc)
- Improve TestRun, TestCase report pages
- Improve error messages reported on missing Selector

### 0.2.4 Bug fixes

- Bug fixes for test run HTML reports
- Bug fix for `assertCurrentUrl` action

### 0.2.3 Bug fixes

- Bug fixes for 'Approve' on Windows


### 0.2.2 Broken release, please don't use it :(

### 0.2.1 Bug fixes

- Bug fixes for `takeScreenshot` action

### 0.2.0 Initial release


## Contact

Feel free to reach me out on github or at info [ at ] softtest.dev.

**Enjoy!**
