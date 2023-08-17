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
package org.ict.rdf.utils;

import java.util.List;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.core.iface.Interfaceable;

public class Utils {
  private static ValueFactory factory;

  public static ValueFactory getValueFactory() {
    if (factory == null) {
      factory = SimpleValueFactory.getInstance();
    }
    return factory;
  }

  public static void addZoneList(ModelBuilder builder, String predicate, List<Zone> list) {
    if (list == null)
      return;
    ValueFactory factory = Utils.getValueFactory();
    for (Zone z : list) {
      builder.add(factory.createIRI(predicate), factory.createIRI(z.getId().toString()));
    }
  }

  public static void addElementList(ModelBuilder builder, String predicate, List<Element> list) {
    if (list == null)
      return;
    ValueFactory factory = Utils.getValueFactory();
    for (Element e : list) {
      builder.add(factory.createIRI(predicate), factory.createIRI(e.getId().toString()));
    }
  }
  public static void addInterfaceableList(ModelBuilder builder, String predicate, List<Interfaceable> list) {
    if (list == null)
      return;
    ValueFactory factory = Utils.getValueFactory();
    for (Interfaceable i : list) {
      builder.add(factory.createIRI(predicate), factory.createIRI(i.getId().toString()));
    }
  }
}
