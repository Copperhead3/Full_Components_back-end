package com.microservicio.Usuarios.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

}