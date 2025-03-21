package com.spring.example.location.controller;

import com.spring.example.location.model.Location;
import com.spring.example.location.model.Weather;
import com.spring.example.location.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/location")
public class LocationController {
    @Autowired
    private LocationRepository repository;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${weather.info}")
    private String weatherUrl;

    @GetMapping("/weather")
    public Weather redirectRequestWeather(@RequestParam String name) {
        Optional<Location> location = repository.findByName(name);
        if (location.isPresent()) {
            Location loc = location.get();
            String url = String.format("http://%s/weather?lat=%s&lon=%s", weatherUrl,
                    loc.getLatitude(), loc.getLongitude());
            return restTemplate.getForObject(url, Weather.class);
        } else {
            throw new RuntimeException("Location not found:" + location);
        }
    }

    @GetMapping("/{name}")
    public Optional<Location> getLocation(@PathVariable String name) {
        return repository.findByName(name);
    }

    @GetMapping
    public Iterable<Location> getLocations() {
        return repository.findAll();
    }

    @PostMapping
    public Location save(@RequestBody Location location) {
        return repository.save(location);
    }

    @PutMapping
    public Location update(@RequestParam String name, @RequestBody Location newLocation) {
        Location loc = repository.findByName(name).get();
        loc.setLatitude(newLocation.getLatitude());
        loc.setLongitude(newLocation.getLongitude());
        loc.setName(newLocation.getName());
        return repository.save(loc);
    }

    @DeleteMapping
    public void delete(@RequestParam String name) {
        repository.delete(repository.findByName(name).get());
    }

}
