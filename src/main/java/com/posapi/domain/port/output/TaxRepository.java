package com.posapi.domain.port.output;

import com.posapi.domain.model.tax.Tax;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaxRepository { // Interfaz de dominio limpia

    Tax save(Tax tax);
    Optional<Tax> findById(UUID id);
    List<Tax> findAll(); // Obtener todos sin paginación
    Page<Tax> findAll(Pageable pageable); // Obtener todos paginados
    void deleteById(UUID id);
    boolean existsByName(String name);
    boolean existsById(UUID id); // Verificar si un impuesto existe por ID
    Optional<Tax> findByName(String name);
    void deleteAll(); // Eliminar todos los impuestos


}
