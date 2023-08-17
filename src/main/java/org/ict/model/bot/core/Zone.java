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
package org.ict.model.bot.core;

import static org.ict.rdf.utils.Utils.addElementList;
import static org.ict.rdf.utils.Utils.addZoneList;
import java.net.URI;
import java.util.List;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.ict.model.bot.core.iface.Interfaceable;
import org.ict.model.bot.core.iface.ModelIf;
import org.ict.model.bot.core.iface.ZoneIf;
import org.ict.model.bot.geo.Coord2D;
import org.ict.model.bot.jsongeo.Geometry;
import org.ict.model.bot.geo.View3D;
import org.ict.model.bot.geo.iface.FeatureIf;
import org.ict.rdf.utils.SingletonModelBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class Zone implements ZoneIf, ModelIf, Interfaceable {
  @SerializedName("@id")
  private URI id;
  @SerializedName("@type")
  private List<URI> atType;

  @SerializedName("schema:name")
  private String name;

  @SerializedName("bot:containsZone")
  private List<Zone> containsZone;
  @SerializedName("bot:hasBuilding")
  private List<Zone> hasBuilding;
  @SerializedName("bot:hasStorey")
  private List<Zone> hasStorey;
  @SerializedName("bot:hasSpace")
  private List<Zone> hasSpace;
  @SerializedName("bot:adjacentZone")
  private List<Zone> adjacentZone;
  @SerializedName("bot:intersectsZone")
  private List<Zone> intersectsZone;

  @SerializedName("bot:hasElement")
  private List<Element> hasElement;
  @SerializedName("bot:containsElement")
  private List<Element> containsElement;
  @SerializedName("bot:adjacentElement")
  private List<Element> adjacentElement;
  @SerializedName("bot:intersectingElement")
  private List<Element> intersectingElement;

  @SerializedName("geo:geometry")
  private Geometry geometry;
  private View3D view3D;
  private List<Coord2D> area;

  public Zone() {}
  public Zone(URI id, List<URI> atType, String name, List<Zone> containsZone,
      List<Zone> hasBuilding, List<Zone> hasStorey, List<Zone> hasSpace, List<Zone> adjacentZone,
      List<Zone> intersectsZone, List<Element> hasElement, List<Element> containsElement,
      List<Element> adjacentElement, List<Element> intersectingElement, Geometry geometry,
      View3D view3D, List<Coord2D> area) {
    super();
    this.id = id;
    this.atType = atType;
    this.name = name;
    this.containsZone = containsZone;
    this.hasBuilding = hasBuilding;
    this.hasStorey = hasStorey;
    this.hasSpace = hasSpace;
    this.adjacentZone = adjacentZone;
    this.intersectsZone = intersectsZone;
    this.hasElement = hasElement;
    this.containsElement = containsElement;
    this.adjacentElement = adjacentElement;
    this.intersectingElement = intersectingElement;
    this.geometry = geometry;
    this.view3D = view3D;
    this.area = area;

    ModelBuilder builder = SingletonModelBuilder.getInstance();
    String sid = id == null ? "default:iri" : id.toString();
    builder = builder.subject(sid);
    // add all zones
    addZoneList(builder, "https://w3id.org/bot#containsZone", containsZone);
    addZoneList(builder, "https://w3id.org/bot#hasBuilding", hasBuilding);
    addZoneList(builder, "https://w3id.org/bot#hasStorey", hasStorey);
    addZoneList(builder, "https://w3id.org/bot#hasSpace", hasSpace);
    addZoneList(builder, "https://w3id.org/bot#adjacentZone", adjacentZone);
    addZoneList(builder, "https://w3id.org/bot#intersectsZone", intersectsZone);
    // add all elements
    addElementList(builder, "https://w3id.org/bot#hasElement", hasElement);
    addElementList(builder, "https://w3id.org/bot#containsElement", containsElement);
    addElementList(builder, "https://w3id.org/bot#adjacentElement", adjacentElement);
    addElementList(builder, "https://w3id.org/bot#intersectingElement", intersectingElement);
    // TODO add geometry
    // builder.add("http://www.opengis.net/ont/geosparql#hasGeometry", geometry);
  }

  @Override
  public String getIdRDF() {
    return id.toString();
  }

  public void setIdRDF(String id) {
    this.id = URI.create(id);
  }

  public Model getModel() {
    return SingletonModelBuilder.getInstance().build();
  }
}
