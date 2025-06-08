package com.mrdotxin.propsmart.config.typehandler;

import org.apache.ibatis.type.MappedTypes;
import org.locationtech.jts.geom.LineString;

@MappedTypes(LineString.class)
public class LineStringTypeHandler extends GeometryTypeHandler {

}