package com.appshop.back_shop.dto.request.categories;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRequest {
    @NotBlank(message = "NAME_CATEGORY_INVALID")
    @Size(max = 255, message = "Name cannot be longer than 255 characters")
    String name;

    @Size(max = 500, message = "Description cannot be longer than 500 characters")
    String description;

    @Size(max = 1000)
    String image;
}
