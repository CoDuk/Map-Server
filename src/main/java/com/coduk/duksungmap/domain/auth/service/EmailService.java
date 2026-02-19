package com.coduk.duksungmap.domain.auth.service;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.global.exception.CustomException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void send(String to, String code) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("[덕성여대 지도 서비스] 이메일 인증 번호 안내");
            helper.setFrom(fromEmail);
            helper.setText(buildBody(code), true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.EMAIL_SEND_FAIL, e.getMessage());
        }
    }

    private String buildBody(String code) {
        return """
            <div style="font-family: Arial, sans-serif; padding: 10px;">
                <h3>
                    안녕하세요!
                    <span style="color:#981B45;">덕성여대 지도 서비스</span>입니다.
                </h3>

                <p>아래 인증 번호를 입력해 이메일 인증을 완료해주세요.</p>
                
                <br>

                <h1 style="color:#981B45; letter-spacing:5px;">%s</h1>

                <p style="color:gray;">
                    * 인증번호는 5분간 유효합니다.
                </p>
            </div>
        """.formatted(code);
    }
}