package productos.infrastructure.entitis;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class TipoEntrega {

    @Id
    @UuidGenerator
    private UUID id;
    private String nombre;
    private String ubicacion;
    private String descripcion;

}
