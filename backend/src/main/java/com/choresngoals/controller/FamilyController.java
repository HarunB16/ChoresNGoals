package com.choresngoals.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.choresngoals.dto.FamilyResponse;
import com.choresngoals.security.AuthenticatedUser;
import com.choresngoals.service.FamilyQueryService;

@RestController
@RequestMapping("/api/families")
public class FamilyController {

    private final FamilyQueryService familyQueryService;

    public FamilyController(FamilyQueryService familyQueryService) {
        this.familyQueryService = familyQueryService;
    }

    @GetMapping("/current")
    public ResponseEntity<FamilyResponse> getCurrentFamily(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return ResponseEntity.ok(familyQueryService.getCurrentFamily(authenticatedUser));
    }
}
