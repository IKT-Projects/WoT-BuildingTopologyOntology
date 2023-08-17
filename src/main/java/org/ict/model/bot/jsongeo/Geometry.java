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
import java.util.List;
import java.util.stream.Collectors;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Geometry implements GeometryIf {
  @SerializedName("@id")
  private URI id;
  @SerializedName("@type")
  private List<URI> atType;

  @Override
  public String getIdRDF() {
    return id.toString();
  }

  public void setIdRDF(String id) {
    this.id = URI.create(id);
  }

  @Override
  public List<URI> getAtTypeRDF() {
    return atType;
  }

  @Override
  public void setAtTypeRDF(List<URI> types) {
    List<URI> filtered = types.stream().distinct().collect(Collectors.toList());
    // types.stream().filter(u -> !u.equals(URI.create("https://purl.org/geojson/vocab#geometry")))
    // .collect(Collectors.toList());
    if (filtered.isEmpty()) {
      setAtType(null);
    } else {
      setAtType(filtered);
    }
  }
}
