package com.hotelsistema.backend.dto.authDTO;

import com.hotelsistema.backend.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Usado SOLO por un administrador para crear cuentas de RECEPCIONISTA o
 * ADMINISTRADOR. Crear un CLIENTE por aqui esta bloqueado a proposito
 * (para eso existe /api/auth/register, que es publico).
 */
public record CrearStaffRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @Size(max = 100)
        String apellidos,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 150)
        String email,

        @Size(max = 20)
        String telefono,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, message = "La contrasena debe tener al menos 8 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
