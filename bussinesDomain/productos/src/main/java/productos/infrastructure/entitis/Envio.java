package productos.infrastructure.entitis;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Envio {

    @Id
    @Column(name = "id_envio")
    @UuidGenerator
    private UUID idEnvio;

    @Column(name = "id_usuario", nullable = false)
    private UUID id_usuario;

    @ManyToOne
    private TipoEntrega tipoEntrega;


    @Column(name = "id_producto")
    private  UUID idProducto;
    private Integer cantidad;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    private LocalDateTime fechaEntrega;
    private Double precioEnvio;
    private Double precioConDescuento;
    private String numeroGuia;
    private String placaVehiculo;
    private String numeroFlota;

}
