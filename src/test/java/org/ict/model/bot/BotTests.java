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
package org.ict.model.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.ict.model.bot.core.Building;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Site;
import org.ict.model.bot.core.Space;
import org.ict.model.bot.core.Storey;
import org.ict.model.bot.core.Zone;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

public class BotTests {
	private static final String TEST_NS = "http://example.org/bot_test#";
	private static Gson gson;
	private static RDFBeanManager manager;

	@BeforeAll
	static void initAll() {
		gson = new Gson();
		Repository db = new SailRepository(new MemoryStore());
		db.init();
		RepositoryConnection con = db.getConnection();
		// set the default namespace
		con.setNamespace("", "https://w3id.org/bot#");
		// set all other namespaces
		con.setNamespace("ex", "http://example.org/bot_test#");
		con.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		con.setNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		con.setNamespace("rdfbeans", "http://viceversatech.com/rdfbeans/2.0/");
		manager = new RDFBeanManager(con);
	}

	@Test
	void testSimpleBoT() {
		List<Element> elements = createElements();
		List<Element> lights = createLights();

		Space room514 = Space.builder().id(URI.create(TEST_NS + "room514"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Space"))).adjacentElement(elements)
				.containsElement(lights).build();

		Storey storey0 = Storey.builder().id(URI.create(TEST_NS + "storey0"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey1 = Storey.builder().id(URI.create(TEST_NS + "storey1"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey2 = Storey.builder().id(URI.create(TEST_NS + "storey2"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey3 = Storey.builder().id(URI.create(TEST_NS + "storey3"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey4 = Storey.builder().id(URI.create(TEST_NS + "storey4"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey5 = Storey.builder().id(URI.create(TEST_NS + "storey5"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey6 = Storey.builder().id(URI.create(TEST_NS + "storey6"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();
		Storey storey7 = Storey.builder().id(URI.create(TEST_NS + "storey7"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Storey"))).build();

		storey5.setHasSpace(Arrays.asList((Zone) room514));

		List<Zone> storeys = new ArrayList<Zone>();
		storeys.add(storey0);
		storeys.add(storey1);
		storeys.add(storey2);
		storeys.add(storey3);
		storeys.add(storey4);
		storeys.add(storey5);
		storeys.add(storey6);
		storeys.add(storey7);

		Building buildingA = Building.builder().id(URI.create(TEST_NS + "Gebaeude_A"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Building"))).hasStorey(storeys).build();
		Site site = Site.builder().id(URI.create(TEST_NS + "FH_Dortmund"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Site")))
				.containsZone(Arrays.asList((Zone) buildingA)).build();

		System.out.println(site);
		System.out.println(gson.toJson(site));
		try {
			Resource res = manager.add(site);
			Site newSite = manager.get(res, Site.class);
			assertEquals(site, newSite);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	private static List<Element> createLights() {
		Element light1 = Element.builder().id(URI.create(TEST_NS + "light1"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element light2 = Element.builder().id(URI.create(TEST_NS + "light2"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element light3 = Element.builder().id(URI.create(TEST_NS + "light3"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element light4 = Element.builder().id(URI.create(TEST_NS + "light4"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		return Arrays.asList(light1, light2, light3, light4);
	}

	private static List<Element> createElements() {
		Element door_514_516 = Element.builder().id(URI.create(TEST_NS + "door_514_516"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element door_entrance = Element.builder().id(URI.create(TEST_NS + "door_entrance"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element wall_514_516 = Element.builder().id(URI.create(TEST_NS + "wall_514_516"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element wall_514_512 = Element.builder().id(URI.create(TEST_NS + "wall_514_512"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element wall_corridor = Element.builder().id(URI.create(TEST_NS + "wall_corridor"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element wall_external = Element.builder().id(URI.create(TEST_NS + "wall_external"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element window514_left = Element.builder().id(URI.create(TEST_NS + "window514_left"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		Element window514_right = Element.builder().id(URI.create(TEST_NS + "window514_right"))
				.atType(Arrays.asList(URI.create("https://w3id.org/bot#Element"))).build();
		return Arrays.asList(door_514_516, door_entrance, wall_514_516, wall_514_512, wall_corridor, wall_external,
				window514_left, window514_right);
	}
}
