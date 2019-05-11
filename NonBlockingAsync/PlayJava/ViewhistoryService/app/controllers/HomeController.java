package controllers;

import model.ViewHistoryRepository;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController extends Controller {

  private final ViewHistoryRepository repo;

  @Inject
  public HomeController(ViewHistoryRepository repo) {
    this.repo = repo;
  }

  public Result getAll() {
    return ok(Json.toJson(repo.getViewHistory().toList().toBlocking()));
  }

  public Result getByCustomer(Integer customerID) {
    return ok(
        Json.toJson(
            repo.getViewHistory()
                .filter(item -> Integer.compare(item.getCustomerID(), customerID) == 0)
                .toList()
                .toBlocking()
                .single()));
  }
}
