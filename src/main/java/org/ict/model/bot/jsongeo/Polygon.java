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
package org.ict.model.bot.jsongeo;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Polygon extends Geometry implements PolygonIf {
  @SerializedName("geo:coordinates")
  private List<Float[]> coordinates;

  private transient List<CoordXY> rdfCoordinates;

  public Polygon() {
    super(null, null);
  }

  @Builder
  public Polygon(URI id, List<URI> atType, List<Float[]> coordinates) {
    super(id, atType);
    this.coordinates = coordinates;
    List<CoordXY> coords = new ArrayList<>(coordinates.size());
    for (Float[] floats : coordinates) {
      coords.add(new CoordXY(floats[0], floats[1]));
    }
    this.rdfCoordinates = coords;
  }

  public void setCoordinates(List<Float[]> coordinates) {
    List<CoordXY> coords = new ArrayList<>(coordinates.size());
    for (Float[] xy : coordinates) {
      coords.add(new CoordXY(xy[0], xy[1]));
    }
    this.coordinates = coordinates;
    this.rdfCoordinates = coords;
  }
  public void setRdfCoordinates(List<CoordXY> rdfCoordinates) {
    List<Float[]> coords = new ArrayList<>(rdfCoordinates.size());
    for (CoordXY xy : rdfCoordinates) {
      coords.add(new Float[] {xy.getX(),xy.getY()});
    }
    this.rdfCoordinates = rdfCoordinates;
    this.coordinates = coords;
  }
}
