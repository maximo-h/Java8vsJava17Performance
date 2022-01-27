
package org.eclipse.jetty.demo;

import http.ManyRequests;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author mcaikovs
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
// @Fork(1)
public class MyMain {

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(MyMain.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public static void testMe() throws Exception {
        final int port = 8080 + new Random().nextInt(55);
        final ServerMain serverMain = new ServerMain(port);
        serverMain.start();

        final String baseUrl = "http://localhost:" + port;
        final List<String> URLS = Arrays.asList(baseUrl,
                baseUrl + "/test/dump.jsp",
                baseUrl + "/test/bean1.jsp",
                baseUrl + "/test/tag.jsp",
                baseUrl + "/test/tag2.jsp",
                baseUrl + "/test/tagfile.jsp",
                baseUrl + "/test/expr.jsp?A=1",
                baseUrl + "/test/jstl.jsp",
                baseUrl + "/test/foo/",
                baseUrl + "/date/");

        List<String> allContents = new LinkedList<>();

        String contents = new ManyRequests().requestManyUrls(URLS, 5).get();
        allContents.add(contents);

        serverMain.stop();
        //serverMain.waitForInterrupt();
    }
}
