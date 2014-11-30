package com.free.walker.service.itinerary.primitive;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.free.walker.service.itinerary.LocalMessages;
import com.free.walker.service.itinerary.exp.InvalidTravelReqirementException;
import com.free.walker.service.itinerary.primitive.Introspection.JSONKeys;

public class IntrospectionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetHotelStar() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.HOTEL_STAR_STD_5, Introspection.JsonValueHelper.getHotelStar(500));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_STD_4, Introspection.JsonValueHelper.getHotelStar(400));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_STD_3, Introspection.JsonValueHelper.getHotelStar(300));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_STD_2, Introspection.JsonValueHelper.getHotelStar(200));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_LST_5, Introspection.JsonValueHelper.getHotelStar(50));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_LST_4, Introspection.JsonValueHelper.getHotelStar(40));
        assertEquals(Introspection.JSONValues.HOTEL_STAR_LST_3, Introspection.JsonValueHelper.getHotelStar(30));
        
        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.STAR, 0));
        Introspection.JsonValueHelper.getHotelStar(0);
    }

    @Test
    public void testGetResortStar() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.RESORT_STAR_STD_5A, Introspection.JsonValueHelper.getResortStar(500));
        assertEquals(Introspection.JSONValues.RESORT_STAR_STD_4A, Introspection.JsonValueHelper.getResortStar(400));
        assertEquals(Introspection.JSONValues.RESORT_STAR_STD_3A, Introspection.JsonValueHelper.getResortStar(300));
        assertEquals(Introspection.JSONValues.RESORT_STAR_STD_2A, Introspection.JsonValueHelper.getResortStar(200));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.STAR, 0));
        Introspection.JsonValueHelper.getResortStar(0);
    }

    @Test
    public void testGetTrafficSeatClass() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_1ST,
            Introspection.JsonValueHelper.getTrafficSeatClass(100));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_2ND,
            Introspection.JsonValueHelper.getTrafficSeatClass(200));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_SEAT_CLASS_3RD,
            Introspection.JsonValueHelper.getTrafficSeatClass(300));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            JSONKeys.TRAFFIC_TOOL_SEAT_CLASS, 0));
        Introspection.JsonValueHelper.getTrafficSeatClass(0);
    }

    @Test
    public void testGetTrafficToolType() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_FLIGHT, Introspection.JsonValueHelper.getTrafficToolType(1));
        assertEquals(Introspection.JSONValues.TRAFFIC_TOOL_TYPE_TRAIN, Introspection.JsonValueHelper.getTrafficToolType(2));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            JSONKeys.TRAFFIC_TOOL_TYPE, 0));
        Introspection.JsonValueHelper.getTrafficToolType(0);
    }

    @Test
    public void testGetTravelTimeRange() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.TIME_RANGE_00_06.realValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(0, 6).realValue());
        assertEquals(Introspection.JSONValues.TIME_RANGE_00_06.imaginaryValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(0, 6).imaginaryValue());

        assertEquals(Introspection.JSONValues.TIME_RANGE_06_12.realValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(6, 6).realValue());
        assertEquals(Introspection.JSONValues.TIME_RANGE_06_12.imaginaryValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(6, 6).imaginaryValue());

        assertEquals(Introspection.JSONValues.TIME_RANGE_12_18.realValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(12, 6).realValue());
        assertEquals(Introspection.JSONValues.TIME_RANGE_12_18.imaginaryValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(12, 6).imaginaryValue());

        assertEquals(Introspection.JSONValues.TIME_RANGE_18_24.realValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(18, 6).realValue());
        assertEquals(Introspection.JSONValues.TIME_RANGE_18_24.imaginaryValue(), Introspection.JsonValueHelper
            .getTravelTimeRange(18, 6).imaginaryValue());

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value,
            JSONKeys.TIME_RANGE_START + ":" + JSONKeys.TIME_RANGE_OFFSET, 0 + ":" + 0));
        Introspection.JsonValueHelper.getTravelTimeRange(0, 0);
    }

    @Test
    public void testGetContinentID() throws InvalidTravelReqirementException {
        assertEquals(Introspection.JSONValues.CONTINENT_ID_ASIA, Introspection.JsonValueHelper.getContinentID(1));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_EUROPE, Introspection.JsonValueHelper.getContinentID(2));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_NORTH_AMERICA, Introspection.JsonValueHelper.getContinentID(3));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_SOUTH_AMERICA, Introspection.JsonValueHelper.getContinentID(4));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_OCEANIA, Introspection.JsonValueHelper.getContinentID(5));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_AFRICA, Introspection.JsonValueHelper.getContinentID(6));
        assertEquals(Introspection.JSONValues.CONTINENT_ID_ANTARCTICA, Introspection.JsonValueHelper.getContinentID(7));

        thrown.expect(InvalidTravelReqirementException.class);
        thrown.expectMessage(LocalMessages.getMessage(LocalMessages.invalid_parameter_with_value, JSONKeys.UUID, 0));
        Introspection.JsonValueHelper.getContinentID(0);
    }
}
