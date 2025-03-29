# Geo Data Converter Documentation

## Project Overview
<!-- Plugin description -->
The Geo Data Converter is a versatile utility for JetBrains IDEs that simplifies working with various geospatial data formats. This tool enables seamless conversion between geographic coordinates, H3 indices, and GeoJSON formats, making it an essential companion for developers working with location-based data and spatial analysis.
<!-- Plugin description end -->
## Key Features

1. **Coordinates → H3**: Convert latitude/longitude coordinates to Uber's H3 geospatial indexing system
2. **H3 → Coordinates**: Convert H3 indices back to standard latitude/longitude coordinates
3. **Map Marker**: Plot location pins on an interactive map by entering coordinates
4. **GeoJSON Generator**: Create GeoJSON objects from coordinates
5. **GeoJSON Visualizer**: Visualize GeoJSON data directly on the interactive map

## Installation

1. Search for "Geo Data Converter" in the JetBrains Marketplace
2. Click the "Install" button to install the plugin
3. Restart your IDE
4. After installation, access the tool by clicking on the "Geo Tool" tab in the right panel of your IDE

## Usage Guide

### 1. Coordinates → H3 Conversion

This feature transforms standard geographic coordinates into Uber's H3 geospatial index.

#### How to use:
1. Select the "Coord → H3" tab
2. Enter the latitude/longitude coordinates in the "Coord (lat, lng)" input field (e.g., `37.4864760734468, 127.02473584702024`)
3. Select the desired resolution (0-15) from the dropdown menu
- Resolution determines the precision of the H3 cell (higher values = smaller cells)
- Default resolution is 8
4. Click the "Convert to H3 Index" button
5. View the result in the output area below

#### Example:
- Input: `37.4864760734468, 127.02473584702024` with resolution 8
- Output: H3 index `885832952a9ffff`

#### Additional Operations:
- **Copy Result**: Click the "Copy to clipboard" button to copy the H3 index
- **View in Web**: Click the "View in web" button to visualize the location on h3geo.org

### 2. H3 → Coordinates Conversion

This feature decodes an H3 index back to standard geographic coordinates.

#### How to use:
1. Select the "H3 → Coord" tab
2. Enter the H3 index in the "H3 Index" input field (e.g., `885832952a9ffff`)
3. Click the "Convert to Coord" button
4. View the coordinates in the output area below

#### Example:
- Input: `885832952a9ffff`
- Output: Coordinates `37.4864760734468, 127.02473584702024`

#### Additional Operations:
- **Copy Result**: Click the "Copy to clipboard" button to copy the coordinates
- **View in Web**: Click the "View in web" button to search for these coordinates on Google

### 3. Map Marker

This feature allows you to plot location pins on an interactive map by simply entering coordinates.

#### How to use:
1. Select the "Map Marker" tab
2. Enter the latitude/longitude coordinates in the "Coord (lat, lng)" input field (e.g., `37.4864760734468, 127.02473584702024`)
3. The map will automatically center on the location and display a pin at the coordinates

#### Additional Features:
- **Zoom Controls**: Adjust map zoom level with the "+" and "-" buttons
- **Copy Coordinates**: Click on any pin to view and copy its exact coordinates

#### Example:
- Input: `37.4864760734468, 127.02473584702024`
- Result: The map centers on Seoul, South Korea with a marker at the specified location

### 4. GeoJSON Generator

This feature allows you to interactively create GeoJSON objects by drawing shapes directly on the map.

#### How to use:
1. Select the "GeoJSON Generator" tab
2. Use the interactive map interface to draw shapes:
    - Select a drawing tool from the toolbar (Point, Line, Polygon, Rectangle, Circle)
    - Click and drag on the map to create your shapes
    - For polygons and lines, click multiple times to add vertices and double-click to complete
3. Edit shapes as needed:
    - Select a shape to move or resize it
    - Use the vertex edit tool to adjust specific points
    - Delete unwanted shapes using the delete button
4. The GeoJSON code updates automatically in real-time as you draw
5. View the generated GeoJSON in the output area below the map

#### Supported Shape Tools:
- **Point Marker**: Create individual location points
- **Line**: Draw connected line segments
- **Polygon**: Create enclosed areas with any number of vertices
- **Rectangle**: Quickly draw rectangular areas
- **Circle**: Create circular areas with customizable radius

#### Example:
- Action: Draw a polygon around an area in Seoul
- Output:
  ```json
  {
    "type": "Feature",
    "geometry": {
      "type": "Polygon",
      "coordinates": [
        [
          [127.02473584702024, 37.4864760734468],
          [127.02699389892358, 37.4892761278432],
          [127.02801294256698, 37.4852759011456],
          [127.02473584702024, 37.4864760734468]
        ]
      ]
    },
    "properties": {}
  }
  ```

#### Additional Operations:
- **Copy Result**: Click the "Copy to clipboard" button to copy the GeoJSON
- **Property Editor**: Add custom properties to your GeoJSON features

### 5. GeoJSON Visualizer

This feature allows you to visualize GeoJSON data directly on the interactive map.

#### How to use:
1. Select the "GeoJSON Visualizer" tab
2. Paste valid GeoJSON data into the input field
3. The map will automatically display the GeoJSON objects with appropriate styling

#### Supported GeoJSON types:
- Point
- MultiPoint
- LineString
- MultiLineString
- Polygon
- MultiPolygon
- GeometryCollection
- FeatureCollection

#### Example:
- Input:
  ```json
  {
    "type": "FeatureCollection",
    "features": [
      {
        "type": "Feature",
        "geometry": {
          "type": "Point",
          "coordinates": [127.02473584702024, 37.4864760734468]
        },
        "properties": {
          "name": "Location A"
        }
      },
      {
        "type": "Feature",
        "geometry": {
          "type": "LineString",
          "coordinates": [
            [127.02473584702024, 37.4864760734468],
            [127.02699389892358, 37.4892761278432]
          ]
        },
        "properties": {
          "name": "Path B"
        }
      }
    ]
  }
  ```
- Result: The map displays a point at Location A and a line connecting to Path B

## Supported Format Specifications

### Geographic Coordinates
- Format: `latitude, longitude`
- Spaces around the comma are optional
- Valid latitude range: -90 to 90 degrees
- Valid longitude range: -180 to 180 degrees

### H3 Geospatial Index
- Format: String of hexadecimal characters (e.g., `885832952a9ffff`)
- Resolution range: 0 (largest cells) to 15 (smallest cells)
- Based on Uber's H3 hierarchical geospatial indexing system

### GeoJSON
- Conforms to the GeoJSON specification (RFC 7946)
- Supported object types: Feature, FeatureCollection
- Supported geometry types: Point, MultiPoint, LineString, MultiLineString, Polygon, MultiPolygon, GeometryCollection
- Coordinate order: [longitude, latitude] as per GeoJSON specification

## Use Cases

The Geo Data Converter is valuable for:

- **GIS Development**: Converting between different geospatial formats
- **Location-Based Services**: Working with coordinates and spatial indices
- **Geospatial Analysis**: Preparing data for spatial processing
- **Database Operations**: Converting data for storage in spatial databases
- **Map Visualization**: Preparing coordinates for mapping applications
- **GeoJSON Creation**: Generating GeoJSON for web mapping applications
- **Spatial Data Visualization**: Inspecting complex geographic data visually

## Error Handling

The tool provides comprehensive error messages for common issues:

- Invalid coordinate format
- Coordinates out of valid range
- Invalid H3 index
- Conversion errors
- Invalid GeoJSON structure
- Unsupported GeoJSON types

If an error occurs, check the error message in the output area for guidance.

## Technical Details

The tool is built using:
- H3 Core library for hierarchical geospatial indexing
- OpenStreetMap
- Swing UI components for the interface
- GeoJSON parsing and validation libraries

## Credits

The SWING framework was crafted by Eric.