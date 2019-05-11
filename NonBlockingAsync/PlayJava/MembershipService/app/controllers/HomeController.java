package controllers;


import model.MembershipRepository;
import play.libs.Json;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeController extends Controller {

    private final MembershipRepository repo;

    @Inject
    public HomeController(MembershipRepository repo) {
        this.repo = repo;
    }

    public Result getMembership(Integer customerID){
        return ok(Json.toJson(repo.getMembership().filter(item -> Integer.compare(item.getCustomerID(),customerID)==0).toBlocking().single()));

    }
}
