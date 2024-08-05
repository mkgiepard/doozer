package dev.softtest.doozer;

import static j2html.TagCreator.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Collectors;


/** Base class for test reports created with j2html. */
public abstract class TestReport {
    protected final String REPORT_FILENAME = "doozer-report.html";
    protected final String resultsDir;
    
    public TestReport(String resultsDir) {
        this.resultsDir = resultsDir;
        copyLogoFromResources();
    }

    public abstract void generate();

    protected String includeCSS() {
        String materialFontLink = link()
                .withRel("stylesheet")
                .withHref(
                        "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0")
                .render();
        String css = style(readResource("/styles.css")).render();
        return materialFontLink + css;
    }

    protected String includeJS() {
        return script(readResource("/script.js")).render();
    }

    protected String includePageHeader(String header) {
        String logoPath = Paths.get(resultsDir + "/doozer-logo.svg").toAbsolutePath().toString();
        if (System.getProperty("os.name").contains("Windows")) {
            logoPath = "file://" + logoPath;
        }
        return div(
                join(img().withSrc(logoPath).withClass("logo"), h1(header))
            ).withClasses("header", "fixed-top")
             .renderFormatted();
    }

    protected String getHeaderRow() {
        return div(div(join(
                div("Test Case"),
                div("RESULT").withClass("center"),
                div("PIXEL DIFF").withClass("center"),
                div("ACTION").withClass("center"))).withClasses("container-testcase-header", "title"))
                .withClass("container-testcase").render();
    }

    private void copyLogoFromResources() {
        Path path = Paths.get(resultsDir + "/doozer-logo.svg").toAbsolutePath();
        try (InputStream in = getClass().getResourceAsStream("/doozer-logo.svg")) {
            if (Files.notExists(path)) {
                Files.copy(in, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readResource(String name) {
        String result = "";
        InputStream in = getClass().getResourceAsStream(name);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            result = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
