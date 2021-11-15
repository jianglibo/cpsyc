package me.resp.cpsyc.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PusherAuthResult {
  private String auth;
  public PusherAuthResult(String a) {}
}
