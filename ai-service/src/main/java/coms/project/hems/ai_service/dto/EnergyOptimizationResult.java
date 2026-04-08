package coms.project.hems.ai_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EnergyOptimizationResult {

    private String site_id;
    private String current_consumption;
    private String battery_capacity;
    private String solar_capacity;

    private List<Recommendation> top_3_actionable_recommendations_to_reduce_consumption;

    @Data
    public static class Recommendation {
        private int recommendation_id;
        private String category;
        private String details;
        private String estimated_impact_area;
    }
}