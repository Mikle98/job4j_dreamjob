package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

@Repository
@ThreadSafe
public class Sql2oCandidateRepository implements CandidateRepository {

    private final Sql2o sql2o;

    public Sql2oCandidateRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Candidate save(Candidate candidate) {
        try (var connection = sql2o.open()) {
            var sql = """
                        INSERT INTO candidates(name, description, creationDate, cityId, fileId)
                        VALUES (:name, :description, :creationDate, :cityId, :fileId)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", candidate.getName())
                    .addParameter("description", candidate.getDescription())
                    .addParameter("creationDate", candidate.getCreationDate())
                    .addParameter("cityId", candidate.getCityId())
                    .addParameter("fileId", candidate.getFileId());
            int generateId = query.executeUpdate().getKey(Integer.class);
            candidate.setId(generateId);
            return candidate;
        }
    }

    @Override
    public void deleteById(int id) {
        try (var connection = sql2o.open()) {
            var sql = """
                        DELETE FROM candidates WHERE id = :id  
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("id", id);
            query.executeUpdate();
        }
    }

    @Override
    public boolean update(Candidate candidate) {
        try (var connection = sql2o.open()) {
            var sql = """
                        UPDATE canditates
                        SET name = : name, description = :description, creationDate = :creationDate,
                        cityId = :cityId, fileId = :fileId
                        WHERE id = :id
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("name", candidate.getName())
                    .addParameter("description", candidate.getDescription())
                    .addParameter("creationDate", candidate.getCreationDate())
                    .addParameter("cityId", candidate.getCityId())
                    .addParameter("fileId", candidate.getFileId())
                    .addParameter("id", candidate.getId());
            var affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }

    @Override
    public Optional<Candidate> findById(int id) {
        try (var connection = sql2o.open()) {
            var sql = """
                        SELECT * FROM candidates
                        WHERE id = :id
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("id", id);
            var candidate = query.executeAndFetchFirst(Candidate.class);
            return Optional.ofNullable(candidate);
        }
    }

    @Override
    public Collection<Candidate> findAll() {
        try (var connection = sql2o.open()) {
            var sql = """
                        SELECT * FROM candidates
                    """;
            var query = connection.createQuery(sql);
            var candites = query.executeAndFetch(Candidate.class);
            return candites;
        }
    }
}
