package com.mhkcode.institution.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
