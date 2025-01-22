package productos;

import com.common.lib.api.controller.DefaultCrudController;
import com.common.lib.infraestructure.adapters.primary.DefaultImpl;
import com.common.lib.infraestructure.services.primary.DefualtService;
import com.common.lib.infraestructure.services.secundary.CrudSecundaryService;
import com.common.lib.utils.UserResponses;
import com.common.lib.utils.errors.AbtractError;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import productos.infrastructure.services.secundary.CrudService;


@Configuration

public class Beans<RES, RQ, E, I> {

//
//    @Bean
//    public UserResponses<E> userResponses() {
//        return new UserResponses<>();
//    }


//    @Bean
//    public DefaultImpl getDefualtImpl(CrudService<RES,RQ,I> crudService){
//        return new DefaultImpl( c);
//    }

//    @Bean
//    public DefualtService defualtService(DefaultImpl defualtImpl){
//        return new DefualtService(defualtImpl);
//    }
//
//    @Bean
//    public DefaultCrudController<RES, RQ, E, I> defaultCrudController(DefualtService<RQ, RES, I> service) {
//        return new DefaultCrudController<>(service);
//    }
//
//    @Bean(name = "abstractError")
//    public AbtractError abstractError() {
//        return new AbtractError();
//    }
}
