package model;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

@Singleton
public class MembershipRepository {
    private static List<Membership> memberships;

    static {
        memberships = new ArrayList<Membership>();
        memberships.add( new Membership("1",100,"GOLD",1001));
    }

    public Observable<Membership> getMembership(){
        return Observable.from(memberships);
    }
}
