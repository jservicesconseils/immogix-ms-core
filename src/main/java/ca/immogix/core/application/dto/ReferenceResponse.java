package ca.immogix.core.application.dto;

import ca.immogix.core.application.ApplicationReference;

import java.util.UUID;

public record ReferenceResponse(
        UUID id,
        String name,
        String phone,
        String email
) {

    public static ReferenceResponse from(ApplicationReference reference) {
        return new ReferenceResponse(reference.getId(), reference.getName(), reference.getPhone(), reference.getEmail());
    }
}
