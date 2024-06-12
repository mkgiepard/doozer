package dev.softtest.doozer;

import static j2html.TagCreator.*;

import java.nio.file.Paths;
import java.nio.file.Files;


/**
 * Base class for test reports created with j2html.
 */
public abstract class TestReport {
    protected final String REPORT_FILENAME = "doozer-report.html";
    protected final String resultsDir;
    
    public TestReport(String resultsDir) {
        this.resultsDir = resultsDir;
    }

    public abstract void generate();

    protected String includeCSS() {
        String materialFontLink = link()
                .withRel("stylesheet")
                .withHref(
                        "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0")
                .render();
        String css = style(readCssFromFile("src/main/resources/styles.css")).render();
        return materialFontLink + css;
    }

    protected String includeJS() {
        return script(readScriptFromFile("src/main/resources/script.js")).render();
    }

    protected String includePageHeader(String header) {
        return div(h1(header)).withClass("header").renderFormatted();
    }

    protected String getHeaderRow() {
        return div(div(join(
                div("Test Case"),
                div("RESULT").withClass("center"),
                div("PIXEL DIFF").withClass("center"),
                div("ACTION").withClass("center"))).withClasses("container-testcase-header", "title"))
                .withClass("container-testcase").render();
    }

    protected String readCssFromFile(String cssFilePath) {
        String styles = "";
        try {
            styles = new String(
                    Files.readAllBytes(Paths.get(cssFilePath)));
        } catch (Exception e) {
            System.out.println(e);
        }
        return styles;
    }

    protected String readScriptFromFile(String jsFilePath) {
        String script = "";
        try {
            script = new String(Files.readAllBytes(Paths.get(jsFilePath)));
        } catch (Exception e) {
            System.out.println(e);
        }
        return script;
    }
}
