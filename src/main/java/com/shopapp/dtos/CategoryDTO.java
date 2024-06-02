package com.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data // toString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category's name cannot be empty.")
    private String name;
}
