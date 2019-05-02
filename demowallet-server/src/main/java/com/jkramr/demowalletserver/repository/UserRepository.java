package com.jkramr.demowalletserver.repository;


import com.jkramr.demowalletserver.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
