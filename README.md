# Geo Data Converter Documentation

## Project Overview
<!-- Plugin description -->
The Geo Data Converter is a versatile utility for JetBrains IDEs that simplifies working with various geospatial data formats. This tool enables seamless conversion between geographic coordinates, H3 indices, and making it an essential companion for developers working with location-based data and spatial analysis.
<!-- Plugin description end -->
## Key Features

1. **Coordinates → H3**: Convert latitude/longitude coordinates to Uber's H3 geospatial indexing system
2. **H3 → Coordinates**: Convert H3 indices back to standard latitude/longitude coordinates
3. **Map Marker**: Plot location pins on an interactive map by entering coordinates

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

## Use Cases

The Geo Data Converter is valuable for:

- **GIS Development**: Converting between different geospatial formats
- **Location-Based Services**: Working with coordinates and spatial indices
- **Geospatial Analysis**: Preparing data for spatial processing
- **Database Operations**: Converting data for storage in spatial databases
- **Map Visualization**: Preparing coordinates for mapping applications

## Error Handling

The tool provides comprehensive error messages for common issues:

- Invalid coordinate format
- Coordinates out of valid range
- Invalid H3 index
- Conversion errors

If an error occurs, check the error message in the output area for guidance.

## Technical Details

The tool is built using:
- H3 Core library for hierarchical geospatial indexing
- OpenStreetMap
- Swing UI components for the interface

## Credits

The SWING framework was crafted by Eric.