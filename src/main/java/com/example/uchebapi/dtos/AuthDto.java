package com.example.uchebapi.dtos;

import com.example.uchebapi.projections.GroupNameInfo;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
public record AuthDto(Integer vkId,
                      String accessToken) {
}