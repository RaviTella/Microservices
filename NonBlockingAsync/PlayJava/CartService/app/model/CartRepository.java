package model;

import rx.Observable;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CartRepository {
  private static final List<Item> cart;

  static {
    cart = new ArrayList<Item>();
    cart.add(new Item("1", "Concurrency with Rust", "", "", 44, 1001));
  }

  public Observable<Item> getCart() {
    return Observable.from(cart);
  }
}
