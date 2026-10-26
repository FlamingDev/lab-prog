package com.example.demo.restaurant;

import com.example.demo.restaurant.internal.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantApi {
    private final RestauranteService restauranteService;

    public Boolean existePorId(UUID id){
        return restauranteService.existePorId(id);
    }
}
