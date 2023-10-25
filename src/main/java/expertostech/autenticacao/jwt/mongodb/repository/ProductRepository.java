package expertostech.autenticacao.jwt.mongodb.repository;

import expertostech.autenticacao.jwt.mongodb.dto.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
