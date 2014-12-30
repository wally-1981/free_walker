package com.free.walker.service.itinerary;

import java.util.List;

public interface MRRoutine {
    public MRRoutine map();

    public MRRoutine reduce();

    public List<Object> collect();

    public boolean immediate();
}
