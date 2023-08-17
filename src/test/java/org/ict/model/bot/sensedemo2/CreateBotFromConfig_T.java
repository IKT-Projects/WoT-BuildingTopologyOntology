/*
 * Copyright © 2023 Institut fuer Kommunikationstechnik - FH-Dortmund (codebase.ikt@fh-dortmund.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ict.model.bot.sensedemo2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.apache.http.impl.conn.Wire;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.ict.gson.GsonUtils;
import org.ict.model.bot.core.Building;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Site;
import org.ict.model.bot.core.Space;
import org.ict.model.bot.core.Storey;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.gson.utils.AdapterFactory;
import org.ict.model.jsonld.context.Context;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

public class CreateBotFromConfig_T {
  private static final Logger LOG = LogManager.getFormatterLogger(CreateBotFromConfig_T.class);
  private static BotSiteBuilder SITEBUILDER = new BotSiteBuilder();
  private static final String THINGS = "/things";
  private static String BOT_BASE_URI;
  private static String WOT_BASE_URI;
  private static Map<String, JsonObject> siteTree = new HashMap<>();
  private static final Map<String, String> prefixToUrl_TypeExpand = new HashMap<>();
  private static Gson gson = AdapterFactory.getGsonBuilderWithDefaultBotTypeAdapters().create();

  private static MongoClient mongoClient;
  private static DB database;
  private static DBCollection collection;

  static {
    prefixToUrl_TypeExpand.put("bot", "https://w3id.org/bot#");
    prefixToUrl_TypeExpand.put("wot", "https://www.w3.org/2019/wot/td#");
    // prefixToUrl.put("wot", "https://www.w3.org/2019/wot/td/v1#");
    prefixToUrl_TypeExpand.put("prod", "https://w3id.org/product#");
    prefixToUrl_TypeExpand.put("geo", "https://purl.org/geojson/vocab#");
  }
  private static RDFBeanManager manager;
  private static boolean scale = true;
  private static float factor = 0.22f;

  public static void main(String[] args) {
    try {
//       mongoClient = new MongoClient(new MongoClientURI("mongodb://192.168.29.240:27017"));
//       database = mongoClient.getDB("WoT");
//       collection = database.getCollection("LocationData");
       // 
       // Suite
//       Repository db = createAndInitDB("http://10.3.0.75:28008/rdf4j-server/", "2");
//       // Wand 2 - Repository db = createAndInitDB("http://192.168.29.240:8080/rdf4j-server/", "4");
//        RepositoryConnection con = setupRepoConnection(db);
//        manager = new RDFBeanManager(con);

      JsonObject siteConfig = readJsonConfig("./examples/suite.json");
      BOT_BASE_URI = siteConfig.getAsJsonPrimitive("bot-base-uri").getAsString();
      WOT_BASE_URI = siteConfig.getAsJsonPrimitive("wot-base-uri").getAsString();
      JsonArray jsonThings = siteConfig.getAsJsonArray("things");
      JsonObject jsonSite = siteConfig.getAsJsonObject("site");
      JsonArray jsonBuildings = siteConfig.getAsJsonArray("buildings");
      JsonArray jsonStoreys = siteConfig.getAsJsonArray("storeys");
      JsonArray jsonSpaces = siteConfig.getAsJsonArray("spaces");
      JsonArray jsonElements = siteConfig.getAsJsonArray("elements");
      JsonArray jsonTree = siteConfig.getAsJsonArray("tree");

      createSiteTree(jsonTree);

      LOG.info("--------------------------------------------------------------------------");
      LOG.info("--------------------------------- THINGS ---------------------------------");
      LOG.info("--------------------------------------------------------------------------");
      List<Element> things = createThings(jsonThings);

      LOG.info("--------------------------------------------------------------------------");
      LOG.info("--------------------------------- SPACES ---------------------------------");
      LOG.info("--------------------------------------------------------------------------");
      List<Zone> spaces = createSpaces(jsonSpaces, things);

      LOG.info("--------------------------------------------------------------------------");
      LOG.info("-------------------------------- STOREYS ---------------------------------");
      LOG.info("--------------------------------------------------------------------------");
      List<Zone> storeys = createStoreys(jsonStoreys, spaces);

      LOG.info("--------------------------------------------------------------------------");
      LOG.info("--------------------------------BUILDINGS---------------------------------");
      LOG.info("--------------------------------------------------------------------------");
      List<Zone> buildings = createBuildings(jsonBuildings, storeys);

      LOG.info("--------------------------------------------------------------------------");
      LOG.info("---------------------------------  SITE  ---------------------------------");
      LOG.info("--------------------------------------------------------------------------");
      Site site = createSite(jsonSite, buildings);
      System.out.println(SITEBUILDER.siteToJSON(site));
      Site siteExpanded = expandTypeUrls(site);
      System.out.println(SITEBUILDER.siteToJSON(siteExpanded));
      // Resource res = manager.add(siteExpanded);
      // Site newSite = manager.get(res, Site.class);
      // System.out.println(siteExpanded.equals(newSite));
      // System.out.println(SITEBUILDER.siteToJSON(newSite));
      // writeToMongoDb(site);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static Site expandTypeUrls(Site site) {
    site.setAtType(
        site.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
    List<Zone> buildings = site.getHasBuilding();
    for (Zone building : buildings) {
      building.setAtType(
          building.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
      List<Zone> storeys = building.getHasStorey();
      for (Zone storey : storeys) {
        storey.setAtType(
            storey.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
        List<Zone> spaces = storey.getHasSpace();
        for (Zone space : spaces) {
          space.setAtType(
              space.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
          List<Element> hasElements = space.getHasElement();
          if (hasElements != null) {
            for (Element e : hasElements) {
              e.setAtType(
                  e.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
            }
          }
          List<Element> containsElements = space.getContainsElement();
          if (containsElements != null) {
            for (Element e : containsElements) {
              e.setAtType(
                  e.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
            }
          }
          List<Element> adjacentElements = space.getAdjacentElement();
          if (adjacentElements != null) {
            for (Element e : adjacentElements) {
              e.setAtType(
                  e.getAtType().stream().map(t -> toExpandedUrl(t)).collect(Collectors.toList()));
            }
          }
        }
      }
    }
    return site;
  }

  private static URI toExpandedUrl(URI uri) {

    String suri = uri.toString();
    LOG.info("Prefixed uri: %s", suri);
    String prefix = suri.substring(0, suri.indexOf(":"));
    LOG.info("Prefix: %s", prefix);
    String prefixUri = prefixToUrl_TypeExpand.get(prefix);
    LOG.info("Uri for prefix: %s", prefixUri);
    String fullUri = suri.replace(prefix + ":", prefixUri);
    LOG.info("Expanded uri: %s", fullUri);
    return URI.create(fullUri);
  }

  private static Site createSite(JsonObject jsonSite, List<Zone> buildings) {
    String baseuri = BOT_BASE_URI;
    String name = jsonSite.get("name").getAsString();
    String id = jsonSite.get("id").getAsString();
    JsonObject conf = siteTree.get(id);
    URI resid = constructSiteResourceURI(baseuri, id, conf);
    List<Float[]> coords = getPolygonAsFloatArrayList(jsonSite.get("coords").getAsJsonArray());
    LOG.info("Creating site: %s", name);
    LOG.info("Resource id: '%s'", resid);
    LOG.info("Position coordinate: %s", coordsToString(coords));
    return SITEBUILDER.createSite(BotSiteBuilder.getDefaultContext(), resid, name, coords,
        buildings);
  }

  private static List<Zone> createBuildings(JsonArray jsonBuildings, List<Zone> storeys) {
    String baseuri = BOT_BASE_URI;
    List<Zone> buildings = new ArrayList<>(jsonBuildings.size());
    for (JsonElement ele : jsonBuildings) {
      JsonObject building = (JsonObject) ele;
      String id = building.get("id").getAsString();
      String name = building.get("name").getAsString();
      List<Float[]> coords = getPolygonAsFloatArrayList(building.get("coords").getAsJsonArray());
      List<Float[]> scaledCoors;
      if (scale) {
        scaledCoors = scaleCoords(coords, factor);
      } else {
        scaledCoors = coords;
      }

      // check for spaces of this storey
      JsonObject conf = siteTree.get(id);
      URI resid = constructBuildingResourceURI(baseuri, id, conf);

      LOG.info("Creating building: %s", name);
      LOG.info("Resource id: '%s'", resid);
      LOG.info("Position coordinate: %s", coordsToString(coords));

      JsonArray building_storeys = conf.get("hasStorey").getAsJsonArray();
      List<String> ele_ids = getIdsFromJsonArray(building_storeys);
      List<Zone> filtered =
          storeys.stream().filter(e -> ele_ids.contains(toShortId(e.getId().toString())))
              .collect(Collectors.toList());
      buildings.add(
          SITEBUILDER.createBuilding(resid, name, scaledCoors, filtered.isEmpty() ? null : filtered));
    }
    return buildings;
  }

  private static List<Float[]> scaleCoords(List<Float[]> coords, final float factor) {
    List<Float[]> scaled = new ArrayList<>(coords.size());
    for (int i=0;i < coords.size(); ++i) {
      Float[] coord = coords.get(i);
      Float[] newCoord = new Float[coord.length];
      scaled.add(newCoord);
      for (int j=0; j< coord.length;++j) {
        newCoord[j] = coord[j] * factor;  
      }
    }
    return scaled;
  }

  private static Object toShortId(String fullId) {
    return fullId.substring(fullId.lastIndexOf("/") + 1);
  }

  private static List<Zone> createStoreys(JsonArray jsonStoreys, List<Zone> spaces) {
    String baseuri = BOT_BASE_URI;
    List<Zone> storeys = new ArrayList<>(jsonStoreys.size());
    for (JsonElement ele : jsonStoreys) {
      JsonObject storey = (JsonObject) ele;
      String id = storey.get("id").getAsString();
      String name = storey.get("name").getAsString();
      List<Float[]> coords = getPolygonAsFloatArrayList(storey.get("coords").getAsJsonArray());
      List<Float[]> scaledCoors;
      if (scale) {
        scaledCoors = scaleCoords(coords, factor);
      } else {
        scaledCoors = coords;
      }
      // check for spaces of this storey
      JsonObject conf = siteTree.get(id);
      URI resid = constructStoreyResourceURI(baseuri, id, conf);

      LOG.info("Creating storey: %s", name);
      LOG.info("Resource id: '%s'", resid);
      LOG.info("Position coordinate: %s", coordsToString(scaledCoors));

      JsonArray storey_spaces = conf.get("hasSpace").getAsJsonArray();
      List<String> ele_ids = getIdsFromJsonArray(storey_spaces);
      List<Zone> filtered =
          spaces.stream().filter(e -> ele_ids.contains(toShortId(e.getId().toString())))
              .collect(Collectors.toList());
      storeys.add(
          SITEBUILDER.createStorey(resid, name, "1", scaledCoors, filtered.isEmpty() ? null : filtered));
    }
    return storeys;
  }

  private static void createSiteTree(JsonArray jsonTree) {
    for (int i = 0; i < jsonTree.size(); i++) {
      JsonObject ele = jsonTree.get(i).getAsJsonObject();
      siteTree.put(ele.get("id").getAsString(), ele);
    }
  }

  private static List<Zone> createSpaces(JsonArray jsonSpaces, List<Element> elements) {
    String baseuri = BOT_BASE_URI;
    List<Zone> spaces = new ArrayList<>(jsonSpaces.size());
    for (JsonElement ele : jsonSpaces) {
      JsonObject space = (JsonObject) ele;
      String id = space.get("id").getAsString();
      String name = space.get("name").getAsString();
      List<Float[]> coords = getPolygonAsFloatArrayList(space.get("coords").getAsJsonArray());
      List<Float[]> scaledCoors;
      if (scale) {
        scaledCoors = scaleCoords(coords, factor);
      } else {
        scaledCoors = coords;
      }
      // String resid = baseuri + id;

      // check for element of this space
      JsonObject conf = siteTree.get(id);
      URI resid = constructSpaceResourceURI(baseuri, id, conf);

      LOG.info("Creating space: %s", name);
      LOG.info("Resource id: '%s'", resid);
      LOG.info("Position coordinate: %s", coordsToString(scaledCoors));

      JsonArray space_ele = conf.get("hasElement").getAsJsonArray();
      List<String> ele_ids = getIdsFromJsonArray(space_ele);
      List<Element> filtered =
          elements.stream().filter(e -> ele_ids.contains(toShortId(e.getId().toString())))
              .collect(Collectors.toList());
      LOG.info("Adding space elements: %s",
          filtered.stream().map(e -> e.getId()).collect(Collectors.toList()));
      spaces.add(SITEBUILDER.createSpace(resid, name, scaledCoors, null, null,
          filtered.isEmpty() ? null : filtered));
    }
    return spaces;
  }

  private static String coordsToString(List<Float[]> coords) {
    String out = "[";
    for (Float[] floats : coords) {
      out += Arrays.toString(floats);
    }
    out += "]";
    return out;
  }

  private static URI constructSpaceResourceURI(String baseuri, String id, JsonObject conf) {
    String storeyId = conf.get("parent").getAsString();
    JsonObject storeyConf = siteTree.get(storeyId);
    String buildingId = storeyConf.get("parent").getAsString();
    JsonObject siteConf = siteTree.get(buildingId);
    String siteId = siteConf.get("parent").getAsString();
    URI resid = SITEBUILDER.buildSpaceResourcePath(baseuri, siteId, buildingId, storeyId, id);
    return resid;
  }

  private static URI constructStoreyResourceURI(String baseuri, String id, JsonObject conf) {
    String buildingId = conf.get("parent").getAsString();
    JsonObject siteConf = siteTree.get(buildingId);
    String siteId = siteConf.get("parent").getAsString();
    URI resid = SITEBUILDER.buildStoreyResourcePath(baseuri, siteId, buildingId, id);
    return resid;
  }

  private static URI constructBuildingResourceURI(String baseuri, String id, JsonObject conf) {
    String siteId = conf.get("parent").getAsString();
    URI resid = SITEBUILDER.buildBuildingResourcePath(baseuri, siteId, id);
    return resid;
  }

  private static URI constructSiteResourceURI(String baseuri, String id, JsonObject conf) {
    URI resid = SITEBUILDER.buildSiteResourcePath(baseuri, id);
    return resid;
  }

  private static List<String> getIdsFromJsonArray(JsonArray space_ele) {
    List<String> ids = new ArrayList<>(space_ele.size());
    for (int i = 0; i < space_ele.size(); i++) {
      ids.add(space_ele.get(i).getAsString());
    }
    return ids;
  }

  private static List<Float[]> getPolygonAsFloatArrayList(JsonArray asJsonArray) {
    List<Float[]> coords = new ArrayList<>();
    for (int i = 0; i < asJsonArray.size(); i++) {
      Float[] coord = getCoordAsFloatArray(asJsonArray.get(i).getAsJsonArray());
      coords.add(coord);
    }
    return coords;
  }

  private static JsonObject readJsonConfig(String filename) throws FileNotFoundException {
    JsonParser parser = new JsonParser();
    return (JsonObject) parser.parse(new FileReader(filename));
  }

  private static List<String> getThingIds(String property) {
    StringTokenizer st = new StringTokenizer(property, ",");
    List<String> things = new ArrayList<>(st.countTokens());
    while (st.hasMoreTokens()) {
      things.add(st.nextToken());
    }
    return things;
  }

  private static Properties loadProperties(String filename) throws IOException {
    InputStream input = new FileInputStream(filename);
    Properties prop = new Properties();
    // load a properties file
    prop.load(input);
    return prop;
  }

  private static List<Element> createThings(JsonArray jsonThings) {
    String baseuri = WOT_BASE_URI;
    List<Element> elements = new ArrayList<>(jsonThings.size());
    for (JsonElement ele : jsonThings) {
      JsonObject thing = (JsonObject) ele;
      String id = thing.get("id").getAsString();
      String name = thing.get("name").getAsString();
      String url = thing.get("url").getAsString();
      Float[] coords = getCoordAsFloatArray(thing.get("coords").getAsJsonArray());
      String resid;
      if (url.equals("")) {
        resid = baseuri + THINGS + "/" + id;
      } else {
        resid = url;
      }
      elements.add(SITEBUILDER.createThing(URI.create(resid), coords, (URI[]) null));
      LOG.info("Creating wot thing '%s' ...", name);
      LOG.info("Resource id: '%s'", resid);
      LOG.info("Position coordinate: %s", Arrays.toString(coords));
    }
    return elements;
  }

  private static Float[] getCoordAsFloatArray(JsonArray asJsonArray) {
    Float[] coord = new Float[2];
    for (int i = 0; i < coord.length; i++) {
      coord[i] = asJsonArray.get(i).getAsFloat();
    }
    return coord;
  }

  private static RepositoryConnection setupRepoConnection(Repository db) {
    RepositoryConnection con = db.getConnection();
    con.setNamespace("bot", "https://w3id.org/bot#");
    con.setNamespace("prod", "https://w3id.org/product#");
    con.setNamespace("loc", "https://example.com/ict-gw/v1/location/");
    con.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    con.setNamespace("rdfbeans", "http://viceversatech.com/rdfbeans/2.0/");
    con.setNamespace("wot", "https://www.w3.org/2019/wot/td/v1");
    con.setNamespace("td", "https://www.w3.org/2019/wot/td#");
    con.setNamespace("device", "https://example.com/ict-gw/v1/thing/");
    con.setNamespace("geo", "http://www.opengis.net/ont/geosparql#");
    con.setNamespace("sf", "http://www.opengis.net/ont/sf#");
    con.setNamespace("ict", "https://www.ict.org/common#");
    con.setNamespace("ict", "https://www.ict.org/common#");
    con.setNamespace("geojson", "https://purl.org/geojson/vocab#");
    return con;
  }

  private static Repository createAndInitDB(String url, String repoId) {
    Repository db = new HTTPRepository(url, repoId);
    db.init();
    return db;
  }

  private static void writeToMongoDb(Site site) {
    try {
      DBObject dbObject = (DBObject) JSON.parse(gson.toJson(site));
      dbObject.put("_id", site.getId().toString());
      collection.insert(dbObject);
    } catch (Throwable th) {
      System.out.printf("Unable to store location '%s' in mongo db.", site.getId());
      System.out.printf(th.getMessage());
      th.printStackTrace();
    }
  }
}
