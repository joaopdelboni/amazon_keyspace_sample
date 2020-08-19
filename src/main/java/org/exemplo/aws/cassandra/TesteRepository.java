package org.exemplo.aws.cassandra;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository()
public interface TesteRepository  extends CrudRepository<Teste, UUID> {
}
