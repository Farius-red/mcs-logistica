package productos.infrastructure.services.secundary;


import org.springframework.data.jpa.repository.JpaRepository;
import productos.infrastructure.domain.entity.Product;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCrudRepository extends JpaRepository<Product,Integer> {
   List<Product>findByIdBussinesOrderByNameAsc(int idBussines);


}
