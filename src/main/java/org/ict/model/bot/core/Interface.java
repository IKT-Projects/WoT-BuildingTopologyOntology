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

import static org.ict.rdf.utils.Utils.addInterfaceableList;
import java.net.URI;
import java.util.List;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.ict.model.bot.core.iface.InterfaceIf;
import org.ict.model.bot.core.iface.Interfaceable;
import org.ict.rdf.utils.SingletonModelBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(callSuper = true, includeFieldNames = true)
public class Interface implements InterfaceIf {
  @SerializedName("@id")
  private URI id;

  private List<Interfaceable> interfaceOf;

  public Interface(URI id, List<Interfaceable> interfaceOf) {
    super();
    this.id = id;
    this.interfaceOf = interfaceOf;

    ModelBuilder builder = SingletonModelBuilder.getInstance();
    builder = builder.subject(id.toString());

    // add all elements
    addInterfaceableList(builder, "https://w3id.org/bot#interfaceOf", interfaceOf);
  }

  @Override
  public String getIdRDF() {
    return id.toString();
  }

  public void setIdRDF(String id) {
    this.id = URI.create(id);
  }
}
