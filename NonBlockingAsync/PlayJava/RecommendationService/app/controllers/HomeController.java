package controllers;

import model.RecommendationRepository;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController extends Controller {

    private final RecommendationRepository repo;

    @Inject
    public HomeController(RecommendationRepository repo) {
        this.repo = repo;
    }

    public Result getRecommendationsByCustomer(Integer customerID) {
        return ok(Json.toJson(repo.getViewHistoryItems().filter(item -> Integer.compare(item.getCustomerID(),customerID)==0).toList().toBlocking().single()));

    }


}
