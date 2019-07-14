package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.RetryPolicy;
import org.mockito.Mockito;

import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static play.libs.Scala.asScala;

import com.typesafe.config.Config;

@Singleton
public class HomeController extends Controller {
    private HttpExecutionContext httpExecutionContext;
    private final WSClient ws;
    private final Config config;
    private final CircuitBreaker<Object> breaker;
    private final Fallback<Object> fallback;
    private final RetryPolicy<Object> retryPolicy;
    private final String fallbackMessage = "{\"message\":\"fallback\"}";

    @Inject
    public HomeController(Config config, HttpExecutionContext ec, WSClient ws) {
        this.config = config;
        this.httpExecutionContext = ec;
        this.ws = ws;
        this.breaker =
                new CircuitBreaker<>()
                        .handle(TimeoutException.class)
                        .handle(ConnectException.class)
                        .withFailureThreshold(3)
                        .withSuccessThreshold(3)
                        .withDelay(Duration.ofMinutes(1))
                        .onClose(() -> System.out.println("Closed"))
                        .onHalfOpen(() -> System.out.println("Half Open"))
                        .onOpen(() -> System.out.println("Open"));

        this.fallback = Fallback.of(error());

        this.retryPolicy =
                new RetryPolicy<>()
                        .handle(TimeoutException.class)
                        .handle(ConnectException.class)
                        .withDelay(Duration.ofSeconds(1))
                        .withMaxRetries(3);
    }

    public CompletionStage<Result> index() {

        CompletableFuture<List<Item>> recommendationsCF =
                getCompletableFuture("externalRestServices.recommendationService", Item.class);
        CompletableFuture<List<Item>> viewedItemsCF =
                getCompletableFuture("externalRestServices.viewedItemsService",Item.class);
        CompletableFuture<List<Customer>> customerCF =
                getCompletableFuture("externalRestServices.customerService",Customer.class);
        CompletableFuture<List<Item>> cartCF =
                getCompletableFuture("externalRestServices.cartService",Item.class);

      return  CompletableFuture.allOf(recommendationsCF,viewedItemsCF,customerCF,cartCF).thenApplyAsync((voidcf) -> ok(
                views.html.home.render(
                        recommendationsCF.join(),
                        viewedItemsCF.join(),
                        customerCF.join(),
                        cartCF.join())
        ), httpExecutionContext.current());

    }

    private <S> CompletableFuture<List<S>> getCompletableFuture(String s, Class<S> c) {
        return ws
                .url(config.getString(s))
                .get().thenApply(l -> convertToObject(l.asJson().toString(),c))
                .toCompletableFuture();
    }


    public CompletionStage<Result> failsafe() {

        return Failsafe
                .with(this.fallback, this.breaker)
                .getStageAsync(
                        () -> {
                            System.out.println("###TRY###");
                            return ws
                                    .url(config.getString("externalRestServices.recommendationService"))
                                    .setRequestTimeout(Duration.ofSeconds(5))
                                    .get();
                        })
                .thenApplyAsync(
                        wsResponse -> {
                            if (wsResponse
                                    .asJson()
                                    .toString()
                                    .equals(fallbackMessage)) {
                                return ok(views.html.home1.render(new Response<Item>("fallback", new ArrayList())));
                            } else {
                                return ok(
                                        views.html.home1.render(
                                                new Response<Item>(
                                                        "original",
                                                        convertToObject(wsResponse
                                                                .asJson()
                                                                .toString(), Item.class))));
                            }
                        },
                        httpExecutionContext.current());
    }


    private WSResponse error() {

        final WSResponse wsResponseMock = Mockito.mock(WSResponse.class);
        Mockito
                .doReturn(200)
                .when(wsResponseMock)
                .getStatus();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(fallbackMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mockito
                .doReturn(jsonNode)
                .when(wsResponseMock)
                .asJson();

        return wsResponseMock;
    }


    public <T> List<T> convertToObject(String jsonString, Class<T> c) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<T> books = null;
        try {
            books =
                    objectMapper.readValue(
                            jsonString, objectMapper
                                    .getTypeFactory()
                                    .constructCollectionType(List.class, c));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }
}
