package org.sc.util;

import com.goebl.simplify.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GpsReadUtils {

    private static class MyPoint implements Point {
        double x;
        double y;

        private MyPoint(double x, double y) {
            this.x = x;
            this.y = y;
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

    public static Point[] readPoints(String fileName) throws Exception {
        List<MyPoint> pointList = new ArrayList<MyPoint>();
        File file = new File("src/test/resources", fileName);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
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
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return pointList.toArray(new MyPoint[pointList.size()]);
    }
}
