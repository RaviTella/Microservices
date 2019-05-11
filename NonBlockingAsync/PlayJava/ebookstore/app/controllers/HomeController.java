package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.*;
import play.data.Form;
import play.data.FormFactory;

import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static play.libs.Scala.asScala;
import com.typesafe.config.Config;

@Singleton
public class HomeController extends Controller {
  private HttpExecutionContext httpExecutionContext;
  private final WSClient ws;
  private final Config config;

  @Inject
  public HomeController(
      Config config, HttpExecutionContext ec, WSClient ws) {
    this.config = config;
    this.httpExecutionContext = ec;
    this.ws = ws;

  }

    public CompletionStage<Result> index() {
        List<WSResponse> results =new ArrayList<>();
        CompletionStage<WSResponse> recommendationsCF =
                ws.url(config.getString("externalRestServices.recommendationService")).get();
        CompletionStage<WSResponse> viewedItemsCF =
                ws.url(config.getString("externalRestServices.viewedItemsService")).get();
        CompletionStage<WSResponse> customerCF =
                ws.url(config.getString("externalRestServices.customerService")).get();
        CompletionStage<WSResponse> cartCF =
                ws.url(config.getString("externalRestServices.cartService")).get();

        return recommendationsCF.thenCombineAsync(
                viewedItemsCF,
                (recommendations, viewedItems) -> {
                    results.add(recommendations);
                    results.add(viewedItems);
                    return results;
                }).thenCombineAsync(customerCF,(res,customers) -> {
                    res.add(customers);
                    return res;
        }).thenCombineAsync(cartCF,(res,cartItems) -> {
            return ok(
                    views.html.home.render(
                            convertToObject(res.get(0).asJson().toString(), Item.class),
                            convertToObject(res.get(1).asJson().toString(), Item.class),
                            convertToObject(res.get(2).asJson().toString(), Customer.class),
                            convertToObject(cartItems.asJson().toString(), Item.class)
                    ));

        },httpExecutionContext.current());
    }

  public <T> List<T> convertToObject(String jsonString, Class<T> c) {
    ObjectMapper objectMapper = new ObjectMapper();
    List<T> books = null;
    try {
      books =
          objectMapper.readValue(
              jsonString, objectMapper.getTypeFactory().constructCollectionType(List.class, c));

    } catch (IOException e) {
      e.printStackTrace();
    }
    return books;
  }
}
