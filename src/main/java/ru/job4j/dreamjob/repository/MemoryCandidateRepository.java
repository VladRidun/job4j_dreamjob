package ru.job4j.dreamjob.repository;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final ConcurrentHashMap<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan", "Intern Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Vlad", "Junior Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Vasya", "Junior+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Nikolay", "Middle Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Sergei", "Middle+ Java Developer", LocalDateTime.now()));
        save(new Candidate(0, "Kostya", "Senior Java Developer", LocalDateTime.now()));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                candidate.getName(), candidate.getDescription(), candidate.getCreationDate())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
