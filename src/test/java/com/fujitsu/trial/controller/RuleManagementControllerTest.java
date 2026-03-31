package com.fujitsu.trial.controller;

import com.fujitsu.trial.entity.RegionalBaseFee;
import com.fujitsu.trial.repository.RegionalBaseFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RuleManagementControllerTest {

    private RegionalBaseFeeRepository repository;
    private RuleManagementController controller;

    @BeforeEach
    void setUp() {
        // Manually mock the repository to match your existing test style
        repository = mock(RegionalBaseFeeRepository.class);
        controller = new RuleManagementController(repository);
    }

    @Test
    void createRule_ValidatesAndSavesRule_ReturnsSavedRule() {
        // Arrange
        RegionalBaseFee inputRule = new RegionalBaseFee(null, "TALLINN", "CAR", 5.0, null);
        RegionalBaseFee savedRule = new RegionalBaseFee(1L, "TALLINN", "CAR", 5.0, LocalDateTime.now());
        when(repository.save(any(RegionalBaseFee.class))).thenReturn(savedRule);

        // Act
        ResponseEntity<RegionalBaseFee> response = controller.createRule(inputRule);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertNotNull(inputRule.getValidFrom()); // Verifies the controller assigned a timestamp
    }

    @Test
    void getAllRules_ReturnsListOfRules() {
        // Arrange
        List<RegionalBaseFee> mockRules = List.of(
                new RegionalBaseFee(1L, "TARTU", "BIKE", 2.5, LocalDateTime.now())
        );
        when(repository.findAll()).thenReturn(mockRules);

        // Act
        ResponseEntity<List<RegionalBaseFee>> response = controller.getAllRules();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("TARTU", response.getBody().get(0).getCity());
    }

    @Test
    void updateRule_ExistingId_UpdatesAndReturnsRule() {
        // Arrange
        Long ruleId = 1L;
        RegionalBaseFee existingRule = new RegionalBaseFee(ruleId, "PÄRNU", "SCOOTER", 2.5, LocalDateTime.now().minusDays(2));
        RegionalBaseFee updatedInfo = new RegionalBaseFee(null, "PÄRNU", "SCOOTER", 3.0, LocalDateTime.now()); // Price increase

        when(repository.findById(ruleId)).thenReturn(Optional.of(existingRule));
        when(repository.save(any(RegionalBaseFee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ResponseEntity<RegionalBaseFee> response = controller.updateRule(ruleId, updatedInfo);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3.0, response.getBody().getFee());
        assertEquals(updatedInfo.getValidFrom(), response.getBody().getValidFrom());
    }

    @Test
    void updateRule_NonExistingId_ReturnsNotFound() {
        // Arrange
        Long ruleId = 99L;
        when(repository.findById(ruleId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<RegionalBaseFee> response = controller.updateRule(ruleId, new RegionalBaseFee());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteRule_ExistingId_DeletesAndReturnsNoContent() {
        // Arrange
        Long ruleId = 1L;
        when(repository.existsById(ruleId)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = controller.deleteRule(ruleId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(repository, times(1)).deleteById(ruleId); // Verify the delete method was actually called
    }

    @Test
    void deleteRule_NonExistingId_ReturnsNotFound() {
        // Arrange
        Long ruleId = 99L;
        when(repository.existsById(ruleId)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = controller.deleteRule(ruleId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(repository, never()).deleteById(anyLong()); // Verify delete was NOT called
    }
}