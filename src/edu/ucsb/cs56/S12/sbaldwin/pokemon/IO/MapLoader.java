package edu.ucsb.cs56.S12.sbaldwin.pokemon.IO;

import edu.ucsb.cs56.S12.sbaldwin.pokemon.Assets;
import edu.ucsb.cs56.S12.sbaldwin.pokemon.World;
import edu.ucsb.cs56.S12.sbaldwin.pokemon.components.Entity;
import edu.ucsb.cs56.S12.sbaldwin.pokemon.components.GraphicsComponent;
import edu.ucsb.cs56.S12.sbaldwin.pokemon.components.PositionComponent;
import edu.ucsb.cs56.S12.sbaldwin.pokemon.components.TileComponent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by William Bennett on 7/15/2016.
 */

/* TODO litter this class with comments
Also it's totally jank right now and the only reason I'm doing this is because deadlines
   */
public class MapLoader {
    int[][] tileGrid;
    // Ultimately this class will read the formatting information encoded in the map itself
    // Because of deadlines, I'm hardcoding it right now
    public void loadMap(String inputStream, World world) {
        loadMap(inputStream);
        Entity[][] entities = new Entity[tileGrid.length][tileGrid[0].length];
        for (int x = 0; x < tileGrid.length; x++) {
            for (int y = 0; y < tileGrid[x].length; y++) {
                entities[x][y] = new Entity().
                        addComponent(new TileComponent(tileGrid[x][y])).
                        addComponent(new PositionComponent(x, y)).
                        addComponent(new GraphicsComponent(Assets.grass_Default));
                //world.addEntity(entities[x][y]);
            }
        }
        world.tiles = entities;
    }

    private void loadMap(String inputStream) {
        // This regex matches the map format, as detailed below
        // Parens denote match boundaries
        // (map_name { // must be one word
        // [0, 0, 0], [0, 1, 0]
        // })
        Pattern allMapsRegex = Pattern.compile("([a-zA-Z_]+\\s*\\{(\\s|.)[^\\}]*\\})");
        Matcher matcher = allMapsRegex.matcher(inputStream);
        matcher.find();
        String[] allMaps = {matcher.group()};

        // This regex matches the tile specification for a map, as shown below
        // Parens denote match boundaries
        // map_name { // must be one word
        // ([0, 0, 0]), ([0, 1, 0])
        // }
        Pattern mapRegex = Pattern.compile("\\[(\\d(,|\\s)*)+\\]");

        // This regex matches the metadata of a map, as shown below
        // Parens denote match boundaries
        /*
        map_name {
        (width:5)
        (height:5) // must be one word
        [0, 0, 0], [0, 1, 0]
        }
         */
        Pattern metadataRegex = Pattern.compile("\\w+:\\s*\\d+");

        for (String map : allMaps) {
            int width = 0, height = 0;
            Matcher metadataMatcher = metadataRegex.matcher(map);
            ArrayList<String> metadataStrings = new ArrayList<>();
            while (metadataMatcher.find()) {
                metadataStrings.add(metadataMatcher.group());
            }
            for (String data : metadataStrings) {
                String[] keyValPair = data.split(":");
                switch (keyValPair[0]) {
                    case "width":
                        width = Integer.parseInt(keyValPair[1]);
                        break;
                    case "height":
                        height = Integer.parseInt(keyValPair[1]);
                }
            }
            tileGrid = new int[width][height];
            Matcher tileMatcher = mapRegex.matcher(map);
            ArrayList<String> tileStrings = new ArrayList<>();
            while (tileMatcher.find()) {
                tileStrings.add(tileMatcher.group());
            }

            for (String tile : tileStrings) {
                Pattern integers = Pattern.compile("\\d+");
                Matcher intMatcher = integers.matcher(tile);
                intMatcher.find();
                int ID = Integer.parseInt(intMatcher.group());
                intMatcher.find();
                int x = Integer.parseInt(intMatcher.group());
                intMatcher.find();
                int y = Integer.parseInt(intMatcher.group());
                tileGrid[x][y] = ID;
            }
        }
    }
}
