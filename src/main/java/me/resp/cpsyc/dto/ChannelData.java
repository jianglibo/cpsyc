package me.resp.cpsyc.dto;

import com.faunadb.client.types.Codec;
import com.faunadb.client.types.FaunaField;
import com.faunadb.client.types.Result;
import com.faunadb.client.types.Value;
import com.faunadb.client.types.Value.TimeV;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelData {

  public static final Codec<ChannelData> CHANNEL_DATA_CODEC =
      new Codec<ChannelData>() {
        public Result<ChannelData> decode(Value value) {
          return Result.success(
              ChannelData.builder()
                  .id(value.at("id").get(String.class))
                  .content(value.at("content").get(String.class))
                  .created_at(value.at("created_at").get(TimeV.class).toString())
                  .build());
        }

        public Result<Value> encode(ChannelData user) {
          throw new RuntimeException("not implemented, we will encode manully.");
        }
      };

  @FaunaField private String id;
  @FaunaField private String content;
  @FaunaField private String created_at;

  /**
   * @param id
   * @param content
   * @param socket_id
   */
  @Builder
  public ChannelData(String id, String content, String created_at) {
    this.id = id;
    this.content = content;
    this.created_at = created_at;
  }
}
