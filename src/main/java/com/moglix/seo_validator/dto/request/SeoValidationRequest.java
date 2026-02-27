package com.moglix.seo_validator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SeoValidationRequest {

    @NotBlank
    private String prodSitemap;

    @NotBlank
    private String uatSitemap;
}