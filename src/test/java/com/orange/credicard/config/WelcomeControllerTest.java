package com.orange.credicard.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/*
* TODO: Este teste foi criado para certificar de que a aplicação está
*  bloqueando corretamente o endpoint que usará o Keycloak como método
*  de autenticação. A ideia é que no futuro os outros endpoints que estão
*  abertos também sejam bloqueados e tenham seus testes alterados.
* */

@SpringBootTest
@AutoConfigureMockMvc
class WelcomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void arrived__should_be_block_access_if_user_not_is_authenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
            .get("/hello"))
            .andExpect(MockMvcResultMatchers
                .status()
                .is(401));
    }

}