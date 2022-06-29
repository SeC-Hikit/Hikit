package org.sc.util;

import com.goebl.simplify.Point;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class GpsReadUtils {

    private static class MyPoint implements Point {
        double x;
        double y;

        private MyPoint(double lat, double lon) {
            this.x = lat;
            this.y = lon;
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public String toString() {
            return "{" + "x=" + x + ", y=" + y + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPoint myPoint = (MyPoint) o;

            if (Double.compare(myPoint.x, x) != 0) return false;
            if (Double.compare(myPoint.y, y) != 0) return false;

            return true;
        }

    }

    public static Point[] readGpxPoints(String filename) throws IOException {
        GPX read = GPX.read(Path.of("src/test/resources" + filename));
        Track trackSegments = read.getTracks().get(0);
        TrackSegment segment = trackSegments.getSegments().get(0);
        return segment.getPoints().stream().map(t -> new MyPoint(t.getLatitude().doubleValue(),
                t.getLongitude().doubleValue())).toArray(MyPoint[]::new);
    }

    public static Point[] readPoints(String fileName) throws Exception {
        List<MyPoint> pointList = new ArrayList<MyPoint>();
        File file = new File("src/test/resources", fileName);

        try (InputStream is = new FileInputStream(file)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String[] xy = line.split(",");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                pointList.add(new MyPoint(x, y));
            }
        }
        return pointList.toArray(new MyPoint[pointList.size()]);
    }
}
