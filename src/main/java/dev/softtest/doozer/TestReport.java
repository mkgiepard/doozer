package dev.softtest.doozer;

import static j2html.TagCreator.*;

public class TestReport {

    public String generateReport() {
        return body(
            h1("Hello, World!"),
            img().withSrc("/img/hello.png")
        ).render();
    }

}
