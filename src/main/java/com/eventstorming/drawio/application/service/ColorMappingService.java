package com.eventstorming.drawio.application.service;

import com.eventstorming.drawio.config.DefaultColorMappings;
import com.eventstorming.drawio.domain.model.ColorMapping;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ColorMappingService {

    public List<ColorMapping> merge(List<ColorMapping> customMappings) {
        if (customMappings == null || customMappings.isEmpty()) {
            return DefaultColorMappings.DEFAULTS;
        }

        Map<String, ColorMapping> merged = new LinkedHashMap<>();
        for (ColorMapping defaultMapping : DefaultColorMappings.DEFAULTS) {
            merged.put(defaultMapping.colorName().toLowerCase(), defaultMapping);
        }
        for (ColorMapping custom : customMappings) {
            merged.put(custom.colorName().toLowerCase(), custom);
        }
        return new ArrayList<>(merged.values());
    }
}
