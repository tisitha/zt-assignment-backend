package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.dto.Mailbody;

public interface EmailService {

    void sendSimpleMessage(Mailbody mailbody);

}
