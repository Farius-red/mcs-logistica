package productos.api.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class EnvioResponse {

    @JsonProperty("cantidad")
    private int cantidad;

    @JsonProperty("fechaRegistro")
    private LocalDateTime fechaRegistro;

    @JsonProperty("fechaEntrega")
    private LocalDateTime fechaEntrega;

    @JsonProperty("precioEnvio")
    private double precioEnvio;

    @JsonProperty("precioConDescuento")
    private double precioConDescuento;

    @JsonProperty("numeroGuia")
    private String numeroGuia;

    @JsonProperty("placaVehiculo")
    private String placaVehiculo;

    @JsonProperty("numeroFlota")
    private String numeroFlota;
}
