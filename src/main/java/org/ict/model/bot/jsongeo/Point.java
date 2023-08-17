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
import java.util.Arrays;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
public class Point extends Geometry implements PointIf {
  @SerializedName("geo:coordinates")
  private Float[] coordinates;

  private transient CoordXY rdfCoordinates;

  @Builder
  public Point(URI id, List<URI> atType, Float[] coordinates) {
    super(id, atType);
    this.coordinates = coordinates;
    this.rdfCoordinates = new CoordXY(coordinates[0], coordinates[1]);
  }

  public void setCoordinates(Float[] coordinate) {
    this.coordinates = coordinate;
    this.rdfCoordinates = new CoordXY(coordinate[0], coordinate[1]);
  }

  public void setRdfCoordinates(CoordXY rdfCoordinate) {
    this.rdfCoordinates = rdfCoordinate;
    this.coordinates = new Float[] {rdfCoordinate.getX(), rdfCoordinate.getY()};
  }
}
