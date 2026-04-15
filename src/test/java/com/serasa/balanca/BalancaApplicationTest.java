package com.serasa.balanca;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BalancaApplicationTest {

    @Test
    public void contextLoads() {
        // Verifica que todos os beans são criados e as configurações carregam sem erro
    }
}
