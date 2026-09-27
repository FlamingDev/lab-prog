package com.example.demo;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    ApplicationModules modules =
            ApplicationModules.of(DemoApplication.class);

    @Test
    void verificarConformidadeModular() {
        modules.verify();
    }

    @Test
    void gerarDocumentacaoArquitetural() {
        new Documenter(modules).writeDocumentation();
    }
}
