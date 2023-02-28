package org.kryptonmc.downloads.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Error", type = "object", description = "Indicates an error occurred")
public record ErrorResponse(
    @Schema(name = "error", type = "string", description = "The error message")
    String error
) {
}
