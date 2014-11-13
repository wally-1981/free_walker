package com.free.walker.service.itinerary.dao.map;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.free.walker.service.itinerary.basic.City;
import com.free.walker.service.itinerary.basic.Country;
import com.free.walker.service.itinerary.basic.Province;

public interface BasicMapper {
    @Select("select count(*) from information_schema.tables where table_name = 'country' or table_name = 'province' or table_name = 'city'")
    public int pingPersistence();

    @Select("select hex(uuid) as uuid, name as name, chinese_name as chineseName, pinyin_name as pinyinName from country")
    public List<Country> getAllCounties();

    @Select("select hex(uuid) as uuid, name as name, chinese_name as chineseName, pinyin_name as pinyinName from province")
    public List<Province> getAllProvinces();

    @Select("select hex(uuid) as uuid, name as name, chinese_name as chineseName, pinyin_name as pinyinName, hex(province_uuid) as provinceUuid, hex(country_uuid) as countryUuid, continent_id as continentId from city")
    public List<City> getAllCities();
}
