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

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.ict.gson.GsonUtils;
import org.ict.model.bot.core.Building;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Site;
import org.ict.model.bot.core.Space;
import org.ict.model.bot.core.Storey;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.jsongeo.Point;
import org.ict.model.bot.jsongeo.Polygon;
import org.ict.model.bot.sensedemo.Coords;
import org.ict.model.jsonld.context.Context;
import com.google.gson.Gson;

public class BotSiteBuilder {
  private static final String PATH_SEPERATOR = "/";
  private static final String BASE = "http://localhost:8092/v1";
  private static final String SITES = "/sites";
  private static final String BUILDINGS = "/buildings";
  private static final String STOREYS = "/storeys";
  private static final String SPACES = "/spaces";
  private static final String ELEMENTS = "/elements";
  private static final String INTERFACES = "/interfaces";

  private static final String WOT = "https://www.w3.org/2019/wot/td/v1/";


  public static enum Products {
    // @formatter:off
    Wall(Constants.ONT_BASE + "Wall", "prod:Wall"),
    Window(Constants.ONT_BASE + "Window", "prod:Window"), 
    Door(Constants.ONT_BASE + "Door", "prod:Door"), 
    Beam(Constants.ONT_BASE + "Beam", "prod:Beam"), 
    Chimney(Constants.ONT_BASE + "Chimney", "prod:Chimney"), 
    Heater(Constants.ONT_BASE + "Heater","prod:Heater"), 
    Column(Constants.ONT_BASE + "Column", "prod:Column"), 
    CurtainWall(Constants.ONT_BASE + "CurtainWall", "prod:CurtainWall"), 
    Floor(Constants.ONT_BASE + "Floor", "prod:Floor"), 
    Slab(Constants.ONT_BASE + "Slab", "prod:Slab"), 
    Railing(Constants.ONT_BASE + "Railing","prod:Railing"), 
    Ramp(Constants.ONT_BASE + "Ramp", "prod:Ramp"), 
    Roof(Constants.ONT_BASE + "Roof","prod:Roof"), 
    ShadingDevice(Constants.ONT_BASE + "ShadingDevice", "prod:ShadingDevice"), 
    Stair(Constants.ONT_BASE + "Stair", "prod:Stair");
    // @formatter:on
    private String id;
    private String prefixedId;

    private Products(String id, String prefixedId) {
      this.id = id;
      this.prefixedId = prefixedId;
    }

    public URI getFullUri() {
      return URI.create(id);
    }

    public URI getPrefixedUri() {
      return URI.create(prefixedId);
    }

    public String getPrefix() {
      return "prod";
    }

    public URI getOntBaseUri() {
      return URI.create(Constants.ONT_BASE);
    }

    private static class Constants {
      public static final String ONT_BASE = "https://w3id.org/product#";
    }
  }

  private static Gson gson =
      new Gson().newBuilder().registerTypeAdapter(Context[].class, GsonUtils.getContextSerializer())
          .registerTypeAdapter(Context[].class, GsonUtils.getContextDeserializer()).create();

  public Element createThing(URI uri, Float[] coords, URI... additionalTypes) {
    int l = additionalTypes != null ? 2 + additionalTypes.length : 2;
    List<URI> atTypes = new ArrayList<>(l);
    atTypes.add(URI.create("bot:Element"));
    atTypes.add(URI.create("wot:Thing"));
    if (additionalTypes != null) {
      for (URI type : additionalTypes) {
        atTypes.add(type);
      }
    }
    Point p = org.ict.model.bot.jsongeo.Point.builder()
        .id(URI.create("point:" + UUID.randomUUID().toString()))
        .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Point")))
        .coordinates(coords).build();
    Element e = Element.builder().id(uri).atType(atTypes).geometry(p).build();
    return e;
  }

  public Element createElement(URI uri, Float[] coords, URI... additionalTypes) {
    int l = additionalTypes != null ? 1 + additionalTypes.length : 1;
    List<URI> atTypes = new ArrayList<>(l);
    atTypes.add(URI.create("bot:Element"));
    if (additionalTypes != null) {
      for (URI type : additionalTypes) {
        atTypes.add(type);
      }
    }
    Point p = org.ict.model.bot.jsongeo.Point.builder()
        .id(URI.create("point:" + UUID.randomUUID().toString()))
        .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Point")))
        .coordinates(coords).build();
    Element e = Element.builder().id(uri).atType(atTypes).geometry(p).build();
    return e;
  }

  public Space createSpace(URI uri, String name, List<Float[]> coords,
      List<Element> adjacentElements, List<Element> containsElements, List<Element> hasElements) {
    Space space = Space.builder().id(uri).name(name)
        .geometry(Polygon.builder()
            .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Polygon")))
            .id(URI.create("polygon:" + UUID.randomUUID().toString())).coordinates(coords).build())
        .atType(Arrays.asList(URI.create("bot:Space"))).adjacentElement(adjacentElements)
        .containsElement(containsElements).hasElement(hasElements).build();
    return space;
  }

  public Storey createStorey(URI uri, String name, String level, List<Float[]> coords,
      List<Zone> hasSpaces) {
    Storey storey = Storey.builder().id(uri).name(name).floorLevel(level)
        .geometry(Polygon.builder()
            .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Polygon")))
            .id(URI.create("polygon:" + UUID.randomUUID().toString())).coordinates(coords).build())
        .atType(Arrays.asList(URI.create("bot:Storey"))).hasSpace(hasSpaces).build();
    return storey;
  }

  public Building createBuilding(URI uri, String name, List<Float[]> coords, List<Zone> storeys) {
    Building building = Building.builder().id(uri).name(name)
        .geometry(Polygon.builder()
            .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Polygon")))
            .id(URI.create("polygon:" + UUID.randomUUID().toString())).coordinates(coords).build())
        .atType(Arrays.asList(URI.create("bot:Building"))).hasStorey(storeys).build();
    return building;
  }

  public Site createSite(Context[] contexts, URI uri, String name, List<Float[]> coords,
      List<Zone> hasBuildings) {
    Site site = Site.builder().context(contexts).id(uri).name(name)
        .geometry(Polygon.builder().coordinates(coords)
            .atType(Arrays.asList(URI.create("https://purl.org/geojson/vocab#Polygon")))
            .id(URI.create("polygon:" + UUID.randomUUID().toString())).build())
        .atType(Arrays.asList(URI.create("bot:Site"))).hasBuilding(hasBuildings).build();
    return site;
  }

  public static Context[] getDefaultContext() {
    return new Context[] {
        Context.builder().prefix("schema").namespace("http://schema.org/").build(),
        Context.builder().prefix("bot").namespace("https://w3id.org/bot#").build(),
        Context.builder().prefix("rdf").namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
            .build(),
        Context.builder().prefix("wot").namespace(WOT).build(),
        Context.builder().prefix("prod").namespace("https://w3id.org/product#").build(),
        Context.builder().prefix("geo").namespace("https://purl.org/geojson/vocab#").build()};
  }

  public String siteToJSON(Site site) {
    return gson.toJson(site);
  }

  private URI buildResourcePath(String baseuri, String siteId, String buildingId, String storeyId,
      String spaceId, String elementId) {
    Objects.requireNonNull(baseuri, "Base uri should not be null!");
    Objects.requireNonNull(siteId, "Site id should not be null!");

    StringBuilder sb = new StringBuilder(baseuri);
    sb.append(SITES);
    sb.append(PATH_SEPERATOR);
    sb.append(siteId);
    if (buildingId != null) {
      sb.append(BUILDINGS);
      sb.append(PATH_SEPERATOR);
      sb.append(buildingId);
    }
    if (storeyId != null) {
      sb.append(STOREYS);
      sb.append(PATH_SEPERATOR);
      sb.append(storeyId);
    }
    if (spaceId != null) {
      sb.append(SPACES);
      sb.append(PATH_SEPERATOR);
      sb.append(spaceId);
    }
    if (elementId != null) {
      sb.append(ELEMENTS);
      sb.append(PATH_SEPERATOR);
      sb.append(elementId);
    }
    return URI.create(sb.toString());
  }

  public URI buildElementResourcePath(String baseuri, String siteId, String buildingId,
      String storeyId, String spaceId, String elementId) {
    return buildResourcePath(baseuri, siteId, buildingId, storeyId, spaceId, elementId);
  }

  public URI buildSpaceResourcePath(String baseuri, String siteId, String buildingId,
      String storeyId, String spaceId) {
    return buildResourcePath(baseuri, siteId, buildingId, storeyId, spaceId, null);
  }

  public URI buildStoreyResourcePath(String baseuri, String siteId, String buildingId,
      String storeyId) {
    return buildResourcePath(baseuri, siteId, buildingId, storeyId, null, null);
  }

  public URI buildBuildingResourcePath(String baseuri, String siteId, String buildingId) {
    return buildResourcePath(baseuri, siteId, buildingId, null, null, null);
  }

  public URI buildSiteResourcePath(String baseuri, String siteId) {
    return buildResourcePath(baseuri, siteId, null, null, null, null);
  }
}
