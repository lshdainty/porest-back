package com.lshdainty.porest.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${app.company.name}")
    private String companyName;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 회원가입 초대 이메일 발송
     *
     * @param toEmail 수신자 이메일
     * @param userName 수신자 이름
     * @param invitationToken 초대 토큰
     */
    public void sendInvitationEmail(String toEmail, String userName, String invitationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(String.format("%s 회원가입 초대", companyName));

            String invitationLink = String.format("%s/signup?token=%s", frontendBaseUrl, invitationToken);
            String htmlContent = String.format("""
                <h2>%s 회원가입 초대</h2>
                <p>안녕하세요, %s님</p>
                <p>아래 링크를 클릭하여 회원가입을 완료해주세요.</p>
                <p><a href="%s" style="background-color: #007bff; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px;">
                회원가입 하기</a></p>
                <p>이 링크는 48시간 후에 만료됩니다.</p>
                """, companyName, userName, invitationLink);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("초대 이메일 발송 완료: {}", toEmail);
        } catch (MessagingException e) {
            log.error("초대 이메일 발송 실패: {}", toEmail, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}