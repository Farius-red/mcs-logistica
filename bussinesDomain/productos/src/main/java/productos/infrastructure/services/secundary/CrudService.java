package productos.infrastructure.services.secundary;

import com.common.lib.infraestructure.services.secundary.CrudSecundaryService;
import org.springframework.stereotype.Service;

@Service
public interface CrudService<R,E,I> extends CrudSecundaryService<R,E,I> {
}
