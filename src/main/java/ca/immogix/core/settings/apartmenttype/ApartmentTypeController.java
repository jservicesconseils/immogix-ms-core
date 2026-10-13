package ca.immogix.core.settings.apartmenttype;

import ca.immogix.core.security.TenantClaims;
import ca.immogix.core.settings.apartmenttype.dto.ApartmentTypeResponse;
import ca.immogix.core.settings.apartmenttype.dto.CreateApartmentTypeRequest;
import ca.immogix.core.settings.apartmenttype.dto.UpdateApartmentTypeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class ApartmentTypeController {

    private final ApartmentTypeService apartmentTypeService;

    public ApartmentTypeController(ApartmentTypeService apartmentTypeService) {
        this.apartmentTypeService = apartmentTypeService;
    }

    @PostMapping("/api/v1/apartment-types")
    public ResponseEntity<ApartmentTypeResponse> create(@Valid @RequestBody CreateApartmentTypeRequest request,
                                                          @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(request.organizationId());
        ApartmentTypeResponse created = apartmentTypeService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/apartment-types/" + created.id())).body(created);
    }

    @GetMapping("/api/v1/apartment-types/{id}")
    public ResponseEntity<ApartmentTypeResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        ApartmentTypeResponse apartmentType = apartmentTypeService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(apartmentType.organizationId());
        return ResponseEntity.ok(apartmentType);
    }

    @GetMapping("/api/v1/apartment-types")
    public ResponseEntity<List<ApartmentTypeResponse>> listByOrganization(@RequestParam UUID organizationId,
                                                                           @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(organizationId);
        return ResponseEntity.ok(apartmentTypeService.listByOrganization(organizationId));
    }

    @PutMapping("/api/v1/apartment-types/{id}")
    public ResponseEntity<ApartmentTypeResponse> update(@PathVariable UUID id,
                                                          @Valid @RequestBody UpdateApartmentTypeRequest request,
                                                          @AuthenticationPrincipal Jwt jwt) {
        ApartmentTypeResponse existing = apartmentTypeService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        return ResponseEntity.ok(apartmentTypeService.update(id, request));
    }

    @DeleteMapping("/api/v1/apartment-types/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        ApartmentTypeResponse existing = apartmentTypeService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        apartmentTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
