package woohakdong.server.api.controller.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import woohakdong.server.domain.member.MemberGender;

public record CreateMemberRequest(
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\d{10,15}$", message = "Invalid phone number format")
        String memberPhoneNumber,

        @NotBlank(message = "Major is required")
        String memberMajor,

        @NotBlank(message = "Student number is required")
        String memberStudentNumber,

        @NotBlank(message = "Gender is required")
        MemberGender memberGender
) {
}
