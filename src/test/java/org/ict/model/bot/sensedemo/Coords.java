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
package org.ict.model.bot.sensedemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coords {
  public static final List<Float[]> siteCoords = new ArrayList<>();
  static {
    siteCoords.add(new Float[] {0f, 10f});
    siteCoords.add(new Float[] {600f, 10f});
    siteCoords.add(new Float[] {600f, 300f});
    siteCoords.add(new Float[] {0f, 300f});
  }
  public static final List<Float[]> buildingCoords = new ArrayList<>();
  static {
    buildingCoords.add(new Float[] {10f, 10f});
    buildingCoords.add(new Float[] {450f, 10f});
    buildingCoords.add(new Float[] {450f, 100f});
    buildingCoords.add(new Float[] {450f, 100f});
    buildingCoords.add(new Float[] {450f, 200f});
    buildingCoords.add(new Float[] {10f, 200f});
  }

  public static final List<Float[]> storeyGroundCoords = new ArrayList<>();
  static {
    storeyGroundCoords.add(new Float[] {0f, 0f});
    storeyGroundCoords.add(new Float[] {300f, 0f});
    storeyGroundCoords.add(new Float[] {300f, 190f});
    storeyGroundCoords.add(new Float[] {0f, 190f});
  }

  public static final List<Float[]> storeyFirstCoords = new ArrayList<>();
  static {
    storeyFirstCoords.add(new Float[] {0f, 0f});
    storeyFirstCoords.add(new Float[] {300f, 0f});
    storeyFirstCoords.add(new Float[] {300f, 190f});
    storeyFirstCoords.add(new Float[] {0f, 190f});
  }

  public static final List<Float[]> spaceSenseLabCoords = new ArrayList<>();
  static {
    spaceSenseLabCoords.add(new Float[] {10f, 10f});
    spaceSenseLabCoords.add(new Float[] {100f, 10f});
    spaceSenseLabCoords.add(new Float[] {100f, 130f});
    spaceSenseLabCoords.add(new Float[] {10f, 130f});
  }
  public static final List<Float[]> spaceFhDoLabCoords = new ArrayList<>();
  static {
    spaceFhDoLabCoords.add(new Float[] {110f, 10f});
    spaceFhDoLabCoords.add(new Float[] {430f, 10f});
    spaceFhDoLabCoords.add(new Float[] {430f, 90f});
    spaceFhDoLabCoords.add(new Float[] {110f, 90f});
  }
  public static final List<Float[]> spaceCorridorCoords = new ArrayList<>();
  static {
    spaceCorridorCoords.add(new Float[] {10f, 140f});
    spaceCorridorCoords.add(new Float[] {110f, 140f});
    spaceCorridorCoords.add(new Float[] {110f, 100f});
    spaceCorridorCoords.add(new Float[] {430f, 100f});
    spaceCorridorCoords.add(new Float[] {430f, 180f});
    spaceCorridorCoords.add(new Float[] {10f, 180f});
  }
  public static final Map<String,Float[]> thingCoords = new HashMap<>();
  static {
    thingCoords.put("906f4202b77b42fe", new Float[] {10f, 10f});
    thingCoords.put("81f1d73750e7495e", new Float[] {10f, 50f});
    thingCoords.put("2bf5bf8c8f724adc", new Float[] {290f, 10f});
    thingCoords.put("11a5423995d544c6", new Float[] {290f, 50f});
    thingCoords.put("59b65e47738a439d", new Float[] {150f, 30f});
  }
  
}
