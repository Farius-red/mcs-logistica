package productos.api.controller

import com.common.lib.api.controller.CrudController
import com.common.lib.api.controller.DefaultCrudController
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import productos.api.dto.request.EnvioRequest
import productos.api.dto.responses.EnvioResponse
import productos.infrastructure.entitis.Envio
import java.util.UUID

@RestController
@RequestMapping("/logistica")
@Tag(name = "envios ", description = "Endpoints relacionados con el manejo de envios" )
class LogisticaController(
    defaultCrudController: DefaultCrudController<EnvioResponse, EnvioRequest, Envio, UUID>
) : CrudController<EnvioResponse, EnvioRequest, Envio, UUID> by defaultCrudController {

}
