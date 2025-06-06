package com.mrdotxin.propsmart.model.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * GeoPoint类用于表示几何数据点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoPoint implements Serializable {
    private double x; // 经度
    private double y; // 纬度

    private static final long serialVersionUID = 1L;

    /**
     * 从WKT(Well-Known Text)格式创建GeoPoint对象
     * @param wkt WKT格式字符串(例如"POINT(120.123456 30.123456)")
     * @return GeoPoint对象
     */
    public static GeoPoint fromWkt(String wkt) {
        if (wkt == null || wkt.isEmpty()) {
            return null;
        }

        // 简单的WKT POINT解析器
        String point = wkt.trim().toUpperCase();
        if (point.startsWith("POINT(") && point.endsWith(")")) {
            String coordinates = point.substring(6, point.length() - 1);
            String[] parts = coordinates.trim().split("\\s+");
            if (parts.length == 2) {
                try {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    return new GeoPoint(x, y);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 转换为WKT格式
     * @return WKT格式字符串
     */
    public String toWkt() {
        return String.format("POINT(%f %f)", x, y);
    }

    /**
     * 从点列表创建多边形WKT字符串
     * @param points 构成多边形的点列表
     * @return 多边形的WKT格式字符串
     */
    public static String createPolygonWkt(List<GeoPoint> points) {
        if (points == null || points.size() < 3) {
            throw new IllegalArgumentException("多边形至少需要3个点");
        }

        StringBuilder sb = new StringBuilder("POLYGON((");
        for (int i = 0; i < points.size(); i++) {
            GeoPoint point = points.get(i);
            sb.append(point.getX()).append(" ").append(point.getY());
            if (i < points.size() - 1) {
                sb.append(",");
            }
        }

        // 通过重复第一个点来闭合多边形
        GeoPoint firstPoint = points.get(0);
        if (!points.get(points.size() - 1).equals(firstPoint)) {
            sb.append(",").append(firstPoint.getX()).append(" ").append(firstPoint.getY());
        }

        sb.append("))");
        return sb.toString();
    }

    /**
     * 计算两点之间的距离(使用Haversine公式)
     * @param other 另一个点
     * @return 距离(单位：米)
     */
    public double distanceTo(GeoPoint other) {
        final int R = 6371; // 地球半径(单位：千米)

        double latDistance = Math.toRadians(other.getY() - this.y);
        double lonDistance = Math.toRadians(other.getX() - this.x);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.y)) * Math.cos(Math.toRadians(other.getY()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 转换为米
        return R * c * 1000;
    }

    /**
     * 检查点是否在多边形内部
     * 使用射线法 (Ray Casting Algorithm)
     * @param point 待检查的点
     * @param polygon 多边形的点列表
     * @return 如果点在多边形内部或边界上，则返回true；否则返回false
     */
    public static boolean isPointInPolygon(GeoPoint point, List<GeoPoint> polygon) {
        if (polygon == null || polygon.size() < 3) {
            return false;
        }

        int numVertices = polygon.size();
        double x = point.getX();
        double y = point.getY();
        boolean inside = false;

        // 存储多边形的第一个点
        GeoPoint p1 = polygon.get(0);
        GeoPoint p2;

        // 遍历多边形的每条边
        for (int i = 1; i <= numVertices; i++) {
            // 获取多边形的下一个点
            p2 = polygon.get(i % numVertices);

            // 检查点是否在边的最小y坐标之上
            if (y > Math.min(p1.getY(), p2.getY())) {
                // 检查点是否在边的最大y坐标之下
                if (y <= Math.max(p1.getY(), p2.getY())) {
                    // 检查点是否在边的最大x坐标之左
                    if (x <= Math.max(p1.getX(), p2.getX())) {
                        // 计算连接点到边的线的x交点
                        double xIntersection = (y - p1.getY()) * (p2.getX() - p1.getX()) 
                                / (p2.getY() - p1.getY()) + p1.getX();

                        // 检查点是否在边上或在x交点的左侧
                        if (p1.getX() == p2.getX() || x <= xIntersection) {
                            // 翻转inside标志
                            inside = !inside;
                        }
                    }
                }
            }

            // 将当前点存储为下一次迭代的第一个点
            p1 = p2;
        }

        return inside;
    }

    /**
     * 检查当前点是否在多边形内部
     * @param polygon 多边形的点列表
     * @return 如果当前点在多边形内部或边界上，则返回true；否则返回false
     */
    public boolean isInsidePolygon(List<GeoPoint> polygon) {
        return isPointInPolygon(this, polygon);
    }
}