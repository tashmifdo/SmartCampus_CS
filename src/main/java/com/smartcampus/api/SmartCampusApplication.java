//Tashmi Fernando - 20241074 - w2121293
package com.smartcampus.api;

import com.smartcampus.api.filters.ApiLoggingFilter;
import com.smartcampus.api.mappers.GlobalThrowableMapper;
import com.smartcampus.api.mappers.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.api.mappers.RoomNotEmptyExceptionMapper;
import com.smartcampus.api.mappers.SensorUnavailableExceptionMapper;
import com.smartcampus.api.resources.DebugResource;
import com.smartcampus.api.resources.DiscoveryResource;
import com.smartcampus.api.resources.SensorResource;
import com.smartcampus.api.resources.SensorRoomResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1") // Base path for all API endpoints
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        register(JacksonFeature.class);

        // Register the API logging filter to log incoming requests and outgoing responses
        register(ApiLoggingFilter.class);

        // Exception mappers for handling specific exceptions and providing meaningful responses
        register(RoomNotEmptyExceptionMapper.class);
        register(LinkedResourceNotFoundExceptionMapper.class);
        register(SensorUnavailableExceptionMapper.class);
        register(GlobalThrowableMapper.class);

        register(DiscoveryResource.class);
        register(DebugResource.class);
        register(SensorRoomResource.class);
        register(SensorResource.class);
    }
}
