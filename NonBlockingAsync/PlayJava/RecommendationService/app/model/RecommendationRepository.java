package model;

import rx.Observable;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RecommendationRepository {
    private static final List<Item> recommendations;

    static{
        recommendations = new ArrayList<Item>();
        recommendations.add(new Item("1","Learn Kubernetes the right way","Learn Kubernetes the right way","https://mtchouimages.blob.core.windows.net/books/Kubernetes.jpg",60,1001));
        recommendations.add(new Item("2","Learning Docker Networking","Docker networking deep dive","https://mtchouimages.blob.core.windows.net/books/DockerNetworking.jpg",40,1001));
        recommendations.add(new Item("3","Concurrent Programming in Scala","Learn the art of building concurrent applications","https://mtchouimages.blob.core.windows.net/books/Scala.jpg",64,1001));
        recommendations.add(new Item("2","Spring Microservices","Build scalable microservices with Spring and Docker","https://mtchouimages.blob.core.windows.net/books/SpringMicroServices.jpg",40,1001));
    }

    public Observable<Item> getViewHistoryItems(){
        return Observable.from(recommendations);
    }
}
