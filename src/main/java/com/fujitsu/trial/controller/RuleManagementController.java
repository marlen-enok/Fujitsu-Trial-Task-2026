package com.fujitsu.trial.controller;

import com.fujitsu.trial.entity.RegionalBaseFee;
import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing Regional Base Fee rules.
 * Provides CRUD operations to dynamically update pricing without code changes.
 */
@RestController
@RequestMapping("/api/rules/base-fee")
@RequiredArgsConstructor
public class RuleManagementController {

    private final RegionalBaseFeeRepository repository;

    /**
     * Creates a new Regional Base Fee rule.
     *
     * @param rule the {@link RegionalBaseFee} object to be saved.
     * @return a {@link ResponseEntity} containing the saved rule.
     */
    @Operation(summary = "Add a new Regional Base Fee rule")
    @PostMapping
    public ResponseEntity<RegionalBaseFee> createRule(@RequestBody RegionalBaseFee rule) {
        if (rule.getValidFrom() == null) {
            rule.setValidFrom(LocalDateTime.now());
        }
        return ResponseEntity.ok(repository.save(rule));
    }

    /**
     * Retrieves all historical and current Regional Base Fee rules.
     *
     * @return a {@link ResponseEntity} containing a list of all rules.
     */
    @Operation(summary = "Get all Regional Base Fee rules")
    @GetMapping
    public ResponseEntity<List<RegionalBaseFee>> getAllRules() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * Updates an existing Regional Base Fee rule by creating a new historical entry.
     * This preserves the historical pricing data for past calculations.
     *
     * @param id          the ID of the rule being superseded.
     * @param updatedRule the updated {@link RegionalBaseFee} details.
     * @return a {@link ResponseEntity} containing the newly created active rule, or 404 if the original ID is not found.
     */
    @Operation(summary = "Update an existing Regional Base Fee rule (Creates a new historical entry)")
    @PutMapping("/{id}")
    public ResponseEntity<RegionalBaseFee> updateRule(@PathVariable Long id, @RequestBody RegionalBaseFee updatedRule) {
        return repository.findById(id).map(existingRule -> {
            // Create a new entry to preserve the historical price of the old entry
            RegionalBaseFee newRule = RegionalBaseFee.builder()
                    .city(updatedRule.getCity() != null ? updatedRule.getCity() : existingRule.getCity())
                    .vehicleType(updatedRule.getVehicleType() != null ? updatedRule.getVehicleType() : existingRule.getVehicleType())
                    .fee(updatedRule.getFee() != null ? updatedRule.getFee() : existingRule.getFee())
                    .validFrom(updatedRule.getValidFrom() != null ? updatedRule.getValidFrom() : LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(repository.save(newRule));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a specific Regional Base Fee rule by its ID.
     *
     * @param id the ID of the rule to delete.
     * @return a {@link ResponseEntity} with status 204 No Content on success, or 404 Not Found.
     */
    @Operation(summary = "Delete a Regional Base Fee rule")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content is standard for successful deletions
        }
        return ResponseEntity.notFound().build();
    }
}