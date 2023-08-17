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

import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cyberborean.rdfbeans.RDFBeanManager;
import org.cyberborean.rdfbeans.exceptions.RDFBeanException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.ict.gson.GsonUtils;
import org.ict.model.bot.core.Building;
import org.ict.model.bot.core.Element;
import org.ict.model.bot.core.Interface;
import org.ict.model.bot.core.Site;
import org.ict.model.bot.core.Space;
import org.ict.model.bot.core.Storey;
import org.ict.model.bot.core.Zone;
import org.ict.model.bot.geo.AxisAngle;
import org.ict.model.bot.geo.Vector3D;
import org.ict.model.bot.geo.View3D;
import org.ict.model.bot.jsongeo.Polygon;
import org.ict.model.jsonld.context.Context;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.util.JSON;

public class Sense_T {
	private static final String BASE = "http://localhost:8092/v1";
	private static final String SITES = "/sites";
	private static final String BUILDINGS = "/buildings";
	private static final String STOREYS = "/storeys";
	private static final String SPACES = "/spaces";
	private static final String INTERFACES = "/interfaces";

	private static final String SITE_VIEWNAME = "Sense Demo Site v0.1";
	private static final String BUILDING_VIEWNAME = "Virtual Building";
	private static final String STOREY_VIEWNAME_FIRST = "First floor";
	private static final String STOREY_VIEWNAME_GROUND = "Ground floor";
	private static final String SPACE_VIEWNAME_FH_LAB = "FH Dortmund Lab";
	private static final String SPACE_VIEWNAME_SENSE_LAB = "Sense Lab";
	private static final String SPACE_VIEWNAME_Stairwell = "Flur";

	private static final String SITE_NAME = "/SenseDemo_Site";
	private static final String BUILDING_NAME = "/VirtualBuilding";
	private static final String STOREY_NAME_FIRST = "/FirstFloor";
	private static final String STOREY_NAME_GROUND = "/GroundFloor";
	private static final String SPACE_NAME_FH_LAB = "/FH_Do_Lab";
	private static final String SPACE_NAME_SENSE_LAB = "/Sense_Lab";
	private static final String SPACE_NAME_Stairwell = "/Corridor";

	private static final String baseSite = BASE + SITES + SITE_NAME;
	private static final String baseBuilding = baseSite + BUILDINGS + BUILDING_NAME;
	private static final String baseStorey_FIRST = baseBuilding + STOREYS + STOREY_NAME_FIRST;
	private static final String baseStorey_GROUND = baseBuilding + STOREYS + STOREY_NAME_GROUND;
	private static final String base_FH_LAB = baseStorey_FIRST + SPACES + SPACE_NAME_FH_LAB;
	private static final String base_SENSE_LAB = baseStorey_FIRST + SPACES + SPACE_NAME_SENSE_LAB;
	private static final String baseCorridor = BASE + SITES + SITE_NAME + BUILDINGS + BUILDING_NAME + STOREYS
			+ STOREY_NAME_FIRST + SPACES + SPACE_NAME_Stairwell;

	private static RDFBeanManager manager;
	private static Gson gson = new Gson().newBuilder()
			.registerTypeAdapter(Context[].class, GsonUtils.getContextSerializer())
			.registerTypeAdapter(Context[].class, GsonUtils.getContextDeserializer()).create();

	private static MongoClient mongoClient;
	private static DB database;
	private static DBCollection collection;

	public static void main(String[] args) throws UnknownHostException {
		try {
			/**
			 * Optional: Configure MongoDB and/or TripleStore instances
			 */
			//mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
			//database = mongoClient.getDB("WoT");
			//collection = database.getCollection("LocationData");

			//Repository db = createAndInitDB("http://localhost:8080/rdf4j-server/", "2");
			//RepositoryConnection con = setupRepoConnection(db);
			//manager = new RDFBeanManager(con);

			Context[] contexts = createContext();

			List<Element> room1_elements = createRoom1Elements();
			List<Element> stairwell_elements = createStairwellElements();
			List<Element> iot = createIoTElements(Arrays.asList(URI.create(BASE + "/things/906f4202b77b42fe"), // Bewegung
					URI.create(BASE + "/things/81f1d73750e7495e"), // Dimmer
					URI.create(BASE + "/things/2bf5bf8c8f724adc"), // Wetter
					URI.create(BASE + "/things/11a5423995d544c6"), // Jalousie
					URI.create(BASE + "/things/59b65e47738a439d"))); // Licht

			// filter for east wall -> needed for interfaces
			Element wall_east = room1_elements.stream()
					.filter(e -> e.getId().toString().equals(base_FH_LAB + "/wall_east")).findFirst().get();
			// add the connecting eastern wall to stairwell element list
			stairwell_elements.add(wall_east);

			// filter common wall to add them aswell (north and south wall)
			Element north = room1_elements.stream()
					.filter(e -> e.getId().toString().equals(base_FH_LAB + "/wall_north")).findFirst().get();
			Element south = room1_elements.stream()
					.filter(e -> e.getId().toString().equals(base_FH_LAB + "/wall_south")).findFirst().get();
			stairwell_elements.add(north);
			stairwell_elements.add(south);

			// filter for the door element because both space adjacent the same door
			Element door = room1_elements.stream().filter(e -> e.getId().toString().equals(base_FH_LAB + "/door1"))
					.findFirst().get();
			stairwell_elements.add(door);

			Space FHDoLab = Space.builder().id(URI.create(base_FH_LAB)).name(SPACE_VIEWNAME_FH_LAB)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.spaceFhDoLabCoords).build())
					.atType(Arrays.asList(URI.create("bot:Space"))).adjacentElement(room1_elements).hasElement(iot)
					.build();

			Space SenseLab = Space.builder().id(URI.create(base_SENSE_LAB)).name(SPACE_VIEWNAME_SENSE_LAB)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.spaceSenseLabCoords).build())
					.atType(Arrays.asList(URI.create("bot:Space"))).adjacentElement(room1_elements).build();

			Space corridor = Space.builder().id(URI.create(baseCorridor)).name(SPACE_VIEWNAME_Stairwell)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.spaceCorridorCoords).build())
					.atType(Arrays.asList(URI.create("bot:Space"))).adjacentElement(stairwell_elements).build();

			Storey groundfloor = Storey.builder().id(URI.create(baseStorey_GROUND)).name(STOREY_VIEWNAME_GROUND)
					.floorLevel("0")
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.storeyGroundCoords).build())
					.atType(Arrays.asList(URI.create("bot:Storey"))).build();

			Storey firstfloor = Storey.builder().id(URI.create(baseStorey_FIRST)).name(STOREY_VIEWNAME_FIRST)
					.floorLevel("1")
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.storeyFirstCoords).build())
					.atType(Arrays.asList(URI.create("bot:Storey")))
					.hasSpace(Arrays.asList(FHDoLab, SenseLab, corridor)).build();

			List<Zone> storeys = new ArrayList<Zone>();
			storeys.add(groundfloor);
			storeys.add(firstfloor);

			Building buildingA = Building.builder().id(URI.create(baseBuilding)).name(BUILDING_VIEWNAME)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.buildingCoords).build())
					.atType(Arrays.asList(URI.create("bot:Building"))).hasStorey(storeys).build();
			Site site = Site.builder().context(contexts).id(URI.create(baseSite)).name(SITE_VIEWNAME)
					.geometry(Polygon.builder().coordinates(Coords.siteCoords)
							.atType(Arrays.asList(URI.create("geo:Polygon"))).build())
					.atType(Arrays.asList(URI.create("bot:Site"))).hasBuilding(Arrays.asList(buildingA)).build();

			System.out.println(site);
			System.out.println(gson.toJson(site));
			/**
			 * Get the RDF4j model and write it in different formats
			 */
			Model model = site.getModel();
			// iterate over every statement in the Model
			System.out.println("Triples:");
			for (Statement statement : model) {
				System.out.println(statement);
			}
			// Rio.write(model, System.out, RDFFormat.JSONLD);
			// Rio.write(model, System.out, RDFFormat.TURTLE);
			/**
			 * Write the BoT model to a RDF4j TripleStore
			 */
			// Resource siteRes = writeToTribleStore(site, iface1, iface2);
			/**
			 * Store BoT model as JSON document in MongoDB
			 */
			// writeToMongoDb(site);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void writeToMongoDb(Site site) {
		try {
			DBObject dbObject = (DBObject) JSON.parse(gson.toJson(site));
			dbObject.put("_id", site.getId().toString());
			collection.insert(dbObject);
		} catch (Throwable th) {
			System.out.printf("Unable to store location '%s' in mongo db.", site.getId());
			System.out.printf(th.getMessage());
			th.printStackTrace();
		}
	}

	private static Resource writeToTribleStore(Site site, Interface iface1, Interface iface2)
			throws RepositoryException, RDFBeanException {
		Resource siteRes = manager.add(site);
		manager.add(iface1);
		manager.add(iface2);
		Site site2 = manager.get(siteRes, Site.class);
		System.out.println(site2);
		return siteRes;
	}

	private static Context[] createContext() {
		return new Context[] { Context.builder().prefix(null).namespace("https://schema.org/").build(),
				Context.builder().prefix("bot").namespace("https://w3id.org/bot#").build(),
				Context.builder().prefix("rdf").namespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#").build(),
				Context.builder().prefix("wot").namespace("https://www.w3.org/2019/wot/td#").build(),
				Context.builder().prefix("prod").namespace("https://w3id.org/product#").build(),
				Context.builder().prefix("geo").namespace("https://purl.org/geojson/vocab#").build() };
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
		return con;
	}

	private static Repository createAndInitDB(String url, String repoId) {
		Repository db = new HTTPRepository(url, repoId);
		db.init();
		return db;
	}

	private static List<Element> createIoTElements(List<URI> ids) {
		List<Element> things = new ArrayList<>(ids.size());
		// String wktFormatter = "POINT(%f %f)";
		// float f1 = 50.0f;
		// float f2 = 72.0f;
		for (URI uri : ids) {
			View3D view = View3D.builder().description("3D view of the the thing.")
					.vector(Vector3D.builder().xaxis(200.5f).yaxis(87.0f).zaxis(120.7f).build())
					.axisAngle(AxisAngle.builder().axis(Vector3D.builder().xaxis(5.0f).yaxis(3.0f).zaxis(7.0f).build())
							.angle(35.6f).build())
					.build();
			String path = uri.getPath();
			String id = path.substring(path.lastIndexOf("/") + 1);
			org.ict.model.bot.jsongeo.Point p = org.ict.model.bot.jsongeo.Point.builder()
					.atType(Arrays.asList(URI.create("geo:Point"))).coordinates(Coords.thingCoords.get(id)).build();
			Element e = Element.builder().id(uri)
					.atType(Arrays.asList(URI.create("bot:Element"), URI.create("wot:Thing"))).geometry(p).build();
			things.add(e);
//      f1 += 5;
//      f2 += 5;
		}
		return things;
	}

	private static List<Element> createRoom1Elements() {

		Element door_1 = Element.builder().id(URI.create(base_FH_LAB + "/door1"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Door"))).build();
		Element wall_east = Element.builder().id(URI.create(base_FH_LAB + "/wall_east"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Wall"))).build();
		Element wall_west = Element.builder().id(URI.create(base_FH_LAB + "/wall_west"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Wall"))).build();

		// add 2 windows for the northern wall
		Element window_left1 = Element.builder().id(URI.create(base_FH_LAB + "/window_left_wn"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element window_right1 = Element.builder().id(URI.create(base_FH_LAB + "/window_rigth_wn"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element wall_north = Element.builder().id(URI.create(base_FH_LAB + "/wall_north"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Wall")))
				.hasSubElement(Arrays.asList(window_left1, window_right1)).build();

		// add 2 windows for the southern wall
		Element window_left2 = Element.builder().id(URI.create(base_FH_LAB + "/window_left_ws"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element window_right2 = Element.builder().id(URI.create(base_FH_LAB + "/window_rigth_ws"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element wall_south = Element.builder().id(URI.create(base_FH_LAB + "/wall_south"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Wall")))
				.hasSubElement(Arrays.asList(window_left2, window_right2)).build();

		return new ArrayList<Element>(Arrays.asList(door_1, wall_east, wall_west, wall_north, wall_south));
	}

	private static List<Element> createStairwellElements() {
		// add 2 windows for the northern wall
		Element window_left3 = Element.builder().id(URI.create(baseCorridor + "/window_left_weo"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element window_right3 = Element.builder().id(URI.create(baseCorridor + "/window_rigth_weo"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Window"))).build();
		Element wall_east_out = Element.builder().id(URI.create(baseCorridor + "/wall_east_out"))
				.atType(Arrays.asList(URI.create("bot:Element"), URI.create("prod:Wall")))
				.hasSubElement(Arrays.asList(window_left3, window_right3)).build();

		return new ArrayList<Element>(Arrays.asList(wall_east_out));
	}
}
