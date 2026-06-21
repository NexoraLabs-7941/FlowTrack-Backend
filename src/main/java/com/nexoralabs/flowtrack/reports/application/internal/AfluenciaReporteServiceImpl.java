package com.nexoralabs.flowtrack.reports.application.internal;

import com.nexoralabs.flowtrack.reports.application.AfluenciaReporteService;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.projections.AfluenciaAgrupadaProjection;
import com.nexoralabs.flowtrack.reports.infrastructure.persistence.jpa.repositories.RegistroAfluenciaRepository;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.AfluenciaAgrupadaResource;
import com.nexoralabs.flowtrack.reports.interfaces.rest.resources.AfluenciaReporteResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AfluenciaReporteServiceImpl implements AfluenciaReporteService {

    private final RegistroAfluenciaRepository registroAfluenciaRepository;

    public AfluenciaReporteServiceImpl(RegistroAfluenciaRepository registroAfluenciaRepository) {
        this.registroAfluenciaRepository = registroAfluenciaRepository;
    }

    @Override
    public AfluenciaReporteResource obtenerReporteAfluencia() {
        return new AfluenciaReporteResource(
                toResource(registroAfluenciaRepository.obtenerTraficoDiario()),
                toResource(registroAfluenciaRepository.obtenerHorasPico())
        );
    }

    private List<AfluenciaAgrupadaResource> toResource(List<AfluenciaAgrupadaProjection> projections) {
        return projections.stream()
                .map(item -> new AfluenciaAgrupadaResource(item.getPeriodo(), item.getTotal()))
                .toList();
    }
}
