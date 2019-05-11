package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class Item {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private double price;
    private Integer customerID;
}