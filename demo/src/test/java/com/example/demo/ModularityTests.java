package com.example.demo;


import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(DemoApplicationTests.class);

    @Test
    void verificarConformidadeModular() {
        // Falha o build se houver ciclos ou acesso a pacotes internal de outro módulo
        modules.verify();
    }

    @Test
    void gerarDocumentacaoArquitetural() {
        // Gera automaticamente diagramas C4 e documentação visual dos módulos
        new Documenter(modules).writeDocumentation();
    }
}
