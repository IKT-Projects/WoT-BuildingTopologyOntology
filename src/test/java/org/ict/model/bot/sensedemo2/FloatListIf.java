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
package org.ict.model.bot.sensedemo2;

import java.util.List;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.ict.model.bot.jsongeo.CoordXY;

@RDFBean("https://test.org#Floatlist")
public interface FloatListIf {
  @RDF("https://test.org#hasCoordinates")
  List<CoordXY> getCoords();
  
  @RDF("https://test.org#hasPoint")
  Float[] getPoint();
  
  @RDF("https://test.org#hasStrings")
  List<String> getStrings();
}
