[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/built-by-developers.svg)](https://forthebadge.com)
[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)

# A implementation of the Building Topology Ontology (BOT) (https://w3c-lbd-cg.github.io/bot/)

### Introduction
This project offers a Java class based implementation of the Building Topology Ontology (BOT).
BOT descriptions are created with corresponding class builders. The resulting site-class can be serialized to a JSON-LD document or stored
in a RDF4j TripleStore.

### Notice
This project was created in the research project SENSE and contains experimental implementations and therefore does not claim to be complete 
or bug-free. The use is at your own risk. Any legal claim is excluded. 

### Basic usage example

First create basic elements located in a room/space. All elements of a room can be group as a list of elements.

```java
 Element door = room1_elements.stream().filter(e -> e.getId().toString().equals(base_FH_LAB + "/door1"))
					.findFirst().get();
 room1_elements.add(door);
```

In the next step you can add these elements to a space or in general every subclass of a site.

```java
Space FHDoLab = Space.builder().id(URI.create(base_FH_LAB)).name(SPACE_VIEWNAME_FH_LAB)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.spaceFhDoLabCoords).build())
					.atType(Arrays.asList(URI.create("bot:Space"))).adjacentElement(room1_elements).hasElement(iot)
					.build();
```

Spaces can now optionally be added to a storey.

```java
Storey firstfloor = Storey.builder().id(URI.create(baseStorey_FIRST)).name(STOREY_VIEWNAME_FIRST)
					.floorLevel("1")
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.storeyFirstCoords).build())
					.atType(Arrays.asList(URI.create("bot:Storey")))
					.hasSpace(Arrays.asList(FHDoLab, SenseLab, corridor)).build();
```

Stories are now attached to a building.

```java
Building buildingA = Building.builder().id(URI.create(baseBuilding)).name(BUILDING_VIEWNAME)
					.geometry(Polygon.builder().atType(Arrays.asList(URI.create("geo:Polygon")))
							.coordinates(Coords.buildingCoords).build())
					.atType(Arrays.asList(URI.create("bot:Building"))).hasStorey(storeys).build();
```

Finally a building is located on a site.

```java
Site site = Site.builder().context(contexts).id(URI.create(baseSite)).name(SITE_VIEWNAME)
					.geometry(Polygon.builder().coordinates(Coords.siteCoords)
							.atType(Arrays.asList(URI.create("geo:Polygon"))).build())
					.atType(Arrays.asList(URI.create("bot:Site"))).hasBuilding(Arrays.asList(buildingA)).build();
```

## License
Apache License

_Version 2.0, January 2004_  
http://www.apache.org/licenses/