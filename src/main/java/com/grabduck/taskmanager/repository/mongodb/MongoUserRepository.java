package com.grabduck.taskmanager.repository.mongodb;

import com.grabduck.taskmanager.domain.User;
import com.grabduck.taskmanager.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Primary
public interface MongoUserRepository extends MongoRepository<UserDocument, String>, UserRepository {
    
    @Query("{ 'username' : ?0 }")
    Optional<UserDocument> findUserDocumentByUsername(String username);
    
    @Query("{ 'email' : ?0 }")
    Optional<UserDocument> findUserDocumentByEmail(String email);

    @Override
    default User save(User user) {
        UserDocument document = new UserDocument(user);
        return save(document).toDomainUser();
    }

    @Override
    default Optional<User> findById(UUID id) {
        return findById(id.toString())
                .map(UserDocument::toDomainUser);
    }

    @Override
    default Optional<User> findByUsername(String username) {
        return findUserDocumentByUsername(username)
                .map(UserDocument::toDomainUser);
    }

    @Override
    default Optional<User> findByEmail(String email) {
        return findUserDocumentByEmail(email)
                .map(UserDocument::toDomainUser);
    }

    @Override
    default void deleteById(UUID id) {
        deleteById(id.toString());
    }

    @Override
    default List<User> findAllUsers() {
        return findAll().stream()
                .map(doc -> doc.toDomainUser())
                .collect(Collectors.toList());
    }
}
