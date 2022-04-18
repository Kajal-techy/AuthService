package com.auth.authmodule.response;

import com.auth.authmodule.request.UserDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserListResponse {
    List<UserDTO> users;
}
