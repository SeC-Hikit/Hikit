package org.sc.processor;

import com.goebl.simplify.PointExtractor;
import com.goebl.simplify.Simplify;
import org.sc.data.model.TrailCoordinates;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TrailSimplifier {

    public static final int POSITIVE_SCALAR = 1000000;
    public static final float SUPER_LOW_Q_TOLERANCE = 50f;
    public static final float LOW_Q_TOLERANCE = 20f;
    public static final float MEDIUM_Q_TOLERANCE = 5f;
    // Douglas-Peucker, false for Radial-Distance
    public static final boolean IS_DOUGLAS_PECKER_ALGORITHM = true;

    public List<TrailCoordinates> simplify(List<TrailCoordinates> allCoordinates,
                                           TrailSimplifierLevel compressionLevel) {

        // create an instance of the simplifier (empty array needed by List.toArray)
        final TrailCoordinates[] trailCoordinates = new TrailCoordinates[allCoordinates.size()];
        final Simplify<TrailCoordinates> simplify = new Simplify<>(new TrailCoordinates[0], latLngPointExtractor);

        // here we have an array with hundreds of points
        //need to convert list to array, since is needed by the pkg
        allCoordinates.toArray(trailCoordinates);

        //float tolerance = 2f;
        float tolerance = 0f;

        if (compressionLevel == TrailSimplifierLevel.LOW) {
            tolerance = SUPER_LOW_Q_TOLERANCE;
        } else if (compressionLevel == TrailSimplifierLevel.MEDIUM) {
            tolerance = LOW_Q_TOLERANCE;
        } else if (compressionLevel == TrailSimplifierLevel.HIGH) {
            tolerance = MEDIUM_Q_TOLERANCE;
        }

        // run simplification process
        final TrailCoordinates[] lessPoints = simplify.simplify(trailCoordinates, tolerance, IS_DOUGLAS_PECKER_ALGORITHM);
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
