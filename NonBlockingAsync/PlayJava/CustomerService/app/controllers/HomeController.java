package controllers;

import model.CustomerRepository;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController extends Controller {
private final CustomerRepository repo;
    @Inject
    public HomeController(CustomerRepository repo) {
        this.repo = repo;
    }

  public Result getCustomer(Integer customerID) {
    return ok(
        Json.toJson(
            repo.getCustomers()
                .filter(customer -> Integer.compare(customer.getId(), customerID) == 0)
                .toList()
                .toBlocking()
                .single()));
        }
}
