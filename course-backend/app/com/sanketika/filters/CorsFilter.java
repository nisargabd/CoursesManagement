package com.sanketika.filters;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class CorsFilter extends Filter {

    @Inject
    public CorsFilter() {
        super(null);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next, Http.RequestHeader request) {
        return next.apply(request).thenApply(result ->
                result.withHeader("Access-Control-Allow-Origin", "*")
                      .withHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                      .withHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin")
        );
    }
}
