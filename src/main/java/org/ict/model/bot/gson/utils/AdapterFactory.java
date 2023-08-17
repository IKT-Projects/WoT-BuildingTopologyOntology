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
package org.ict.model.bot.gson.utils;

import org.ict.gson.GsonUtils;
import org.ict.model.bot.core.Building;
import org.ict.model.bot.core.Site;
import org.ict.model.bot.core.Space;
import org.ict.model.bot.core.Storey;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.jsongeo.Geometry;
import org.ict.model.bot.jsongeo.Point;
import org.ict.model.bot.jsongeo.Polygon;
import org.ict.model.jsonld.context.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AdapterFactory {

  public static GsonBuilder getGsonBuilderWithDefaultBotTypeAdapters() {
    RuntimeTypeAdapterFactory<Geometry> geoAdapterFactory = RuntimeTypeAdapterFactory
        .of(Geometry.class, "type").registerSubtype(Point.class, "org.ict.model.bot.jsongeo.Point")
        .registerSubtype(Polygon.class, "org.ict.model.bot.jsongeo.Polygon");
    RuntimeTypeAdapterFactory<Zone> zoneAdapterFactory = RuntimeTypeAdapterFactory
        .of(Zone.class, "type").registerSubtype(Site.class, "org.ict.model.bot.core.Site")
        .registerSubtype(Building.class, "org.ict.model.bot.core.Building")
        .registerSubtype(Storey.class, "org.ict.model.bot.core.Storey")
        .registerSubtype(Space.class, "org.ict.model.bot.core.Space");
    return new GsonBuilder().registerTypeAdapterFactory(geoAdapterFactory)
        .registerTypeAdapterFactory(zoneAdapterFactory)
        .registerTypeAdapter(Context[].class, GsonUtils.getContextSerializer())
        .registerTypeAdapter(Context[].class, GsonUtils.getContextDeserializer());
  }

  public static Gson getGsonWithDefaultBotTypeAdapters(boolean prettyPrint) {
    if (prettyPrint)
      return getGsonBuilderWithDefaultBotTypeAdapters().setPrettyPrinting().create();
    else
      return getGsonBuilderWithDefaultBotTypeAdapters().create();
  }

  public static Gson getGsonWithDefaultBotTypeAdapters() {
    return getGsonWithDefaultBotTypeAdapters(false);
  }
}
