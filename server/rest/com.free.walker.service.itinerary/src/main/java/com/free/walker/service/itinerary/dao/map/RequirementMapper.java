package com.free.walker.service.itinerary.dao.map;

import org.apache.ibatis.annotations.Select;

public interface RequirementMapper {
    @Select("select version()")
    public String pingPersistence();
}
