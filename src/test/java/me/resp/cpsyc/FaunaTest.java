package me.resp.cpsyc;

import static com.faunadb.client.query.Language.Arr;
import static com.faunadb.client.query.Language.Collection;
import static com.faunadb.client.query.Language.Create;
import static com.faunadb.client.query.Language.CreateCollection;
import static com.faunadb.client.query.Language.CreateDatabase;
import static com.faunadb.client.query.Language.CreateIndex;
import static com.faunadb.client.query.Language.CreateKey;
import static com.faunadb.client.query.Language.Database;
import static com.faunadb.client.query.Language.Delete;
import static com.faunadb.client.query.Language.Do;
import static com.faunadb.client.query.Language.Documents;
import static com.faunadb.client.query.Language.Exists;
import static com.faunadb.client.query.Language.Foreach;
import static com.faunadb.client.query.Language.Get;
import static com.faunadb.client.query.Language.If;
import static com.faunadb.client.query.Language.Index;
import static com.faunadb.client.query.Language.Keys;
import static com.faunadb.client.query.Language.Lambda;
import static com.faunadb.client.query.Language.Let;
import static com.faunadb.client.query.Language.Map;
import static com.faunadb.client.query.Language.Match;
import static com.faunadb.client.query.Language.Obj;
import static com.faunadb.client.query.Language.Paginate;
import static com.faunadb.client.query.Language.Path;
import static com.faunadb.client.query.Language.Ref;
import static com.faunadb.client.query.Language.Select;
import static com.faunadb.client.query.Language.SelectAsIndex;
import static com.faunadb.client.query.Language.Value;
import static com.faunadb.client.query.Language.Var;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.faunadb.client.FaunaClient;
import com.faunadb.client.query.Expr;
import com.faunadb.client.types.Value;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import me.resp.cpsyc.dto.ChannelData;

@Slf4j
public class FaunaTest {

  private static final String DB_NAME = "cpsyc";
  private static final String COLLECTION_NAME = "channel_content";

  @Test
  void tMyDbKeys() throws MalformedURLException, InterruptedException, ExecutionException {
    FaunaUtil faunaUtil = Tutil.faunaUtil(COLLECTION_NAME);
    Value keyResults = faunaUtil.getAdminClient().query(Paginate(Keys())).get();
    String cc =
        keyResults.at("data").collect(Value.RefV.class).stream()
            .map(v -> v.getId())
            .collect(Collectors.joining(","));
    log.info("keys: {}", cc);
  }

  @Test
  void tquery() throws MalformedURLException, InterruptedException, ExecutionException {
    FaunaUtil faunaUtil = Tutil.faunaUtil(COLLECTION_NAME);
    try {
      Value result =
          faunaUtil
              .getClient()
              .query(
                  Create(
                      Ref(Collection(COLLECTION_NAME), Value("102")), Obj("content", Value("vvv"))))
              .get();
      log.info("result: {}", result);

    } catch (Exception e) {
      log.error("duplicated key: {}", e.getCause().getMessage());
    }
    Value results =
        faunaUtil
            .getClient()
            .query(
                Map(
                    Paginate(Documents(Collection(COLLECTION_NAME))).size(1000),
                    Lambda("ref", Get(Var("ref")))))
            .get();
    log.info("results: {}", results);

    Value one =
        faunaUtil
            .getClient()
            .query(
                Let("doc", Get(Ref(Collection(COLLECTION_NAME), Value("102"))))
                    .in(
                        Obj(
                            "id", Select(Arr(Value("ref"), Value("id")), Var("doc")),
                            "content", Select(Arr(Value("data"), Value("content")), Var("doc")))))
            .get();

    // Field<ChannelData> dataField = Field.at("data").to(ChannelData.class);

    // RefV refv = one.at("ref").get(RefV.class);

    // assertThat(refv.getId()).isEqualTo("102");

    // log.info("one: {}", refv);
    // log.info("one: {}", one.at("data").get(ChannelData.class));
    // log.info("one: {}", one.get(dataField));
    log.info("one: {}", one.get(ChannelData.class));
  }

  @Test
  void tQuery() throws Exception {
    FaunaUtil faunaUtil = Tutil.faunaUtil(COLLECTION_NAME);
    /*
     * Create an admin connection to FaunaDB.
     *
     * If you are using the FaunaDB-Cloud version:
     *  - remove the 'withEndpoint' line below
     *  - substitute "secret" for your authentication key's secret
     */
    // FaunaClient adminClient = FaunaClient.builder()
    //     .withEndpoint("http://127.0.0.1:8443")
    //     .withSecret("secret")
    //     .build();
    System.out.println("Succesfully connected to FaunaDB as Admin\n");

    /*
     * Create a database
     */

    String DB_NAME = "demo";

    Value dbResults =
        faunaUtil
            .getAdminClient()
            .query(
                Do(
                    If(Exists(Database(DB_NAME)), Delete(Database(DB_NAME)), Value(true)),
                    CreateDatabase(Obj("name", Value(DB_NAME)))))
            .get();

    System.out.println(
        "Successfully created database: "
            + dbResults.at("name").to(String.class).get()
            + "\n"
            + dbResults
            + "\n");

    /*
     *  Create a client connection to the demo database
     */
    Value keyResults =
        faunaUtil
            .getAdminClient()
            .query(CreateKey(Obj("database", Database(Value(DB_NAME)), "role", Value("server"))))
            .get();

    String key = keyResults.at("secret").to(String.class).get();

    FaunaClient client = faunaUtil.getAdminClient().newSessionClient(key);
    System.out.println("Connected to Fauna database " + DB_NAME + " with server role\n");
    runSpellExamples(DB_NAME, client);

    /*
     * Delete the database
     */
    deleteDB(faunaUtil.getAdminClient(), DB_NAME);
    System.out.println("Disconnected from FaunaDB as Admin!");
  }

  private static void runSpellExamples(String DB_NAME, FaunaClient client) throws Exception {
    /*
     * Create the spell class and index
     */
    String SPELLS_CLASS = "spells";
    String INDEX_NAME = "spells_index";

    // https://docs.fauna.com/fauna/current/api/fql/cheat_sheet

    Value classResults = client.query(CreateCollection(Obj("name", Value(SPELLS_CLASS)))).get();
    System.out.println("Create Class for " + DB_NAME + ":\n " + classResults + "\n");

    Value indexResults =
        client
            .query(
                CreateIndex(
                    Obj("name", Value(INDEX_NAME), "source", Collection(Value(SPELLS_CLASS)))))
            .get();
    System.out.println("Create Index for " + DB_NAME + ":\n " + indexResults + "\n");

    /*
     * Add some entries to the spells class
     */

    Value addFireResults =
        client
            .query(
                Create(
                    Collection(Value(SPELLS_CLASS)),
                    Obj(
                        "data",
                        Obj(
                            "name",
                            Value("Fire Beak"),
                            "element",
                            Value("water"),
                            "cost",
                            Value(15)))))
            .get();
    System.out.println("Added spell to class " + SPELLS_CLASS + ":\n " + addFireResults + "\n");

    Value addDragonResults =
        client
            .query(
                Create(
                    Collection(Value(SPELLS_CLASS)),
                    Obj(
                        "data",
                        Obj(
                            "name",
                            Value("Water Dragon's Claw"),
                            "element",
                            Value("water"),
                            "cost",
                            Value(25)))))
            .get();
    System.out.println("Added spell to class " + SPELLS_CLASS + ":\n " + addDragonResults + "\n");

    Value addHippoResults =
        client
            .query(
                Create(
                    Collection(Value(SPELLS_CLASS)),
                    Obj(
                        "data",
                        Obj(
                            "name",
                            Value("Hippo's Wallow"),
                            "element",
                            Value("water"),
                            "cost",
                            Value(35)))))
            .get();
    System.out.println("Added spell to class " + SPELLS_CLASS + ":\n " + addHippoResults + "\n");

    // The results at 'ref' are a resource pointer to the class that was just created.
    Value hippoRef = addHippoResults.at("ref");
    System.out.println("hippoRef = " + hippoRef);

    /*
     * Read the hippo back that we just created
     */

    Value readHippoResults = client.query(Select(Value("data"), Get(hippoRef))).get();
    System.out.println("Hippo Spell:\n " + readHippoResults + "\n");

    // convert the hippo results into primitive elements
    String name = readHippoResults.at("name").to(String.class).get();
    Integer cost = readHippoResults.at("cost").to(Integer.class).get();
    String element = readHippoResults.at("element").to(String.class).get();

    System.out.println(
        String.format("Spell Details: Name=%s, Cost=%d, Element=%s", name, cost, element));

    // This would return an empty option if the field is not found or the conversion fails
    Optional<String> optSpellElement =
        readHippoResults.at("element").to(String.class).getOptional();
    if (optSpellElement.isPresent()) {
      String element2 = optSpellElement.get();
      System.out.println("optional spell element = " + element2);
    } else {
      System.out.println("Something went wrong reading the spell");
    }

    /*
     * Query for all the spells in the index
     */
    Value queryIndexResults =
        client
            .query(SelectAsIndex(Path("data", "id"), Paginate(Match(Index(Value(INDEX_NAME))))))
            .get();

    Collection<String> spellsRefIds = queryIndexResults.asCollectionOf(String.class).get();
    System.out.println("spellsRefIds = " + spellsRefIds + "\n");

    /*
     * Store a Spell java class
     */
    Spell newSpell = new Spell("Water Dragon's Claw", "water", 25);
    Value storeSpellResult =
        client.query(Create(Collection(Value(SPELLS_CLASS)), Obj("data", Value(newSpell)))).get();
    System.out.println("Stored spell:\n " + storeSpellResult + "\n");

    /*
     * Read the spell we just created
     */
    Value dragonRef = storeSpellResult.at("ref");
    Value getDragonResult = client.query(Select(Value("data"), Get(dragonRef))).get();
    Spell spell = getDragonResult.to(Spell.class).get();
    System.out.println("dragon spell: " + spell + "\n");

    /*
     * Store a list of Spells
     */

    Spell spellOne = new Spell("Chill Touch", "ice", 18);
    Spell spellTwo = new Spell("Dancing Lights", "fire", 45);
    Spell spellThree = new Spell("Fire Bolt", "fire", 32);
    List<Spell> spellList = Arrays.asList(spellOne, spellTwo, spellThree);

    // Lambda Variable for each spell
    String NXT_SPELL = "NXT_SPELL";

    // Encode the list of spells into an expression
    Expr encodedSpellsList = Value(spellList);

    // This query can be approximately read as for each spell in the list of spells evaluate the
    // lambda function.
    // That lambda function creates a temporary variable with each spell in the list and passes it
    // to the create function.
    // The create function then stores that spell in the database
    Value spellsListSave =
        client
            .query(
                Foreach(
                    encodedSpellsList,
                    Lambda(
                        Value(NXT_SPELL),
                        Create(Collection(Value(SPELLS_CLASS)), Obj("data", Var(NXT_SPELL))))))
            .get();

    System.out.println("Created list of spells from java list: \n" + spellsListSave + "\n");
    Collection<Spell> spellCollection = spellsListSave.asCollectionOf(Spell.class).get();
    System.out.println("save " + spellCollection.size() + " spells:");
    spellCollection.forEach(nextSpell -> System.out.println("   " + nextSpell));

    System.out.println("\n");

    /*
     * Read all Spells for the Spells Index
     */

    // Lambda Variable for each spell ref
    String REF_SPELL_ID = "NXT_SPELL";

    // Select causes the return data to be stored in the data field that is expected when the data
    // is covered to a collection
    // The Map is equivalent to a functional map which maps over the set of all values returned by
    // the paginate.
    // Then for each value in the list it runs the lambda function which gets and returns the value.
    Value findAllSpells =
        client
            .query(
                SelectAsIndex(
                    Path("data", "data"),
                    Map(
                        Paginate(Match(Index(Value(INDEX_NAME)))),
                        Lambda(Value(REF_SPELL_ID), Get(Var(REF_SPELL_ID))))))
            .get();

    Collection<Spell> allSpellsCollection = findAllSpells.asCollectionOf(Spell.class).get();
    System.out.println("read " + allSpellsCollection.size() + " spells:");
    allSpellsCollection.forEach(nextSpell -> System.out.println("   " + nextSpell));
  }

  private static void deleteDB(FaunaClient adminClient, String dbName) throws Exception {
    /*
     * Delete the Database created
     */
    Value result =
        adminClient
            .query(If(Exists(Database(dbName)), Delete(Database(dbName)), Value(true)))
            .get();
    System.out.println("Deleted database: " + dbName + ":\n " + result + "\n");
  }
}
