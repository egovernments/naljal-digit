package org.egov.naljalcustomisation.web.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Recepient {

    private List<String> toRoles;

    private List<String> toUsers;

}

