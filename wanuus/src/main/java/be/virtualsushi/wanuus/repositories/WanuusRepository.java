package be.virtualsushi.wanuus.repositories;

import org.springframework.data.repository.CrudRepository;

import be.virtualsushi.wanuus.model.BaseEntity;

public interface WanuusRepository<E extends BaseEntity> extends CrudRepository<E, Long> {

}
