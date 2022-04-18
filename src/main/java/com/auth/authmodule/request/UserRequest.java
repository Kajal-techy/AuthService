package com.auth.authmodule.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
    private String firstName;

    private String lastName;
}
