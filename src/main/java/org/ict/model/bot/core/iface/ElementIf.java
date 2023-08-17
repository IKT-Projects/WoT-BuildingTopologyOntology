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
import org.ict.model.bot.geo.View3D;

@RDFBean("https://w3id.org/bot#Element")
public interface ElementIf {
  @RDFSubject
  String getIdRDF();

  @RDF("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
  List<URI> getAtTypeRDF();

  void setAtTypeRDF(List<URI> types);

  @RDF("https://w3id.org/bot#hasSubElement")
  List<Element> getHasSubElement();

  @RDF("https://www.ict.org/common#hasView3D")
  View3D getView3D();
}
