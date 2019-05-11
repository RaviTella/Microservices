package controllers;

import model.CartRepository;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController extends Controller {

    private final CartRepository repo;

    @Inject
    public HomeController(CartRepository repo) {
        this.repo = repo;
    }

    public Result getByCustomer(Integer customerID) {
        return ok(Json.toJson(repo.getCart().filter(item -> Integer.compare(item.getCustomerID(),customerID)==0).toList().toBlocking().single()));
    }
}
