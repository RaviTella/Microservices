package model;

import rx.Observable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CustomerRepository {
    private static final List<Customer> customers;

    static {
        customers= new ArrayList<Customer>();
        customers.add(new Customer(1001,"Ravi Tella","123 W 22nd Street, Houston, TX, 12345"));
    }
    public Observable<Customer> getCustomers(){
        return Observable.from(customers);
    }
}
