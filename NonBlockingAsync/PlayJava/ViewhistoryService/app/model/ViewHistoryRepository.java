package model;

import rx.Observable;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ViewHistoryRepository {
    private static final List<Item> viewHistory;

    static {
        viewHistory = new ArrayList<Item>();
        viewHistory.add(new Item("1","Microsoft Azure Service fabric","Service fabric for developers","https://mtchouimages.blob.core.windows.net/books/ServiceFabric.jpg",50,1001));
        viewHistory.add(new Item("2","Microsoft Azure SQL","Setp by step guide for developers","https://mtchouimages.blob.core.windows.net/books/AzureSQL.jpg",70,1001));
        viewHistory.add(new Item("3","Modern Authentication with AzureAD","Azure active directory capabilities","https://mtchouimages.blob.core.windows.net/books/AzureAD.jpg",60,1001));
    }

     public Observable<Item> getViewHistory(){
        return Observable.from(viewHistory);
    }
}
