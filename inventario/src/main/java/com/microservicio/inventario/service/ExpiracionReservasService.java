package com.microservicio.inventario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ExpiracionReservasService {
    @Autowired private InventarioService inventarioService;

    @Scheduled(fixedDelayString = "${inventario.expiracion-intervalo-ms:60000}")
    public void expirarReservas() {
        inventarioService.expirarReservas();
    }
}
