package com.mrdotxin.propsmart.utils;

import com.mrdotxin.propsmart.model.enums.GeoCoordinationEnum;
import com.mrdotxin.propsmart.model.geo.GeoPoint;
import org.osgeo.proj4j.*;

public class GeoUtil {

    private static final CRSFactory crsFactory = new CRSFactory();
    private static final CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

    public static GeoPoint convertCoordinate(GeoPoint geoPoint, GeoCoordinationEnum source, GeoCoordinationEnum target) {
        CoordinateReferenceSystem targetSys = crsFactory.createFromName(target.getValue());
        CoordinateReferenceSystem sourceSys = crsFactory.createFromName(source.getValue());

        CoordinateTransform transform = ctFactory.createTransform(sourceSys, targetSys);

        ProjCoordinate sourceProj = new ProjCoordinate(geoPoint.getX(), geoPoint.getY());
        ProjCoordinate targetProj = new ProjCoordinate();

        transform.transform(sourceProj, targetProj);

        return new GeoPoint(targetProj.x, targetProj.y);
    }
}
