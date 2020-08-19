package org.exemplo.aws.cassandra;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.InsertOptions;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class TesteController {
    private final TesteRepository testeRepository;
    private final CassandraTemplate cassandraTemplate;

    public TesteController(TesteRepository testeRepository, CassandraTemplate cassandraTemplate) {
        this.testeRepository = testeRepository;
        this.cassandraTemplate = cassandraTemplate;
    }

    @GetMapping
    public Iterable<Teste> listar() {
        return this.testeRepository.findAll();
    }

    @PostMapping
    public Teste listar(@RequestBody Teste teste) {
        if(teste.getId() == null) {
            teste.setId(UUID.randomUUID());
        }
        return this.save(teste);
    }

    private Teste save(Teste teste) {
        return this.cassandraTemplate.insert(teste, InsertOptions.builder()
                .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .build()).getEntity();
    }
}
