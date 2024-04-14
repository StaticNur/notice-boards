package com.greenatom.noticeboards.service;


import com.greenatom.noticeboards.model.dto.JwtResponse;
import com.greenatom.noticeboards.model.entity.User;

public interface SecurityService {

    User register(String userName, String password, String role);


    JwtResponse authorization(String login, String password);

}
