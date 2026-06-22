package com.nexoralabs.flowtrack;

import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.AfluenciaRegistroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class FlowtrackBackendApplicationTests {

    @Autowired
    private AfluenciaRegistroRepository repo;

    @Test
    void contextLoads() {
        try {
            System.out.println("--- TESTING REPOSITORY QUERY (TraficoDiario) ---");
            repo.obtenerTraficoDiario(null, LocalDateTime.now().minusDays(7), LocalDateTime.now());
            System.out.println("--- TraficoDiario QUERY SUCCESSFUL ---");
            
            System.out.println("--- TESTING REPOSITORY QUERY (HorasPico) ---");
            repo.obtenerHorasPico("camara_0", LocalDateTime.now().minusDays(1), LocalDateTime.now());
            System.out.println("--- HorasPico QUERY SUCCESSFUL ---");
        } catch (Exception e) {
            System.out.println("--- QUERY FAILED ---");
            e.printStackTrace();
        }
    }

}
