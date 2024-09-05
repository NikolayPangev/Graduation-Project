package org.example.studentmanagementsystem.model.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackForm {

    private Long studentId;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull
    private String feedbackType;

}
