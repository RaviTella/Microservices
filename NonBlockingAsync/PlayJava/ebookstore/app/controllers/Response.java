package controllers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Response<T> {
  public  String responseType;
  public List<T> items;
}
