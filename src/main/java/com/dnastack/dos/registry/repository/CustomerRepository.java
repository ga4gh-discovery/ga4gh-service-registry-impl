package com.dnastack.dos.registry.repository;

import com.dnastack.dos.registry.model.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
public interface CustomerRepository extends CrudRepository<Customer, Long>{
}
