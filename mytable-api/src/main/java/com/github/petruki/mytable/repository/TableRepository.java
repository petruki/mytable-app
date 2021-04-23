package com.github.petruki.mytable.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.github.petruki.mytable.model.Table;

@Repository
public interface TableRepository extends MongoRepository<Table, String> {
	
	Table findByAlias(String alias);

}
