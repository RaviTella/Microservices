package model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class Membership {
    private String id;
    private Integer points;
    private String type;
    private Integer customerID;
}
