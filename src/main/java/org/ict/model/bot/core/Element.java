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
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.ict.model.bot.core.iface.ElementIf;
import org.ict.model.bot.core.iface.Interfaceable;
import org.ict.model.bot.jsongeo.Geometry;
import org.ict.model.bot.geo.View3D;
import org.ict.model.bot.geo.iface.FeatureIf;
import org.ict.rdf.utils.SingletonModelBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
public class Element implements ElementIf, FeatureIf, Interfaceable {
  @SerializedName("@type")
  private List<URI> atType;
  @SerializedName("@id")
  private URI id;
  private List<Element> hasSubElement;
  @SerializedName("geo:geometry")
  private Geometry geometry;
  private View3D view3D;
  
  public Element(URI id, List<Element> hasSubElement) {
    this.id = id;
    this.hasSubElement = hasSubElement;

    ModelBuilder builder = SingletonModelBuilder.getInstance();
    builder = builder.subject(id.toString());

    // add all elements
    addElementList(builder, "https://w3id.org/bot#hasSubElement", hasSubElement);
  }

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
//        types.stream().filter(u -> !u.equals(URI.create("https://w3id.org/bot#Element")))
//            .collect(Collectors.toList());
    if (filtered.isEmpty()) {
      setAtType(null);
    } else {
      setAtType(filtered);
    }
  }
}
