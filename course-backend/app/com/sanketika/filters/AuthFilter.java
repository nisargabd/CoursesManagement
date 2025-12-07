package com.sanketika.filters;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import play.libs.typedmap.TypedKey;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

@Singleton
public class AuthFilter extends Filter {

    private final String jwkSetUri;
    public static final TypedKey<DecodedJWT> VERIFIED_JWT = TypedKey.create("verifiedJwt");

    @Inject
    public AuthFilter(Config config) {
        super(null); 
        this.jwkSetUri = config.getString("security.jwt.jwk-set-uri");
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> next, Http.RequestHeader request) {
        if (request.method().equals("OPTIONS")) {
            return next.apply(request);
        }

        Optional<String> authHeader = request.header("Authorization");
        if (authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ")) {
             return CompletableFuture.completedFuture(Results.unauthorized("Missing or invalid Authorization header"));
        }

        String token = authHeader.get().substring(7);

        try {
            DecodedJWT jwt = JWT.decode(token);
            JwkProvider provider = new UrlJwkProvider(new URL(jwkSetUri));
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);

            // Add JWT to request attributes
            return next.apply(request.addAttr(VERIFIED_JWT, jwt));

        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(Results.unauthorized("Invalid token: " + e.getMessage()));
        }
    }
}
