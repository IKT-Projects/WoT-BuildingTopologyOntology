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

import java.util.ArrayList;
import java.util.List;
import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.ict.model.bot.jsongeo.CoordXY;

public class Test_FloatList {
  private static RDFBeanManager manager;

  public static void main(String[] args) {
    Repository db = createAndInitDB("http://localhost:8080/rdf4j-server/", "2");
    RepositoryConnection con = setupRepoConnection(db);
    manager = new RDFBeanManager(con);

    List<CoordXY> coords = new ArrayList<>();
    coords.add(new CoordXY(10f, 10f));
    coords.add(new CoordXY(20f, 20f));
    coords.add(new CoordXY(30f, 30f));
    coords.add(new CoordXY(40f, 40f));
    coords.add(new CoordXY(50f, 50f));
    coords.add(new CoordXY(60f, 60f));


    List<String> strings = new ArrayList<>();
    strings.add("hello");
    strings.add("world");
    strings.add("today");
    FloatList fl = FloatList.builder().point(null).coords(coords).build();
    try {
      Resource res = manager.update(fl);

      FloatList newFl = manager.get(res, FloatList.class);
      System.out.println(newFl);
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (RDFBeanException e) {
      e.printStackTrace();
    }
  }

  private static RepositoryConnection setupRepoConnection(Repository db) {
    RepositoryConnection con = db.getConnection();
    con.setNamespace("bot", "https://w3id.org/bot#");
    con.setNamespace("prod", "https://w3id.org/product#");
    con.setNamespace("loc", "https://example.com/ict-gw/v1/location/");
    con.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    con.setNamespace("rdfbeans", "http://viceversatech.com/rdfbeans/2.0/");
    con.setNamespace("wot", "https://www.w3.org/2019/wot/td#");
    con.setNamespace("device", "https://example.com/ict-gw/v1/thing/");
    con.setNamespace("geo", "http://www.opengis.net/ont/geosparql#");
    con.setNamespace("sf", "http://www.opengis.net/ont/sf#");
    con.setNamespace("ict", "https://www.ict.org/common#");
    con.setNamespace("ict", "https://www.ict.org/common#");
    con.setNamespace("geojson", "https://purl.org/geojson/vocab#");
    return con;
  }

  private static Repository createAndInitDB(String url, String repoId) {
    Repository db = new HTTPRepository(url, repoId);
    db.init();
    return db;
  }
}
