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
package org.ict.model.bot.core.iface;

import java.net.URI;
import java.util.List;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFSubject;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.geo.Coord2D;
import org.ict.model.bot.jsongeo.Geometry;

@RDFBean("https://w3id.org/bot#Zone")
public interface ZoneIf {
  @RDFSubject
  String getIdRDF();
  
  @RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
  List<URI> getAtTypeRDF();
  
  void setAtTypeRDF(List<URI> types);
  
  @RDF("https://schema.org/name")
  String getName();
  
  @RDF("https://w3id.org/bot#containsZone")
  List<Zone> getContainsZone();

  @RDF("https://w3id.org/bot#hasBuilding")
  List<Zone> getHasBuilding();

  @RDF("https://w3id.org/bot#hasStorey")
  List<Zone> getHasStorey();

  @RDF("https://w3id.org/bot#hasSpace")
  List<Zone> getHasSpace();

  @RDF("https://w3id.org/bot#adjacentZone")
  List<Zone> getAdjacentZone();

  @RDF("https://w3id.org/bot#intersectsZone")
  List<Zone> getIntersectsZone();

  @RDF("https://w3id.org/bot#hasElement")
  List<Element> getHasElement();

  @RDF("https://w3id.org/bot#containsElement")
  List<Element> getContainsElement();

  @RDF("https://w3id.org/bot#adjacentElement")
  List<Element> getAdjacentElement();

  @RDF("https://w3id.org/bot#intersectingElement")
  List<Element> getIntersectingElement();
  
  @RDF("https://www.ict.org/common#hasArea")
  List<Coord2D> getArea();
  
  @RDF("https://purl.org/geojson/vocab#geometry")
  Geometry getGeometry();
}
