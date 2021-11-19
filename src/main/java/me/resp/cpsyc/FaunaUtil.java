package me.resp.cpsyc;

import static com.faunadb.client.query.Language.Collection;
import static com.faunadb.client.query.Language.Create;
import static com.faunadb.client.query.Language.CreateCollection;
import static com.faunadb.client.query.Language.CreateDatabase;
import static com.faunadb.client.query.Language.Database;
import static com.faunadb.client.query.Language.Delete;
import static com.faunadb.client.query.Language.Exists;
import static com.faunadb.client.query.Language.Get;
import static com.faunadb.client.query.Language.If;
import static com.faunadb.client.query.Language.Now;
import static com.faunadb.client.query.Language.Obj;
import static com.faunadb.client.query.Language.Ref;
import static com.faunadb.client.query.Language.Update;
import static com.faunadb.client.query.Language.Value;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.faunadb.client.FaunaClient;
import com.faunadb.client.errors.BadRequestException;
import com.faunadb.client.errors.NotFoundException;
import com.faunadb.client.query.Expr;
import com.faunadb.client.types.Field;
import com.faunadb.client.types.Value;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.resp.cpsyc.dto.ChannelData;

@Getter
@Slf4j
public class FaunaUtil {

  private final FaunaClient adminClient;
  private final FaunaClient client;
  private final String collection_name;

  /**
   * @param adminClient
   * @param client
   * @throws MalformedURLException
   */
  public FaunaUtil(String endpoint, String collectionName, String adminSecret, String serverSecret)
      throws MalformedURLException {
    collection_name =
        (collectionName == null || collectionName.isBlank())
            ? CpsycConstants.COLLECTION_NAME
            : collectionName;
    adminClient =
        FaunaClient.builder()
            .withSecret(adminSecret)
            .withEndpoint(
                (endpoint == null || endpoint.isBlank()) ? "https://db.fauna.com/" : endpoint)
            // NOTE: Use the correct endpoint for your database's Region Group.
            .build();
    this.client = adminClient.newSessionClient(serverSecret);
  }

  public Value createDbIfNotExists(String dbname) throws InterruptedException, ExecutionException {
    return adminClient
        .query(
            If(Exists(Database(dbname)), Value(true), CreateDatabase(Obj("name", Value(dbname)))))
        .get();
  }

  public Optional<ChannelData> byId(String id) throws InterruptedException, ExecutionException {
    try {
      Value one =
          client
              .query(
                  Get(Ref(Collection(collection_name), Value(id)))
                  // Let("doc", Get(Ref(Collection(collection_name), Value(id))))
                  //     .in(
                  //         Obj(
                  //             "id", Select(Arr(Value("ref"), Value("id")), Var("doc")),
                  //             "content",
                  //                 Select(
                  //                     Arr(Value("data"), Value("content")), Var("doc"),
                  // Value("")),
                  //             "socker_id",
                  //                 Select(
                  //                     Arr(Value("data"), Value("socket_id")),
                  //                     Var("doc"),
                  //                     Value(""))))
                  )
              .get();
      return one.at("data").to(ChannelData.CHANNEL_DATA_CODEC).getOptional();

    } catch (ExecutionException e) {
      if (e.getCause() instanceof NotFoundException) {
        return Optional.empty();
      } else {
        throw e;
      }
    }
  }

  public ChannelData insertChannelData(ChannelData data)
      throws InterruptedException, ExecutionException {
    Map<String, Expr> values = new HashMap<>();
    values.put("id", Value(data.getId()));
    values.put("content", Value(data.getContent()));
    values.put("created_at", Now());
    Value one =
        client
            .query(
                // Let("data", values)
                //     .in(
                Create(
                    Ref(Collection(collection_name), Value(data.getId())),
                    Obj("data", Obj(values))))
            // )
            .get();
    Field<ChannelData> dataField = Field.at("data").to(ChannelData.CHANNEL_DATA_CODEC);
    return one.get(dataField);
  }

  public boolean collectionExists() throws InterruptedException, ExecutionException {
    Value result = client.query(Exists(Collection(collection_name))).get();
    return result.get(Boolean.class);
  }

  public void createCollection() throws InterruptedException, ExecutionException {
    try {
      Value classResults =
          client.query(CreateCollection(Obj("name", Value(collection_name)))).get();
      log.info("Create Class for " + classResults + "\n");

    } catch (ExecutionException e) {
      if (e.getCause() instanceof BadRequestException) {
        String ms = e.getCause().getMessage();
        if (ms.contains("Collection already exists")) {
          log.info("already exists: {}", e.getCause().getMessage());
        } else {
          throw e;
        }
      } else {
        throw e;
      }
    }
  }

  public void deleteCollection() throws InterruptedException, ExecutionException {
    Value classResults = client.query(Delete(Collection(collection_name))).get();
    log.info("Delete Collection " + classResults + "\n");
  }

  public ChannelData update(Map<String, Object> map)
      throws InterruptedException, ExecutionException {
    String id = map.get("id").toString();
    return update(id, map);
  }

  public ChannelData update(String id, Map<String, Object> map)
      throws InterruptedException, ExecutionException {
    Map<String, Expr> vmap = new HashMap<>();
    for (Entry<String, Object> entry : map.entrySet()) {
      vmap.put(entry.getKey(), Value(entry.getValue()));
    }
    Value result =
        client
            .query(Update(Ref(Collection(collection_name), Value(id)), Obj("data", Obj(vmap))))
            .get();
    Field<ChannelData> dataField = Field.at("data").to(ChannelData.CHANNEL_DATA_CODEC);
    return result.get(dataField);
  }

  public ChannelData delete(String id) throws InterruptedException, ExecutionException {
    Value result = client.query(Delete(Ref(Collection(collection_name), Value(id)))).get();

    return result.to(ChannelData.class).get();
  }
}
