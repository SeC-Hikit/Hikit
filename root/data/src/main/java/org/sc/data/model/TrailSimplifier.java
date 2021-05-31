package org.sc.data.model;

import com.goebl.simplify.Point;
import com.goebl.simplify.Simplify;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TrailSimplifier {

    public List<TrailCoordinates>  simplify(List<TrailCoordinates> allcoordinates){

        // create an instance of the simplifier (empty array needed by List.toArray)
        TrailCoordinates[] trailCoordinates = new TrailCoordinates[allcoordinates.size()];
        Simplify<Point> simplify = new Simplify<Point>(trailCoordinates);

        // here we have an array with hundreds of points
        //need to convert list to array, since is needed by the pkg
        allcoordinates.toArray(trailCoordinates);
        // Point[] allPoints = trailCoordinates;
        double tolerance = 1.5d;
        boolean highQuality = true; // Douglas-Peucker, false for Radial-Distance

        // run simplification process
        Point[] lessPoints = simplify.simplify(trailCoordinates, tolerance, highQuality);
        return Arrays.stream(lessPoints).filter(Objects::nonNull).map(point -> (TrailCoordinates) point).collect(Collectors.toList());
    }


}
