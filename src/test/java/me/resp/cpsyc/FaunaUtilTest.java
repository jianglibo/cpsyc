package me.resp.cpsyc;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

import me.resp.cpsyc.dto.ChannelData;

public class FaunaUtilTest {
  private static final String COLLECTION_NAME = "t_collection";
  private static final String D_ID = "192837";

  @Test
  void tCrud() throws MalformedURLException, InterruptedException, ExecutionException {
    FaunaUtil faunaUtil = Tutil.faunaUtil(COLLECTION_NAME);

    // if (faunaUtil.collectionExists()) {
    //   faunaUtil.deleteCollection();
    //   // Thread.sleep(1000);
    //   assertThat(faunaUtil.collectionExists()).isFalse();
    // }
    faunaUtil.createCollection();
    assertThat(faunaUtil.collectionExists()).isTrue();

    ChannelData cd;
    Optional<ChannelData> cdOp;

    cdOp = faunaUtil.byId(D_ID);

    if (cdOp.isPresent()) {
      faunaUtil.delete(cdOp.get().getId());
    }

    cd =
        faunaUtil.insertChannelData(
            ChannelData.builder().id(D_ID).content("content").build());

    assertThat(cd)
        .hasFieldOrPropertyWithValue("id", D_ID)
        .hasFieldOrPropertyWithValue("content", "content")
        .hasNoNullFieldsOrProperties();

    String created_at = cd.getCreated_at();

    Map<String, Object> toUpdate = new HashMap<>();

    toUpdate.put("content", "content1");
    toUpdate.put("socket_id", "socket_id1");

    cd = faunaUtil.update(cd.getId(), toUpdate);
    assertThat(cd)
        .hasFieldOrPropertyWithValue("id", D_ID)
        .hasFieldOrPropertyWithValue("content", "content1")
        .hasFieldOrPropertyWithValue("created_at", created_at);
  }
}
