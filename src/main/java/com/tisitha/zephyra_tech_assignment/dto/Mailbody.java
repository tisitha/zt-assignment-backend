package com.tisitha.zephyra_tech_assignment.dto;

import lombok.Builder;

@Builder
public record Mailbody(String to,String subject, String text) {
}
