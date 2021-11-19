package me.resp.cpsyc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CpsycConstants {

  public static final String COLLECTION_NAME = "channelData";

  public static final String isoDateTimeString() {
    return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
  }
}
