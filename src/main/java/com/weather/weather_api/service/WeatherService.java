package com.weather.weather_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> getWeather(String city) {
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(city);

        if (cached != null) {
            return cached;
        }

        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
                + city + "?unitGroup=metric&key=" + apiKey + "&include=current";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        redisTemplate.opsForValue().set(city, response, 12, TimeUnit.HOURS);

        return response;
    }
}
