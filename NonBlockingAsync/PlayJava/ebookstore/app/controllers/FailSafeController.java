package controllers;

import akka.parboiled2.support.OpTreeContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import model.Customer;
import model.Item;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Fallback;
import net.jodah.failsafe.RetryPolicy;
import org.mockito.Mockito;
import play.api.libs.ws.ahc.cache.CacheableHttpResponseStatus;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.libs.ws.ahc.AhcWSResponse;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Singleton
public class FailSafeController extends Controller {
  private HttpExecutionContext httpExecutionContext;
  private final WSClient ws;
  private final Config config;
  private final CircuitBreaker<Object> breaker;
  private final Fallback<Object> fallback;
  private final RetryPolicy<Object> retryPolicy;

  @Inject
  public FailSafeController(Config config, HttpExecutionContext ec, WSClient ws) {
    this.config = config;
    this.httpExecutionContext = ec;
    this.ws = ws;
    this.breaker =
        new CircuitBreaker<>()
            .handle(TimeoutException.class)
            .handle(ConnectException.class)
            .withFailureThreshold(3)
            .withSuccessThreshold(5)
            .withDelay(Duration.ofMinutes(1))
            .onClose(() -> System.out.println("Closed"))
            .onHalfOpen(() -> System.out.println("Half Open"))
            .onOpen(() -> System.out.println("Open"));
    this.retryPolicy =
        new RetryPolicy<>()
            .handle(TimeoutException.class)
            .withDelay(Duration.ofSeconds(1))
            .withMaxRetries(3);

    this.fallback = Fallback.of(error());
  }

  public CompletionStage<Result> index() {

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    return Failsafe.with(this.fallback, this.breaker)
        .getStageAsync(
            () -> {
              System.out.println("TRYTRYTRY");
              return ws.url(config.getString("externalRestServices.recommendationService"))
                  .setRequestTimeout(Duration.ofSeconds(10))
                  .get();
            })
        .thenApplyAsync(
            response -> {
              System.out.println(response.asJson().toString());
              if (response.asJson().toString().equals("{\"message\":\"unavialble\"}")) {
                return ok("Works");
              } else {
                return ok(response.asJson().toString());
              }
            },
            httpExecutionContext.current());
  }

  private WSResponse error() {
    final String jsonStr = "{\"message\": \"unavialble\"}";
    final WSResponse wsResponseMock = Mockito.mock(WSResponse.class);
    Mockito.doReturn(200).when(wsResponseMock).getStatus();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = null;
    try {
      jsonNode = mapper.readTree(jsonStr);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Mockito.doReturn(jsonNode).when(wsResponseMock).asJson();

    return wsResponseMock;
  };
}
