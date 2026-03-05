package com.coduk.duksungmap.domain.auth.service;

import com.coduk.duksungmap.domain.auth.exception.AuthErrorCode;
import com.coduk.duksungmap.global.exception.CustomException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.logo-path:static/images/logo.png}")
    private String logoPath;

    public void send(String to, String code) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            ClassPathResource logo = new ClassPathResource(logoPath);
            boolean hasLogo = logo.exists();

            helper.setTo(to);
            helper.setSubject("[덕성여대 지도 서비스] 이메일 인증 번호 안내");
            helper.setFrom(fromEmail);
            helper.setText(buildBody(code, hasLogo), true);

            if (hasLogo) {
                helper.addInline("logo", logo, "image/png");
            }

            mailSender.send(message);

        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.EMAIL_SEND_FAIL);
        }
    }

    private String buildBody(String code, boolean hasLogo) {
        String logoHtml = hasLogo ? """
        <img src="cid:logo" width="90" alt="덕성여대 지도 서비스"
             style="display:block; margin-left:45px; margin-bottom:40px;" />
        """ : "";

        return """
    <!doctype html>
    <html>
    <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <style>
        /* 모바일 메일 앱 자동 글자확대 방지 */
        body, table, td, a { -webkit-text-size-adjust:100%%; -ms-text-size-adjust:100%%; }

        /* iOS Mail 링크 파랗게 되는 것 방지(선택) */
        a[x-apple-data-detectors] { color: inherit !important; text-decoration: none !important; }

        /* 모바일에서 폰트/간격 줄이기 */
        @media screen and (max-width: 480px) {
          .container { padding: 28px !important; }
          .title { font-size: 34px !important; line-height: 1.25 !important; }
          .desc  { font-size: 20px !important; line-height: 1.35 !important; }
          .code  { font-size: 64px !important; letter-spacing: 12px !important; }
          .hint  { font-size: 14px !important; }
        }
      </style>
    </head>

    <body style="margin:0; padding:0; background:#f5f5f5;">
      <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0"
             style="background:#f5f5f5; margin:0; padding:0;">
        <tr>
          <td align="center" style="padding:24px 12px;">
            <table role="presentation" width="600" cellspacing="0" cellpadding="0" border="0"
                   style="width:600px; max-width:600px; background:#ffffff; border-radius:12px;">
              <tr>
                <td class="container"
                    style="padding:40px; font-family:'Apple SD Gothic Neo','Noto Sans KR',Arial,sans-serif;">

                  <!-- 로고 이미지 -->
                  %s
                
                  <!-- 제목 -->
                  <div class="title"
                       style="font-size:15px; font-weight:550; color:#000; line-height:1.3; margin:0;">
                    안녕하세요.
                    <span style="color:#981B45;">덕성여대 지도 서비스</span>입니다.
                  </div>

                  <div style="height:15px;"></div>

                  <!-- 설명 -->
                  <div class="desc"
                       style="font-size:15px; font-weight:550; color:#000; line-height:1.4;">
                    아래 인증 번호를 입력해 이메일 인증을 완료해주세요.
                  </div>

                  <div style="height:35px;"></div>

                  <!-- 코드 -->
                  <div style="display:inline-flex; align-items:center;">
                    <span class="code"
                        style="font-size:45px; font-weight:600; color:#981B45; letter-spacing:6px; line-height:1; white-space:nowrap;">
                        %s
                    </span>
                  </div>

                  <div style="height:28px;"></div>

                  <!-- 안내 -->
                  <div class="hint" style="font-size:13px; color:#9e9e9e; font-weight:400;">
                    * 인증 번호는 5분간 유효합니다.
                  </div>

                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </body>
    </html>
    """.formatted(logoHtml, code);
    }
}