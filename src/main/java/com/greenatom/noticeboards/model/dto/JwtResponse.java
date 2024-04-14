package com.greenatom.noticeboards.model.dto;


public record JwtResponse(String login, String accessToken, String refreshToken) {
}
