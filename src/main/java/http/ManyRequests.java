/*
 * Here should be licence
 */
package http;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static http.HttpClient.get;

/**
 * @author mcaikovs
 */
public class ManyRequests {

    List<String> getUrls(List<String> urls, int timesToRepeatEachUrl) {
        List<String> l = new LinkedList<>();
        IntStream.range(0, timesToRepeatEachUrl).forEach(i -> l.addAll(urls));
        return l;
    }

    CompletableFuture<String> load(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return get(url);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

    }

    public CompletableFuture<String> requestManyUrls(List<String> urls, int timesToRequestEachUrl) {

        CompletableFuture<String>[] requests = getUrls(urls, timesToRequestEachUrl)
                .stream().map(url -> load(url)).toArray(i -> new CompletableFuture[i]);

        return CompletableFuture.allOf(requests)
                .thenApply(v
                        -> Stream.of(requests)
                        .map(future -> future.join())
                        .collect(Collectors.joining("\n")));
    }
}
