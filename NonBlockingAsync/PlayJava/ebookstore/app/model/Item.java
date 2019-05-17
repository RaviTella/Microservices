package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Item {
    private String id;
    public String name;
    private String description;
    public String imageUrl;
    public Integer price;
    private Integer customerID;
}