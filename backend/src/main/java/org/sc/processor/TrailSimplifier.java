package org.sc.processor;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import org.sc.data.model.TrailCoordinates;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.sc.processor.TrailSimplifierLevel.LOW;

public class TrailSimplifier {

    public static final int POSITIVE_SCALAR = 1000000;

    public List<TrailCoordinates> simplify(List<TrailCoordinates> allcoordinates,
                                           TrailSimplifierLevel compressionLevel){

        // create an instance of the simplifier (empty array needed by List.toArray)
        TrailCoordinates[] trailCoordinates = new TrailCoordinates[allcoordinates.size()];
        Simplify<TrailCoordinates> simplify = new Simplify<>(new TrailCoordinates[0], latLngPointExtractor);

        // here we have an array with hundreds of points
        //need to convert list to array, since is needed by the pkg
        allcoordinates.toArray(trailCoordinates);

        //float tolerance = 2f;
        float tolerance = 0f;

        if( compressionLevel == TrailSimplifierLevel.SUPER_LOW) {
            tolerance = 50f;
        } else if( compressionLevel == TrailSimplifierLevel.LOW) {
            tolerance = 20f;
        } else if( compressionLevel == TrailSimplifierLevel.MEDIUM) {
           tolerance = 5f;
        }




        boolean highQuality = true; // Douglas-Peucker, false for Radial-Distance

        // run simplification process
        //TrailCoordinates[] lessPoints = simplify.simplify(trailCoordinates, tolerance, highQuality);

        TrailCoordinates[] lessPoints = simplify.simplify(trailCoordinates, tolerance, highQuality);
        return Arrays.stream(lessPoints).filter(Objects::nonNull).collect(Collectors.toList());
    }
    private static final PointExtractor<TrailCoordinates> latLngPointExtractor = new PointExtractor<TrailCoordinates>() {
        @Override
        public double getX(TrailCoordinates trailCoordinates) {
            return trailCoordinates.getLongitude() * POSITIVE_SCALAR;
        }

        @Override
        public double getY(TrailCoordinates trailCoordinates) {
            return trailCoordinates.getLatitude() * POSITIVE_SCALAR;
        }

    };



}