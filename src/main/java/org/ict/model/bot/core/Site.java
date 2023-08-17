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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.ict.model.bot.core.iface.SiteIf;
import org.ict.model.bot.geo.Coord2D;
import org.ict.model.bot.geo.View3D;
import org.ict.model.bot.jsongeo.Geometry;
import org.ict.model.jsonld.context.Context;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Site extends Zone implements SiteIf {
  private static final transient List<URI> defaultType = new ArrayList<>();
  static {
    defaultType.add(URI.create("https://w3id.org/bot#Site"));
  }
  @NonNull
  @SerializedName("@context")
  private Context[] context;

  public Site() {
    super(null, defaultType, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null);
  }

  @Builder
  public Site(URI id, List<URI> atType, String name, List<Zone> containsZone,
      List<Zone> hasBuilding, List<Zone> hasStorey, List<Zone> hasSpace, List<Zone> adjacentZone,
      List<Zone> intersectsZone, List<Element> hasElement, List<Element> containsElement,
      List<Element> adjacentElement, List<Element> intersectingElement, Geometry geometry,
      View3D view3D, List<Coord2D> area, Context[] context) {
    super(id, atType, name, containsZone, hasBuilding, hasStorey, hasSpace, adjacentZone,
        intersectsZone, hasElement, containsElement, adjacentElement, intersectingElement, geometry,
        view3D, area);
    this.context = context;
  }

  @Override
  public List<URI> getAtTypeRDF() {
    return super.getAtType();
  }

  @Override
  public void setAtTypeRDF(List<URI> types) {
    List<URI> filtered = types.stream().distinct().collect(Collectors.toList());
    // types.stream().filter(u -> !u.equals(URI.create("https://w3id.org/bot#Element")))
    // .collect(Collectors.toList());
    if (filtered.isEmpty()) {
      filtered.add(URI.create("https://w3id.org/bot#Site"));
      setAtType(filtered);
    } else {
      setAtType(filtered);
    }
  }
}
