package com.example.aquascape.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final AquariumCatalogRepository repository;

    public CatalogService(AquariumCatalogRepository repository) {
        this.repository = repository;
    }

    public List<CatalogCategoryDto> getGroupedCatalog() {
        List<AquariumCatalog> items = repository.findByIsActiveTrue();

        Map<String, List<AquariumCatalog>> grouped = items.stream()
                .collect(Collectors.groupingBy(item -> 
                        item.getCategory() != null ? item.getCategory() : "Uncategorized"));

        return grouped.entrySet().stream()
                .map(entry -> new CatalogCategoryDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
