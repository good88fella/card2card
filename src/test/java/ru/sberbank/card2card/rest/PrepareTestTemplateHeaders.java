package ru.sberbank.card2card.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.sberbank.card2card.dto.AuthRequestDto;
import ru.sberbank.card2card.dto.AuthResponseDto;

import java.util.Collections;
import java.util.Objects;

@Component
public class PrepareTestTemplateHeaders {

    @Autowired
    private AuthenticationRestController authenticationRestController;

    public void prepareHeaders(TestRestTemplate template, String username, String password) {
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername(username);
        requestDto.setPassword(password);
        ResponseEntity<AuthResponseDto> responseEntity = authenticationRestController.login(requestDto);

        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization",
                                    "Bearer_" + Objects.requireNonNull(responseEntity.getBody()).getToken());
                    return execution.execute(request, body);
                })
        );
    }

    public void cleanHeaders(TestRestTemplate template) {
        template.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .set("Authorization", "");
                    return execution.execute(request, body);
                })
        );
    }
}
